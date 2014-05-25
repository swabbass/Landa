package ward.landa.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import utilites.DBManager;
import ward.landa.Course;
import ward.landa.R;
import ward.landa.Teacher;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class CourseFragment extends Fragment {

	List<Teacher> teachers;
	HashMap<String, List<String>> timesForEachTeacher;
	ListView l;
	ExpandableListView exList;
	ExpandableListAdapter exAdapter;
	int courseID;
	int imgId;
	float rating;
	static String courseName;
	String courseDesription;
	private static TextView courseDesriptionLable;
	TextView courseNameLable;
	ImageView courseImg;
	ImageView feedbackImg;
	ImageLoaderConfiguration config;
	private static ImageLoader image_loader;
	PauseOnScrollListener listener;
	DBManager db_mngr;
	AlarmCallBack alarmCheckListner;

	private ImageLoaderConfiguration initilizeImageLoader(
			DisplayImageOptions options) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).memoryCacheExtraOptions(120, 120)
				.denyCacheImageMultipleSizesInMemory()
				.threadPriority(Thread.MAX_PRIORITY)
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
				.showImageForEmptyUri(R.drawable.person)
				// resource or drawable
				.showImageOnFail(R.drawable.person)
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

	private void initlizeImageLoad() {
		config = initilizeImageLoader(initlizeImageDisplay());
		image_loader = ImageLoader.getInstance();
		image_loader.init(config);
		boolean pauseOnScroll = false; // or true
		boolean pauseOnFling = true; // or false
		listener = new PauseOnScrollListener(image_loader, pauseOnScroll,
				pauseOnFling);

	}

	public interface AlarmCallBack {
		public void onTimeChecked(String time, boolean isChecked);
	}

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
		try {
			alarmCheckListner = (AlarmCallBack) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement callbackTeacher");
		}
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
		// l = (ListView) root.findViewById(R.id.courseTeachers);
		exList = (ExpandableListView) root.findViewById(R.id.courseTeachers);
		teachers = db_mngr.getTeachersForCourse(courseName);
		timesForEachTeacher = new HashMap<String, List<String>>();
		for (Teacher t : teachers) {
			timesForEachTeacher.put(t.getId_number(),
					t.getTimePlaceForCourse(courseName));
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
		db_mngr = new DBManager(getActivity());
		initlizeImageLoad();
		fetchArguments();
		initlizeUI(root);
		initlizeTeachers();
		ExpandableListAdapter ad = new ExpandableListAdapter(getActivity(),
				teachers, timesForEachTeacher, alarmCheckListner);
		exList.setAdapter(ad);
		return root;
	}

	static class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context _context;
		private List<Teacher> _teachers;
		private HashMap<String, List<String>> _listTimes;
		private LayoutInflater inflater;
		private AlarmCallBack listner;

		public ExpandableListAdapter(Context context, List<Teacher> teachers,
				HashMap<String, List<String>> listTimes, AlarmCallBack lister) {
			this._context = context;
			this._teachers = teachers;
			this._listTimes = listTimes;
			this.listner = lister;
			this.inflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public Object getChild(int groupPos, int ChildPos) {
			// return the list of theacher time for this course (String List) by
			// id number given and get the spiceifec child (String )from strings
			// list
			return this._listTimes.get(
					this._teachers.get(groupPos).getId_number()).get(ChildPos);
		}

		@Override
		public long getChildId(int groupPos, int ChildPos) {
			// TODO Auto-generated method stub
			return ChildPos;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final String dateTime = (String) getChild(groupPosition,
					childPosition);
			View v = convertView;
			if (v == null) {
				v = inflater.inflate(R.layout.teacher_course_times, null);
				v.setTag(R.id.day_time_workshop,
						v.findViewById(R.id.day_time_workshop));

			}
			CheckBox chkBox = (CheckBox) v.getTag(R.id.day_time_workshop);
			String[] info = dateTime.split(Pattern.quote(" - "));
			int lastIndex = info.length - 3;
			String day = info[lastIndex - 2];
			String timeFrom = info[lastIndex - 1];
			String tumeTo = info[lastIndex];
			String place = info[lastIndex - 3];
			String notify = info[lastIndex + 1];
			String id = info[lastIndex + 2];
			for (int i = lastIndex - 4; i >= 0; --i) {
				place += " " + info[i];
			}
			chkBox.setText(place + " " + day + " " + timeFrom + "-" + tumeTo);
			if (notify.equals("1")) {
				chkBox.setChecked(true);
			} else {
				chkBox.setChecked(false);
			}
			chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					courseDesriptionLable.append(dateTime);
					listner.onTimeChecked(dateTime, isChecked);
				}
			});
			return v;
		}

		@Override
		public int getChildrenCount(int arg0) {

			return this._listTimes.get(this._teachers.get(arg0).getId_number())
					.size();
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
			ImageAware image = new ImageViewAware(teacherAvatar, false);
			ImageView alarmMe = (ImageView) v.getTag(R.id.alaramMe);
			TextView faculty = (TextView) v.getTag(R.id.teacherFacultyLable);
			TextView name = (TextView) v.getTag(R.id.teacherCourseName);
			Teacher t = (Teacher)_teachers.get(groupPosition);
			image_loader.displayImage("file://" + t.getImageLocalPath(), image);
			name.setText(t.getName()+" "+t.getLast_name());
			faculty.setText(t.getFaculty());

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
