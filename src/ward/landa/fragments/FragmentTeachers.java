package ward.landa.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilites.DBManager;
import utilites.JSONParser;
import ward.landa.GCMUtils;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.ImageUtilities.BitmapUtils;
import ward.landa.activities.MainActivity;
import ward.landa.activities.Settings;
import ward.landa.activities.Utilities;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FragmentTeachers extends Fragment {

	private static List<Teacher> tutors;
	List<Teacher> searched;
	callbackTeacher tCallback;
	gridAdabter gAdapter;
	private static DBManager db_mngr;
	JSONParser jParser;
	GridView gridView;
	boolean toFetchDataFromDB;

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
		View root = inflater.inflate(R.layout.teacher_custom_grid, container,
				false);
		db_mngr = new DBManager(getActivity());
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		toFetchDataFromDB = sh.getBoolean(GCMUtils.LOAD_TEACHERS, false);
		jParser = new JSONParser();

		searched = new ArrayList<Teacher>();

		gridView = (GridView) root.findViewById(R.id.gridview);
		tutors = new ArrayList<Teacher>();
		gAdapter = new gridAdabter(root.getContext(), tutors, getResources(), 0);
		if (!toFetchDataFromDB) {
			// fetch from internet
			new loadDataFromBackend().execute();
		} else {
			// fetch from database
			tutors = null;
			tutors = db_mngr.getCursorAllTeachers();
			gAdapter = new gridAdabter(root.getContext(), tutors,
					getResources(), 0);
			SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
					gAdapter);
			sb.setAbsListView(gridView);
			gridView.setAdapter(sb);
			gAdapter.notifyDataSetChanged();
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

			final Teacher teacher = (Teacher) getItem(pos);

			List<Bitmap> cached = MemoryCacheUtil.findCachedBitmapsForImageUri(
					teacher.getImageUrl(), ImageLoader.getInstance()
							.getMemoryCache());
			Log.d("eee", "cached image : " + cached.size());
			if (cached.size() > 0) {
				if (teacher.isDownloadedImage()) {
					picture.setImageBitmap(cached.get(0));
				} else {
					picture.setImageResource(R.drawable.ic_error);
				}
			} else {
				if (!teacher.isDownloadedImage()) {
					SimpleImageLoadingListener s=new SimpleImageLoadingListener() {
						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							teacher.setDownloadedImage(false);
							picture.setImageResource(R.drawable.ic_error);
							super.onLoadingCancelled(imageUri, view);
						}

						@Override
						public void onLoadingFailed(String imageUri,
								View view, FailReason failReason) {
							teacher.setDownloadedImage(false);
							picture.setImageResource(R.drawable.ic_error);
							Log.d("test", failReason.toString());
							super.onLoadingFailed(imageUri, view,
									failReason);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							picture.setImageBitmap(loadedImage);
							Utilities.saveImageToSD(teacher,
									loadedImage);

							teacher.setDownloadedImage(true);
							db_mngr.updateTeacher(teacher);
							super.onLoadingComplete(imageUri, view,
									loadedImage);
						}
					};
					MainActivity.image_loader.displayImage(teacher.getImageUrl(), picture, s);
					//MainActivity.image_loader.loadImage(teacher.getImageUrl(),
							
				} else {
					SimpleImageLoadingListener s2=new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							Log.e("sd", "loading from sd is started");
							super.onLoadingStarted(imageUri, view);
						}
						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							Log.e("sd", "loading from sd is failed cause : "+failReason.getCause().getMessage());
							super.onLoadingFailed(imageUri, view, failReason);
						}
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							picture.setImageBitmap(loadedImage);
							Log.d("sd", "loading from sd is successfull ");
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					};
					//MainActivity.image_loader.loadImage("file://"+teacher.getImageLocalPath(), 
					MainActivity.image_loader.displayImage(
							"file://"+teacher.getImageLocalPath(), picture,s2);

				}
			}
			name.setText(teacher.toString());

			return v;
		}
	}

	class loadDataFromBackend extends AsyncTask<String, String, String> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		boolean allOk = false;

		@Override
		protected String doInBackground(String... arg0) {
			JSONObject jsonUsers = jParser.makeHttpRequest(
					Settings.URL_teachers, "GET", params);
			/*
			 * JSONObject jsonCourses = jParser.makeHttpRequest(
			 * Settings.URL_COURSES, "GET", params);
			 */
			Log.d("ward", jsonUsers.toString());
			// Log.d("ward", jsonCourses.toString());

			try {

				JSONArray teachers = jsonUsers.getJSONArray("users");
				// JSONArray courses = jsonCourses.getJSONArray("courses");

				for (int i = 0; i < teachers.length(); i++) {
					JSONObject c = teachers.getJSONObject(i);
					Teacher t = new Teacher(c.getString("fname"),
							c.getString("lname"), c.getString("email"),
							c.getString("id"), "T", c.getString("faculty"));
					tutors.add(t);
					db_mngr.insertTeacher(t);
				}
				/*
				 * for (int i = 0; i < courses.length(); i++) { JSONObject c =
				 * courses.getJSONObject(i); Course tmp = new Course(i,
				 * c.getString("subject_name"), c.getString("day"),
				 * c.getString("time_from"), c.getString("time_to"),
				 * c.getString("place"), c.getString("tutor_id"));
				 * MainActivity.courses.add(tmp); db_mngr.insertCourse(tmp); }
				 */
				allOk = true;
			} catch (JSONException e) {
				e.printStackTrace();

			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (allOk) {
				saveLoadedTeachersState(true);
				SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
						gAdapter);
				sb.setAbsListView(gridView);
				gridView.setAdapter(sb);
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
