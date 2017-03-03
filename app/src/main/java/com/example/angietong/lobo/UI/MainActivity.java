package com.example.angietong.lobo.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Persistence;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.angietong.lobo.Model.Post;
import com.example.angietong.lobo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String BACKENDLESS_APP_ID = "5B5E6CAE-0C25-19FE-FF24-600425E97500";
    private static final String BACKENDLESS_SECRET_KEY = "DE82CF74-A7CF-9241-FF82-04CD84301800";
    private static final String TAG = "mainAct";
    private Post testPost;
    private Callbacks n = new Callbacks();
    private List<Post> retrivedArray;

    // GoogleApiClient mGoogleApiClient = null;



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

        //TEST setArray and getPostArray HERE
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

    public void setPost(String image, String title)
    {
        testPost = new Post(image, title);
        Backendless.Persistence.save(testPost, n);
        Log.d(TAG, "in the post method");
    }

    public void getPostArray()
    {

        Backendless.Persistence.of(Post.class).find(new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> postBackendlessCollection) {

                retrivedArray = postBackendlessCollection.getCurrentPage();
                for (Post p:retrivedArray)
                {
                    Log.d(TAG, p.getImageTitle() + " | " + p.getImageURI());
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    class Callbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, AsyncCallback<Post>
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

        @Override
        public void handleResponse(Post savedPost) {
            Log.d(TAG, "it worked");
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Log.d(TAG, "it DID NOT worked" + backendlessFault);

        }
    }
}



