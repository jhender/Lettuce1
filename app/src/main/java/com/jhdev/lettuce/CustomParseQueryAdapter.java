package com.jhdev.lettuce;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomParseQueryAdapter extends ParseQueryAdapter<Post> {

    public CustomParseQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Post>() {
            public ParseQuery<Post> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
                ParseQuery query = new ParseQuery("Post");

                return query;
            }
        });
    }

    @Override
    public View getItemView(Post post, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.fragment_post_list_item, null);
        }

        super.getItemView(post, v, parent);

        ParseImageView image = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile photoFile = post.getParseFile("photo");
        if (photoFile != null) {
            image.setParseFile(photoFile);
            image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        TextView titleTextView = (TextView) v.findViewById(R.id.textView);
        titleTextView.setText(post.getTitle());
//        TextView ratingTextView = (TextView) v.findViewById(R.id.textView);
//        ratingTextView.setText(meal.getRating());
        return v;
    }

}

/**
 * Created by Jh on 27/4/14.
 */
//public class CustomParseQueryAdapter extends ParseQueryAdapter<Post> {
//
//    public CustomParseQueryAdapter(Context context) {
//        // Use the QueryFactory to construct a PQA that will only show
//        super(context, new ParseQueryAdapter.QueryFactory<Post>() {
//            public ParseQuery<Post> create() {
//                ParseUser currentUser = ParseUser.getCurrentUser();
//
//                ParseQuery query = new ParseQuery("Post");
//                query.setLimit(20);
//                query.whereEqualTo("createdBy", currentUser);
//                query.orderByDescending("createdBy");
//                return query;
//            }
//        });
//    }
//
//    // Customize the layout by overriding getItemView
//    @Override
//    public View getItemView(Post post, View v, ViewGroup parent) {
//        if (v == null) {
//            v = View.inflate(getContext(), R.layout.fragment_post_list_item, null);
//        }
//
//        super.getItemView(post, v, parent);
//
//        // Add and download the image
//        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
//        ParseFile imageFile = post.getParseFile("photo");
//        if (imageFile != null) {
//            todoImage.setParseFile(imageFile);
//            todoImage.loadInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] data, ParseException e) {
//                    //nothing.
//                }
//            });
//        }
//
//        // Add the title view
//        TextView titleTextView = (TextView) v.findViewById(R.id.textView);
//        titleTextView.setText(post.getTitle());
//
////        // Add a reminder of how long this item has been outstanding
////        TextView timestampView = (TextView) v.findViewById(R.id.textView);
////        timestampView.setText(object.getCreatedAt().toString());
//        return v;
//    }
//
//}