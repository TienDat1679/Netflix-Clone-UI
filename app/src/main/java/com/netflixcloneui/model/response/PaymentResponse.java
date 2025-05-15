package com.netflixcloneui.model.response;

public class PaymentResponse {
    public int code;
    public String message;
    public PaymentData data;

    public static class PaymentData {
        public String code;
        public String message;
        public String paymentUrl;
    }
}
