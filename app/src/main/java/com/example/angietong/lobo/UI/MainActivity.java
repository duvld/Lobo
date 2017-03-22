package com.example.angietong.lobo.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.angietong.lobo.Model.PostUtil;
import com.google.android.gms.location.LocationServices;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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

    private Callbacks n = new Callbacks();
    private List <Post> retrivedArray;
    private GoogleMap mMap;
    private Bitmap mImage;

    // Bottom Tool Bar and Buttons
    private RelativeLayout mBtmToolbar = null;
    private ImageView mNearbyButton = null;
    private ImageView mCameraButton = null;
    private ImageView mUserButton = null;

    // Image Viewer
    private RelativeLayout mMiniPost = null;
    private TextView mPostText = null;
    private ImageView mPostImage = null;

    //Pre Post Layout Variables
    RelativeLayout mPrePostGroup = null;
    TextView mPrePostTitle = null;
    ImageView mPrePostImage = null;
    private Button mOkButton = null;
    private Button mNoButton = null;

    // Location Variables
    public GoogleApiClient mGoogleApiClient = null;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    public Location mLastLocation;

    //TODO: Don't use global variables
    private String mBackendlessURL;

    private String mNewPostImagePath;
    private String mBackendlessFileURL;
    // GoogleApiClient mGoogleApiClient = null;

    // Create Map Style
    String styleString = "[{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#165c64\"},{\"saturation\":34},{\"lightness\":-69},{\"visibility\":\"on\"}]},{\"featureType\":\"landscape\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#b7caaa\"},{\"saturation\":-14},{\"lightness\":-18},{\"visibility\":\"on\"}]},{\"featureType\":\"landscape.man_made\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#cbdac1\"},{\"saturation\":-6},{\"lightness\":-9},{\"visibility\":\"on\"}]},{\"featureType\":\"road\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#8d9b83\"},{\"saturation\":-89},{\"lightness\":-12},{\"visibility\":\"on\"}]},{\"featureType\":\"road.highway\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#d4dad0\"},{\"saturation\":-88},{\"lightness\":54},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.arterial\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#bdc5b6\"},{\"saturation\":-89},{\"lightness\":-3},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.local\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#bdc5b6\"},{\"saturation\":-89},{\"lightness\":-26},{\"visibility\":\"on\"}]},{\"featureType\":\"poi\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#c17118\"},{\"saturation\":61},{\"lightness\":-45},{\"visibility\":\"on\"}]},{\"featureType\":\"poi.park\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#8ba975\"},{\"saturation\":-46},{\"lightness\":-28},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#a43218\"},{\"saturation\":74},{\"lightness\":-51},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#ffffff\"},{\"saturation\":0},{\"lightness\":100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.neighborhood\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#ffffff\"},{\"saturation\":0},{\"lightness\":100},{\"visibility\":\"off\"}]},{\"featureType\":\"administrative.locality\",\"elementType\":\"labels\",\"stylers\":[{\"hue\":\"#ffffff\"},{\"saturation\":0},{\"lightness\":100},{\"visibility\":\"off\"}]},{\"featureType\":\"administrative.land_parcel\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#ffffff\"},{\"saturation\":0},{\"lightness\":100},{\"visibility\":\"off\"}]},{\"featureType\":\"administrative\",\"elementType\":\"all\",\"stylers\":[{\"hue\":\"#3a3935\"},{\"saturation\":5},{\"lightness\":-57},{\"visibility\":\"off\"}]},{\"featureType\":\"poi.medical\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#cba923\"},{\"saturation\":50},{\"lightness\":-46},{\"visibility\":\"on\"}]}]";
    public MapStyleOptions style = new MapStyleOptions(styleString);

    // API Client
    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(n).addOnConnectionFailedListener(n).addApi(LocationServices.API).build();
        }
    }

    // Activity on Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String appVersion = "v1";
        Backendless.initApp(this, BACKENDLESS_APP_ID, BACKENDLESS_SECRET_KEY, appVersion);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(n);

        // Bottom Tool Bar and Buttons
        mBtmToolbar = (RelativeLayout) findViewById(R.id.bottomToolbar);
        mNearbyButton = (ImageView) findViewById(R.id.nearbyButton);
        mUserButton = (ImageView) findViewById(R.id.userButton);
        mCameraButton = (ImageView) findViewById(R.id.createPostButton);

        // Image Viewer
        mMiniPost = (RelativeLayout) findViewById(R.id.activity_miniPost);
        mPostText = (TextView) findViewById(R.id.postTextView);
        mPostImage = (ImageView) findViewById(R.id.postImageView);

        mMiniPost = (RelativeLayout) findViewById(R.id.activity_miniPost);

        mPrePostGroup = (RelativeLayout) findViewById(R.id.prePostViewGroup);
        mPrePostTitle = (TextView) findViewById(R.id.prePostTitleTextView);
        mPrePostImage = (ImageView) findViewById(R.id.prePostImageView);
        mPrePostGroup.setVisibility(View.INVISIBLE);
        mPrePostGroup.setEnabled(false);
        mOkButton = (Button) findViewById(R.id.okButton);
        mNoButton = (Button) findViewById(R.id.noButton);

        mPostText.bringToFront();
        mMiniPost.bringToFront();
        mMiniPost.setVisibility(View.INVISIBLE);
        mBtmToolbar.setVisibility(View.VISIBLE);

        buildGoogleAPIClient();

        //TEST setArray and getPostArray HERE
        getPostArray();
    }

    //ON CLICK FOR CAPTURE BUTTONS
    public void okButtonFunction(View view)
    {
        Log.d(TAG, "OKButton");
        mPrePostGroup.setEnabled(false);
        mPrePostGroup.setVisibility(View.INVISIBLE);
        mPrePostImage.setImageBitmap(null);
        createPost();

    }
    public void noButtonFunction(View view)
    {
        Log.d(TAG, "NOButton");
        mPrePostGroup.setEnabled(false);
        mPrePostGroup.setVisibility(View.INVISIBLE);
        mPrePostImage.setImageBitmap(null);
    }

    public void createPost() {
        Post postToSave = new Post();

        try {

            //Temporary
            postToSave.setImageTitle(Long.toString(System.currentTimeMillis()));
            postToSave.setLoc(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            Log.d(TAG, "Create post: " + mBackendlessFileURL);
            postToSave.setImageURI(mBackendlessFileURL);
            Backendless.Persistence.save(postToSave, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "in the post method");
    }

    /**
     * TODO: Camera Activity TO BE OPTIMIZED
     * */
    public void launchCameraIntent(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "launchCamera1");
            File image = null;
            File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            try {

                String prefix = Long.toString(System.currentTimeMillis());
                image = File.createTempFile(prefix, ".JPG", fileDir);
                Log.d(TAG, "launchCamera2");

            } catch (Exception e) {
                Log.d(TAG, "launchCamera3");
                e.printStackTrace();
            }

            if (image != null) {
                final Uri deviceImageURI = Uri.fromFile(image);

                mNewPostImagePath = image.getPath();

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, deviceImageURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_RESULT);
            }
        }
    }protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + CAPTURE_IMAGE_RESULT + " | " + resultCode + " " + RESULT_OK);

        Log.d(TAG, mNewPostImagePath);
        mImage = BitmapFactory.decodeFile(mNewPostImagePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap compressedImage = Bitmap.createScaledBitmap(mImage, mImage.getWidth() / 4, mImage.getHeight() / 4, false);
        mImage = Bitmap.createBitmap(compressedImage, 0, 0, compressedImage.getWidth(), compressedImage.getHeight(), matrix, false);


        try {
            AsyncCallback responder = new AsyncCallback() {
                @Override
                public void handleResponse(Object o) {
                    BackendlessFile bf = (BackendlessFile) o;
                    mBackendlessFileURL = bf.getFileURL();
                    Log.d(TAG, "It saved | " + mBackendlessFileURL);
                    setPrePost();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Log.d(TAG, "it didnt save" + backendlessFault.toString());
                }

            };
            setPrePost();

            Backendless.Files.Android.upload(mImage, Bitmap.CompressFormat.JPEG, 50, Long.toString(System.currentTimeMillis()), "media", responder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //temp - reduce image quality
        //Backendless.Files.Android.upload(mImage, Bitmap.CompressFormat.JPEG, 50, Long.toString(System.currentTimeMillis()), "media", responder);
    }

    private void setPrePost() {
        mPrePostGroup.setEnabled(true);
        mPrePostGroup.setVisibility(View.VISIBLE);

        mPrePostImage.setImageBitmap(mImage);
        mPrePostGroup.bringToFront();
    }





    public void getPostArray()
    {

        Backendless.Persistence.of(Post.class).find(new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> postBackendlessCollection) {

                retrivedArray = postBackendlessCollection.getCurrentPage();
                for (Post p: retrivedArray) {
                    Log.d(TAG, p.getImageTitle() + " | " + p.getImageURI() + " | " + p.getLoc().getLatitudeE6());
                    setMarker(p);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d(TAG, "GETTING POST ARRAY FAILED" + backendlessFault);
            }
        });
    }

    private void findLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findLocation();
                }
            }
        }
    }

    public void setMarker(Post p)
    {
        mMap.addMarker(new MarkerOptions()
                .title(p.getImageTitle())
                .position(new LatLng(p.getLoc().getLatitude(), p.getLoc().getLongitude())))
                .setTag(p);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient();
    }

    class Callbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
            OnMapReadyCallback, AsyncCallback<Post>, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
    {

        @Override
        public void onConnected(Bundle bundle) {
            findLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        // Map
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapStyle(style);
            mMap = googleMap;
            mMap.setOnMapClickListener(n);
            mMap.setOnMarkerClickListener(n);

            // Bottom Toolbar Button Listeners
            mUserButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {startActivity(new Intent(MainActivity.this, Splash.class));}});
            mCameraButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {startActivity(new Intent(MainActivity.this, PostActivity.class));}});
            mNearbyButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {startActivity(new Intent(MainActivity.this, Splash.class));}});
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
                mBtmToolbar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "IM IN MARKER CLICKER LISTENRE");
                mMiniPost.setVisibility(View.VISIBLE); mMiniPost.bringToFront();
                Post temp = (Post) marker.getTag();
                Log.d(TAG, "MY IMAGE IS FROM: " + temp.getImageURI());
                mPostText.setText(temp.getImageTitle()); mPostImage.setImageBitmap(temp.getImage());
            } catch (Exception e) {e.printStackTrace();}
            return false;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            mMiniPost.setVisibility(View.INVISIBLE);
            mBtmToolbar.setVisibility(View.VISIBLE);
        }
    }

    class ImageUploadCallback implements AsyncCallback<BackendlessFile>
    {
        String imageURL = null;
        @Override
        public void handleResponse(BackendlessFile backendlessFile) {
            Log.d(TAG, "Uploaded " + backendlessFile.getFileURL());
            setImageURL(backendlessFile.getFileURL());
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Log.d(TAG, "Did not upload");
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



