package com.jhdev.lettuce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

public class PostCreateActivity extends Activity {

	Uri fileUri;
	ImageView imgPreview;
	Button btnSave;
	String stringTitle = null;
	String stringDescription = null;
	ParseFile file;
	String imageFileName;
    private static ParseGeoPoint geoPoint;
	
	
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
      ParseObject imgupload = new ParseObject("ImageUpload");

      // Create a column named "ImageName" and set the string          
      imgupload.put("ImageName", imageFileName);

      // Create a column named "ImageFile" and insert the image
      imgupload.put("Photo", file);

      imgupload.put("Title", stringTitle);
      imgupload.put("Description", stringDescription);
        
      //set user who created this. TODO add check that user is logged in.
      imgupload.put("createdBy", ParseUser.getCurrentUser());
      
      //GeoPoint. Generate and save Location

      // Saves location. TODO Need to catch errors when location not retrieved.
      imgupload.put("geoPoint", geoPoint);
      // Saves fake location when no geopoint is retrieved.
      if (geoPoint == null){
          ParseGeoPoint point = new ParseGeoPoint(1.0, 1.01);
          imgupload.put("geoPoint", point);
      }
      
      // Create the class and the columns
      imgupload.saveInBackground();

      // Show a simple toast message   
      Toast.makeText(PostCreateActivity.this, "Image Uploaded",
              Toast.LENGTH_SHORT).show();
      
      Intent returnIntent = new Intent();
      //returnIntent.putExtra("result",result);
      setResult(RESULT_OK,returnIntent);     
      finish();
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
