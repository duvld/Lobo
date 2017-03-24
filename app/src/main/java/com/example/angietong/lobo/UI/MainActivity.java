package com.example.angietong.lobo.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angietong.lobo.Model.PostUtil;
import com.example.angietong.lobo.R_L.LoginActivity;
import com.example.angietong.lobo.R_L.RegisterActivity;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
    public static boolean loginT; //Lets u know, you're logged in

    public void toRegisterActivity(View view){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void toLoginActivity(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    SlidingUpPanelLayout main = null;

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
    private RelativeLayout mPrePostGroup = null;
    private EditText mPrePostTitle = null;
    private ImageView mPrePostImage = null;
    private Button mOkButton = null;
    private Button mNoButton = null;

    // Location Variables
    public GoogleApiClient mGoogleApiClient = null;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    public Location mLastLocation;

    //TODO: Don't use global variables
    private String mBackendlessURL;

    private boolean enabler = true;

    //title creator
    private EditText mTitleCreator;

    //toolvar
    private Toolbar myToolbar;

    //Full Image
    private ImageView mFullImage;

    private String mNewPostImagePath;
    private String mPostTitle;
    private String mBackendlessFileURL;
    // GoogleApiClient mGoogleApiClient = null;

    //COMMENTS
    private ListView mComments;


    // Create Map Style
    String styleString = "[ { \"featureType\": \"administrative\", \"elementType\": \"labels.text.fill\", \"stylers\": [ { \"color\": \"#444444\" } ] }, { \"featureType\": \"landscape\", \"elementType\": \"all\", \"stylers\": [ { \"color\": \"#f2f2f2\" } ] }, { \"featureType\": \"poi\", \"elementType\": \"all\", \"stylers\": [ { \"visibility\": \"off\" } ] }, { \"featureType\": \"road\", \"elementType\": \"all\", \"stylers\": [ { \"saturation\": -100 }, { \"lightness\": 45 } ] }, { \"featureType\": \"road.highway\", \"elementType\": \"all\", \"stylers\": [ { \"visibility\": \"simplified\" } ] }, { \"featureType\": \"road.arterial\", \"elementType\": \"labels.icon\", \"stylers\": [ { \"visibility\": \"off\" } ] }, { \"featureType\": \"transit\", \"elementType\": \"all\", \"stylers\": [ { \"visibility\": \"off\" } ] }, { \"featureType\": \"water\", \"elementType\": \"all\", \"stylers\": [ { \"color\": \"#cae0e9\" }, { \"visibility\": \"on\" } ] } ]";
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

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        Log.d(TAG, "LoginVariable = " + loginT); //Printf checking if logged in
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        mPrePostGroup = (RelativeLayout) findViewById(R.id.prePostViewGroup);
        mPrePostTitle = (EditText) findViewById(R.id.prePostTitleTextView);
        mPrePostImage = (ImageView) findViewById(R.id.prePostImageView);
        mPrePostGroup.setVisibility(View.INVISIBLE);
        mPrePostGroup.setEnabled(false);

        mOkButton = (Button) findViewById(R.id.okButton);
        mNoButton = (Button) findViewById(R.id.noButton);

        mFullImage = (ImageView) findViewById(R.id.fullImageView);

        main = (SlidingUpPanelLayout) findViewById(R.id.activity_main);
        main.setDragView(mMiniPost);

        mPostText.bringToFront();
        mMiniPost.bringToFront();
        mMiniPost.setVisibility(View.INVISIBLE);
        mBtmToolbar.setVisibility(View.VISIBLE);


        buildGoogleAPIClient();
        commentsListView();

        //DISABLE SCROLLING

        main.setEnabled(!enabler);

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
        myToolbar.setVisibility(View.VISIBLE);
        createPost();
        mPrePostTitle.setText("");



    }
    public void noButtonFunction(View view)
    {
        Log.d(TAG, "NOButton");
        mPrePostGroup.setEnabled(false);
        mPrePostGroup.setVisibility(View.INVISIBLE);
        myToolbar.setVisibility(View.VISIBLE);
        mPrePostTitle.setText("");

        mPrePostImage.setImageBitmap(null);
    }
    public void createPost() {
        Post postToSave = new Post();

        try {

            //Creates local Post object then saves to Backendless

            mPostTitle = mPrePostTitle.getText().toString();
            postToSave.setImageTitle(mPostTitle);

            postToSave.setLoc(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

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
            String prefix = "";

            try {

                prefix = Long.toString(System.currentTimeMillis());
                image = File.createTempFile(prefix, ".JPG", fileDir);
                Log.d(TAG, "launchCamera2");

            } catch (Exception e) {
                Log.d(TAG, "launchCamera3");
                e.printStackTrace();
            }

            if (image != null) {
                final Uri deviceImageURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",image);

                //temp
                mNewPostImagePath = image.getAbsolutePath();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, deviceImageURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_RESULT);
            }
        }
    }protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + CAPTURE_IMAGE_RESULT + " | " + resultCode + " " + RESULT_OK);

        if (requestCode == CAPTURE_IMAGE_RESULT) {
            if (resultCode == RESULT_OK) {

                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.d(TAG, "PostImagePath: " + mNewPostImagePath + "\n ExternalStorageDir AbsolutePath: " + Environment.getExternalStorageDirectory().getAbsolutePath()
                        + " \n ExternalStorageDir Path: " + Environment.getExternalStorageDirectory().getPath()
                        + " \n ExternalPublicDir AbsoPath: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + " \n ExternalPublicDir Path: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());

                int rotationInDegrees = 0; //VARIABLE TO REORIENT THE IMAGE

                mImage = BitmapFactory.decodeFile(mNewPostImagePath);
                try {
                    ExifInterface exifInterface = new ExifInterface(mNewPostImagePath);
                    int exifRotation = 0;
                    if (exifInterface != null) {
                        exifRotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.i("imageRotatin", "exif not null");
                    }
                    if (exifRotation == ExifInterface.ORIENTATION_ROTATE_90) {
                        rotationInDegrees = 90;
                    } else if (exifRotation == ExifInterface.ORIENTATION_ROTATE_180) {
                        rotationInDegrees = 180;
                    } else if (exifRotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        rotationInDegrees = 270;
                    }

                    Log.d(TAG, "THE ORENTTATION IS: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION) + " Normal: " + ExifInterface.ORIENTATION_NORMAL + " 180: " + ExifInterface.ORIENTATION_ROTATE_180);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationInDegrees);
                Bitmap compressedImage = Bitmap.createScaledBitmap(mImage, mImage.getWidth() / 2, mImage.getHeight() / 2, false);
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
        }
    }

    private void setPrePost() {
        mPrePostGroup.setEnabled(true);
        mPrePostGroup.setVisibility(View.VISIBLE);

        myToolbar.setVisibility(View.INVISIBLE);

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

                Toast.makeText(MainActivity.this, "REFRESHING!", Toast.LENGTH_SHORT).show();
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

    private void commentsListView() {
        String[] fakeComments = {"wow", "much fake", "ian eats sausage", "lul", "kiam eats tofu", "moegoe360 eats hummus",
                "anji eats rice", "anji eats rice", "anji eats rice", "anji eats rice"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                fakeComments
        );

        ListView list = (ListView) findViewById(R.id.comments);
        list.setAdapter(adapter);
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
                .title("V")
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

    //THIS IS A USELESS ON CLICK LISTENER FOR THE FULL IMAGE TO FIX onMapClick listener issue
    public void doesNothing(View view){}

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
                public void onClick(View v) {toLoginActivity(null);}});
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


                main.setEnabled(enabler);
                mBtmToolbar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "IM IN MARKER CLICKER LISTENRE");
                mMiniPost.setVisibility(View.VISIBLE);
                mMiniPost.bringToFront();
                main.setPanelHeight(mMiniPost.getHeight());
                Post temp = (Post) marker.getTag();
                Log.d(TAG, "MY IMAGE IS FROM: " + temp.getImageURI());
                mPostText.setText(temp.getImageTitle());
                Bitmap b = PostUtil.getImageFromURL(temp.getImageURI());
                mPostImage.setImageBitmap(b);
                mFullImage.setImageBitmap(b);

            } catch (Exception e) {e.printStackTrace();}
            return false;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            main.setEnabled(!enabler);
            mMiniPost.setVisibility(View.INVISIBLE);
            mBtmToolbar.setVisibility(View.VISIBLE);
            main.setPanelHeight(mBtmToolbar.getHeight());
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



