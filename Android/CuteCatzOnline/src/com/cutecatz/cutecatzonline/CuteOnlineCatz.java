package com.cutecatz.cutecatzonline;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CuteOnlineCatz extends Activity {
	public static TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cute_catz);
		tv = (TextView) findViewById(R.id.catTextView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cute_catz, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_exit:
	            super.finish();
	            return true;
	        case R.id.action_save:
	            //saves image to Download folder
	        	String s = LoaderImageView.saveImageToSD(false);
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();	
	            return true;
	        case R.id.action_share:
	        	LoaderImageView.saveImageToSD(true);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void loadNewImage(View v) {
		((LoaderImageView) v).setImageDrawable("http://thecatapi.com/api/images/get?format=src&type=jpg");
	}
}
