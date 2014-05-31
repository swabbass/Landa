package ward.landa;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class GCMUtils {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String DATA = "data";
	public static final String SENDER_ID = "498258787681";
	public static final String REGSITER = "isReg";
	public static final String LOAD_TEACHERS="load_teachers";
	public static final String LOAD_UPDATES="load_updates";
	public static final String LOAD_COURSES="load_courses";
	public static final String REG_KEY = "REGKEY";
	public static final String URL = "http://wabbass.byethost9.com/wordpress/";
	public static final String TAG = "wordpress";
	public static final String NLANDA_GCM_REG="http://nlanda.technion.ac.il/LandaSystem/registerGcm.aspx";

	public static  String sendRegistrationIdToBackend(String regKey) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL + "/?regId=" + regKey);
		HttpPost httppostNlanda = new HttpPost(NLANDA_GCM_REG + "?reg_id=" + regKey);
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpResponse resNlanda=httpclient.execute(httppostNlanda);
			Log.d(TAG, EntityUtils.toString(response.getEntity()));
			Log.d(TAG, EntityUtils.toString(resNlanda.getEntity()));
		return  "";

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		
		return null;
	}


}
