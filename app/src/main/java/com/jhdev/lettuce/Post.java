package com.jhdev.lettuce;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by jianhui.ho on 4/25/2014.
 */
@ParseClassName("Post")
public class Post extends ParseObject {

    private String photo;
    private String title;
    private String location;
    private String coordinates;
    private String rating;

    public Post () {
        // A default constructor is required
    }

    public ParseFile getPhoto() {

        return getParseFile("photo");
    }

    public void setPhoto(ParseFile file) {
        //this.photo = photo;
        put("photo", file);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public ParseUser getUser() {
        return getParseUser("createdBy");
    }

    public void setUser(ParseUser value){
        put("createdBy", value);
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public static ParseQuery<Post> getQuery() {
        return ParseQuery.getQuery(Post.class);
    }

}


//public class ImageUpload {
//
//    private String photo;
//    //should be auto //private String objectID;
//    private String title;
//    private String location;
//    private String coordinates;
//    private String rating;
//
//    public String getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(String photo) {
//        this.photo = photo;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title){
//        this.title = title;
//    }
//
//
//}
