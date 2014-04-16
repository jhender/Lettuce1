package com.jhdev.lettuce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GridViewFragment extends Fragment {

    //Define stuff
    //static String imageFileName;

    // Activity request codes
    //private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    //public static final int MEDIA_TYPE_IMAGE = 1;

    // directory name to store captured images and videos
    //private static final String IMAGE_DIRECTORY_NAME = "Lettuce";

    private Uri fileUri; // file URI to store image/video
    private static GridView gridview;
    static List<ParseObject> ob;
    private static ProgressDialog mProgressDialog;
    private static GridViewAdapter adapter;
    private static List<PhotoList> photoarraylist = null;

    //fragment
    private static View fragmentView;
    private WeakReference<MyAsyncTask> asyncTaskWeakReference;

    /** Fragment initiate View */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentActivity faActivity = super.getActivity();

        fragmentView = inflater.inflate(R.layout.activity_main, container, false);

        if (photoarraylist != null) {
            adapter = new GridViewAdapter(faActivity,photoarraylist);
            // Binds the Adapter to the ListView
            gridview = (GridView) fragmentView.findViewById(R.id.gridview);
            gridview.setAdapter(adapter);
        }

        return fragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        //For adding menu action bar options
        setHasOptionsMenu(true);

        mProgressDialog = new ProgressDialog(getActivity());

        startNewAsyncTask();
    }


    private void startNewAsyncTask() {
        MyAsyncTask asyncTask = new MyAsyncTask(this);
        this.asyncTaskWeakReference = new WeakReference<MyAsyncTask>(asyncTask);
        asyncTask.execute();
    }

//    private boolean isAsyncTaskPendingOrRunning() {
//        return this.asyncTaskWeakReference != null &&
//              this.asyncTaskWeakReference.get() != null &&
//                !this.asyncTaskWeakReference.get().getStatus().equals(AsyncTask.Status.FINISHED);
//    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<GridViewFragment> fragmentWeakReference;

        private MyAsyncTask (GridViewFragment fragment) {
            this.fragmentWeakReference = new WeakReference<GridViewFragment>(fragment);
        }

        //@Override
        protected void onPreExecute() {

            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progress dialog
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
                    String stringObjectID = po.getObjectId();

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
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if (this.fragmentWeakReference.get() != null ){
                Activity myActivity = (Activity) fragmentView.getContext();
                // Locate the gridview in gridview_main.xml
                gridview = (GridView) fragmentView.findViewById(R.id.gridview);
                // Pass the results into ListViewAdapter.java
                adapter = new GridViewAdapter(myActivity,photoarraylist);
                // Binds the Adapter to the ListView
                gridview.setAdapter(adapter);
                // Close the progress dialog
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.gridviewfragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.Refresh:
                //Toast.makeText(getActivity(), "Warning: async activate", Toast.LENGTH_SHORT).show();
                startNewAsyncTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /***
     * above section optimised for fragments
     */







//    /**
//     * Capturing Camera Image will launch camera app request image capture
//     */
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);

        //FragmentManager manager = getFragmentManager();
        outState.putParcelable("file_uri", fileUri);

        //manager.putFragment(outState, "file_uri");

        // save file url in bundle as it will be null on screen orientation
        // changes
        //outState.putParcelable("file_uri", fileUri);
    }

    //@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


}
