package com.jhdev.lettuce;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.app.ActionBar.Tab;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = {"Browse", "Blank 2", " Blank 1"};

    // Camera file
    static String imageFileName;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Lettuce";
    // file URI to store image/video
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // Top Rated fragment activity
                    return new GridViewFragment();
                case 1:
                    // Games fragment activity
                    return new BlankFragment2();
                case 2:
                    // Movies fragment activity
                    return new BlankFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }
    }


    //    @Override
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
                //TODO check for userlogged in
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(this, "Warning: You're not logged in. Saving will crash if you don't login first.", Toast.LENGTH_SHORT).show();
                }
                captureImage();
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
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image

                Intent intent = new Intent(this, PostCreateActivity.class);
                intent.setData(fileUri);
                intent.putExtra("filename", imageFileName);
                startActivityForResult(intent, 1);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(this,
                        "Image capture was cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

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


}