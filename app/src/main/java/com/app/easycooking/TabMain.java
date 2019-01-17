package com.app.easycooking;
 
 import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


public class TabMain extends TabActivity {
 	/** Called when the activity is first created. */
 	
 	public void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
 		setContentView(R.layout.main);
 		setTabs();
 	}
 	private void setTabs()
 	{
		addTab("All Resep", R.drawable.tab_recipe, AllRecipeActivity.class);
 		addTab("Dapur", R.drawable.tab_home, RefrigeratorActivity.class);
 		/*addTab("History", R.drawable.tab_history, HistoryActivity.class); 		*/
 		/*addTab("Bookmarks", R.drawable.tab_bookmarks, BookmarksActivity.class);*/
 		/*addTab("Search", R.drawable.tab_search, SearchActivity.class);*/

 		addTab("Logout", R.drawable.tab_logout, LogoutActivity.class);
 	}
 	
 	private void addTab(String labelId, int drawableId, Class<?> c)
 	{
 		TabHost tabHost = getTabHost();
 		Intent intent = new Intent(this, c);
 		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
 		
 		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
 		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
 		title.setText(labelId);
 		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
 		icon.setImageResource(drawableId);
 		
 		spec.setIndicator(tabIndicator);
 		spec.setContent(intent);
 		tabHost.addTab(spec);
 	}
 }