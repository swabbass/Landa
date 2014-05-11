package ward.landa.activities;

import ward.landa.R;
import ward.landa.fragments.CourseFragment;
import Utilites.Settings;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class CourseDeatilsActivity extends FragmentActivity {

	private String courseName;
	private int imgId;
	private int courseID;
	private int parentIndex = -1;

	private void fetchArguments() {
		Bundle ex = getIntent().getExtras();
		if (ex != null) {
			this.setCourseName(ex.getString("name"));
			this.setImgId(ex.getInt("ImageID"));
			this.setCourseID(ex.getInt("courseID"));
			this.setParentIndex(ex.getInt("position"));
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_deatils);
		fetchArguments();
		Intent result = new Intent(getApplicationContext(), MainActivity.class);
		setTitle(courseName);
		setResult(Settings.COURSES, result);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		if (savedInstanceState == null) {
			CourseFragment cf = new CourseFragment();

			Bundle extras = new Bundle();
			extras.putString("name", getCourseName());
			extras.putInt("ImageID", getImgId());
			extras.putInt("courseID", getCourseID());
			cf.setArguments(extras);
			FragmentTransaction tr = getSupportFragmentManager()
					.beginTransaction();
			tr.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.add(R.id.container, cf).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_deatils, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent result = new Intent(getApplicationContext(),
					MainActivity.class);

			setResult(Settings.COURSES, result);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public int getParentIndex() {
		return parentIndex;
	}

	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}

}
