package com.netflixcloneui.screen.ui.my_netflix;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.R;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.QrResponse;
import com.netflixcloneui.model.VNPayResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSummaryActivity extends AppCompatActivity {

    private ImageView imageViewQR;

    View dialogView,dialogSuccess;
    Integer amount;
    Button button,btnOk,btnCancel,btnSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        amount = (Integer) getIntent().getIntExtra("amount",-1);
        button = findViewById(R.id.btnConfirmPayment);
        button.setOnClickListener(view -> openDialog()
                );

    }
    private void openDialog() {
        Log.d("amout",amount.toString());
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<QrResponse> call = apiService.generateVnpayQR(amount.toString());
        call.enqueue(new Callback<QrResponse>() {
            @Override
            public void onResponse(Call<QrResponse> call, Response<QrResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String qrBase64 = response.body().getQrCode();
                    byte[] decodedString = Base64.decode(qrBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Log.d("qr", "tao thanh cong");
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentSummaryActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.qr_payment_dialog, null);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    imageViewQR = dialogView.findViewById(R.id.qr_code_image);
                    imageViewQR.setImageBitmap(bitmap);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                    Button btnOk = dialogView.findViewById(R.id.btn_ok);
                    btnOk.setOnClickListener(v -> testCallBack(amount,"00",dialog));
                } else {
                    Toast.makeText(PaymentSummaryActivity.this, "Lỗi khi lấy mã QR!", Toast.LENGTH_SHORT).show();
                }
            }
            private void testCallBack(Integer amount, String status,AlertDialog dialog) {
                Map<String, String> params = new HashMap<>();
                params.put("vnp_ResponseCode", "00");
                params.put("vnp_Amount", amount.toString());

                ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                Call<VNPayResponse > call = apiService.checkPayment(params);
                call.enqueue(new Callback<VNPayResponse >() {
                    @Override
                    public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse > response) {
                        if (response.isSuccessful()) {
                            VNPayResponse vnPayResponse=response.body();
                            if (vnPayResponse.getStatus().equals("success")) {
                                congratulations(dialog);
                            } else {

                            }
                        } else {
                            Toast.makeText(PaymentSummaryActivity.this, "Lỗi khi lấy mã QR!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VNPayResponse > call, Throwable t) {
                        Log.e("Payment process", "API Call failed: " + t.getMessage());
                    }

                });
            }

            private void congratulations(AlertDialog dialog ) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentSummaryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialog1 = inflater.inflate(R.layout.success_dialog, null);
                builder.setView(dialog1);
                AlertDialog dialogS = builder.create();
                dialogS.setCanceledOnTouchOutside(true);
                dialogS.show();
                Button btn = dialogS.findViewById(R.id.btnSuccess);
                btn.setOnClickListener(v->dialogS.dismiss());
            }

            @Override
            public void onFailure(Call<QrResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());

            }
        });
    }
}