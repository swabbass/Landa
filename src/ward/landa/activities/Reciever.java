package ward.landa.activities;

import java.util.Set;

import utilites.DBManager;
import ward.landa.Course;
import ward.landa.GCMUtils;
import ward.landa.Teacher;
import ward.landa.Update;
import ward.landa.activities.Utilities.PostListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Reciever extends BroadcastReceiver implements PostListener {
	DBManager dbmngr;
	Context cxt;
	final public static String ONE_TIME = "onetime";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("wordpress", "" + intent.getAction());
		Set<String> keys = intent.getExtras().keySet();
		this.cxt = context;
		for (String key : keys) {
			Log.d("wordpress", key + " : " + intent.getExtras().getString(key));
		}
		if (intent.getAction().toString()
				.compareTo("com.google.android.c2dm.intent.RECEIVE") == 0) {
			dbmngr = new DBManager(context);
			if (intent.getStringExtra("Type") != null) {
				String t=intent.getStringExtra("Type");
				if(t.contains("INSTRUCTOR"))
				{
					GCMUtils.HandleInstructor(t,context, dbmngr, intent);
				}
				else if(t.contains("WORKSHOP")){
				GCMUtils.HandleWorkshop(t, context, dbmngr, intent);
				}
			} else {
				Update u = Utilities.generateUpdateFromExtras(
						intent.getExtras(), context);
				if (u != null && u.getUrlToJason() == null) {
					dbmngr.insertUpdate(u);
					Utilities.showNotification(context, u.getSubject(),
							u.getText());
				} else {
					Utilities.fetchUpdateFromBackEndTask task = new Utilities.fetchUpdateFromBackEndTask(
							context, this);
					task.execute(u.getUpdate_id());
				}
			}
		}
		if (intent.getAction().equals(Settings.WARD_LANDA_ALARM)) {
			Course c = (Course) intent.getSerializableExtra("course");
			String s = String.format("׳�׳§׳•׳� : %s \n ׳©׳¢׳” : %s",
					c.getPlace(), c.getTimeFrom());
			Utilities.showNotification(context, c.getName(), s);
		}

	}

	@Override
	public void onPostUpdateDownloaded(Update u) {
		if (u != null && cxt != null) {
			dbmngr.updateUpdate(u);
			Utilities.showNotification(cxt, u.getSubject(), u.getText());
		}

	}

}
