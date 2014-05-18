package ward.landa.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utilites.DBManager;
import ward.landa.Course;
import ward.landa.R;
import ward.landa.Teacher;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

public class CourseFragment extends Fragment {

	List<Teacher> teachers;
	HashMap< String, List<String>> timesForEachTeacher;
	ListView l;
	ExpandableListView exList;
	ExpandableListAdapter exAdapter;
	int courseID;
	int imgId;
	float rating;
	static String courseName;
	String courseDesription;
	TextView courseDesriptionLable;
	TextView courseNameLable;
	ImageView courseImg;
	ImageView feedbackImg;
	DBManager db_mngr;
	

	@Override
	public void onStart() {

		// list already viewed
		Log.e("Fragment", "fragment course is started");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.e("Fragment", "fragment course is paused");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e("Fragment", "fragment course is stopped");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e("Fragment", "fragment course is destroyed");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e("Fragment", "fragment course is deattached");
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
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
		/*
		 * teachers = new ArrayList<Teacher>(); Teacher t1 = new Teacher(0000,
		 * R.drawable.ward, "Ward Abbass", "mail@ward.com", "0555", "חברתי",
		 * "CS"); Teacher t2 = new Teacher(111, R.drawable.mohammed,
		 * "Mohammed Abd Elraziq", "mail@mohammed.com", "015", "", "ה.חומרים");
		 * Teacher t3 = new Teacher(111, R.drawable.hamed, "Hammed Abbass",
		 * "mail@hamed.com", "0555", "", "ת.נ"); Teacher t4 = new Teacher(333,
		 * R.drawable.stlzbale, "חסן ח'לאילה", "mail@stlzbale.com", "0555", "",
		 * "חשמל");
		 * 
		 * teachers.add(t1); teachers.add(t2); teachers.add(t3);
		 * teachers.add(t4);
		 * 
		 * Course c = new Course(7, courseName, "WednesDay", "17:30", "19:30",
		 * "Ulman 603", "204112130"); HashMap<String, Course> stamTest = new
		 * HashMap<String, Course>(); stamTest.put(courseName, c);
		 * t1.setCourses(stamTest); t2.setCourses(stamTest);
		 * t3.setCourses(stamTest); t4.setCourses(stamTest);
		 */
	}

	private void fetchArguments() {
		Bundle ex = getArguments();
		if (ex != null) {
			courseName = ex.getString("name");
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
	
		courseNameLable.setText(courseName);
		courseDesriptionLable
				.setText(courseDesription == null ? "No Description"
						: courseDesription);
		courseImg.setImageResource(imgId);
		addListnerOnFeedClick();
		//l = (ListView) root.findViewById(R.id.courseTeachers);
		exList=(ExpandableListView)root.findViewById(R.id.courseTeachers);
		teachers=db_mngr.getTeachersForCourse(courseName);
		timesForEachTeacher=new HashMap<String, List<String>>();
		for(Teacher t : teachers)
		{
			timesForEachTeacher.put(t.getId_number(), t.getTimePlaceForCourse(courseName));
		}
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
		db_mngr=new DBManager(getActivity());
		
		
		fetchArguments();
		initlizeUI(root);
		initlizeTeachers();
		///adapter ad = new adapter(getActivity(), teachers, getResources(),
		//		courseName);
		
		ExpandableListAdapter ad=new ExpandableListAdapter(getActivity(), teachers, timesForEachTeacher);
		//SwingRightInAnimationAdapter sRin = new SwingRightInAnimationAdapter(ad);
		//sRin.setAbsListView(l);
		//l.setAdapter(sRin);
		exList.setAdapter(ad);
		return root;
	}
/*
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
		//	HashMap<String, String> data = t.getCourseDetailsToShow(courseName);
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
*/
	static class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context _context;
		private List<Teacher> _teachers;
		private HashMap<String, List<String>> _listTimes;
		private LayoutInflater inflater;
		public ExpandableListAdapter(Context context, List<Teacher> teachers,
				HashMap<String, List<String>> listTimes) {
			this._context=context;
			this._teachers=teachers;
			this._listTimes=listTimes;
			this.inflater=(LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public Object getChild(int groupPos, int ChildPos) {
			//return the list of theacher time for this course (String List) by id number given and get the spiceifec child (String )from strings list
			return this._listTimes.get(this._teachers.get(groupPos).getId_number()).get(ChildPos);
		}

		@Override
		public long getChildId(int groupPos, int ChildPos) {
			// TODO Auto-generated method stub
			return ChildPos;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final String dateTime=(String) getChild(groupPosition, childPosition);
			View v=convertView;
			if(v==null)
			{
				v=inflater.inflate(R.layout.teacher_course_times, null);
				v.setTag(R.id.day_time_workshop,v.findViewById(R.id.day_time_workshop));
				
			}
			CheckBox chkBox=(CheckBox)v.getTag(R.id.day_time_workshop);
			chkBox.setText(dateTime);
			return v;
		}

		@Override
		public int getChildrenCount(int arg0) {
			
			return this._listTimes.get(this._teachers.get(arg0).getId_number()).size();
		}

		@Override
		public Object getGroup(int arg0) {
			return this._teachers.get(arg0);
		}

		@Override
		public int getGroupCount() {
			
			return this._teachers.size();
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent) {
		
			View v=convertView;
			if(v==null) {
			v = inflater.inflate(R.layout.course_teacher_details, parent,
					false);
			v.setTag(R.id.tutorSmallAvatar,
					v.findViewById(R.id.tutorSmallAvatar));
			v.setTag(R.id.alaramMe, v.findViewById(R.id.alaramMe));
			v.setTag(R.id.teacherFacultyLable,
					v.findViewById(R.id.teacherFacultyLable));
			v.setTag(R.id.teacherCourseName,
					v.findViewById(R.id.teacherCourseName));
		
			}
			ImageView teacherAvatar = (ImageView) v
					.getTag(R.id.tutorSmallAvatar);
			ImageView alarmMe = (ImageView) v.getTag(R.id.alaramMe);
			TextView faculty = (TextView) v.getTag(R.id.teacherFacultyLable);
			TextView name = (TextView) v.getTag(R.id.teacherCourseName);

			Teacher t = (Teacher)_teachers.get(groupPosition);
		//	HashMap<String, String> data = t.getCourseDetailsToShow(courseName);
			teacherAvatar.setImageResource(t.getImgId());
			name.setText(t.getName()+" "+t.getLast_name());
			faculty.setText(t.getFaculty());
		//	day.setText(data.get("day"));
			//timeFrom.setText(data.get("from"));
		//	timeTo.setText(data.get("to"));
		//	place.setText(data.get("place"));
			return v;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

	}

}
