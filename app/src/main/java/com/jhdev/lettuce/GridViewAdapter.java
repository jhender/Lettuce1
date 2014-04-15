package com.jhdev.lettuce;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

   // Declare Variables
   Context context;
   LayoutInflater inflater;
   ImageLoader imageLoader;
   private List<PhotoList> photoarraylist = null;
   private ArrayList<PhotoList> arraylist;

   public GridViewAdapter(Context context, List<PhotoList> photoarraylist) {
       this.context = context;
       this.photoarraylist = photoarraylist;
       inflater = LayoutInflater.from(context);
       this.arraylist = new ArrayList<PhotoList>();
       this.arraylist.addAll(photoarraylist);
       imageLoader = new ImageLoader(context);
   }

   public class ViewHolder {
       ImageView photo;
       TextView title;
   }

   @Override
   public int getCount() {
       return photoarraylist.size();
   }

   @Override
   public Object getItem(int position) {
       return photoarraylist.get(position);
   }

   @Override
   public long getItemId(int position) {
       return position;
   }

   public View getView(final int position, View view, ViewGroup parent) {
       final ViewHolder holder;
       if (view == null) {
           holder = new ViewHolder();
           view = inflater.inflate(R.layout.gridview_item, null);
           // Locate the ImageView in gridview_item.xml
           holder.photo = (ImageView) view.findViewById(R.id.photo);
           holder.title = (TextView) view.findViewById(R.id.textView1);
           view.setTag(holder);
       } else { 
           holder = (ViewHolder) view.getTag();
       }
       // Load image into GridView
       imageLoader.DisplayImage(photoarraylist.get(position).getPhoto(),
               holder.photo);
       // Load title text into GridView   
       holder.title.setText(photoarraylist.get(position).getTitle());
       
       // Capture GridView item click
       view.setOnClickListener(new OnClickListener() {

           @Override
           public void onClick(View arg0) {
               // Send single item click data to SingleItemView Class
               Intent intent = new Intent(context, SingleItemView.class);
               // Pass all data photo
               intent.putExtra("photo", photoarraylist.get(position)
                       .getPhoto());
               intent.putExtra("objectID", photoarraylist.get(position).getObjectID());
               context.startActivity(intent);
           }
       });
       return view;
   }
}
