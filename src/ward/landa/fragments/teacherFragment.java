package ward.landa.fragments;

import ward.landa.R;
import ward.landa.R.id;
import ward.landa.R.layout;
import ward.landa.activities.Utilities;
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
				int imgid=ext.getInt("imgid");
				img.setImageBitmap(Utilities.decodeSampledBitmapFromResource(
						getResources(), imgid, 100, 100));
				name.setText(ext.getString("name"));
				email.setText(ext.getString("email"));
				faculty.setText(ext.getString("faculty"));
				position.setText(ext.getString("position"));
			}
			
		return root;
	}
}
