package com.jhdev.lettuce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LocationCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap.CancelableCallback;
//import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.Circle;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;

public class PostCreateActivity extends Activity implements LocationListener,
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener {

    /**
     * reference to
     * https://github.com/ParsePlatform/AnyWall/blob/master/AnyWall-android/Anywall/src/com/parse/anywall/MainActivity.java
     *
     */


    Uri fileUri;
	ImageView imgPreview;
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

    // Stores the current instantiation of the location client in this object
    private LocationClient locationClient;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_create);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //1. start camera
        //2. return data from camera
        //3. convert data
        //4. put into the text

        //start retrieving location
        getLocation();

        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        if (myLoc == null) {
            Toast.makeText(PostCreateActivity.this,
                    "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
            return;
        }
        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);


        //Receive image from main activity
        fileUri = getIntent().getData();
    	Log.d("PostCreateAct", "getfileURIdata");

        imageFileName = getIntent().getStringExtra("filename");
        //Toast.makeText(PostCreateActivity.this, "Image: " + imageFileName, Toast.LENGTH_LONG).show();
        
        btnSave = (Button) findViewById(R.id.saveButton1);
        final EditText editTextTitle = (EditText) findViewById(R.id.editTextTitle);        
        final EditText editTextDescription = (EditText) findViewById(R.id.editTextDescription);                
        
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

      // Create a New Class called "Photo" in Parse
      ParseObject po = new ParseObject("Post");

      // Create a column named "ImageName" and set the string          
      po.put("imageName", imageFileName);

      // Create a column named "ImageFile" and insert the image
      po.put("photo", file);

      po.put("title", stringTitle);
      po.put("description", stringDescription);
        
      //set user who created this. TODO add check that user is logged in.
      po.put("createdBy", ParseUser.getCurrentUser());

      po.put("status", "active");

      //GeoPoint. Generate and save Location

      // Saves location. TODO Need to catch errors when location not retrieved.
      po.put("location", geoPoint);
      // Saves fake location when no geopoint is retrieved.
      if (geoPoint == null){
          ParseGeoPoint point = new ParseGeoPoint(1.0, 1.01);
          po.put("geoPoint", point);
      }

        //set access control to READ ONLY for public
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        po.setACL(acl);


      // Create the class and the columns
      po.saveInBackground();


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

    /*
 * Report location updates to the UI.
 */
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!hasSetUpInitialLocation) {
            // Zoom to the current location.
            updateZoom(myLatLng);
            hasSetUpInitialLocation = true;
        }
        // Update map radius indicator
        updateCircle(myLatLng);
        doMapQuery();
        doListQuery();
    }


    //TEST TODO
    void getLocation() {
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);

        /*
        This seems to work with Coarse Location in the Manifest. But throws error location timeout when fine location is selected.
        Coarse Location is very far off. Way too inaccurate for saving locations. Probably can work for finding nearby items.
         */
        ParseGeoPoint.getCurrentLocationInBackground(10000, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {

                if (e == null) {
                    Toast.makeText(PostCreateActivity.this, "location is:" + parseGeoPoint, Toast.LENGTH_LONG).show();
                    geoPoint = parseGeoPoint;
                } else {
                    Toast.makeText(PostCreateActivity.this, "location error", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

}
