package com.example.angietong.lobo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.backendless.Backendless;

public class MainActivity extends AppCompatActivity {

    private static final String YOUR_APP_ID = "5B5E6CAE-0C25-19FE-FF24-600425E97500";
    private static final String YOUR_SECRET_KEY = "DE82CF74-A7CF-9241-FF82-04CD84301800";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String appVersion = "v1";
        Backendless.initApp( this, YOUR_APP_ID, YOUR_SECRET_KEY, appVersion );
    }
}
