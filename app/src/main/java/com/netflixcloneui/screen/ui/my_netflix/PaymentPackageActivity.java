package com.netflixcloneui.screen.ui.my_netflix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.R;
import com.netflixcloneui.screen.FullScreenVideoActivity;

public class PaymentPackageActivity extends AppCompatActivity {

    LinearLayout option1,option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_package);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        option1.setOnClickListener(v->openSumary(30000));
        option2.setOnClickListener(v->openSumary(280000));
    }

    private void openSumary(int v) {
        Intent intent = new Intent(this, PaymentSummaryActivity.class);
        intent.putExtra("amount", v); // Truyền videoId vào Intent
        startActivity(intent);
    }
}