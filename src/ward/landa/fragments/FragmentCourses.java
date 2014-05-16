package ward.landa.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

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
import ward.landa.activities.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
	DBManager db_mngr;
	public JSONParser jParser;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
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
		jParser = new JSONParser();
		db_mngr = new DBManager(getActivity());
		searced = new ArrayList<Course>();
		courses = new ArrayList<Course>();
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		loadFromDb = sh.getBoolean(GCMUtils.LOAD_COURSES, false);
		if (!loadFromDb) {
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
			ImageView picture;
			TextView name;
			RatingBar rb;
			Course c = (Course) getItem(position);
			if (v == null) {
				v = inflater.inflate(R.layout.course_item_grid, parent, false);
				v.setTag(R.id.list_image, v.findViewById(R.id.list_image));
				v.setTag(R.id.title, v.findViewById(R.id.title));
			}
			picture = (ImageView) v.getTag(R.id.list_image);
			name = (TextView) v.getTag(R.id.title);

			// picture.setImageResource(c.getImgID());
			bmpUtils.loadBitmap(c.getImgID(), picture);
			/*
			 * picture.setImageBitmap(Utilities.decodeSampledBitmapFromResource(
			 * res, c.getImgID(), 100, 100));
			 */
			name.setText(c.getName());
			return v;
		}

	}

	class loadDataFromBackend extends AsyncTask<String, String, String> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		boolean allOk = false;

		@Override
		protected String doInBackground(String... arg0) {

			JSONObject jsonCourses = jParser.makeHttpRequest(
					Settings.URL_COURSES, "GET", params);
			Log.d("ward", jsonCourses.toString());

			try {
				JSONArray jsonCoursesArray = jsonCourses
						.getJSONArray("courses");

				for (int i = 0; i < jsonCoursesArray.length(); i++) {
					JSONObject c = jsonCoursesArray.getJSONObject(i);
					Course tmp = new Course(i, c.getString("subject_name"),
							c.getString("day"), c.getString("time_from"),
							c.getString("time_to"), c.getString("place"),
							c.getString("tutor_id"));
					tmp.setImgID(R.drawable.ic_error);
					if (!courses.contains(tmp))
						courses.add(tmp);

					db_mngr.insertCourse(tmp);
				}

				allOk = true;
			} catch (JSONException e) {
				e.printStackTrace();

			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (allOk) {
				saveLoadedCoursesState(true);
				uAdapter = new coursesAdapter(courses, getActivity(),
						getResources(), 0);

				SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
						uAdapter);
				sb.setAbsListView(g);
				g.setAdapter(sb);
			}
			super.onPostExecute(result);
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
