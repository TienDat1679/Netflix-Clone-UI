package com.netflixcloneui.screen.auth;

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

import com.netflixcloneui.R;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnVerifyEmail;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        btnVerifyEmail = findViewById(R.id.btn_verify_email);
        progressBar = findViewById(R.id.progressBar);

        btnVerifyEmail.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (isValidEmail(email)) {
                // Hiển thị ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                verifyEmail(email);
            } else {
                Toast.makeText(this, "Hãy nhập địa chỉ Email hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyEmail(String email) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.verifyForgotPasswordEmail(email);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi mã OTP vào Email của bạn !", Toast.LENGTH_SHORT).show();

                    // Điều hướng đến màn hình tiếp theo để nhập OTP
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpForgotPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}