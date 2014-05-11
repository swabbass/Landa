package ward.landa.activities;

import java.util.Locale;

import ward.landa.Course;
import ward.landa.LoginFragment;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.Update;
import ward.landa.Utilities;
import ward.landa.fragments.CourseFragment;
import ward.landa.fragments.FragmentCourses;
import ward.landa.fragments.FragmentTeachers;
import ward.landa.fragments.FragmentUpdates;
import ward.landa.fragments.teacherFragment;
import Utilites.Settings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// saveCurentPage(2);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.e("Fragment", "Main Activity started");
		loadSettings();
		setLocalLang();
		setTitle(R.string.app_name);
		initlizeFragments();
		initlizeDrawerNavigation();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		initlizePager();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
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
		this.localLang=Settings.getLocalLang();
		this.isCourseNotify=Settings.isToNotifyCourse();
		this.isUpdateNotify=Settings.isToNotifyUpdates();
		
	}

	private void setLocalLang() {
		Locale locale = new Locale(localLang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
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
		// startActivityFromFragment(pages[0], i, 122);

		/*
		 * CourseFragment cf; FragmentManager fm = getSupportFragmentManager();
		 * cf = (CourseFragment) fm.findFragmentByTag("CourseFragment"); if (cf
		 * == null) {
		 * 
		 * cf = new CourseFragment();
		 * 
		 * Bundle extras = new Bundle(); extras.putString("name", c.getName());
		 * extras.putInt("ImageID", c.getImgID()); extras.putInt("courseID",
		 * c.getCourseID()); cf.setArguments(extras); FragmentTransaction
		 * transaction = getSupportFragmentManager() .beginTransaction();
		 * transaction
		 * .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		 * transaction.replace(R.id.fragment_container, cf, "CourseFragment");
		 * transaction
		 * .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		 * transaction.addToBackStack(null); transaction.commit(); } else {
		 * Log.d("hehe", "null"); }
		 */

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
		/*
		 * teacherFragment tf = new teacherFragment(); Bundle extras = new
		 * Bundle(); extras.putInt("imgid", t.getImgId());
		 * extras.putString("name", t.getName()); extras.putString("email",
		 * t.getEmail()); extras.putString("faculty", t.getFaculty());
		 * extras.putString("position", t.getPosition());
		 * tf.setArguments(extras); FragmentTransaction transaction =
		 * getSupportFragmentManager() .beginTransaction();
		 * transaction.replace(R.id.fragment_container, tf, "TeacherFragment");
		 * transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		 * transaction.addToBackStack(null); transaction.commit();
		 */

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

		/*
		 * FragmentTransaction transaction
		 * =android.support.v4.app.Fragment.getChildFragmentManager
		 * ().beginTransaction(); transaction.replace(R.id.updates_listView, new
		 * UpdateDetailFragment()); transaction.commit();
		 */

	}

	@Override
	public void onBackStackChanged() {
		// TODO Auto-generated method stub
		boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
		getActionBar().setDisplayHomeAsUpEnabled(canback);
	}

}
