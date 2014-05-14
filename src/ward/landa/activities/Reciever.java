package ward.landa.activities;

import java.util.Set;

import utilites.DBManager;
import ward.landa.Update;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Reciever extends BroadcastReceiver {
	DBManager dbmngr;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("wordpress", ""+intent.getAction());
		Set<String >keys=	intent.getExtras().keySet();
	
		if(intent.getAction().toString().compareTo("com.google.android.c2dm.intent.RECEIVE")==0)
		{
			dbmngr=new DBManager(context);
			String title=intent.getExtras().getString(Settings.EXTRA_TITLE);
			String msg=intent.getExtras().getString(Settings.EXTRA_MESSAGE);
			String date=Settings.getexactTime();
			Update u=new Update("id", title, date, msg);
			u.setUrl("url");
			dbmngr.insertUpdate(u);
			Utilities.displayMessage(context, msg, date, title, Settings.DISPLAY_MESSAGE_ACTION);
		}
		for(String key : keys )
		{
			Log.d("wordpress", key +" : "+intent.getExtras().getString(key) );
		}

	}

}
