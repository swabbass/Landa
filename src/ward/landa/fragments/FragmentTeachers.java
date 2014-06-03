package ward.landa.fragments;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilites.ConnectionDetector;
import utilites.DBManager;
import utilites.JSONParser;
import ward.landa.GCMUtils;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.ImageUtilities.BitmapUtils;
import ward.landa.activities.MainActivity;
import ward.landa.activities.Settings;
import ward.landa.activities.Utilities;
import ward.landa.fragments.FragmentCourses.reciever;
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
import android.speech.RecognizerResultsIntent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.internal.fb;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class FragmentTeachers extends Fragment {

	private static List<Teacher> tutors;
	List<Teacher> searched;
	callbackTeacher tCallback;
	gridAdabter gAdapter;
	private static DBManager db_mngr;
	JSONParser jParser;
	GridView gridView;
	boolean toFetchDataFromDB;
	ConnectionDetector connectionDetector;
	TeacherReciever tRsvr;
	SwingBottomInAnimationAdapter sb;
	View root;
	@Override
	public void onStop() {
		if (tRsvr != null) {

			getActivity().unregisterReceiver(tRsvr);
		}
		super.onStop();
	}

	@Override
	public void onResume() {
		tRsvr = new TeacherReciever();
		IntentFilter intentFilter = new IntentFilter(
				"com.google.android.c2dm.intent.RECEIVE");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		getActivity().registerReceiver(tRsvr, intentFilter);
		super.onResume();
	}

	public interface callbackTeacher {
		public void OnTeacherItemClick(Teacher t);
	}

	private void initlizeSearchEngine(View v) {

		EditText search = (EditText) v.findViewById(R.id.teacher_txt_search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("text", "text now changed");
				if (s.length() != 0) {
					gAdapter.setL(search(s.toString()), 1);
					gAdapter.notifyDataSetChanged();

				} else {
					gAdapter.setL(tutors, 0);
					gAdapter.notifyDataSetChanged();
				}

			}

			private List<Teacher> search(String string) {
				searched.clear();
				for (Teacher t : tutors) {
					if (t.getName().contains(string)) {
						searched.add(t);
					}
				}

				return searched;
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

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.teacher_menu, menu);
		View v = (View) menu.findItem(R.id.teacher_menu_search).getActionView();
		initlizeSearchEngine(v);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			tCallback = (callbackTeacher) activity;
			setHasOptionsMenu(true);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement callbackTeacher");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 root = inflater.inflate(R.layout.teacher_custom_grid, container,
				false);
		connectionDetector = new ConnectionDetector(getActivity());

		db_mngr = new DBManager(getActivity());
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		toFetchDataFromDB = sh.getBoolean(GCMUtils.LOAD_TEACHERS, false);
		jParser = new JSONParser();

		searched = new ArrayList<Teacher>();

		gridView = (GridView) root.findViewById(R.id.gridview);
		tutors = new ArrayList<Teacher>();

		boolean isConnected = connectionDetector.isConnectingToInternet();
		if (!toFetchDataFromDB && isConnected) {
			// fetch from internet
			new loadDataFromBackend().execute();
		} else {
			// fetch from database
			tutors = null;
			tutors = db_mngr.getCursorAllTeachers();
			gAdapter = new gridAdabter(root.getContext(), tutors,
					getResources(), 0);
			sb = new SwingBottomInAnimationAdapter(
					gAdapter);
			sb.setAbsListView(gridView);
			gridView.setAdapter(sb);
			sb.notifyDataSetChanged();
		}

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tCallback.OnTeacherItemClick(gAdapter.searched == 0 ? tutors
						.get(arg2) : searched.get(arg2));

			}
		});

		return root;
	}

	static class gridAdabter extends BaseAdapter {

		LayoutInflater inflater;
		List<Teacher> l;
		Resources res;
		BitmapUtils bmpUtils;

		int searched = 0;

		public void setL(List<Teacher> l, int search) {
			this.l = l;
			this.searched = search;
		}

		public gridAdabter(Context context, List<Teacher> l, Resources res,
				int search) {
			this.inflater = LayoutInflater.from(context);
			this.l = l;
			this.res = res;
			this.searched = search;
			this.bmpUtils = new BitmapUtils(context);
		}

		@Override
		public int getCount() {

			return l.size();
		}

		@Override
		public Object getItem(int arg0) {

			return l.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return l.get(arg0).getID();
		}

		@Override
		public View getView(int pos, View view, ViewGroup viewGroup) {
			View v = view;
			final ImageView picture;
			TextView name;

			if (v == null) {
				v = inflater.inflate(R.layout.grid_textimg_item, viewGroup,
						false);
				v.setTag(R.id.picture, v.findViewById(R.id.picture));
				v.setTag(R.id.text, v.findViewById(R.id.text));
			}
			picture = (ImageView) v.getTag(R.id.picture);
			name = (TextView) v.getTag(R.id.text);
			ImageAware image = new ImageViewAware(picture, false);
			final Teacher teacher = (Teacher) getItem(pos);

			List<Bitmap> cached = MemoryCacheUtil.findCachedBitmapsForImageUri(
					teacher.getImageUrl(), ImageLoader.getInstance()
							.getMemoryCache());
			Log.d("eee", "cached image : " + cached.size());
			if (cached.size() > 0) {
				if (teacher.isDownloadedImage()) {

					picture.setImageBitmap(cached.get(0));
				} else {
					// picture.setImageResource(R.drawable.ic_error);
				}
			} else {
				if (!teacher.isDownloadedImage()) {
					SimpleImageLoadingListener s = new SimpleImageLoadingListener() {
						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							teacher.setDownloadedImage(false);
							picture.setImageResource(R.drawable.person);
							super.onLoadingCancelled(imageUri, view);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							teacher.setDownloadedImage(false);
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
							Utilities.saveImageToSD(teacher.getImageLocalPath(), loadedImage);

							teacher.setDownloadedImage(true);
							db_mngr.updateTeacher(teacher);
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					};
					MainActivity.image_loader.displayImage(
							teacher.getImageUrl(), image, s);
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
							"file://" + teacher.getImageLocalPath(), image, s2);

				}
			}
			name.setText(teacher.toString());

			return v;
		}
	}

	class TeacherReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().toString()
					.compareTo("com.google.android.c2dm.intent.RECEIVE") == 0) {
				if (intent.getStringExtra("Type") != null) {
					if (intent.getStringExtra("Type").contains("INSTRUCTOR")) {
						String type = intent.getStringExtra("Type");
						Teacher t = GCMUtils.HandleInstructor(type, context,
								db_mngr, intent);
						tutors = null;
						tutors = db_mngr.getCursorAllTeachers();
						gAdapter = new gridAdabter(root.getContext(), tutors,
								getResources(), 0);
						sb = new SwingBottomInAnimationAdapter(
								gAdapter);
						sb.setAbsListView(gridView);
						gridView.setAdapter(sb);
						sb.notifyDataSetChanged();
						abortBroadcast();
					}
				}
			}

		}

	}

	class loadDataFromBackend extends AsyncTask<String, String, String> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		boolean allOk = false;
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
			JSONObject jsonUsers = jParser.makeHttpRequest(
					Settings.URL_teachers, "GET", params);
			if (jsonUsers == null) {
				if (cancel(true)) {
					Log.e(GCMUtils.TAG,
							"loading teachers from internet canceled");
				}
			}
			Log.d("ward", jsonUsers.toString());
			try {

				JSONArray teachers = jsonUsers.getJSONArray("users");
				for (int i = 0; i < teachers.length(); i++) {
					JSONObject c = teachers.getJSONObject(i);
					Teacher t = new Teacher(c.getString("fname"),
							c.getString("lname"), c.getString("email"),
							c.getString("id"), "T", c.getString("faculty"));
					t.setDownloadedImage(false);
					tutors.add(t);

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
				saveLoadedTeachersState(true);
				for (Teacher t : tutors) {
					db_mngr.insertTeacher(t);
				}
				pDialog.dismiss();
				gAdapter = new gridAdabter(getActivity(), tutors,
						getResources(), 0);
				SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
						gAdapter);
				sb.setAbsListView(gridView);
				gridView.setAdapter(sb);
				sb.notifyDataSetChanged(true);
			}
			super.onPostExecute(result);
		}

	}

	private void saveLoadedTeachersState(boolean flag) {
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(GCMUtils.LOAD_TEACHERS, flag);
		ed.commit();
	}

}
