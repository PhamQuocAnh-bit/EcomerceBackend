package com.techstore.utils;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public final class VNPayUtil {

    private VNPayUtil() {
    }

    public static String hmacSHA512(
            String secretKey,
            String data
    ) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException(
                    "VNPay hash secret không được để trống"
            );
        }

        try {
            Mac hmac = Mac.getInstance("HmacSHA512");

            SecretKeySpec keySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            hmac.init(keySpec);

            byte[] result = hmac.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder(result.length * 2);

            for (byte item : result) {
                hex.append(String.format("%02x", item));
            }

            return hex.toString();
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Không thể tạo chữ ký VNPay",
                    exception
            );
        }
    }

    public static String buildHashData(
            Map<String, String> parameters
    ) {
        List<String> fieldNames = new ArrayList<>(
                parameters.keySet()
        );

        Collections.sort(fieldNames);

        StringJoiner joiner = new StringJoiner("&");

        for (String fieldName : fieldNames) {
            String fieldValue = parameters.get(fieldName);

            if (fieldValue == null || fieldValue.isBlank()) {
                continue;
            }

            joiner.add(
                    encode(fieldName)
                            + "="
                            + encode(fieldValue)
            );
        }

        return joiner.toString();
    }

    public static String buildQueryString(
            Map<String, String> parameters
    ) {
        return buildHashData(parameters);
    }

    public static Map<String, String> getCallbackParameters(
            HttpServletRequest request
    ) {
        Map<String, String> parameters = new HashMap<>();

        Enumeration<String> names =
                request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getParameter(name);

            if (value != null && !value.isBlank()) {
                parameters.put(name, value);
            }
        }

        return parameters;
    }

    public static Map<String, String> getSecureParameters(
            Map<String, String> callbackParameters
    ) {
        Map<String, String> secureParameters =
                new HashMap<>(callbackParameters);

        secureParameters.remove("vnp_SecureHash");
        secureParameters.remove("vnp_SecureHashType");

        return secureParameters;
    }

    public static boolean verifySignature(
            Map<String, String> callbackParameters,
            String hashSecret
    ) {
        String receivedHash =
                callbackParameters.get("vnp_SecureHash");

        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> secureParameters =
                getSecureParameters(callbackParameters);

        String hashData = buildHashData(secureParameters);

        String calculatedHash =
                hmacSHA512(hashSecret, hashData);

        return MessageDigest.isEqual(
                calculatedHash.toLowerCase(Locale.ROOT)
                        .getBytes(StandardCharsets.UTF_8),
                receivedHash.toLowerCase(Locale.ROOT)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String getClientIp(
            HttpServletRequest request
    ) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);

            if (ip != null
                    && !ip.isBlank()
                    && !"unknown".equalsIgnoreCase(ip)) {

                if (ip.contains(",")) {
                    return normalizeIp(
                            ip.substring(0, ip.indexOf(",")).trim()
                    );
                }

                return normalizeIp(ip.trim());
            }
        }

        return normalizeIp(request.getRemoteAddr());
    }

    private static String normalizeIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return "127.0.0.1";
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)
                || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

    private static String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.US_ASCII
        );
    }
}