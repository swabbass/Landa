package ward.landa.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import utilites.ConnectionDetector;
import utilites.DBManager;
import utilites.JSONParser;
import ward.landa.Course;
import ward.landa.GCMUtils;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.ImageUtilities.BitmapUtils;
import ward.landa.R.drawable;
import ward.landa.R.id;
import ward.landa.R.layout;
import ward.landa.R.menu;
import ward.landa.activities.MainActivity;
import ward.landa.activities.Settings;
import ward.landa.activities.Utilities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FragmentCourses extends Fragment {

	OnCourseSelected callback;

	public interface OnCourseSelected {
		public void onCourseClick(int position);

		public void onCourseClick(Course c);
	}

	ListView l;
	GridView g;
	List<Course> courses;
	View root;
	coursesAdapter uAdapter;
	List<Course> searced;
	boolean loadFromDb;
	static DBManager db_mngr;
	public JSONParser jParser;
	reciever corseRsvr;
	ConnectionDetector connectionDetector;

	@Override
	public void onStop() {
		if (corseRsvr != null) {

			getActivity().unregisterReceiver(corseRsvr);
		}
		super.onStop();
	}

	@Override
	public void onResume() {
		corseRsvr = new reciever();
		IntentFilter intentFilter = new IntentFilter(
				"com.google.android.c2dm.intent.RECEIVE");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 2);
		getActivity().registerReceiver(corseRsvr, intentFilter);
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.course_menu, menu);
		View v = (View) menu.findItem(R.id.course_menu_search).getActionView();

		EditText search = (EditText) v.findViewById(R.id.course_txt_search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.d("text", "text now changed");
				if (s.length() != 0) {
					uAdapter.setCourses(search(s.toString()), 1);
					uAdapter.notifyDataSetChanged();

				} else {
					uAdapter.setCourses(courses, 0);
					uAdapter.notifyDataSetChanged();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.d("text", "before text now changed");

			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d("text", "after text now changed");

			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onAttach(Activity activity) {

		try {
			callback = (OnCourseSelected) activity;
			setHasOptionsMenu(true);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCourseSelected");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.courses_frag_grid, container, false);
		g = (GridView) root.findViewById(R.id.gridviewcourses);
		connectionDetector = new ConnectionDetector(getActivity());
		jParser = new JSONParser();
		db_mngr = new DBManager(getActivity());
		searced = new ArrayList<Course>();
		courses = new ArrayList<Course>();
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		loadFromDb = sh.getBoolean(GCMUtils.LOAD_COURSES, false);
		boolean isConnected = connectionDetector.isConnectingToInternet();
		if (!loadFromDb && isConnected) {
			new loadDataFromBackend().execute();
		} else {
			courses = null;
			courses = db_mngr.getCursorAllWithCourses();
			uAdapter = new coursesAdapter(courses, getActivity(),
					getResources(), 0);

			SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
					uAdapter);
			sb.setAbsListView(g);
			g.setAdapter(sb);

		}

		g.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				callback.onCourseClick(uAdapter.searched == 0 ? courses
						.get(arg2) : searced.get(arg2));
				g.setItemChecked(arg2, true);
			}

		});

		return root;
	}

	public List<Course> search(String st) {
		searced.clear();
		for (Course c : courses) {
			if (c.getName().contains(st)) {
				searced.add(c);
			}
		}
		return searced;
	}

	static class coursesAdapter extends BaseAdapter {

		List<Course> courses;
		LayoutInflater inflater = null;
		Context cxt = null;
		Resources res;
		BitmapUtils bmpUtils;
		int searched;

		public void setCourses(List<Course> courses, int search) {
			this.courses = courses;
			this.searched = search;
		}

		public coursesAdapter(List<Course> courses, Context cxt, Resources res,
				int search) {
			this.courses = courses;
			this.cxt = cxt;
			this.inflater = (LayoutInflater) cxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.res = res;
			this.searched = search;
			this.bmpUtils = new BitmapUtils(cxt);

		}

		@Override
		public int getCount() {

			return courses.size();
		}

		@Override
		public Object getItem(int position) {

			return courses.get(position);
		}

		@Override
		public long getItemId(int position) {

			return courses.get(position).getCourseID();
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			View v = view;
			final ImageView picture;
			TextView name;
			ImageView alarm;
			final Course c = (Course) getItem(position);
			if (v == null) {
				v = inflater.inflate(R.layout.course_item_grid, parent, false);
				v.setTag(R.id.list_image, v.findViewById(R.id.list_image));
				v.setTag(R.id.title, v.findViewById(R.id.title));
				v.setTag(R.id.alarm_course_image_view,
						v.findViewById(R.id.alarm_course_image_view));
			}
			picture = (ImageView) v.getTag(R.id.list_image);
			ImageAware image = new ImageViewAware(picture, false);
			name = (TextView) v.getTag(R.id.title);
			alarm = (ImageView) v.getTag(R.id.alarm_course_image_view);
			if (c.getNotify() == 0) {
				alarm.setVisibility(ImageView.INVISIBLE);
			} else if (c.getNotify() == 1) {
				alarm.setVisibility(ImageView.VISIBLE);
			}
			String imageUri = c.getImageUrl();
			List<Bitmap> cached = MemoryCacheUtil
					.findCachedBitmapsForImageUri(c.getImageUrl(), ImageLoader
							.getInstance().getMemoryCache());
			Log.d("eee", "cached image : " + cached.size());
			if (cached.size() > 0) {
				if (c.isDownloadedImage()) {

					picture.setImageBitmap(cached.get(0));
				} else {
					// picture.setImageResource(R.drawable.ic_error);
				}
			} else {
				if (!c.isDownloadedImage()) {
					SimpleImageLoadingListener s = new SimpleImageLoadingListener() {
						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							c.setDownloadedImage(false);
							picture.setImageResource(R.drawable.ic_launcher);
							super.onLoadingCancelled(imageUri, view);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							c.setDownloadedImage(false);
							// picture.setImageResource(R.drawable.ic_error);
							Log.d("test", failReason.toString());
							super.onLoadingFailed(imageUri, view, failReason);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							ImageView img = (ImageView) view;
							img.setImageBitmap(loadedImage);
							// picture.setImageBitmap(loadedImage);
							Utilities.saveImageToSD(c.getImagePath(),
									loadedImage);

							c.setDownloadedImage(true);
							// TODO save isdownlaoded flag
							db_mngr.UpdateCourse(c, c.getNotify());
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					};
					MainActivity.image_loader.displayImage(c.getImageUrl(),
							image, s);
					// MainActivity.image_loader.loadImage(teacher.getImageUrl(),

				} else {
					SimpleImageLoadingListener s2 = new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							Log.e("sd", "loading from sd is started");
							super.onLoadingStarted(imageUri, view);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							Log.e("sd", "loading from sd is failed cause : "
									+ failReason.getCause().getMessage());
							super.onLoadingFailed(imageUri, view, failReason);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							ImageView img = (ImageView) view;
							img.setImageBitmap(loadedImage);
							// picture.setImageBitmap(loadedImage);
							Log.d("sd", "loading from sd is successfull ");
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					};
					// MainActivity.image_loader.loadImage("file://"+teacher.getImageLocalPath(),
					MainActivity.image_loader.displayImage(
							"file://" + c.getImagePath(), image, s2);

				}
			}

			name.setText(c.getName());
			return v;
		}

	}

	class loadDataFromBackend extends AsyncTask<String, String, String> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		boolean allOk = false;
		List<Course> toSave = new ArrayList<Course>();

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			JSONObject jsonCourses = jParser.makeHttpRequest(
					Settings.URL_COURSES, "GET", params);
			if (jsonCourses == null) {
				if (cancel(true)) {
					Log.e(GCMUtils.TAG,
							"loading courses from internet canceled");
				}
			}
			Log.d("ward", jsonCourses.toString());

			try {
				JSONArray jsonCoursesArray = jsonCourses
						.getJSONArray("courses");

				for (int i = 0; i < jsonCoursesArray.length(); i++) {
					JSONObject c = jsonCoursesArray.getJSONObject(i);
					Course tmp = new Course(i, DBManager.removeQoutes(c
							.getString("subject_name")), c.getString("day"),
							c.getString("time_from"), c.getString("time_to"),
							c.getString("place"), c.getString("tutor_id"));
					tmp.setImgID(R.drawable.ic_error);
					tmp.setSubject_id(c.getString("subject_id"));
					tmp.setImageUrl(tmp.getSubject_id_string());
					tmp.setImagePath(tmp.getSubject_id_string());
					tmp.setDownloadedImage(false);
					if (!courses.contains(tmp))
						courses.add(tmp);
					toSave.add(tmp);
				}

				allOk = true;
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e(GCMUtils.TAG, e.toString());
				if (!connectionDetector.isConnectingToInternet()) {
					Log.e(GCMUtils.TAG, "faild no internet ");
					cancel(true);
				}

			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (allOk) {
				saveLoadedCoursesState(true);
				pDialog.dismiss();
				uAdapter = new coursesAdapter(courses, getActivity(),
						getResources(), 0);

				SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
						uAdapter);
				sb.setAbsListView(g);
				g.setAdapter(sb);
				for (Course c : toSave) {
					c.setCourse_db_id(db_mngr.insertCourse(c));
				}
				toSave = null;
			}
			super.onPostExecute(result);
		}

	}

	class reciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().toString()
					.compareTo("com.google.android.c2dm.intent.RECEIVE") == 0) {
				if (intent.getStringExtra("Type") != null) {
					if (intent.getStringExtra("Type").contains("WORKSHOP")) {
						GCMUtils.HandleWorkshop(intent.getStringExtra("Type"),
								context, db_mngr, intent);
						courses = null;
						courses = db_mngr.getCursorAllWithCourses();
						uAdapter = new coursesAdapter(courses, getActivity(),
								getResources(), 0);

						SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
								uAdapter);
						sb.setAbsListView(g);
						g.setAdapter(sb);
						abortBroadcast();
					}
				}
			}

		}

	}

	public void saveLoadedCoursesState(boolean b) {
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(GCMUtils.LOAD_COURSES, b);
		ed.commit();

	}

}
