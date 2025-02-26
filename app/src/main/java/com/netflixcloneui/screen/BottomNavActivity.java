package com.netflixcloneui.screen;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.netflixcloneui.R;
import com.netflixcloneui.databinding.ActivityBottomNavBinding;

public class BottomNavActivity extends AppCompatActivity {

    private ActivityBottomNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar làm ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hiển thị nút back (nếu cần)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
            getSupportActionBar().setHomeButtonEnabled(true); // Cho phép nhấn nút back
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Gắn menu vào Toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý sự kiện khi người dùng nhấn vào các nút
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Xử lý khi nhấn nút tìm kiếm
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            // Xử lý khi nhấn nút back
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}