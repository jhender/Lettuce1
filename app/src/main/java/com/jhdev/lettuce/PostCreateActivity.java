package com.jhdev.lettuce;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

public class PostCreateActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    /**
     * reference to
     * https://github.com/ParsePlatform/AnyWall/blob/master/AnyWall-android/Anywall/src/com/parse/anywall/MainActivity.java
     */

    Uri fileUri;
	ImageView imgPreview;
    TextView textViewLocation;
	Button btnSave;
	String stringTitle = null;
	String stringDescription = null;
	ParseFile file;
	String imageFileName;
    private static ParseGeoPoint geoPoint;
    private Location lastLocation = null;
    private Location currentLocation = null;

    // A request to connect to Location Services
    private LocationRequest locationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Stores the current instantiation of the location client in this object
    LocationClient mLocationClient;
    Location mCurrentLocation;

    private Post post;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_create);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationClient = new LocationClient(this, this, this);

        //1. start camera
        //2. return data from camera
        //3. convert data
        //4. put into the text

        // start retrieving location
        //getLocation();

        //Receive image from main activity
        fileUri = getIntent().getData();
    	Log.d("PostCreateAct", "getfileURIdata");

        imageFileName = getIntent().getStringExtra("filename");
        //Toast.makeText(PostCreateActivity.this, "Image: " + imageFileName, Toast.LENGTH_LONG).show();
        
        btnSave = (Button) findViewById(R.id.saveButton1);
        final EditText editTextTitle = (EditText) findViewById(R.id.editTextTitle);        
        final EditText editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        
		//code that uploads an image to Parse immediately.
        //if user presses back or cancels, file is still on server but not linked to a imageUpload.
    	
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down-sizing image as it throws OutOfMemory Exception for larger
        // images, especially on older devices
        options.inSampleSize = 8;
        //check out ways to reduce load size. options.inJustDecodeBounds = true;
        //http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
    	Log.d("PostCreateAct", "decodeFiledone");
        
        //Set image into the preview box
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        imgPreview.setImageBitmap(bitmap);
    	Log.d("PostCreateAct", "getImagepreview");
    	
		// Convert it to byte
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// Compress image to lower quality scale 1 - 100
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
		byte[] image = stream.toByteArray();
    	Log.d("PostCreateAct", "compress done");



		
		// Create the ParseFile
		file = new ParseFile(imageFileName, image);
		// Upload the image into Parse Cloud
		file.saveInBackground();
    	Log.d("PostCreateAct", "file saved");



		btnSave.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
		        stringTitle = editTextTitle.getText().toString();
		        stringDescription = editTextDescription.getText().toString();
				savePost();
			}				
		});		
	}
	
	private void savePost(){
        post = new Post();
//      // Create a New Class called "Photo" in Parse
//      ParseObject po = new ParseObject("Post");
//
//      // Create a column named "ImageName" and set the string
//      po.put("imageName", imageFileName);

      // Create a column named "ImageFile" and insert the image
      //x po.put("photo", file);
        post.setTitle(stringTitle);
        post.setLocation(geoPoint);
        post.setUser(ParseUser.getCurrentUser());
        post.setPhoto(file);
        post.saveInBackground();

//      po.put("title", stringTitle);
//      po.put("description", stringDescription);
//
//      //set user who created this. TODO add check that user is logged in.
//      po.put("createdBy", ParseUser.getCurrentUser());
//
//      po.put("status", "active");

      //GeoPoint. Generate and save Location


//      // Saves fake location when no geopoint is retrieved.
//      if (geoPoint == null){
//          geoPoint = new ParseGeoPoint(1.0, 1.01);
//          //po.put("location", point);
//      }
//        // Saves location. TODO Need to catch errors when location not retrieved.
//        po.put("location", geoPoint);
//
//        //set access control to READ ONLY for public
//        ParseACL acl = new ParseACL();
//        acl.setPublicReadAccess(true);
//        po.setACL(acl);

//
//      // Create the class and the columns
//      po.saveInBackground();


//        post.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                // Update the display
//            }
//        });


      // Show a simple toast message   
      Toast.makeText(PostCreateActivity.this, "Image Uploaded to Post",
              Toast.LENGTH_SHORT).show();
      
      Intent returnIntent = new Intent();
      //returnIntent.putExtra("result",result);
      setResult(RESULT_OK,returnIntent);     
      finish();
	}

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }




//    //TEST
//    void getLocation() {
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//        /*
//        This seems to work with Coarse Location in the Manifest. But throws error location timeout when fine location is selected.
//        Coarse Location is very far off. Way too inaccurate for saving locations. Probably can work for finding nearby items.
//         */
//        ParseGeoPoint.getCurrentLocationInBackground(20000, criteria, new LocationCallback() {
//            @Override
//            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
//
//                if (e == null) {
//                    Toast.makeText(PostCreateActivity.this, "location is:" + parseGeoPoint, Toast.LENGTH_LONG).show();
//                    geoPoint = parseGeoPoint;
//                    textViewLocation.setText("location found");
//                } else {
//                    Toast.makeText(PostCreateActivity.this, "location error", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                    textViewLocation.setText("location error not found");
//                }
//            }
//        });
//    }



    /**
     *  ***********************************************************************
     *  LOCATION STUFF
     *
     */

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return true;
        }
    }

    /*
 * Called by Location Services when the request to connect the
 * client finishes successfully. At this point, you can
 * request the current location or start periodic updates
 */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        mCurrentLocation = mLocationClient.getLastLocation();

        Toast.makeText(this,
                "Your location is "
                + mCurrentLocation.getLatitude()
                + ", "
                + mCurrentLocation.getLongitude(),
                Toast.LENGTH_LONG).show();

        geoPoint = new ParseGeoPoint(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /*
 * Called when the Activity becomes visible.
 */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }




}
