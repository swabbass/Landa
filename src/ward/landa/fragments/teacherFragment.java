package ward.landa.fragments;

import utilites.Role;
import ward.landa.R;
import ward.landa.R.id;
import ward.landa.R.layout;
import ward.landa.Teacher;
import ward.landa.activities.Utilities;
import android.content.Intent;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class teacherFragment extends Fragment {
	Teacher t;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.teacherfragmentlayout, container,
				false);
		ImageView img = (ImageView) root.findViewById(R.id.teacher_picture);
		TextView name = (TextView) root.findViewById(R.id.nameteacher);
		TextView email = (TextView) root.findViewById(R.id.emailTeacher);
		TextView faculty = (TextView) root.findViewById(R.id.facultyTeacher);
		TextView position = (TextView) root.findViewById(R.id.positionteacher);
		ImageView sendEmail = (ImageView) root.findViewById(R.id.sendEmail);
		Bundle ext = getArguments();
		if (ext != null) {

			t = (Teacher) ext.getSerializable("teacher");
			img.setImageURI(t.getUriFromLocal());
			getActivity().setTitle(t.getName());
			name.setText(t.getName() + " " + t.getLast_name());
			email.setText(t.getEmail());
			faculty.setText(t.getFaculty());
			position.setText(getRoleFromResourse(t.getPosition()));
		}
		sendEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
						.fromParts("mailto", t.getEmail(), null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "From Landa Application");
				startActivity(Intent
						.createChooser(emailIntent, "Send email..."));

			}
		});
		return root;
	}

	private String getRoleFromResourse(String s) {
		switch (s) {
		case "TUTOR":
			return getResources().getString(R.string.tutor);
		case "ACADIMIC_CORDINATOR":
			return getResources().getString(R.string.AC);
		case "SOCIAL_CORDINATOR":
			return getResources().getString(R.string.SC);
		}
		return null;
	}
}
