package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.R;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.response.PaymentResponse;


import org.jetbrains.annotations.Nullable;

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

    private ActivityResultLauncher<Intent> paymentLauncher;
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
        TextView price=findViewById(R.id.tvPrice);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView amout=findViewById(R.id.amout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView total=findViewById(R.id.total);

        amount = (Integer) getIntent().getIntExtra("amount",-1);
        if(amount==30000){
            price.setText("$30.000 /tháng");
            amout.setText("30.000");
            total.setText("30.000");
        }
        else{
            price.setText("$280.000 /tháng");
            amout.setText("280.000");
            total.setText("280.000");
        }
        button = findViewById(R.id.btnConfirmPayment);
        button.setOnClickListener(view -> payment()
                );
        paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && "00".equals(data.getStringExtra("vnp_ResponseCode"))) {
                            congratulations();
                        }
                        else {
                            failed();
                        }
                    }
                }
        );
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }

    private void failed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentSummaryActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialog1 = inflater.inflate(R.layout.failed_dialog, null);
        builder.setView(dialog1);
        AlertDialog dialogS = builder.create();
        dialogS.setCanceledOnTouchOutside(true);
        dialogS.show();
        Button btn = dialogS.findViewById(R.id.btnOk);
        btn.setOnClickListener(v->dialogS.dismiss());
    }

    private void payment() {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<PaymentResponse> call = apiService.payment(amount.toString(),"NCB");
        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().data.paymentUrl;
                    Intent intent=new Intent(PaymentSummaryActivity.this,PaymentProcessAcitvity.class);
                    intent.putExtra("url",url);
                    paymentLauncher.launch(intent);
                } else {
                    Toast.makeText(PaymentSummaryActivity.this, "Lỗi khi thanh toan!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());

            }
        });
    }
    private void congratulations () {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentSummaryActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialog1 = inflater.inflate(R.layout.success_dialog, null);
        builder.setView(dialog1);
        AlertDialog dialogS = builder.create();
        dialogS.setCanceledOnTouchOutside(true);
        dialogS.show();
        Button btn = dialogS.findViewById(R.id.btnSuccess);
        btn.setOnClickListener(v -> {
            dialogS.dismiss();
            Intent intent = new Intent(PaymentSummaryActivity.this, BottomNavActivity.class);
            startActivity(intent);
            finish();
        });
    }
}