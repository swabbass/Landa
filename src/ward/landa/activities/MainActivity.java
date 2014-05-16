package ward.landa.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.wallet.LineItem.Role;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import utilites.DBManager;
import utilites.JSONParser;
import ward.landa.Course;
import ward.landa.GCMUtils;
import ward.landa.LoginFragment;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.Update;
import ward.landa.fragments.CourseFragment;
import ward.landa.fragments.FragmentCourses;
import ward.landa.fragments.FragmentTeachers;
import ward.landa.fragments.FragmentUpdates;
import ward.landa.fragments.teacherFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		FragmentCourses.OnCourseSelected, FragmentTeachers.callbackTeacher,
		FragmentUpdates.updateCallback, OnBackStackChangedListener {
	GoogleCloudMessaging gcm;
	String localLang;
	boolean isUpdateNotify;
	boolean isCourseNotify;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	PagerTitleStrip strip;
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle drawertoggle;
	ListView draweList;
	String[] drawertitles;
	int viewpagerid = -1;
	Fragment[] pages;
	boolean isReg;
	String regKey;
	utilites.DBManager db_mngr;
	JSONParser jParser;
	private  registerGcm task;
	public static ImageLoaderConfiguration config;
	public static ImageLoader image_loader;
	public static PauseOnScrollListener listener;

	private ImageLoaderConfiguration initilizeImageLoader(
			DisplayImageOptions options) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).memoryCacheExtraOptions(120, 120)
				.denyCacheImageMultipleSizesInMemory().threadPriority(Thread.MAX_PRIORITY)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).threadPoolSize(5)
				.defaultDisplayImageOptions(options)
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.writeDebugLogs().build();
		return config;

	}

	private DisplayImageOptions initlizeImageDisplay() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				// resource or drawable
				.showImageForEmptyUri(R.drawable.ic_empty)
				// resource or drawable
				.showImageOnFail(R.drawable.ic_error)
				// resource or drawable
				.resetViewBeforeLoading(false)
				// default
				.delayBeforeLoading(200).cacheInMemory(true).cacheOnDisc(true)
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.handler(new Handler()) // default
				.build();
		return options;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initlizeImageLoad() {
		config = initilizeImageLoader(initlizeImageDisplay());
		image_loader = ImageLoader.getInstance();
		image_loader.init(config);
		boolean pauseOnScroll = false; // or true
		boolean pauseOnFling = true; // or false
		listener = new PauseOnScrollListener(image_loader, pauseOnScroll,
				pauseOnFling);

	}

	@Override
	protected void onStart() {
		Log.e("Fragment", "Main Activity started");
		initlizeImageLoad();
		initlizeDataBase();
		loadSettings();
		loadRegsetrationData();
		setLocalLang();
		setTitle(R.string.app_name);
		initlizeFragments();
		initlizeDrawerNavigation();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		initlizePager();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initlizeGCM();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
	//	image_loader.clearDiscCache();
	//	image_loader.clearMemoryCache();
		Log.e("Fragment", "Main Activity stopped");
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.e("Fragment", "Main Activity paused");
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	private void initlizeGCM() {
		gcm = GoogleCloudMessaging.getInstance(this);
		if (!isReg) {
			task = new registerGcm();
			task.execute();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 122) {
			mViewPager.setCurrentItem(resultCode);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

	}

	private void loadSettings() {
		Settings.initlizeSettings(getApplicationContext());
		this.localLang = Settings.getLocalLang();
		this.isCourseNotify = Settings.isToNotifyCourse();
		this.isUpdateNotify = Settings.isToNotifyUpdates();

	}

	private void setLocalLang() {
		Locale locale = new Locale(localLang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	private void initlizeDataBase() {
		db_mngr = new DBManager(getApplicationContext());

	}

	private void initlizeFragments() {

		pages = new Fragment[3];
		pages[0] = new FragmentCourses();
		pages[1] = new FragmentTeachers();
		pages[2] = new FragmentUpdates();
	}

	private void initlizeDrawerNavigation() {
		drawertitles = new String[] {
				getResources().getString(R.string.updates),
				getResources().getString(R.string.teachers),
				getResources().getString(R.string.courses) };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		draweList = (ListView) findViewById(R.id.left_drawer);
		draweAdapter dAdapter = new draweAdapter(getApplicationContext(),
				drawertitles);
		draweList.setAdapter(dAdapter);
		draweList.setOnItemClickListener(new drawerOnItemClick(this));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		drawertoggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {

				super.onDrawerClosed(drawerView);

				getActionBar().setTitle(
						getResources().getString(R.string.app_name));
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {

				super.onDrawerOpened(drawerView);
				getActionBar().setTitle("Navigate to ");
				invalidateOptionsMenu();
			}

		};

		drawerLayout.setDrawerListener(drawertoggle);
	}

	private void initlizePager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), getResources(), pages);

		// Setup the ViewPager with the sections adapter.
		strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager.setAdapter(mSectionsPagerAdapter);

		getSupportFragmentManager().addOnBackStackChangedListener(this);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mViewPager.setCurrentItem(2);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		jParser = new JSONParser();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		drawertoggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		drawertoggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO find the items and in menu and set it !drawerOpen

		boolean draweOpen = drawerLayout.isDrawerOpen(draweList);

		return super.onPrepareOptionsMenu(menu);
	}

	static class drawerOnItemClick implements ListView.OnItemClickListener {

		MainActivity activityRef;

		public drawerOnItemClick(MainActivity activity) {
			// TODO Auto-generated constructor stub
			activityRef = activity;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			CourseFragment c = (CourseFragment) activityRef
					.getSupportFragmentManager().findFragmentByTag(
							"CourseFragment");
			teacherFragment t = (teacherFragment) activityRef
					.getSupportFragmentManager().findFragmentByTag(
							"TeacherFragment");

			if ((c != null && c.isVisible()) || (t != null && t.isVisible())) {
				Log.i("LANDA", "CourseFragment");

			}
			activityRef.getSupportFragmentManager().popBackStack();
			if (arg2 < activityRef.drawertitles.length)
				if (arg2 == activityRef.drawertitles.length - 1) {
					activityRef.openLoginFragment();
				} else {
					activityRef.mViewPager.setCurrentItem(arg2);
				}
			activityRef.drawerLayout.closeDrawer(activityRef.draweList);
		}

	}

	public void openLoginFragment() {
		LoginFragment login = new LoginFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_container, login, "LoginFragment");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawertoggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	static class SectionsPagerAdapter extends FragmentPagerAdapter {

		Resources res;
		Fragment[] f;

		public SectionsPagerAdapter(FragmentManager fm, Resources res,
				Fragment[] f) {
			super(fm);
			this.f = f;
			this.res = res;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Log.i("FragmentPosition", "Fposition = " + position);

			switch (position) {
			case 0:
				return f[0];
			case 1:

				return f[1];
			case 2:
				return f[2];

			}

			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();

			switch (position) {
			case 0:
				return res.getString(R.string.courses);
			case 1:
				return res.getString(R.string.teachers);
			case 2:
				return res.getString(R.string.updates);

			}
			return null;
		}

		@Override
		public int getItemPosition(Object object) {

			return POSITION_NONE;
		}

	}

	@Override
	public void onCourseClick(int position) {

	}

	@Override
	public void onCourseClick(Course c) {
		Bundle extras = new Bundle();
		extras.putString("name", c.getName());
		extras.putInt("ImageID", c.getImgID());
		extras.putInt("courseID", c.getCourseID());
		extras.putInt("position", 0);
		Intent i = new Intent(this, CourseDeatilsActivity.class);
		i.putExtras(extras);
		startActivityForResult(i, 122);

	}

	@Override
	public void OnTeacherItemClick(Teacher t) {

		Bundle extras = new Bundle();
		extras.putInt("imgid", t.getImgId());
		extras.putString("name", t.getName());
		extras.putString("email", t.getEmail());
		extras.putString("faculty", t.getFaculty());
		extras.putString("position", t.getPosition());
		Intent i = new Intent(this, TutorDetails.class);
		i.putExtras(extras);
		startActivityFromFragment(pages[1], i, 122);

	}

	static class draweAdapter extends BaseAdapter {

		LayoutInflater inflater = null;
		String[] drawertitles;

		public draweAdapter(Context cxt, String[] drawertitles) {
			this.drawertitles = drawertitles;
			inflater = (LayoutInflater) cxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {

			return drawertitles.length;
		}

		@Override
		public Object getItem(int arg0) {

			return drawertitles[arg0];
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			View v = arg1;
			if (v == null) {
				v = inflater.inflate(R.layout.draweritem, arg2, false);
				v.setTag(R.id.drawer, v.findViewById(R.id.drawer));
			}
			TextView tv = (TextView) v.getTag(R.id.drawer);
			tv.setText(drawertitles[position]);

			return v;

		}

	}

	@Override
	public void onUpdateClick(Update u) {

		Bundle extras = new Bundle();
		extras.putString("subject", u.getSubject());
		extras.putString("dateTime", u.getDateTime());
		extras.putString("content", u.getText());
		Intent i = new Intent(this, UpdateDetailActivity.class);
		i.putExtras(extras);
		startActivityFromFragment(pages[2], i, 122);

	}

	@Override
	public void onBackStackChanged() {
		// TODO Auto-generated method stub
		boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
		getActionBar().setDisplayHomeAsUpEnabled(canback);
	}

	/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
	private void loadRegsetrationData() {
		SharedPreferences sh = getSharedPreferences(GCMUtils.DATA,
				Activity.MODE_PRIVATE);
		isReg = sh.getBoolean(GCMUtils.REGSITER, false);
		regKey = sh.getString(GCMUtils.REG_KEY, null);

	}

	private void saveRegstrationData(boolean isReg, String regKey) {
		SharedPreferences sh = getSharedPreferences(GCMUtils.DATA,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(GCMUtils.REGSITER, isReg);
		ed.putString(GCMUtils.REG_KEY, regKey);
		ed.commit();
	}

	public class registerGcm extends AsyncTask<String, String, String> {
		String st = null;

		@Override
		protected String doInBackground(String... arg0) {
			try {
				st = gcm.register(GCMUtils.SENDER_ID);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(GCMUtils.TAG, e.toString());
			}
			if (!st.isEmpty() || st != null) {
				isReg = true;
				regKey = new String(st);
				Log.d(GCMUtils.TAG, "regKey is : " + regKey);
				GCMUtils.sendRegistrationIdToBackend(regKey);
			}

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			if (isReg) {
				saveRegstrationData(true, st);
			}
			super.onPostExecute(result);
		}

	}

	/*
	 * class loadDataFromBackend extends AsyncTask<String, String, String> {
	 * List<NameValuePair> params = new ArrayList<NameValuePair>(); boolean
	 * allOk = false;
	 * 
	 * @Override protected String doInBackground(String... arg0) { JSONObject
	 * jsonUsers = jParser.makeHttpRequest( Settings.URL_teachers, "GET",
	 * params); JSONObject jsonCourses = jParser.makeHttpRequest(
	 * Settings.URL_COURSES, "GET", params); Log.d("ward",
	 * jsonUsers.toString()); Log.d("ward", jsonCourses.toString());
	 * 
	 * try {
	 * 
	 * JSONArray teachers = jsonUsers.getJSONArray("users"); JSONArray courses =
	 * jsonCourses.getJSONArray("courses");
	 * 
	 * for (int i = 0; i < teachers.length(); i++) { JSONObject c =
	 * teachers.getJSONObject(i); Teacher t = new Teacher(c.getString("fname"),
	 * c.getString("lname"), c.getString("email"), c.getString("id"), "T",
	 * c.getString("faculty")); MainActivity.teachers.add(t);
	 * db_mngr.insertTeacher(t); } for (int i = 0; i < courses.length(); i++) {
	 * JSONObject c = courses.getJSONObject(i); Course tmp = new Course(i,
	 * c.getString("subject_name"), c.getString("day"),
	 * c.getString("time_from"), c.getString("time_to"), c.getString("place"),
	 * c.getString("tutor_id")); MainActivity.courses.add(tmp);
	 * db_mngr.insertCourse(tmp); }
	 * 
	 * allOk = true; } catch (JSONException e) { e.printStackTrace();
	 * 
	 * } return ""; }
	 * 
	 * @Override protected void onPostExecute(String result) { if (allOk)
	 * saveLoadedDataFlag(true); super.onPostExecute(result); }
	 * 
	 * }
	 */

}
