package ward.landa.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ward.landa.Teacher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;


public class Utilities {

	public static InputMethodManager getInputMethodManager(Activity activity) {
		return (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/*
	 * sends msgs fort covActivity
	 */
	public static void displayMessage(Context context, String message,
			String time,String title, String Action) {
		Intent intent = new Intent(Action);
		intent.putExtra(Settings.EXTRA_MESSAGE, message);
		intent.putExtra(Settings.EXTRA_Date, time);
		intent.putExtra(Settings.EXTRA_TITLE, title);
		context.sendBroadcast(intent);
	}


	/**
	 * @param imageName : image name is the id with suffix .jpg
	 * 
	 * @param bmp : bitmap from the cache to save
	 * 
	 * @return the path to the pic
	 */
	public static String saveImageToSD(Teacher t, Bitmap bmp) {
		FileOutputStream fos = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		
		// check external state
		String dirPath = Environment.getExternalStorageDirectory() + File.separator
				+ Settings.picsPathDir ;
		File dir=new File(dirPath);
		if(!dir.exists())
		{
			if(!dir.mkdirs())
			{
				return null;
			}
		}
			
		File file = new File(t.getImageLocalPath());
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fos.write(bytes.toByteArray());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("eee", "saved Image : " + t.getImageLocalPath());
		return t.getImageLocalPath();

	}


}
