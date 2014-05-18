package ward.landa.fragments;

import ward.landa.R;
import ward.landa.R.id;
import ward.landa.R.layout;
import ward.landa.Teacher;
import ward.landa.activities.Utilities;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class teacherFragment extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View root=inflater.inflate(R.layout.teacherfragmentlayout, container,false);
			ImageView img=(ImageView) root.findViewById(R.id.teacher_picture);
			TextView name=(TextView) root.findViewById(R.id.nameteacher);
			TextView email=(TextView) root.findViewById(R.id.emailTeacher);
			TextView faculty=(TextView) root.findViewById(R.id.facultyTeacher);
			TextView position=(TextView) root.findViewById(R.id.positionteacher);
			Bundle ext =getArguments();
			if(ext!=null)
			{
				Teacher t=(Teacher) ext.getSerializable("teacher");
				img.setImageURI(t.getUriFromLocal());
				name.setText(t.getName()+" "+t.getLast_name());
				email.setText(t.getEmail());
				faculty.setText(t.getFaculty());
				position.setText(t.getRole());
			}
			
		return root;
	}
}
