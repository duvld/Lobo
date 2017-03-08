package com.example.angietong.lobo.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Geo;
import com.backendless.Persistence;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.example.angietong.lobo.Model.Post;
import com.example.angietong.lobo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String BACKENDLESS_APP_ID = "5B5E6CAE-0C25-19FE-FF24-600425E97500";
    private static final String BACKENDLESS_SECRET_KEY = "DE82CF74-A7CF-9241-FF82-04CD84301800";
    private static final String TAG = "mainAct";
    private static final int CAPTURE_IMAGE_RESULT = 001;

    private Post testPost;
    private Callbacks n = new Callbacks();
    private List<Post> retrivedArray;
    private GoogleMap mMap;
    private Bitmap mImage;
    private TextView mPostText = null;
    private ImageView mPostImage = null;
    private RelativeLayout mMiniPost = null;
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

        mPostText = (TextView) findViewById(R.id.postTextView);
        mPostImage = (ImageView) findViewById(R.id.postImageView);
        mMiniPost = (RelativeLayout) findViewById(R.id.activity_miniPost);
        mPostText.bringToFront();
        mMiniPost.bringToFront();
        mMiniPost.setVisibility(View.INVISIBLE);

        //buildGoogleAPIClient();

        //TEST setArray and getPostArray HERE
       /* setPost("bullshit1", "bullshit2", (Math.random() * 140.0 * -1), (Math.random() * 140.0));
        setPost("bullshit3123", "bullshi3123t2", (Math.random() * 140.0 * -1), (Math.random() * 140.0));
        setPost("bulls1412hit1", "bull41234shit2", (Math.random() * 140.0 * -1), (Math.random() * 140.0));*/
        getPostArray();
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

    public void setPost(String image, String title, double Lat, double Long)
    {
        GeoPoint g = new GeoPoint(Lat, Long);
        testPost = new Post(image, title, g);
        Backendless.Persistence.save(testPost, n);
        Log.d(TAG, "in the post method");
    }

    /**
     * TODO: Camera Activity
     * */
//    public void launchCameraIntent(View view)
//    {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (cameraIntent.resolveActivity(getPackageManager()) != null)
//        {
//            File image = null;
//            File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//            try {
//
//                String prefix = Long.toString(System.currentTimeMillis());
//                image = File.createTempFile(prefix,".JPG", fileDir);
//                Log.d(TAG, "fileCreated");
//
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//            if (image != null)
//            {
//                Log.d(TAG, "fileSaved");
//                Uri deviceImageURI = Uri.fromFile(image);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, deviceImageURI);
//                startActivityForResult(cameraIntent, CAPTURE_IMAGE_RESULT);
//            }
//
//        }
//    }



//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    if (requestCode == CAPTURE_IMAGE_RESULT)
//        if (requestCode ==  RESULT_OK)
//        {
//            try {
//                Uri deviecUri = data.getData();
//                mImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(deviecUri));
//                Log.d(TAG, "onActivityResult");
//                Backendless.Files.Android.upload(mImage, Bitmap.CompressFormat.JPEG, 100, Long.toString(System.currentTimeMillis()), "media");
//            } catch (Exception e) {
//                Log.d(TAG, "onActivityResult din work");
//                e.printStackTrace();
//            }
//        }
//    }

    public void getPostArray()
    {

        Backendless.Persistence.of(Post.class).find(new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> postBackendlessCollection) {

                retrivedArray = postBackendlessCollection.getCurrentPage();
                for (Post p:retrivedArray)
                {
                    Log.d(TAG, p.getImageTitle() + " | " + p.getImageURI() + " | " + p.getLoc().getLatitudeE6());
                    setMarker(p);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    public void setMarker(Post p)
    {
        mMap.addMarker(new MarkerOptions()
                .title(p.getImageTitle())
                .position(new LatLng(p.getLoc().getLatitude(), p.getLoc().getLongitude()))
        );
    }

    class Callbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
            OnMapReadyCallback, AsyncCallback<Post>, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
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
            mMap = googleMap;
            mMap.setOnMarkerClickListener(n);
            mMap.setOnMapClickListener(n);
        }

        @Override
        public void handleResponse(Post savedPost) {
            Log.d(TAG, "it worked");
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Log.d(TAG, "it DID NOT worked" + backendlessFault);

        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Log.d(TAG, "IM IN MARKER CLICKER LISTENRE");
            mMiniPost.setVisibility(View.VISIBLE);
            mMiniPost.bringToFront();
            return false;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            mMiniPost.setVisibility(View.INVISIBLE);
        }
    }
}



