package com.example.angietong.lobo.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.example.angietong.lobo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MainActivity extends AppCompatActivity {

    private static final String BACKENDLESS_APP_ID = "5B5E6CAE-0C25-19FE-FF24-600425E97500";
    private static final String BACKENDLESS_SECRET_KEY = "DE82CF74-A7CF-9241-FF82-04CD84301800";

    private Callbacks n = new Callbacks();

    //
    // GoogleApiClient mGoogleApiClient = null;

    //TEST COMMIT TEST COMMIT TEST COMMIT TEST COMMIT TEST COMMIT
    //TEST COMMIT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String appVersion = "v1";
        Backendless.initApp( this, BACKENDLESS_APP_ID, BACKENDLESS_SECRET_KEY, appVersion );

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(n);

        //buildGoogleAPIClient();
    }

//    private void buildGoogleAPIClient() {
//
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(n)
//                    .addOnConnectionFailedListener(n)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//    }

    class Callbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback
    {

        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

        }
    }
}



