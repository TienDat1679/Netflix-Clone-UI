package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.R;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.UserResponse;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentProcessAcitvity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_process_acitvity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        String paymentUrl = getIntent().getStringExtra("url");
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(paymentUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.contains("vn-pay-callback")) {
                    Uri uri = Uri.parse(url);
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode"); // lấy mã phản hồi
                    if ("00".equals(responseCode)) {
                        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                        Call<Void> call = apiService.playbackVnpay(uri.getQueryParameter("vnp_Amount"));// Không cần chuyển đổi bằng `Long.valueOf()`
                        call.enqueue(new Callback<Void>() {
                                         @Override
                                         public void onResponse(Call<Void> call, Response<Void> response) {
                                         }
                                         @Override
                                         public void onFailure(Call<Void> call, Throwable t) {
                                         }
                                     }
                        );
                        Log.d("VNPayCallback", "Thanh toán THÀNH CÔNG");

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("vnp_ResponseCode", "00");
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish(); // Quay về PaymentSummaryActivity

                    } else {
                        Log.d("VNPayCallback", "Thanh toán THẤT BẠI, code: " + responseCode);
                        // TODO: Hiển thị lỗi, xử lý retry v.v.
                    }

                    finish(); // Đóng WebView hoặc chuyển activity
                    return true;
                }

                return false;
            }
        });
    }
}