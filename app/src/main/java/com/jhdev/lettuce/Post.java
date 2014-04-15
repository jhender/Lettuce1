package com.jhdev.lettuce;

/**
 * 
 * This is the main item type that is created by the user. 
 * Contains the photo, title, description.
 * Linked by comments
 * Must have an owner
 * 
 */

public class Post {
	
	private String photo;
	//should be auto //private String objectID;
	private String title;
	private String location;
	private String coordinates;
	private String rating;
	
	public String getPhoto() {
		return photo;
	}
	
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
}
