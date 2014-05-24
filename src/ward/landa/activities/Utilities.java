package ward.landa.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import utilites.ConnectionDetector;
import utilites.JSONParser;
import ward.landa.GCMUtils;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.Update;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Utilities {

	public static final String NEW_UPDATE = "new_Update";
	private static final int notfyId = 12;

	public static InputMethodManager getInputMethodManager(Activity activity) {
		return (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public static int dayWeekNumber(String hebrewDay) {
		
		switch (hebrewDay.replaceAll("\\s", "")) {
		case "ראשון":
			return Calendar.SUNDAY;
		case "שני":
			return Calendar.MONDAY;
		case "שלישי":
			return Calendar.TUESDAY;
		case "רבעי":
			return Calendar.WEDNESDAY;
		case "חמישי":
			return Calendar.THURSDAY;

		}
		return -1;
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
			String time, String title, String Action) {
		Intent intent = new Intent(Action);
		intent.putExtra(Settings.EXTRA_MESSAGE, message);
		intent.putExtra(Settings.EXTRA_Date, time);
		intent.putExtra(Settings.EXTRA_TITLE, title);
		context.sendBroadcast(intent);
	}

	public static void displayMessageUpdate(Context context, Update update,
			String Action) {
		Intent intent = new Intent(Action);
		intent.putExtra(NEW_UPDATE, update);
		context.sendBroadcast(intent);
	}

	/**
	 * @param imageName
	 *            : image name is the id with suffix .jpg
	 * 
	 * @param bmp
	 *            : bitmap from the cache to save
	 * 
	 * @return the path to the pic
	 */
	public static String saveImageToSD(Teacher t, Bitmap bmp) {
		FileOutputStream fos = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		// check external state
		String dirPath = Environment.getExternalStorageDirectory()
				+ File.separator + Settings.picsPathDir;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
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

	/**
	 * @param extras
	 *            from intent
	 * 
	 * @return new Update with the message delivered from back-end
	 */
	public static Update generateUpdateFromExtras(Bundle extras, Context cxt) {
		String msg = extras.getString(Settings.EXTRA_MESSAGE);
		Update u = null;
		String[] data = msg.split("\n");
		String post = data[0];
		if (!post.equals("Post updated")) {

			try {
				JSONObject jObj = new JSONObject(msg);
				String id = jObj.getString("ID");
				String title = jObj.getString("post_name");
				String content = jObj.getString("post_content");
				String date = jObj.getString("post_date");
				String url = jObj.getString("guid");
				u = new Update(id, title, date, content);
				u.setUrl(url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			// Update FromBackGroudn:
			// http://wabbass.byethost9.com/wordpress/?p=34
			String[] postInfo = data[1].split(":");// title : url
			String url = postInfo[1] + ":" + postInfo[2];

			String idTemp = postInfo[2].split(Pattern.quote("?"))[1];// p=34
			url += "&json=1";
			String id = idTemp.split("=")[1];// 34
			u = new Update(id, url);

		}
		return u;
	}

	public static interface PostListener {
		public void onPostUpdateDownloaded(Update u);
	}

	public static class fetchUpdateFromBackEndTask extends
			AsyncTask<String, String, Update> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		boolean downloadOk = false;
		Context cxt;
		ConnectionDetector connectionDetector;
		Update u;
		PostListener listner;
		String url = "http://wabbass.byethost9.com/wordpress/";

		public fetchUpdateFromBackEndTask(Context cxt, PostListener listner) {
			this.cxt = cxt;
			this.listner = listner;
			this.u = null;
			connectionDetector = new ConnectionDetector(cxt);
		}

		@Override
		protected Update doInBackground(String... params) {
			JSONParser jParser = new JSONParser();
			if (params[0] != null) {
				this.params.add(new BasicNameValuePair("p", params[0]));
				this.params.add(new BasicNameValuePair("json", "1"));
				JSONObject jObject = jParser.makeHttpRequest(url, "GET",
						this.params);
				if (jObject == null) {
					if (cancel(true)) {
						Log.e(GCMUtils.TAG,
								"loading Updates from internet canceled");
					}
				} else {
					Log.d("ward", jObject.toString());
					try {
						JSONObject update = jObject.getJSONObject("post");
						Log.d("ward", jObject.toString());

						u = new Update(update.getString("id"),
								update.getString("title"),
								update.getString("date"),
								update.getString("content"));
						u.setUrl(update.getString("url"));

						downloadOk = true;
					} catch (JSONException e) {

						e.printStackTrace();
						Log.e(GCMUtils.TAG, e.toString());
						if (!connectionDetector.isConnectingToInternet()) {
							Log.e(GCMUtils.TAG, "faild no internet ");
							cancel(true);
						}

					}
				}

			}
			return u;
		}

		@Override
		protected void onPostExecute(Update result) {

			listner.onPostUpdateDownloaded(result);
			super.onPostExecute(result);
		}

	}


	public static void showNotification(Context context, String title, String text) {
		
		// Notification notification=new Notification(R.drawable.success,
		// tickerText, when)
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title).setContentText(text).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
		// PendingMsgs.add(msg);
		Intent i = new Intent(context, MainActivity.class);
		// inorder to return to home if back pressed
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(i);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		nBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notfyId, nBuilder.build());
	}




}
