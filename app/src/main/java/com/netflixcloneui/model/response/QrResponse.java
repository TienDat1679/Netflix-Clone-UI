package com.netflixcloneui.model.response;

public class QrResponse {
    private String paymentUrl;
    private String qrCode; // Ảnh QR dạng Base64

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getQrCode() {
        return qrCode;
    }
}
