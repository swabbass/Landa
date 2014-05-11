package Utilites;

import ward.landa.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	private static final String TO_NOTIFY_UPDATE = "toNotifyUpdate";
	private static final String TO_CORSE_NOTIFY = "toCorseNotify";
	private static final String LOCAL_KEY = "local";
	public static final String SETTINGS = "SHAREDPREFRENCES";
	public static final int COURSES = 0;
	public static final int UPDATES = 2;
	public static final int TEACHERS = 1;
	public static final String HEBREW = "he";
	public static final String ENGLISH = "en";
	public static final String ARABIC = "ar";

	private static String localLang;
	private static boolean toNotifyUpdates;
	private static boolean toNotifyCourse;

	public static void initlizeSettings(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SETTINGS,
				Activity.MODE_PRIVATE);
		localLang = settings.getString(LOCAL_KEY, HEBREW);
		toNotifyCourse = settings.getBoolean(TO_CORSE_NOTIFY, true);
		toNotifyUpdates = settings.getBoolean(TO_NOTIFY_UPDATE, true);
	}

	public static  void saveSettings(Context c, String local, boolean courseNotify,
			boolean updateNotify) {
		SharedPreferences settings = c.getSharedPreferences(SETTINGS,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(TO_CORSE_NOTIFY, courseNotify);
		editor.putBoolean(TO_NOTIFY_UPDATE, updateNotify);
		editor.putString(LOCAL_KEY, local);
		editor.commit();
	}

	public static String getLocalLang() {
		return localLang;
	}

	public static void setLocalLang(String localLang) {
		Settings.localLang = localLang;
	}

	public static boolean isToNotifyUpdates() {
		return toNotifyUpdates;
	}

	public static void setToNotifyUpdates(boolean toNotifyUpdates) {
		Settings.toNotifyUpdates = toNotifyUpdates;
	}

	public static boolean isToNotifyCourse() {
		return toNotifyCourse;
	}

	public static void setToNotifyCourse(boolean toNotifyCourse) {
		Settings.toNotifyCourse = toNotifyCourse;
	}

	public static int langId(String lang) {
		if (lang != null && !lang.isEmpty()) {
			switch (lang) {
			case HEBREW:
				return R.id.radioHE;
			case ARABIC:
				return R.id.radioAr;
			case ENGLISH:
				return R.id.radioEn;
			}
		}
		return -1;
	}
}
