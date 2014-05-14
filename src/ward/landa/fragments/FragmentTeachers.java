package ward.landa.fragments;

import java.util.ArrayList;
import java.util.List;

import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.ImageUtilities.BitmapUtils;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class FragmentTeachers extends Fragment {

	List<Teacher> teachers;
	List<Teacher> searched;
	callbackTeacher tCallback;
	gridAdabter gAdapter;
	public interface callbackTeacher {
		public void OnTeacherItemClick(Teacher t);
	}
	
	private void initlizeSearchEngine(View v) {

		EditText search = (EditText) v.findViewById(R.id.teacher_txt_search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("text", "text now changed");
				if (s.length() != 0) {
					gAdapter.setL(search(s.toString()), 1);
					gAdapter.notifyDataSetChanged();

				} else {
					gAdapter.setL(teachers, 0);
					gAdapter.notifyDataSetChanged();
				}

			}

			private List<Teacher> search(String string) {
				searched.clear();
				for (Teacher t : teachers) {
					if (t.getName().contains(string)) {
						searched.add(t);
					}
				}

				return searched;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.d("text", "before text now changed");

			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d("text", "after text now changed");

			}
		});

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.teacher_menu, menu);
		View v = (View) menu.findItem(R.id.teacher_menu_search).getActionView();
		initlizeSearchEngine(v);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			tCallback = (callbackTeacher) activity;
			setHasOptionsMenu(true);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement callbackTeacher");
		}
		super.onAttach(activity);
	}

	private void runDemo() {

		// ////////////////////Testing//////////////////////////
		Teacher t1 = new Teacher(0000, R.drawable.ward, "ward",
				"mail@ward.com", "0555", "חברתי", "CS");
		Teacher t2 = new Teacher(111, R.drawable.mohammed, "Mohammed",
				"mail@mohammed.com", "015", "אקדמי", "CS");
		Teacher t3 = new Teacher(121, R.drawable.hamed, "Hammed",
				"mail@hamed.com", "0555", "רכז אקדמי", "מתמטיקה");
		Teacher t4 = new Teacher(333, R.drawable.stlzbale, "حسن",
				"mail@stlzbale.com", "0555", "רכז חברתי", "CS");
		Teacher t5 = new Teacher(888, R.drawable.aiman, "ايمن",
				"mail@aiman.com", "0555", "אקדמי", "כימיה");
		Teacher t6 = new Teacher(444, R.drawable.almotna, "Almothana",
				"mail@almotna.com", "0555", "אקדמי", "CS");
		Teacher t7 = new Teacher(555, R.drawable.mhde, "Mahade",
				"mail@mhde.com", "0555", "חברתי", "אזרחית");
		Teacher t8 = new Teacher(666, R.drawable.gasan, "Ghasan",
				"mail@gasan.com", "0555", "אקדמי", "CS");
		Teacher t9 = new Teacher(777, R.drawable.rami, "الله لا يكبرو",
				"mail@rami.com", "0555", "חברתי", "תעשיה ניהוך");
		teachers.add(t1);
		teachers.add(t2);
		teachers.add(t3);
		teachers.add(t4);
		teachers.add(t5);
		teachers.add(t6);
		teachers.add(t7);
		teachers.add(t8);
		teachers.add(t9);

	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.teacher_custom_grid, container,
				false);
		teachers = new ArrayList<Teacher>();
		searched = new ArrayList<Teacher>();
		runDemo();
		gAdapter = new gridAdabter(root.getContext(), teachers, getResources(),
				0);
		GridView gridView = (GridView) root.findViewById(R.id.gridview);
		SwingBottomInAnimationAdapter sb = new SwingBottomInAnimationAdapter(
				gAdapter);
		sb.setAbsListView(gridView);
		gridView.setAdapter(sb);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tCallback.OnTeacherItemClick(gAdapter.searched == 0 ? teachers
						.get(arg2) : searched.get(arg2));

			}
		});

		return root;
	}

	static class gridAdabter extends BaseAdapter {

		LayoutInflater inflater;
		List<Teacher> l;
		Resources res;
		BitmapUtils bmpUtils;
		int searched = 0;

		public void setL(List<Teacher> l, int search) {
			this.l = l;
			this.searched = search;
		}

		public gridAdabter(Context context, List<Teacher> l, Resources res,
				int search) {
			this.inflater = LayoutInflater.from(context);
			this.l = l;
			this.res = res;
			this.searched = search;
			this.bmpUtils = new BitmapUtils(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {

			return l.size();
		}

		@Override
		public Object getItem(int arg0) {

			return l.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return l.get(arg0).getID();
		}

		@Override
		public View getView(int pos, View view, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			View v = view;
			ImageView picture;
			TextView name;

			if (v == null) {
				v = inflater.inflate(R.layout.grid_textimg_item, viewGroup,
						false);
				v.setTag(R.id.picture, v.findViewById(R.id.picture));
				v.setTag(R.id.text, v.findViewById(R.id.text));
			}
			picture = (ImageView) v.getTag(R.id.picture);
			name = (TextView) v.getTag(R.id.text);

			Teacher teacher = (Teacher) getItem(pos);
			bmpUtils.loadBitmap(teacher.getImgId(), picture);
			/*
			 * picture.setImageBitmap(Utilities.decodeSampledBitmapFromResource(
			 * res, teacher.getImgId(), 150, 150));
			 */
			name.setText(teacher.toString());

			return v;
		}
	}

}
