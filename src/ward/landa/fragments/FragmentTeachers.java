package ward.landa.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import ward.landa.ImageUtilities.UILTools;
import ward.landa.activities.MainActivity;
import ward.landa.activities.Settings;
import ward.landa.activities.Utilities;
import ward.landa.fragments.FragmentCourses.reciever;
import ward.landa.fragments.FragmentUpdates.downloadRecentUpdates;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.internal.fb;
import com.google.android.gms.wallet.LineItem.Role;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

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
	EditText search;
	Spinner spinner;
	private static ImageLoader imageLoader;

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

		search = (EditText) v.findViewById(R.id.teacher_txt_search);
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
		
		if (!getArguments().getBoolean("rtl"))
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		initlizeSearchEngine(v);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {


		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
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
		root = inflater.inflate(R.layout.teacher_custom_grid, container, false);
		connectionDetector = new ConnectionDetector(getActivity());
		imageLoader = UILTools.initlizeImageLoad(UILTools.initilizeImageLoader(
				UILTools.initlizeImageDisplay(R.drawable.person,
						R.drawable.person, R.drawable.person), getActivity()),
				getActivity());
		
		
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
			loadFromDataBase();

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

	private void loadFromDataBase() {
		tutors = null;
		tutors = db_mngr.getCursorAllTeachers();
		gAdapter = new gridAdabter(root.getContext(), tutors, getResources(), 0);
		sb = new SwingBottomInAnimationAdapter(gAdapter);
		sb.setAbsListView(gridView);
		gridView.setAdapter(sb);
		sb.notifyDataSetChanged();

	}

	static class gridAdabter extends BaseAdapter {

		LayoutInflater inflater;
		List<Teacher> l;
		Resources res;
		BitmapUtils bmpUtils;
		Context cxt;
		int searched = 0;

		public void setL(List<Teacher> l, int search) {
			this.l = l;
			this.searched = search;
		}

		public gridAdabter(Context context, List<Teacher> l, Resources res,
				int search) {
			this.cxt = context;
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
			Target target = new Target() {
				@Override
				public void onBitmapFailed(Drawable arg0) {
					// TODO Auto-generated method stub
					teacher.setDownloadedImage(false);
				}

				@Override
				public void onBitmapLoaded(final Bitmap arg0, LoadedFrom arg1) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							Utilities.saveImageToSD(
									teacher.getImageLocalPath(), arg0);

						}
					}).start();
					teacher.setDownloadedImage(true);
					db_mngr.UpdateTeacherImageDownloaded(teacher, true);
				}

				@Override
				public void onPrepareLoad(Drawable arg0) {
					// TODO Auto-generated method stub

				}

			};
			if (!teacher.isDownloadedImage()) {
				Picasso.with(cxt).load(teacher.getImageUrl())
						.error(R.drawable.ic_launcher).into(picture);
				Picasso.with(cxt).load(teacher.getImageUrl()).into(target);
			} else {
				Picasso.with(cxt).load(new File(teacher.getImageLocalPath()))
						.error(R.drawable.ic_launcher).into(picture);
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
						sb = new SwingBottomInAnimationAdapter(gAdapter);
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
			pDialog.setCanceledOnTouchOutside(false);
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
							c.getString("id"), utilites.Role.getRole(
									Integer.valueOf(c.getString("position")))
									.name(), c.getString("faculty"));
					t.setDownloadedImage(false);
					tutors.add(t);

				}
				if (connectionDetector.isConnectingToInternet())
					allOk = true;
			} catch (JSONException e) {
				Log.e(GCMUtils.TAG, e.toString());
				if (!connectionDetector.isConnectingToInternet()) {
					Log.e(GCMUtils.TAG, "faild no internet ");

				}
				db_mngr.clearDb();
				return "";
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
			if (allOk) {
				saveLoadedTeachersState(true);
				for (Teacher t : tutors) {
					db_mngr.insertTeacher(t);
				}

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						gAdapter = new gridAdabter(getActivity(), tutors,
								getResources(), 0);
						SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
								gAdapter);
						sb.setAbsListView(gridView);
						gridView.setAdapter(sb);
						gAdapter.notifyDataSetChanged();
						sb.notifyDataSetChanged(true);

					}
				});

			} else {
				showDialogNoconnection(!toFetchDataFromDB);
			}
			super.onPostExecute(result);
		}

	}

	private void showDialogNoconnection(boolean isfirst) {
		new AlertDialog.Builder(getActivity())
				.setCancelable(false)
				.setTitle("Info")
				.setMessage(
						isfirst ? getResources().getString(
								R.string.noConntectionMsgFirsttime)
								: getResources().getString(
										R.string.noConntection))

				.setNeutralButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (connectionDetector.isConnectingToInternet()) {
									new loadDataFromBackend().execute();

								} else if (!toFetchDataFromDB) {
									getActivity().finish();
								} else if (toFetchDataFromDB) {
									loadFromDataBase();
								}

							}
						}).show();
	}

	private void saveLoadedTeachersState(boolean flag) {
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(GCMUtils.LOAD_TEACHERS, flag);
		ed.commit();
	}

	private List<Teacher> filterCords() {
		List<Teacher> cords = new ArrayList<Teacher>();
		for (Teacher t : tutors) {

			if (!t.getPosition().equals(utilites.Role.TUTOR.name())) {
				cords.add(t);
			}
		}
		return cords;
	}

	private List<Teacher> filterTutors() {
		List<Teacher> tutor = new ArrayList<Teacher>();
		for (Teacher t : tutors) {

			if (t.getPosition().equals(utilites.Role.TUTOR.name())) {
				tutor.add(t);
			}
		}
		return tutor;
	}


	private boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			gAdapter.setL(tutors, 0);
			gAdapter.notifyDataSetChanged();
			break;
		case 1:
			gAdapter.setL(filterCords(), 1);
			gAdapter.notifyDataSetChanged();
			break;
		case 2:
			gAdapter.setL(filterTutors(), 1);
			gAdapter.notifyDataSetChanged();
			break;
		}
		return true;
	}
}
