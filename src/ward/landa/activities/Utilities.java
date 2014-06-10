package ward.landa.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

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
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Utilities {

	public static final String NEW_UPDATE = "new_Update";
	private static final int notfyId = 12;

	public static void saveDownloadOnceStatus(boolean b, final String key,
			Context cxt) {
		SharedPreferences sh = cxt.getSharedPreferences(GCMUtils.DATA,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(key, b);
		ed.commit();

	}

	/**
	 * removes all kind of tags like <13455> will be removed
	 * 
	 * @param inp
	 *            html text input
	 * @return
	 */
	public static String html2Text(String inp) {
		boolean intag = false;
		String outp = "";

		for (int i = 0; i < inp.length(); ++i) {

			char c = inp.charAt(i);
			if (!intag && inp.charAt(i) == '<') {
				intag = true;
				continue;
			}
			if (intag && inp.charAt(i) != '>') {

				continue;
			}
			if (intag && inp.charAt(i) == '>') {
				intag = false;
				continue;
			}
			if (!intag) {
				outp = outp + inp.charAt(i);
			}
		}
		return outp;
	}

	public static String FetchTableTagHtml(String html) {
		boolean intag = false;
		String outp = null;
		//List<Table> tables = getTableTags(html);
		/*for(Table t : tables)
		{
			String remove=html.substring(t.firstTrStartIndex, t.firstTrEndIndex);
			html=html.replace(remove, "");
			tables=getTableTags(html);
		}*/
		int firstTrTag = html.indexOf("<tr");
		int lastTrTag = html.indexOf("</tr>") + "</tr>".length();
		String firstTr = null;
		if (firstTrTag != -1 && lastTrTag != -1)
			firstTr = html.substring(firstTrTag, lastTrTag);
		if (firstTr != null)
			outp = html.replace(firstTr, "");

		return outp;
	}

	private static List<Table> getTableTags(String html) {
		List<Table> tabels = new ArrayList<Table>();
		for (int i = 0; i < html.length(); ++i) {
			char c = html.charAt(i);
			if (c == '<') {
				if (Table.TABLE_OPENER.length() + i - 1 < html.length()) {
					int x = -1;
					if (html.charAt(i + 1) == 't' && html.charAt(i + 2) == 'a'
							&& html.charAt(i + 3) == 'b'
							&& html.charAt(i + 4) == 'l'){
						if ((x = html.indexOf(Table.TABLE_OPENER, i)) != -1) {
							Table t = new Table();
							t.startIndex = x;
							t.endIndex = html.indexOf(Table.TABLE_CLOSER, i);
							t.firstTrEndIndex = html
									.indexOf(Table.TR_OPENER, i);
							t.firstTrStartIndex = html.indexOf(Table.TR_CLOSER,
									i);
							tabels.add(t);
						} else {
							continue;
						}
				} else {
					continue;
				}
				}
				else{
					break;
				}
			} else {
				continue;
			}
		}
		return tabels;
	}

	static class Table {
		public static final String TABLE_OPENER = "<table";
		public static final String TABLE_CLOSER = "</table>";
		public static final String TR_OPENER = "<tr";
		public static final String TR_CLOSER = "</tr>";
		public int startIndex;
		public int endIndex;
		public int firstTrStartIndex;
		public int firstTrEndIndex;

		public Table() {
			// TODO Auto-generated constructor stub
		}
	}

	public static InputMethodManager getInputMethodManager(Activity activity) {
		return (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * 
	 * @param outBuffer
	 *            String buffer of the text to replace % and escaped characters
	 * @return Formated String with replaced tags and encodded
	 */
	public static String replacer(StringBuffer outBuffer) {

		String data = outBuffer.toString();
		try {
			StringBuffer tempBuffer = new StringBuffer();
			int incrementor = 0;
			int dataLength = data.length();
			while (incrementor < dataLength) {
				char charecterAt = data.charAt(incrementor);
				if (charecterAt == '%') {
					tempBuffer.append("<percentage>");
				} else if (charecterAt == '+') {
					tempBuffer.append("<plus>");
				} else {
					tempBuffer.append(charecterAt);
				}
				incrementor++;
			}
			data = tempBuffer.toString();
			data = URLDecoder.decode(data, "UTF-8");
			data = data.replaceAll("<percentage>", "%");
			data = data.replaceAll("<plus>", "+");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 
	 * @param hebrewDay
	 *            day in hebrew language
	 * @return Calendar.SUNDAY- Calendar.THURSDAY
	 */
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

	/**
	 * 
	 * @param context
	 *            Context
	 * @param message
	 *            Message to send to BoroadCastReciever
	 * @param time
	 *            Time day hour minute seconds milliseconds
	 * @param title
	 *            Title of the message
	 * @param Action
	 *            Action type for intent
	 */
	public static void displayMessage(Context context, String message,
			String time, String title, String Action) {
		Intent intent = new Intent(Action);
		intent.putExtra(Settings.EXTRA_MESSAGE, message);
		intent.putExtra(Settings.EXTRA_Date, time);
		intent.putExtra(Settings.EXTRA_TITLE, title);
		context.sendBroadcast(intent);
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @param update
	 *            Update object to send to broadcasrreciever
	 * @param Action
	 *            ActionType for intent
	 */
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
	public static String saveImageToSD(String localPath, Bitmap bmp) {
		FileOutputStream fos = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);

		// check external state
		String dirPath = Environment.getExternalStorageDirectory()
				+ File.separator + Settings.picsPathDir;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				return null;
			}
		}

		File file = new File(localPath);
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
		Log.d("eee", "saved Image : " + localPath);
		return localPath;

	}

	/**
	 * @param extras
	 *            from intent
	 * 
	 * @return new Update with the message delivered from back-end
	 */
	public static Update generateUpdateFromExtras(Bundle extras, Context cxt) {
		String msg = extras.getString(Settings.EXTRA_MESSAGE);
		String subject = extras.getString("title");
		Update u = null;
		String[] data = msg.split("\n");
		String post = data[0];
		if (!post.equals("Post updated")) {

			try {
				JSONObject jObj = new JSONObject(msg);
				String id = jObj.getString("ID");
				String title = subject;
				String content = jObj.getString("post_content");
				String date = jObj.getString("post_date");
				String url = jObj.getString("guid");
				u = new Update(id, title, date, content, false);
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

	/**
	 * after getting Update for an existed update call back
	 * 
	 * @author wabbass
	 * 
	 */
	public static interface PostListener {
		public void onPostUpdateDownloaded(Update u);

	}

	/**
	 * AsyncTask to Fetch Update from the internet by post id given from the
	 * push notification
	 * 
	 * @author wabbass
	 * 
	 */
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
								update.getString("content"), false);
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

	/**
	 * Shows notification on the notification bar
	 * 
	 * @param context
	 *            Context
	 * @param title
	 *            Notification title
	 * @param text
	 *            Notification text content (info)
	 */
	public static void showNotification(Context context, String title,
			String text) {

		// Notification notification=new Notification(R.drawable.success,
		// tickerText, when)
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true).setContentTitle(title)
				.setContentText(text).setAutoCancel(true)
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
