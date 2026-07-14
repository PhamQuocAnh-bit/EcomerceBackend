package com.techstore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.vnpay")
public class VNPayProperties {

    private String payUrl;
    private String returnUrl;
    private String ipnUrl;

    private String tmnCode;
    private String hashSecret;

    private String version = "2.1.0";
    private String command = "pay";
    private String orderType = "other";
    private String locale = "vn";
    private String currencyCode = "VND";

    private int expireMinutes = 15;
    private String timeZone = "Asia/Ho_Chi_Minh";
}