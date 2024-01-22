package com.chessproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.chessproject.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        if(navController.getCurrentDestination().getId() == R.id.navigation_result){
            return navController.popBackStack(R.id.navigation_camera, false);
        }
        return navController.navigateUp();
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
