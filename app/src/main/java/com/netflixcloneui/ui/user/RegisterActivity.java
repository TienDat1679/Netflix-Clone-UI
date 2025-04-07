package com.netflixcloneui.ui.user;

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
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.request.RegisterRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.utils.ApiErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etName;
    private Button btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        etName = findViewById(R.id.et_name);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                registerAccount(email, password, name);
            } else {
                Toast.makeText(this, "Hãy điền đầy đủ thông tin !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerAccount(String email, String password, String name){
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.registerAccount(new RegisterRequest(email, password, name));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                ApiResponse<?> apiResponse;
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đã gửi OTP vào Email của bạn.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    apiResponse = ApiErrorHandler.parseError(response);
                    switch (apiResponse.getCode()) {
                        case 1002:
                            Toast.makeText(RegisterActivity.this, "Email đã được sử dụng.", Toast.LENGTH_SHORT).show();
                            break;
                        case 1003:
                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                        case 1004:
                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                        case 1005:
                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}