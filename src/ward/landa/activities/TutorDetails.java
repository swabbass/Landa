package ward.landa.activities;

import ward.landa.R;
import ward.landa.fragments.teacherFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class TutorDetails extends FragmentActivity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutor_details);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		if (savedInstanceState == null) {
			teacherFragment tf = new teacherFragment();
		setResult(Settings.TEACHERS);
			tf.setArguments(getIntent().getExtras());
			FragmentTransaction tr = getSupportFragmentManager()
					.beginTransaction();
			tr.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.add(R.id.tutorDetailContainer, tf).commit();
		}
		setTitle(getIntent().getExtras().getCharSequence("name"));
	}
	
	
}
