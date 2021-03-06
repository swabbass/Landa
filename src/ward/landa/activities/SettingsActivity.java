package ward.landa.activities;


import ward.landa.R;
import ward.landa.fragments.SettingsFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SettingsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		if (savedInstanceState == null) {
			SettingsFragment sf=new SettingsFragment();
			FragmentTransaction tr = getSupportFragmentManager()
					.beginTransaction();
			tr.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.add(R.id.settingsContainer, sf).commit();						
		}
		
		setTitle(R.string.settings);
	}
}
