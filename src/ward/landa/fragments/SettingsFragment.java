package ward.landa.fragments;


import ward.landa.R;
import ward.landa.activities.Settings;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingsFragment extends Fragment implements OnClickListener {

	CheckBox updatesChkbox,workshopsChkbox;
	RadioGroup langChoice;
	boolean updateMe,workshopMe;
	String localLang;
	private void initlizeUI(View root) {
	updatesChkbox=(CheckBox)root.findViewById(R.id.UpdatecheckBox);
	workshopsChkbox=(CheckBox)root.findViewById(R.id.workshopChkBox);
	langChoice=(RadioGroup)root.findViewById(R.id.languageChoice);
	}
	
	private void loadSettings() {
		
		Settings.initlizeSettings(getActivity());
		updateMe=Settings.isToNotifyUpdates();
		workshopMe=Settings.isToNotifyCourse();
		localLang=Settings.getLocalLang();
	}
	private void setSettings(View root) {
		
		updatesChkbox.setChecked(updateMe);
		workshopsChkbox.setChecked(workshopMe);
		RadioButton btn=(RadioButton)root.findViewById(Settings.langId(localLang));
		btn.setChecked(true);
		langChoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.radioEn:
					localLang=Settings.ENGLISH;
					break;
				case R.id.radioAr:
					localLang=Settings.ARABIC;
					break;
				case R.id.radioHE:
					localLang=Settings.HEBREW;
					break;
				}
				
			}
		});
		updatesChkbox.setOnClickListener(this);
		workshopsChkbox.setOnClickListener(this);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View root=inflater.inflate(R.layout.settings_fragment, container,false);
		initlizeUI(root);
		loadSettings();
		setSettings(root);
		return root;
	}
	@Override
	public void onPause() {
		Settings.saveSettings(getActivity(), localLang, workshopMe, updateMe);
		super.onPause();
	}
	@Override
	public void onStop() {
	
		super.onStop();
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.UpdatecheckBox:
			updateMe=updatesChkbox.isChecked();
			break;
		case R.id.workshopChkBox:
			workshopMe=workshopsChkbox.isChecked();
			break;
		}
	}
}