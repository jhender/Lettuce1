package com.jhdev.lettuce;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LettuceActivity extends Activity {
	 
	//Define stuff
	//Button uploadButton;
	static String imageFileName;
	
	// Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
 
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Lettuce";
 
    private Uri fileUri; // file URI to store image/video    
    GridView gridview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    GridViewAdapter adapter;
    private List<PhotoList> photoarraylist = null;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Parse tracking how many times your app is opened
		ParseAnalytics.trackAppOpened(getIntent());
		
		new RemoteDataTask().execute();
	} //end OnCreate
	
    /**
     * Capturing Camera Image will launch camera app request image capture
     */
    private void captureImage() {
    	//TODO upgrade to camera or filepicker
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
 
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
 
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    
    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
            	
            	uploadImage();
                //previewCapturedImage();
            	
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Image capture was cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }         
    }
 
//    /**
//     * Display image from a path to ImageView
//     */
//    private void previewCapturedImage() {
//        try {
//            // hide video preview 
//            imgPreview.setVisibility(View.VISIBLE);
// 
//            // bitmap factory
//            BitmapFactory.Options options = new BitmapFactory.Options();
// 
//            // downsizing image as it throws OutOfMemory Exception for larger
//            // images
//            options.inSampleSize = 8;
// 
//            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
//                    options);
// 
//            imgPreview.setImageBitmap(bitmap);
//            
//            // upload this file
//            uploadImage();
//            
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
 


     
    /**
     * ------------ Helper Methods ---------------------- 
     * */
 
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
 
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
 
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
 
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
 
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        imageFileName = "IMG" + timeStamp + ".jpg";
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        } else {
            return null;
        }
 
        return mediaFile;
    }
   /**
    *  ----------------END HELPER-------------------------
    **/
    
    
    public void uploadImage () {
		//code that uploads an image to Parse
//    	
//        // bitmap factory
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        // downsizing image as it throws OutOfMemory Exception for larger images
//        options.inSampleSize = 8;
//        
//        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
//         
//		// Convert it to byte
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		// Compress image to lower quality scale 1 - 100
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//		byte[] image = stream.toByteArray();
//		
//		// Create the ParseFile
//		ParseFile file = new ParseFile(imageFileName, image);
//		// Upload the image into Parse Cloud
//		file.saveInBackground();
//    	Log.d("LA", "uploadImagestart");
		  
		Intent intent = new Intent(this, PostCreateActivity.class);
		intent.setData(fileUri);
		intent.putExtra("filename", imageFileName);
		startActivityForResult(intent, 1);	          
  }	
	
	/**
	 * 
	 * Next section loads the items for the gridview
	 *
	 */
	
    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(LettuceActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            photoarraylist = new ArrayList<PhotoList>();
            try {
                // Locate the class table named "ImageUpload" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "ImageUpload");
                // Locate the column named "position" in Parse.com and order list
                // by descending order of created.
                query.orderByDescending("createdAt");
                query.setLimit(15);
                ob = query.find();
                for (ParseObject po : ob) {
                	//retrieve objectID and Title
                	String stringTitle = (String) po.get("Title");
                	String stringObjectID = (String) po.getObjectId();

                	//retrieve the image file
                    ParseFile image = (ParseFile) po.get("Photo");
                    PhotoList map = new PhotoList();
                    map.setPhoto(image.getUrl());
                    map.setObjectID(stringObjectID);
                    map.setTitle(stringTitle);
                    photoarraylist.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            // Locate the gridview in gridview_main.xml
            gridview = (GridView) findViewById(R.id.gridview);
            // Pass the results into ListViewAdapter.java
            adapter = new GridViewAdapter(LettuceActivity.this,photoarraylist);
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
	
    /**
     *  Action Bar
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.take_picture:
                captureImage();
                return true;
            case R.id.Refresh:
            	new RemoteDataTask().execute();
                //Toast.makeText(this, "Not yet available", Toast.LENGTH_SHORT).show();
            	return true;
            case R.id.Login:
            	parseLogin();
            	return true;
            case R.id.Logout:
    			parseLogout();
        	default:
                return super.onOptionsItemSelected(item);
        }
    }
        
    // User login to Parse.com back end TODO check if user is logged in
    private void parseLogin () {
    	
    	//If Anonymous user is activated in ParseApplication.java, then currentUser is usually non null.
		ParseUser currentUser = ParseUser.getCurrentUser();
    	if (currentUser == null) {    
    		
            //Toast.makeText(this, "current User null", Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(this, ParseLoginActivity.class);
			startActivity(intent);
    	} else {
    		// seems like there is no reply callback
            Toast.makeText(this, "You are logged in.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    // User logout from Parse.com back end. 
    private void parseLogout () {  
    	//TODO check if user is logged in?; do not let Automatic Anonymous Users Log Out
		ParseUser.logOut();            
		Toast.makeText(this, "You are logged out.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ParseLoginActivity.class);
		startActivity(intent);
    }
    
	
}
