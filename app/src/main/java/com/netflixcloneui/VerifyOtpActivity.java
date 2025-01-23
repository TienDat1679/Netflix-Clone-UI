package com.netflixcloneui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp, btnResendOtp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etOtp = findViewById(R.id.et_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        btnResendOtp = findViewById(R.id.btn_resend_otp);
        progressBar = findViewById(R.id.progressBar);

        String email = getIntent().getStringExtra("email");

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (!otp.isEmpty()) {
                // Hiển thị ProgressBar
                progressBar.setVisibility(View.VISIBLE);

                verifyOtp(email, otp);
            } else {
                Toast.makeText(this, "Hãy nhập OTP !", Toast.LENGTH_SHORT).show();
            }
        });

        btnResendOtp.setOnClickListener(v -> resendOtp(email));
    }

    private void verifyOtp(String email, String otp) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.verifyOtp(otp, email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    // Xác minh thành công
                    Toast.makeText(VerifyOtpActivity.this, "Tài khoản đã được xác thực thành công.", Toast.LENGTH_SHORT).show();

                    // Chuyển đến màn hình đăng nhập
                    Intent intent = new Intent(VerifyOtpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý lỗi (nếu có)
                    Toast.makeText(VerifyOtpActivity.this, "Mã OTP không hợp lệ.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                Toast.makeText(VerifyOtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp(String email) {
        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.resendOtp(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(VerifyOtpActivity.this, "Đã gửi lại mã OTP vào Email " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Failed to resend OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                Toast.makeText(VerifyOtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}