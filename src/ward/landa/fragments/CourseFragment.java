package ward.landa.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ward.landa.Course;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.R.drawable;
import ward.landa.R.id;
import ward.landa.R.layout;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class CourseFragment extends Fragment {

	List<Teacher> teachers;
	ListView l;
	int courseID;
	int imgId;
	float rating;
	String courseName, courseDesription;
	TextView courseDesriptionLable;
	TextView courseNameLable;
	ImageView courseImg;
	ImageView feedbackImg;

	@Override
	public void onStart() {

		// list already viewed
		Log.e("Fragment", "fragment course is started");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.e("Fragment", "fragment course is paused");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e("Fragment", "fragment course is stopped");
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e("Fragment", "fragment course is destroyed");
		super.onDestroy();
	}

	@Override
	public void onDetach() {

		// TODO Auto-generated method stub
		Log.e("Fragment", "fragment course is deattached");
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		ActionBar ab = getActivity().getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		super.onCreateOptionsMenu(menu, inflater);
	}


	private void initlizeTeachers() {
		teachers = new ArrayList<Teacher>();
		Teacher t1 = new Teacher(0000, R.drawable.ward, "Ward Abbass",
				"mail@ward.com", "0555", "חברתי", "CS");
		Teacher t2 = new Teacher(111, R.drawable.mohammed,
				"Mohammed Abd Elraziq", "mail@mohammed.com", "015", "",
				"ה.חומרים");
		Teacher t3 = new Teacher(111, R.drawable.hamed, "Hammed Abbass",
				"mail@hamed.com", "0555", "", "ת.נ");
		Teacher t4 = new Teacher(333, R.drawable.stlzbale, "חסן ח'לאילה",
				"mail@stlzbale.com", "0555", "", "חשמל");

		teachers.add(t1);
		teachers.add(t2);
		teachers.add(t3);
		teachers.add(t4);

		Course c = new Course(7, courseName, "WednesDay", "17:30", "19:30",
				"Ulman 603", imgId, (float) (rating + 0.5));
		HashMap<String, Course> stamTest = new HashMap<String, Course>();
		stamTest.put(courseName, c);
		t1.setCourses(stamTest);
		t2.setCourses(stamTest);
		t3.setCourses(stamTest);
		t4.setCourses(stamTest);
	}

	private void fetchArguments() {
		Bundle ex = getArguments();
		if (ex != null) {
			this.courseName = ex.getString("name");
			this.imgId = ex.getInt("ImageID");
			this.courseID = ex.getInt("courseID");
		}

		this.rating = -1;
	}

	private void initlizeUI(View root) {
		courseNameLable = (TextView) root.findViewById(R.id.courseLable);
		courseDesriptionLable = (TextView) root
				.findViewById(R.id.courseDescription);
		courseImg = (ImageView) root.findViewById(R.id.courseAvatar);
		feedbackImg = (ImageView) root.findViewById(R.id.feedbackImg);
		l = (ListView) root.findViewById(R.id.courseTeachers);
		courseNameLable.setText(courseName);
		courseDesriptionLable
				.setText(courseDesription == null ? "No Description"
						: courseDesription);
		courseImg.setImageResource(imgId);
		addListnerOnFeedClick();
	}

	private void addListnerOnFeedClick() {
		feedbackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_course, null);
		fetchArguments();
		initlizeUI(root);
		initlizeTeachers();
		adapter ad = new adapter(getActivity(), teachers, getResources(),
				courseName);
		SwingRightInAnimationAdapter sRin=new SwingRightInAnimationAdapter(ad);
		sRin.setAbsListView(l);
		l.setAdapter(sRin);
		return root;
	}

	static class adapter extends BaseAdapter {
		LayoutInflater inflater;
		List<Teacher> teachers;
		String courseName;
		Resources res;

		public adapter(Context cxt, List<Teacher> teachers, Resources res,
				String courseName) {
			this.teachers = teachers;
			this.inflater = (LayoutInflater) cxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.res = res;
			this.courseName = courseName;
		}

		@Override
		public int getCount() {

			return teachers.size();
		}

		@Override
		public Object getItem(int arg0) {

			return teachers.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return teachers.get(arg0).getID();
		}

		@Override
		public View getView(int pos, View view, ViewGroup parent) {

			View v = view;

			if (v == null) {
				v = inflater.inflate(R.layout.course_teacher_details, parent,
						false);
				v.setTag(R.id.tutorSmallAvatar,
						v.findViewById(R.id.tutorSmallAvatar));
				v.setTag(R.id.alaramMe, v.findViewById(R.id.alaramMe));
				v.setTag(R.id.teacherFacultyLable,
						v.findViewById(R.id.teacherFacultyLable));
				v.setTag(R.id.teacherCourseName,
						v.findViewById(R.id.teacherCourseName));
				v.setTag(R.id.courseDay, v.findViewById(R.id.courseDay));
				v.setTag(R.id.courseTimeTo, v.findViewById(R.id.courseTimeTo));
				v.setTag(R.id.CourseTimeFrom,
						v.findViewById(R.id.CourseTimeFrom));
				v.setTag(R.id.CoursePlace, v.findViewById(R.id.CoursePlace));
			}
			ImageView teacherAvatar = (ImageView) v
					.getTag(R.id.tutorSmallAvatar);
			ImageView alarmMe = (ImageView) v.getTag(R.id.alaramMe);
			TextView faculty = (TextView) v.getTag(R.id.teacherFacultyLable);
			TextView name = (TextView) v.getTag(R.id.teacherCourseName);
			TextView day = (TextView) v.getTag(R.id.courseDay);
			TextView timeFrom = (TextView) v.getTag(R.id.CourseTimeFrom);
			TextView timeTo = (TextView) v.getTag(R.id.courseTimeTo);
			TextView place = (TextView) v.getTag(R.id.CoursePlace);

			Teacher t = (Teacher) getItem(pos);
			HashMap<String, String> data = t.getCourseDetailsToShow(courseName);
			teacherAvatar.setImageResource(t.getImgId());
			name.setText(t.getName());
			faculty.setText(t.getFaculty());
			day.setText(data.get("day"));
			timeFrom.setText(data.get("from"));
			timeTo.setText(data.get("to"));
			place.setText(data.get("place"));
			return v;
		}

	}

}
