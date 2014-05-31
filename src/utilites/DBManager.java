package utilites;

import java.util.ArrayList;
import java.util.List;

import ward.landa.Course;
import ward.landa.R;
import ward.landa.Teacher;
import ward.landa.Update;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
	public static final String DB_NAME = "db_LANDA";// ׳”׳ ׳™׳×׳•׳ ׳™׳� ׳�׳¡׳“
													// ׳©׳�
	public static final int DB_VER = 9;// ׳”׳ ׳×׳•׳ ׳™׳� ׳�׳¡ ׳©׳� ׳”׳’׳¨׳¡׳”

	DB_HELPER dbHelper;
	Context cxt;

	public DBManager(Context context) {
		this.cxt = context;
		this.dbHelper = new DB_HELPER(context, DB_NAME, null, DB_VER);
	}

	class DB_HELPER extends SQLiteOpenHelper {

		public DB_HELPER(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, null, version);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String sql, sql2, sql3;
			sql = String.format("drop table if exists %s;",
					dbTeacher.TEACHERS_TABLE);
			sql2 = String.format("drop table if exists %s;",
					dbUpdate.UPDATES_TABLE);
			sql3 = String.format("drop table if exists %s;",
					dbCourse.COURSE_TABLE);
			db.execSQL(sql);
			db.execSQL(dbTeacher.CREATE);
			db.execSQL(sql2);
			db.execSQL(dbUpdate.CREATE);
			db.execSQL(sql3);
			db.execSQL(dbCourse.CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			onCreate(db);

		}

	}

	public long insertTeacher(Teacher teacher) {
		SQLiteDatabase teacher_db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbTeacher.ID_NUMBER, teacher.getId_number());
		values.put(dbTeacher.FIRST_NAME, teacher.getName());
		values.put(dbTeacher.LAST_NAME, teacher.getLast_name());
		values.put(dbTeacher.EMAIL, teacher.getEmail());
		values.put(dbTeacher.FACULTY, teacher.getFaculty());
		values.put(dbTeacher.ROLE, teacher.getPosition());
		values.put(dbTeacher.DOWNLOADED_IMAGE,
				Boolean.toString(teacher.isDownloadedImage()));
		values.put(dbTeacher.IMAGE_URL, teacher.getImageUrl());
		values.put(dbTeacher.LOCAL_IMAGE_PATH, teacher.getImageLocalPath());
		long id = teacher_db.insert(dbTeacher.TEACHERS_TABLE, null, values);
		teacher_db.close();
		return id;
	}

	public long insertUpdate(Update update) {
		SQLiteDatabase updated_db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbUpdate.UPDATE_ID, update.getUpdate_id());
		values.put(dbUpdate.UPDATE_SUBJECT, update.getSubject());
		values.put(dbUpdate.UPDATE_CONTENT, update.getText());
		values.put(dbUpdate.UPDATE_DATE, update.getDateTime());
		values.put(dbUpdate.UPDATE_URL, update.getUrl());
		long id = updated_db.insert(dbUpdate.UPDATES_TABLE, null, values);
		updated_db.close();
		return id;
	}

	public long insertCourse(Course course) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbCourse.COURSE_ID, course.getCourseID());
		values.put(dbCourse.TEACHER_ID, course.getTutor_id());
		values.put(dbCourse.COURSE_NAME, removeQoutes(course.getName()));
		values.put(dbCourse.COURSE_PLACE, course.getPlace());
		values.put(dbCourse.COURSE_DAY, course.getDateTime());
		values.put(dbCourse.COURSE_TIME_FROM, course.getTimeFrom());
		values.put(dbCourse.COURSE_TIME_TO, course.getTimeTo());
		values.put(dbCourse.NOTIFIED, 0);
		long id = db.insert(dbCourse.COURSE_TABLE, null, values);
		db.close();
		return id;
	}

	public boolean UpdateCourse(Course course, int notify) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbCourse.NOTIFIED, notify);
		boolean res = db.update(dbCourse.COURSE_TABLE, values,
				dbCourse.COURSE_ID + " = " + getSQLText(Integer.toString(course.getCourseID())), null) > 0;
		db.close();
		return res;
	}

	public boolean updateTeacher(Teacher teacher) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbTeacher.ID_NUMBER, teacher.getId_number());
		values.put(dbTeacher.FIRST_NAME, teacher.getName());
		values.put(dbTeacher.LAST_NAME, teacher.getLast_name());
		values.put(dbTeacher.EMAIL, teacher.getEmail());
		values.put(dbTeacher.ROLE, teacher.getPosition());
		values.put(dbTeacher.IMAGE_URL, teacher.getImageUrl());
		values.put(dbTeacher.DOWNLOADED_IMAGE,
				Boolean.toString((teacher.isDownloadedImage())));
		values.put(dbTeacher.LOCAL_IMAGE_PATH, teacher.getImageLocalPath());
		boolean res = database.update(dbTeacher.TEACHERS_TABLE, values,
				dbTeacher.ID_NUMBER + " = "
						+ getSQLText(teacher.getId_number()), null) > 0;
		database.close();
		return res;
	}

	public boolean updateUpdate(Update update) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbUpdate.UPDATE_SUBJECT, update.getSubject());
		values.put(dbUpdate.UPDATE_CONTENT, update.getText());
		values.put(dbUpdate.UPDATE_DATE, update.getDateTime());
		values.put(dbUpdate.UPDATE_URL, update.getUrl());
		boolean res = database.update(dbUpdate.UPDATES_TABLE, values,
				dbUpdate.UPDATE_ID + " = " + getSQLText(update.getUpdate_id()),
				null) > 0;
		database.close();
		return res;
	}

	public boolean deleteTeacher(Teacher teacher) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		boolean b = database.delete(dbTeacher.TEACHERS_TABLE,
				dbTeacher.ID_NUMBER + "=" + getSQLText(teacher.getId_number()),
				null) > 0;
		database.close();
		return b;
	}

	public boolean deleteUpdate(Update update) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		boolean b = database.delete(dbUpdate.UPDATES_TABLE, dbUpdate.UPDATE_ID
				+ "=" + getSQLText(update.getUpdate_id()), null) > 0;
		database.close();
		return b;
	}

	public String getSQLText(String text) {
		char c = 34;
		Character ch = Character.valueOf(c);
		return ch.toString() + text + ch.toString();
	}

	public List<Course> getAllNotifiedCourses() {
		Cursor cursor;

		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbCourse.COURSE_TABLE, new String[] {
				dbCourse.COURSE_ID, dbCourse.COURSE_NAME, dbCourse.COURSE_DAY,
				dbCourse.COURSE_TIME_FROM, dbCourse.COURSE_TIME_TO,
				dbCourse.COURSE_PLACE

		}, dbCourse.NOTIFIED + " = " + "1", null, null, null, null);
		List<Course> result = new ArrayList<Course>(cursor.getCount());
		while (cursor.moveToNext()) {
			Course c = new Course(cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4),
					cursor.getString(5));
			c.setCourseID(Integer.parseInt(cursor.getString(0)));
			c.setNotify(1);
			result.add(c);
		}
		database.close();
		return result;
	}

	public List<Teacher> getCursorAllTeachers() {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbTeacher.TEACHERS_TABLE, new String[] {
				dbTeacher.ID_NUMBER, dbTeacher.FIRST_NAME, dbTeacher.LAST_NAME,
				dbTeacher.ROLE, dbTeacher.EMAIL, dbTeacher.FACULTY,
				dbTeacher.IMAGE_URL, dbTeacher.LOCAL_IMAGE_PATH,
				dbTeacher.DOWNLOADED_IMAGE }, null, null, null, null, null);

		List<Teacher> res = new ArrayList<>(cursor.getCount());
		while (cursor.moveToNext()) {
			String id_number = cursor.getString(0);
			String first_name = cursor.getString(1);
			String last_name = cursor.getString(2);
			String role = cursor.getString(3);
			String email = cursor.getString(4);
			String faculty = cursor.getString(5);
			String imageUrl = cursor.getString(6);
			String localImgPath = cursor.getString(7);
			String isDownloaded = cursor.getString(8);
			Teacher t = new Teacher(first_name, last_name, email, id_number,
					role, faculty);
			t.setImageUrl(imageUrl);
			t.setImageLocalPath(localImgPath);
			t.setDownloadedImage(Boolean.valueOf(isDownloaded));
			res.add(t);
		}
		return res;

	}

	public List<Update> getCursorAllUpdates() {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbUpdate.UPDATES_TABLE, new String[] {
				dbUpdate.UPDATE_ID, dbUpdate.UPDATE_SUBJECT,
				dbUpdate.UPDATE_CONTENT, dbUpdate.UPDATE_DATE,
				dbUpdate.UPDATE_URL }, null, null, null, null,
				dbUpdate.UPDATE_DATE + " DESC");
		List<Update> updates = new ArrayList<Update>();
		while (cursor.moveToNext()) {
			Update u = new Update(cursor.getString(0), cursor.getString(1),
					cursor.getString(3), cursor.getString(2));
			u.setUrl(cursor.getString(4));
			updates.add(u);
		}

		return updates;
	}

	public List<Course> getCursorAllWithCourses() {
		List<Course> notified = getAllNotifiedCourses();
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(false, dbCourse.COURSE_TABLE, new String[] {
				dbCourse.COURSE_ID, dbCourse.COURSE_NAME, dbCourse.TEACHER_ID,
				dbCourse.COURSE_PLACE, dbCourse.COURSE_DAY,
				dbCourse.COURSE_TIME_FROM, dbCourse.COURSE_TIME_TO }, null,
				null, dbCourse.COURSE_NAME, null, null, null);
		List<Course> courses = new ArrayList<Course>();
		while (cursor.moveToNext()) {

			Course c = new Course(Integer.parseInt(cursor.getString(0)),
					cursor.getString(1), "", cursor.getString(2),
					R.drawable.ic_error, 0);
			if (notified.contains(c)) {
				c.setNotify(1);
			} else {
				c.setNotify(0);
			}
			courses.add(c);
		}
		database.close();
		return courses;
	}

	public List<Teacher> getTeachersForCourse(String name) {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		List<Teacher> teachers = new ArrayList<Teacher>();
		cursor = database.query(dbCourse.COURSE_TABLE,
				new String[] { dbCourse.TEACHER_ID, dbCourse.COURSE_PLACE,
						dbCourse.COURSE_DAY, dbCourse.COURSE_TIME_FROM,
						dbCourse.COURSE_TIME_TO, dbCourse.NOTIFIED,
						dbCourse.COURSE_ID }, dbCourse.COURSE_NAME + " = "
						+ getSQLText(name), null, null, null, null);
		cursor.moveToNext();
		do {
			String tID = cursor.getString(0);
			Teacher t = getTeacherByIdNumber(tID);
			if (!teachers.contains(t)) {
				teachers.add(t);
			}
			t = teachers.get(teachers.indexOf(t));
			String timeInfo = cursor.getString(1) + " - " + cursor.getString(2)
					+ " - " + cursor.getString(3) + " - " + cursor.getString(4)
					+ " - " + cursor.getString(5) + " - " + cursor.getString(6);
			t.addTimeToCourse(name, timeInfo);
			teachers.remove(t);
			teachers.add(t);
		} while (cursor.moveToNext());
		return teachers;
	}

	public Teacher getTeacherByIdNumber(String idNum) {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbTeacher.TEACHERS_TABLE, new String[] {
				dbTeacher.ID_NUMBER, dbTeacher.FIRST_NAME, dbTeacher.LAST_NAME,
				dbTeacher.FACULTY }, dbTeacher.ID_NUMBER + " = "
				+ getSQLText(idNum), null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToNext();
			Teacher t = new Teacher(cursor.getString(1), cursor.getString(2),
					"", idNum, "T", cursor.getString(3));
			return t;
		} else {
			return null;
		}

	}

	public Course getCourseById(String id) {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbCourse.COURSE_TABLE, new String[] {
				dbCourse.COURSE_ID, dbCourse.COURSE_NAME,
				dbCourse.COURSE_PLACE, dbCourse.COURSE_DAY,
				dbCourse.COURSE_TIME_FROM, dbCourse.COURSE_TIME_TO,
				dbCourse.TEACHER_ID }, dbCourse.COURSE_ID + " = "
				+ getSQLText(id), null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToNext();
			Course c = new Course(cursor.getString(1), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(2));
			c.setCourseID(Integer.parseInt(cursor.getString(0)));
			c.setTutor_id(cursor.getString(6));
			return c;
		} else {
			return null;
		}

	}

	public Cursor getTeachersForCourse(String teacher_id, String course_name) {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbTeacher.TEACHERS_TABLE, new String[] {
				dbTeacher.ID_NUMBER, dbTeacher.FIRST_NAME, dbTeacher.LAST_NAME,
				dbTeacher.EMAIL, dbTeacher.LOCAL_IMAGE_PATH, },
				dbTeacher.ID_NUMBER + " = " + getSQLText(teacher_id), null,
				null, null, null);
		return cursor;
	}

	public static String removeQoutes(String s) {
		return s.replace("\"", "");
	}

}

class dbTeacher {
	public static final String TEACHERS_TABLE = "Teachers";// ׳”׳˜׳‘׳�׳” ׳©׳�
	public static final String UID = "id";
	public static final String ID_NUMBER = "id_number";
	public static final String FIRST_NAME = "first_name";//
	public static final String LAST_NAME = "last_name";// ׳”׳˜׳�׳₪׳•׳� ׳�׳¡׳₪׳¨
	public static final String EMAIL = "email";
	public static final String FACULTY = "faculty";
	public static final String IMAGE_URL = "image_url";
	public static final String ROLE = "role";
	public static final String DOWNLOADED_IMAGE = "cached_img";
	public static final String LOCAL_IMAGE_PATH = "local_img_path";

	public static final String CREATE = "create table " + TEACHERS_TABLE + " ("
			+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ID_NUMBER
			+ " text not null, " + FIRST_NAME + " text not null, " + LAST_NAME
			+ " text not null, " + EMAIL + " text not null, " + FACULTY
			+ " text not null, " + ROLE + " text not null, " + DOWNLOADED_IMAGE
			+ " text not null, " + IMAGE_URL + " text not null, "
			+ LOCAL_IMAGE_PATH + " text not null " + ");";
	public static String ROLE_TEACHER = "T";
	public static String ROLE_INSTRUCTOR = "I";

}

class dbCourse {
	public static final String COURSE_TABLE = "Courses";
	public static final String UID = "id";
	public static final String COURSE_ID = "course_id";
	public static final String TEACHER_ID = "id_number";
	public static final String COURSE_NAME = "course_name";
	public static final String COURSE_DAY = "course_day";
	public static final String NOTIFIED = "notified";
	public static final String COURSE_PLACE = "course_place";
	public static final String COURSE_DESCRIPTION = "course_description";
	public static final String COURSE_TIME_FROM = "course_time_from";
	public static final String COURSE_TIME_TO = "course_time_to";
	public static final String CREATE = "create table " + COURSE_TABLE + " ("
			+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEACHER_ID
			+ " text not null, " + COURSE_ID + " text not null, " + COURSE_NAME
			+ " text not null, " + COURSE_DAY + " text not null, "
			+ COURSE_PLACE + " text not null, " + COURSE_TIME_FROM
			+ " text not null, " + COURSE_TIME_TO + " text not null, "
			+ NOTIFIED + " NUMERIC not null " + " );";
}

class dbUpdate {
	public static final String UPDATES_TABLE = "updates";// ׳”׳˜׳‘׳�׳” ׳©׳�
	public static final String UID = "id";
	public static final String UPDATE_ID = "subject_id";
	public static final String UPDATE_SUBJECT = "subject";
	public static final String UPDATE_CONTENT = "content";// ׳”׳�׳©׳™׳�׳” ׳©׳�
															// ׳”׳˜׳§׳¡׳˜
	public static final String UPDATE_DATE = "date";// ׳”׳˜׳�׳₪׳•׳� ׳�׳¡׳₪׳¨
	public static final String UPDATE_URL = "url";
	public final static String CREATE = "create table " + UPDATES_TABLE + " ("
			+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + UPDATE_ID
			+ " text not null, " + UPDATE_SUBJECT + " text not null, "
			+ UPDATE_CONTENT + " text not null, " + UPDATE_DATE
			+ " text not null, " + UPDATE_URL + " text not null" + ");";
}
