/*
package com.netflixcloneui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.QrResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class qr_test extends AppCompatActivity {

    private ImageView imageViewQR;
    private EditText editTextAmount, editTextUserId;
    private Button btnGenerateQR;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.qr_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageViewQR = findViewById(R.id.imageViewQR);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextUserId = findViewById(R.id.editTextUserId);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);



        btnGenerateQR.setOnClickListener(v -> {
            int amount = Integer.parseInt(editTextAmount.getText().toString());
            String userId = editTextUserId.getText().toString();
            generateVnpayQR(amount, userId);
        });


    }
    private void generateVnpayQR(int amount, String userId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<QrResponse> call = apiService.generateVnpayQR(amount);
        call.enqueue(new Callback<QrResponse>() {
            @Override
            public void onResponse(Call<QrResponse> call, Response<QrResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String qrBase64 = response.body().getQrCode();
                    byte[] decodedString = Base64.decode(qrBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageViewQR.setImageBitmap(bitmap);
                    Log.d("qr", "tao thanh cong");
                } else {
                    Toast.makeText(qr_test.this, "Lỗi khi lấy mã QR!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QrResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
                Toast.makeText(qr_test.this, "Không kết nối được server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}*/
