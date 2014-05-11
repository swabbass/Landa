package ward.landa.fragments;

import java.util.ArrayList;
import java.util.List;

import ward.landa.ExpandableTextView;
import ward.landa.R;
import ward.landa.Update;
import ward.landa.activities.SettingsActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

public class FragmentUpdates extends Fragment {

	ListView l;
	//SwipeListView l;
	List<Update> ups;
	List<Update> hidden;
	// maybe more effiecent to deal with indexes of the active ones but first
	// solve adapter issues
	List<Update> active;
	boolean isExpanded = false;
	updateCallback callBack;
	boolean showAll;
	updatesAdapter uAdapter;

	public interface updateCallback {
		public void onUpdateClick(Update u) ;
	}

	@Override
	public void onResume() {
		Log.d("Fragment", "on resume updates");
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.d("Fragment", "on stop updates");
		super.onStop();
	}

	@Override
	public void onStart() {
		Log.d("Fragment", "on start updates");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.d("Fragment", "on pause updates");
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("Fragment", "on attach updates");
		try {
			callBack = (updateCallback) activity;
			setHasOptionsMenu(true);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement callbackTeacher");
		}

		super.onAttach(activity);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.settings:
			Intent i=new Intent(getActivity(), SettingsActivity.class);
			startActivity(i);
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		getActivity().getMenuInflater().inflate(R.menu.listmenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("Fragment", "on create updates");
		View root = inflater.inflate(R.layout.updates_frag_list, container,
				false);
		l = (ListView) root.findViewById(R.id.updates_listView);
		//l = (SwipeListView) root.findViewById(R.id.updates_listView);
		showAll = false;
		String lo = "לקראת תחילת תהליך החייאת השפה העברית, בעת שהיא שימשה רק כשפה שנייה בפי יהודים ושומרונים, היו לשפה שני ניבים - יהודי ושומרוני, כשהניב השומרוני היה על סף כליה גם כשפה שנייה, יחד עם העדה השומרונית עצמה. לניב היהודי היו שלוש דרכי הגיה עיקריות: אשכנזית, ספרדית ותימנית (יש המציינים גם הגייה עיראקית). עם החייאת השפה, בן-יהודה הכריז על ההגייה הספרדית כהגייה התקנית של העברית. אולם בפועל מחיי השפה דבקו בהגייה שהיא מעין פשרה בין הספרדית לאשכנזית וזו נשתרשה בעם.";
		ups = new ArrayList<Update>();
		ups.add(new Update("subject Dummy","25/11/1992 14:00","hey hey noce content"));
		ups.add(new Update("subject Dummy","14/11/2001 14:00",
				"בשל התחייה המאוחרת של השפה העברית, אין כמעט ניבים אזוריים עבריים. למעשה, השפה הנשמעת בפי דוברים ילידיים זהה כמעט בכל חלקי ישראל. אפשר להבחין בשוני בין הניבים המדוברים בפי עדות יהודיות שונות (אתנולקטים). אולם, שוני זה מתבטא בעיקר בפונולוגיה, ולא בתחביר או במורפולוגיה. עיקרי שוני זה הם בהשפעה ספרדית-תימנית על ההגייה המקובלת ולעתים בהשפעה אשכנזית על ההגייה המקובלת (למשל בקרב חלק מהעדה החרדית בירושלים ובברוקלין). שוני מסוים בתחביר ובמורפולוגיה קיים בין ניבים מעמדיים של השפה (סוציולקטים), אולם שוני זה אינו גדול יחסית."));
		ups.add(new Update("subject Dummy","14/11/2001 14:00",lo));
		ups.add(new Update("subject Dummy","14/11/2001 14:00",lo.substring(20)));
		ups.add(new Update("subject Dummy","14/11/2001 14:00",lo.substring(10, lo.length() - 1)));

		
		uAdapter = new updatesAdapter(ups, ups, getActivity(), callBack);
		uAdapter.setShowall(false);
		ScaleInAnimationAdapter sc = new ScaleInAnimationAdapter(uAdapter);
		sc.setAbsListView(l);
		l.setAdapter(sc);
		
		l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				callBack.onUpdateClick(ups.get(arg2));
			}

		});

		l.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			int count = 0;

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.update_contextual_menu,
						menu);
				mode.setTitle(count +" "+ getActivity().getResources().getString(R.string.selected));
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

				return true;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {

				if (checked) {
					if (count <= ups.size())
						count++;
					mode.setTitle(count + " "
							+ getResources().getString(R.string.selected));

				} else {
					if (count >= 0) {
						count--;
					}
					// l.setItemChecked(position, !checked);
					mode.setTitle(count + " "
							+ getResources().getString(R.string.selected));

				}

			}
		});

		l.setItemsCanFocus(false);

		return root;
	}

	static class updatesAdapter extends BaseAdapter {

		private List<Update> updates;
		LayoutInflater inflater = null;
		Context cxt = null;
		updateCallback callback;
		private boolean showall;
		List<Update> source;
		private boolean isActionMode;

		public List<Update> getUpdates() {
			return updates;
		}

		public void setUpdates(List<Update> updates) {
			this.updates = updates;
		}

		public void setShowall(boolean showall) {
			this.showall = showall;
		}

		@Override
		public boolean isEnabled(int position) {
			if (this.isActionMode) {
				return true;
			}
			// no actionmode = everything enabled
			return true;
		}

		public boolean isShowall() {
			return showall;
		}

		public updatesAdapter(List<Update> updates, List<Update> source,
				Context cxt, updateCallback callback) {
			showall = false;
			this.updates = updates;
			this.cxt = cxt;
			this.inflater = (LayoutInflater) cxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.callback = callback;
			this.source = source;
		}

		@Override
		public int getCount() {

			return updates.size();
		}

		@Override
		public Object getItem(int position) {

			return updates.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View v = convertView;
			TextView title;
			TextView time;
			ExpandableTextView subject;

			if (v == null) {
				v = inflater.inflate(R.layout.updates_item, parent, false);
				v.setTag(R.id.title_updateLable,
						v.findViewById(R.id.title_updateLable));
				v.setTag(R.id.contentTextBox,
						v.findViewById(R.id.contentTextBox));
				v.setTag(R.id.update_timeLable,
						v.findViewById(R.id.update_timeLable));
				// v.setTag(R.id.imgattention,
				// v.findViewById(R.id.imgattention));
			}
			subject = (ExpandableTextView) v.getTag(R.id.contentTextBox);
			title = (TextView) v.getTag(R.id.title_updateLable);
			title.setText(updates.get(position).getSubject());
			subject.setText(updates.get(position).getText());
			time = (TextView) v.getTag(R.id.update_timeLable);
			time.setText(updates.get(position).getDateTime());
			/*
			 * final ImageButton btn = (ImageButton)
			 * v.getTag(R.id.imgattention); if
			 * (updates.get(position).isActive()) {
			 * btn.setImageResource(R.drawable.update_icon_new); } else {
			 * btn.setImageResource(R.drawable.megaphone_after); }
			 */

			return v;
		}

	}
	public int convertDpToPixel(float dp) {
	       DisplayMetrics metrics = getResources().getDisplayMetrics();
	       float px = dp * (metrics.densityDpi / 160f);
	       return (int) px;
	   }
	private static List<Update> filterActive(List<Update> updates) {
		List<Update> result = new ArrayList<Update>();
		for (Update u : updates) {
			if (u.isActive()) {
				result.add(u);
			}
		}
		return result;
	}

}
