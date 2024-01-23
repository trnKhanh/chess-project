package com.chessproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.chessproject.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_games)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (getIntent().hasExtra("FRAGMENT_TO_SHOW")) {
            Log.d("FRAGMENT NAME", getIntent().getStringExtra("FRAGMENT_TO_SHOW"));
            String fragmentToShow = getIntent().getStringExtra("FRAGMENT_TO_SHOW");
            switchFragment(navController, fragmentToShow);
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navController.navigate(item.getItemId());
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(navController.getCurrentDestination().getId() == R.id.navigation_result){
            return navController.popBackStack(R.id.navigation_camera, false);
        }
        return navController.navigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_help){
            int destination = navController.getCurrentDestination().getId();
            if(destination == R.id.navigation_home){
                // do nothing
            } else if(destination == R.id.navigation_camera
                    || destination == R.id.navigation_result){
                navController.navigate(R.id.navigation_help_detection);
            } else if(destination == R.id.navigation_puzzle){
                navController.navigate(R.id.navigation_help_puzzle);
            } else if(destination == R.id.navigation_blind_puzzle){
                navController.navigate(R.id.navigation_help_blind_puzzle);
            } else if(destination == R.id.navigation_evaluation_game){
                navController.navigate(R.id.navigation_help_evaluation_game);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(NavController navController, String fragmentTag) {
        int destinationId = 0;
        switch (fragmentTag) {
            case "CameraFragment":
                destinationId = R.id.navigation_camera;
                break;
            case "HomeFragment":
                destinationId = R.id.navigation_home;
                break;
            case "NotificationFragment":
                destinationId = R.id.navigation_games;
                break;
        }
        if (destinationId != 0) {
            navController.navigate(destinationId);
        }
    }
}
