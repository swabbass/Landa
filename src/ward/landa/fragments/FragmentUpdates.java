package ward.landa.fragments;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilites.ConnectionDetector;
import utilites.DBManager;
import utilites.JSONParser;
import ward.landa.ExpandableTextView;
import ward.landa.GCMUtils;
import ward.landa.R;
import ward.landa.Update;
import ward.landa.activities.Settings;
import ward.landa.activities.SettingsActivity;
import ward.landa.activities.Utilities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

public class FragmentUpdates extends Fragment {

	GoogleCloudMessaging gcm;
	boolean isReg;
	String regKey;
	ListView l;
	List<Update> updates;
	
	boolean isExpanded = false;
	updateCallback callBack;
	boolean showAll;
	updatesAdapter uAdapter;
	updateReciever uR;
	JSONParser jParser;
	ConnectionDetector connectionDetector;
	DBManager db_mngr;
	private boolean toFetchDataFromDB;
	private int lastChangedIndex = -1;

	public interface updateCallback {
		public void onUpdateClick(Update u);
	}

	@Override
	public void onResume() {
		Log.d("Fragment", "on resume updates");

		uR = new updateReciever();
		IntentFilter intentFilter = new IntentFilter(
				"com.google.android.c2dm.intent.RECEIVE");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		getActivity().registerReceiver(uR, intentFilter);

		super.onResume();
	}

	@Override
	public void onStop() {
		Log.d("Fragment", "on stop updates");
		try {
			saveUpdatesChanges();
		} catch (SQLDataException e) {

			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	public void onStart() {

		Log.d("Fragment", "on start updates");
		super.onStart();
	}

	@Override
	public void onPause() {
		if (uR != null) {

			getActivity().unregisterReceiver(uR);
		}
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
			Intent i = new Intent(getActivity(), SettingsActivity.class);
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

	private void initlizeFragment(View root) {
		db_mngr = new DBManager(getActivity());
		lastChangedIndex = -1;
		connectionDetector = new ConnectionDetector(getActivity());
		jParser = new JSONParser();
		l = (ListView) root.findViewById(R.id.updates_listView);
		showAll = false;
		updates = new ArrayList<Update>();

		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		toFetchDataFromDB = sh.getBoolean(GCMUtils.LOAD_UPDATES, false);

	}

	private void initlizeListners() {
		l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				callBack.onUpdateClick(updates.get(arg2));
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
				mode.setTitle(count
						+ " "
						+ getActivity().getResources().getString(
								R.string.selected));
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
					if (count <= updates.size())
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

	}

	private void initlizeDataForFragment() {
		boolean isConnected = connectionDetector.isConnectingToInternet();
		if (!toFetchDataFromDB && isConnected) {
			new downloadRecentUpdates().execute();
		} else {
			updates = null;
			updates = db_mngr.getCursorAllUpdates();
			uAdapter = new updatesAdapter(updates, updates, getActivity(),
					callBack);
			uAdapter.setShowall(false);
			ScaleInAnimationAdapter sc = new ScaleInAnimationAdapter(uAdapter);
			sc.setAbsListView(l);
			l.setAdapter(sc);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("Fragment", "on create updates");
		View root = inflater.inflate(R.layout.updates_frag_list, container,
				false);
		initlizeFragment(root);
		initlizeDataForFragment();
		initlizeListners();
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
			}
			subject = (ExpandableTextView) v.getTag(R.id.contentTextBox);
			title = (TextView) v.getTag(R.id.title_updateLable);
			title.setText(updates.get(position).getSubject());
			subject.setText(updates.get(position).getText());
			time = (TextView) v.getTag(R.id.update_timeLable);
			time.setText(updates.get(position).getDateTime());
			return v;
		}

	}

	class updateReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getAction().toString()
					.compareTo("com.google.android.c2dm.intent.RECEIVE") == 0) {
				abortBroadcast();
				Update u = Utilities.generateUpdateFromExtras(arg1.getExtras(),
						arg0);
				if (u != null && u.getUrlToJason() == null) {
					updates.add(0,u);


					if (lastChangedIndex == -1) {
						lastChangedIndex = 0;
					}
					else {
						lastChangedIndex++;
					}
					uAdapter.notifyDataSetChanged();
					Utilities.showNotification(getActivity(), u.getSubject(),
							u.getText());
				} else if (u.getUrlToJason() != null) {
					Utilities.PostListener listner = new Utilities.PostListener() {

						@Override
						public void onPostUpdateDownloaded(Update u) {

							boolean toSaveAdded = addUpdate(u);
							if (lastChangedIndex == -1 && toSaveAdded) {
								lastChangedIndex = 0;
							}
							else if(toSaveAdded){
								lastChangedIndex++;
							}
							Utilities.showNotification(getActivity(),
									u.getSubject(), u.getText());
							uAdapter.notifyDataSetChanged();

						}
					};
					Utilities.fetchUpdateFromBackEndTask task = new Utilities.fetchUpdateFromBackEndTask(
							getActivity(), listner);
					task.execute(u.getUpdate_id());
				}
			}

		}

	}

	private boolean addUpdate(Update u) {
		for (Update tmp : updates) {
			if (tmp.equals(u)) {
				tmp.setSubject(u.getSubject());
				tmp.setText(u.getText());
				tmp.setDateTime(u.getDateTime());
				tmp.setUrl(u.getUrl());
				db_mngr.updateUpdate(u);
				return false;
			}
		}
		updates.add(0,u);
		return true;
	}

	class downloadRecentUpdates extends AsyncTask<String, String, String> {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		private ProgressDialog pDialog;
		boolean downloadOk = false;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			JSONObject jObject = jParser.makeHttpRequest(Settings.URL_UPDATES,
					"GET", this.params);
			if (jObject == null) {
				if (cancel(true)) {
					Log.e(GCMUtils.TAG,
							"loading Updates from internet canceled");
				}
			} else {
				Log.d("ward", jObject.toString());

				try {
					JSONArray updatesArray = jObject.getJSONArray("posts");
					for (int i = 0; i < updatesArray.length(); ++i) {
						JSONObject update = updatesArray.getJSONObject(i);
						Update u = new Update(update.getString("id"),
								update.getString("title"),
								update.getString("date"),
								update.getString("content"));
						u.setUrl(update.getString("url"));
						updates.add(u);
					}
					downloadOk = true;
				} catch (JSONException e) {

					e.printStackTrace();
					Log.e(GCMUtils.TAG, e.toString());
					if (!connectionDetector.isConnectingToInternet()) {
						Log.e(GCMUtils.TAG, "faild no internet ");
						cancel(true);
					}

				}
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (downloadOk) {
				saveDownloadOnceStatus(true);
				for (Update u : updates) {
					db_mngr.insertUpdate(u);
				}
				pDialog.dismiss();
				uAdapter = new updatesAdapter(updates, updates, getActivity(),
						callBack);
				uAdapter.setShowall(false);
				ScaleInAnimationAdapter sc = new ScaleInAnimationAdapter(
						uAdapter);
				sc.setAbsListView(l);
				l.setAdapter(sc);
			}
			super.onPostExecute(result);
		}

	}

	public void saveDownloadOnceStatus(boolean b) {
		SharedPreferences sh = getActivity().getSharedPreferences(
				GCMUtils.DATA, Activity.MODE_PRIVATE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putBoolean(GCMUtils.LOAD_UPDATES, b);
		ed.commit();

	}

	private void saveUpdatesChanges() throws SQLDataException {
		int size = updates.size();// o(n)
		if (lastChangedIndex != -1) {
			// each itration is o(1)
			for (int i = 0; i <= lastChangedIndex; ++i) {
				if (db_mngr.insertUpdate(updates.get(i)) < 0) {
					throw new SQLDataException(
							"Damn Rome You Have some Issues with Saving Updates ");
				}
			}
		}
	}
}
