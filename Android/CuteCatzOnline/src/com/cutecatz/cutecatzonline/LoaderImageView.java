package com.cutecatz.cutecatzonline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Free for anyone to use, just say thanks and share :-)
 * @author Blundell
 * @modified Mitko
 */
public class LoaderImageView extends LinearLayout{

	private static final int COMPLETE = 0;
	private static final int FAILED = 1;

	private static Context mContext;
	private static Drawable mDrawable;
	private static ImageView mImage;
	
	/**
	 * This is used when creating the view in XML
	 * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
	 * Replacing the url with your desired image
	 * Once you have instantiated the XML view you can call
	 * setImageDrawable(url) to change the image
	 * @param context
	 * @param attrSet
	 */
	public LoaderImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);
		final String url = attrSet.getAttributeValue(null, "image");
		if(url != null){
			instantiate(context, url);
		} else {
			instantiate(context, null);
		}
	}
	
	/**
	 * This is used when creating the view programatically
	 * Once you have instantiated the view you can call
	 * setImageDrawable(url) to change the image
	 * @param context the Activity context
	 * @param imageUrl the Image URL you wish to load
	 */
	public LoaderImageView(final Context context, final String imageUrl) {
		super(context);
		instantiate(context, imageUrl);		
	}

	/**
	 *  First time loading of the LoaderImageView
	 *  Sets up the LayoutParams of the view, you can change these to
	 *  get the required effects you want
	 */

	private void instantiate(final Context context, final String imageUrl) {
		mContext = context;
		mImage = new ImageView(mContext);
		mImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mImage);		
		if(imageUrl != null){
			setImageDrawable(imageUrl);
		}
		
		// Garbage collector
		// Deletes old ../Download/.cachecats folder
		File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String path = root.getAbsolutePath() + "/.cachecats";
		deleteCacheDirectory(new File(path));		
	}

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * @param imageUrl the url of the image you wish to load
	 */
	public void setImageDrawable(final String imageUrl) {
		mDrawable = null;
		if (CuteOnlineCatz.tv != null) CuteOnlineCatz.tv.setText(R.string.loading);
		new Thread(){
			public void run() {
				try {
					mDrawable = getDrawableFromUrl(imageUrl);
					imageLoadedHandler.sendEmptyMessage(COMPLETE);
				} catch (MalformedURLException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				} catch (IOException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				}
			};
		}.start();
	}
	
	/**
	 * Callback that is received once the image has been downloaded
	 * Also switches the content of the TextView
	 * From "have a break, have a kitty cat" to "loading new cat"
	 */
	private final Handler imageLoadedHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case COMPLETE:
				mImage.setImageDrawable(mDrawable);
				mImage.setVisibility(View.VISIBLE);
				CuteOnlineCatz.tv.setText(R.string.catText);
				break;
			case FAILED:
			default:
				// Could change image here to a 'failed' image
				// otherwise will just keep on spinning
				mImage.setImageResource(R.drawable.touchme);
				mImage.setVisibility(View.VISIBLE);
				CuteOnlineCatz.tv.setText(R.string.catText);
				break;
			}
			return true;
		}		
	});

	/**
	 * Pass in an image url to get a drawable object
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), "name");
	}

	/**
	 * Saves image to the default Download directory.
	 * @param share serves to show whether we want to share or only save the image
	 * @return string for the Toast, containing "Fail" or "Saved"
	 */
	public static String saveImageToSD(boolean share) {
		String toast ="";
		
		// If Image exists
		if (mDrawable != null) {
			// Enables drawing cache 
			mImage.setDrawingCacheEnabled(true);
			Bitmap bm = mImage.getDrawingCache();
			
			// Path to file.
			File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			root.mkdirs();
			String path = root.getAbsolutePath() + "/";

			// Creates ../Download/.cachecats if we want to share
			if (share) {
				path += ".cachecats";
				(new File(path)).mkdirs();
				path += "/.cache";				
			}
			
			// Completes the filename
			path += "cat" + Long.toString(System.currentTimeMillis()) + ".jpeg";
			File file = new File(path);
			
			// File save magic
			try {
				FileOutputStream out = new FileOutputStream(file);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
				toast = "Saved.";
				
				// Share magic
				if (share) {
					Intent picMessageIntent = new Intent(Intent.ACTION_SEND);
					picMessageIntent.setType("image/jpeg");
					picMessageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
					mContext.startActivity(Intent.createChooser(picMessageIntent, "Send cat using:"));					
				}
				out.close();
				
			} catch (Exception e) {
				Log.d("ioerror", e.toString());
				e.printStackTrace();
				toast = "Failed!";
			}
			
			// Must be disabled at the end, otherwise, next saved image will be the same. 
			mImage.setDrawingCacheEnabled(false);
		}
		else {	
			toast = "Failed.";	
		}
		return toast;
	}
	
	/**
	 * Deletes the directory and all its content.
	 * @param path the directory to delete.
	 * @return whether deleting was successful.
	 */
	public static boolean deleteCacheDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      if (files == null) {
	          return true;
	      }
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteCacheDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }	
}
