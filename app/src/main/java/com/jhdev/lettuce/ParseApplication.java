package com.jhdev.lettuce;

import com.parse.Parse;
import com.parse.ParseACL;
import android.app.Application;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
	    Parse.initialize(this, "H43u08age9NuU2KuJ2O4lBnEFeCaD7IPu8Tav5WF", "RL5XW3UOfAoecHdWIgQjG27wEp5mwB0gmfhum9to");
	    
	    //Automatic user interferes with getCurrentUser and currentUser == null
		//ParseUser.enableAutomaticUser();
		
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
	}
	
	
	
	
	

}
