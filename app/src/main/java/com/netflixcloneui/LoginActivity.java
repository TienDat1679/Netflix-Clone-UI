package com.netflixcloneui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.LoginRequest;
import com.netflixcloneui.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị ProgressBar
                    progressBar.setVisibility(View.VISIBLE);

                    loginAccount(email, password);
                }
            }
        });

        // Xử lý khi nhấn vào "Register"
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý khi nhấn vào "Forgot Password"
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginAccount(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //SharedPreferences sharedPreferences1 = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                //SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                //editor1.remove("jwt_token");
                //editor1.apply();

                // String jwtToken1 = sharedPreferences1.getString("jwt_token", null);
                // Log.d("SharedPreferences", "JWT Token: " + jwtToken1);

                // Ẩn ProgressBar khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    String jwtToken = response.body().getToken();
                    // Lưu JWT vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("jwt_token", jwtToken);
                    editor.apply();
                    // Chuyển đến HomeActivity
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Tài khoản chưa được kích hoạt.", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(LoginActivity.this, "Sai jwt.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Ẩn ProgressBar khi có lỗi
                progressBar.setVisibility(View.GONE);
                // Xử lý khi có lỗi kết nối mạng
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}