package com.example.angietong.lobo.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Geo;
import com.backendless.Persistence;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
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

/*TODO:
(Bug Fixes)

*/
public class MainActivity extends AppCompatActivity {

    private static final String BACKENDLESS_APP_ID = "5B5E6CAE-0C25-19FE-FF24-600425E97500";
    private static final String BACKENDLESS_SECRET_KEY = "DE82CF74-A7CF-9241-FF82-04CD84301800";
    private static final String TAG = "mainAct";
    private static final int CAPTURE_IMAGE_RESULT = 001;

    private Callbacks n = new Callbacks();
    private List<Post> retrivedArray;
    private GoogleMap mMap;
    private Bitmap mImage;
    private TextView mPostText = null;
    private ImageView mPostImage = null;
    private RelativeLayout mMiniPost = null;

    //TODO: Don't use global variables
    private String mBackendlessURL;

    private String mBackendlessFileURL;
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

    public void createPost(View view)
    {
        Post postToSave = new Post();

        try {

            //Temporary
            postToSave.setImageTitle(Long.toString(System.currentTimeMillis()));
            postToSave.setLoc(new GeoPoint(-43.6532, 79.3832));

            String tmp = launchCameraIntent();
            postToSave.setImageURI(tmp);

            Backendless.Persistence.save(postToSave, n);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "in the post method");
    }

    /**
     * TODO: Camera Activity TO BE OPTIMIZED
     * */
    public String launchCameraIntent() throws Exception {

        Intent launchCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);




        //TODO: Retrieve String from thread (NO GLOBAL VARIABLES)
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                File f = new File("/sdcard/DCIM/Camera/IMG_20170308_183820.jpg");
                try {
                    mBackendlessFileURL = Backendless.Files.upload(f, "media").getFileURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, mBackendlessFileURL);

            }
        });

        t.start();
        Log.d(TAG, "Before " + mBackendlessFileURL);
        t.join();
        Log.d(TAG, "After " + mBackendlessFileURL);
        return(mBackendlessFileURL);
    }



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

                Toast.makeText(MainActivity.this, "REFRESHING!", Toast.LENGTH_SHORT).show();
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
                .position(new LatLng(p.getLoc().getLatitude(), p.getLoc().getLongitude())))
                .setTag(p);
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

            // Bottom Toolbar Button Listeners
            mUserButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {startActivity(new Intent(MainActivity.this, Splash.class));}});
            mCameraButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {launchCameraIntent(null);}});
            mNearbyButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {getPostArray();}});
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
            try {
                Log.d(TAG, "IM IN MARKER CLICKER LISTENRE");
                mMiniPost.setVisibility(View.VISIBLE); mMiniPost.bringToFront();
                Post temp = (Post) marker.getTag();
                Log.d(TAG, "MY IMAGE IS FROM: " + temp.getImageURI());
                mPostText.setText(temp.getImageTitle());
                mPostImage.setImageBitmap(PostUtil.getImageFromURL(temp.getImageURI()));
            } catch (Exception e) {e.printStackTrace();}
            return false;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            mMiniPost.setVisibility(View.INVISIBLE);
        }
    }

    class ImageUploadCallback implements AsyncCallback<BackendlessFile>
    {
        String imageURL = null;
        @Override
        public void handleResponse(BackendlessFile backendlessFile) {
            Log.d(TAG, "UPLOADED " + backendlessFile.getFileURL());
            setImageURL(backendlessFile.getFileURL());
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Log.d(TAG, "DID NT UPLOADED");
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
            Log.d(TAG, "setImageURL" + this.imageURL);
        }

        public String getImageURL() {
            Log.d(TAG,"getImageURL" + imageURL);
            return imageURL;
        }

    }
}



