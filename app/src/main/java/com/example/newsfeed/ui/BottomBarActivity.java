package com.example.newsfeed.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.newsfeed.R;
import com.example.newsfeed.ui.saved.SavedListFragment;
import com.example.newsfeed.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BottomBarActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    public static final String TAG = "BottomBarActivity";

    final Fragment fragmentHome = new HomeFragment();
    final Fragment fragmentsSaved = new SavedListFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);
        BottomNavigationView navView = findViewById(R.id.nav_view);


        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentsSaved, "2").hide(fragmentsSaved).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentHome, "1").commit();
        navView.setOnNavigationItemSelectedListener(navListenr);




        /*
         * here we are registering our app in firebase cloud messaging by topic
         * */
        fcmRegistrationByTopic();

        /*check for runtime permission*/
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListenr = item -> {
        switch (item.getItemId()) {

            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(fragmentHome).commit();
                active = fragmentHome;
                return true;

            case R.id.navigation_saved:
                fm.beginTransaction().hide(active).show(fragmentsSaved).commit();
                fragmentsSaved.onResume();
                active = fragmentsSaved;
                return true;
        }

        return false;

    };


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this,
                        "Permission Denied, app needs this permission to load the images",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

    }


    private void fcmRegistrationByTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.fcm_topic))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "fcm subscribed successfully");
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "fcm subscription failed");

                        }
                    }
                });
    }
}
