package com.netflixcloneui.model;

//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Base64;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class  VnpayQRCode{
//    private final Context context;
//    private final RequestQueue requestQueue;
//    public VnpayQRCode(Context context) {
//        this.context = context;
//        this.requestQueue = Volley.newRequestQueue(context);
//    }
//    public void generateVnpayQRCode(int amount, String userId, ImageView imageViewQR) {
//        String url = "http://localhost:8888/api/vnpay/generateQR?amount=" + amount + "&userId=" + userId;
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    try {
//                        String qrBase64 = response.getString("qrCode");
//                        byte[] decodedString = Base64.decode(qrBase64, Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        imageViewQR.setImageBitmap(bitmap);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//                error -> Log.e("API_ERROR", "Lỗi gọi API: " + error.toString()));
//
//        requestQueue.add(request);
//    }
//    public void checkPaymentStatus() {
//        String url = "http://YOUR_SERVER_IP:8888/api/vnpay/callback";
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show(),
//                error -> Toast.makeText(context, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show());
//
//        requestQueue.add(request);
//    }
}
