package ward.landa;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import utilites.DBManager;
import ward.landa.activities.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GCMUtils {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String DATA = "data";
	public static final String SENDER_ID = "498258787681";
	public static final String REGSITER = "isReg";
	public static final String LOAD_TEACHERS = "load_teachers";
	public static final String LOAD_UPDATES = "load_updates";
	public static final String LOAD_COURSES = "load_courses";
	public static final String REG_KEY = "REGKEY";
	public static final String URL = "http://wabbass.byethost9.com/wordpress/";
	public static final String TAG = "wordpress";
	public static final String NLANDA_GCM_REG = "http://nlanda.technion.ac.il/LandaSystem/registerGcm.aspx";

	public static String sendRegistrationIdToBackend(String regKey) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL + "/?regId=" + regKey);
		HttpPost httppostNlanda = new HttpPost(NLANDA_GCM_REG + "?reg_id="
				+ regKey);
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpResponse resNlanda = httpclient.execute(httppostNlanda);
			Log.d(TAG, EntityUtils.toString(response.getEntity()));
			Log.d(TAG, EntityUtils.toString(resNlanda.getEntity()));
			return "";

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return null;
	}

	public static Teacher HandleInstructor(String action, Context cxt,
			DBManager dbmngr, Intent intent)

	{
		Teacher tmp = new Teacher(intent.getStringExtra("fname"),
				intent.getStringExtra("lname"),
				intent.getStringExtra("email"),
				intent.getStringExtra("id"), "T",
				intent.getStringExtra("faculty"));
		if (action.equals("INSTRUCTOR")) {
			if (dbmngr.getTeacherByIdNumber(tmp.getId_number()) == null) {
				tmp.setDownloadedImage(false);
				dbmngr.insertTeacher(tmp);
				Utilities.showNotification(cxt, "new Teacher", tmp.getName()
						+ " " + tmp.getLast_name());

			} else {

				dbmngr.updateTeacher(tmp);
				Utilities.showNotification(cxt, "Teacher Updated",
						tmp.getName() + " " + tmp.getLast_name());
			}

		}else if(action.equals("RINSTRUCTOR"))
		{
			
			dbmngr.deleteTeacher(tmp);
			Utilities.showNotification(cxt, "Teacher Deleted", tmp.getName()+" "+tmp.getLast_name());
		}
		return tmp;
	}
	public static Course HandleWorkshop(String action, Context cxt,
			DBManager dbmngr, Intent intent)
	{
		Course cTmp=new Course(intent.getStringExtra("subject_name")
				, intent.getStringExtra("day"),
				intent.getStringExtra("time_from"), 
				intent.getStringExtra("time_to"),
				intent.getStringExtra("place"));
		cTmp.setCourseID(Integer.parseInt(intent.getStringExtra("id")));
		cTmp.setTutor_id(intent.getStringExtra("tutor_id"));
		if(action.equals("WORKSHOP")){
			if(dbmngr.getCourseById(Integer.toString(cTmp.getCourseID()))==null)
			{
				dbmngr.insertCourse(cTmp);
				Utilities.showNotification(cxt, "Course Added",
						cTmp.getName() + " ");
			}
			else{
				dbmngr.UpdateCourse(cTmp, 0);
				Utilities.showNotification(cxt, "Course Updated",
						cTmp.getName() + " ");
			}
		}
		else if(action.equals("RWORKSHOP"))
		{
			if(dbmngr.deleteCourse(cTmp))
			{
				Utilities.showNotification(cxt, "Course Removed",
						cTmp.getName() + " ");
			}
		}
		return cTmp;
	}

}
