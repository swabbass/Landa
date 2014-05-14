package utilites;

import ward.landa.Teacher;
import ward.landa.Update;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
	public static final String DB_NAME = "db_LANDA";// הניתונים מסד שם
	public static final int DB_VER = 2;// הנתונים מס של הגרסה

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
			String sql, sql2;
			sql = String.format("drop table if exists %s;",
					dbTeacher.TEACHERS_TABLE);
			sql2 = String.format("drop table if exists %s;",
					dbUpdate.UPDATES_TABLE);
			db.execSQL(sql);
			db.execSQL(dbTeacher.CREATE);
			db.execSQL(sql2);
			db.execSQL(dbUpdate.CREATE);

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
		values.put(dbTeacher.ROLE, teacher.getPosition());
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

	public boolean updateTeacher(Teacher teacher) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(dbTeacher.ID_NUMBER, teacher.getId_number());
		values.put(dbTeacher.FIRST_NAME, teacher.getName());
		values.put(dbTeacher.LAST_NAME, teacher.getLast_name());
		values.put(dbTeacher.EMAIL, teacher.getEmail());
		values.put(dbTeacher.ROLE, teacher.getPosition());
		values.put(dbTeacher.IMAGE_URL, teacher.getImageUrl());
		values.put(dbTeacher.LOCAL_IMAGE_PATH, teacher.getImageLocalPath());
		boolean res = database.update(DB_NAME, values, dbTeacher.ID_NUMBER
				+ " = " + getSQLText(teacher.getId_number()), null) > 0;
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
		boolean res = database.update(DB_NAME, values, dbTeacher.ID_NUMBER
				+ " = " + getSQLText(update.getUpdate_id()), null) > 0;
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

	public Cursor getCursorAllTeachers() {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbTeacher.TEACHERS_TABLE, new String[] {
				dbTeacher.ID_NUMBER, dbTeacher.FIRST_NAME, dbTeacher.LAST_NAME,
				dbTeacher.ROLE, dbTeacher.EMAIL, dbTeacher.IMAGE_URL,
				dbTeacher.LOCAL_IMAGE_PATH }, null, null, null, null, null);
		return cursor;
	}

	public Cursor getCursorAllUpdates() {
		Cursor cursor;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		cursor = database.query(dbUpdate.UPDATES_TABLE, new String[] {
				dbUpdate.UPDATE_ID, dbUpdate.UPDATE_SUBJECT,
				dbUpdate.UPDATE_CONTENT, dbUpdate.UPDATE_DATE,
				dbUpdate.UPDATE_URL }, null, null, null, null, null);
		return cursor;
	}

}

class dbTeacher {
	public static final String TEACHERS_TABLE = "Teachers";// הטבלה שם
	public static final String UID = "id";
	public static final String ID_NUMBER = "id_number";
	public static final String FIRST_NAME = "first_name";// המשימה של הטקסט
	public static final String LAST_NAME = "last_name";// הטלפון מספר
	public static final String EMAIL = "email";
	public static final String IMAGE_URL = "image_url";
	public static final String ROLE = "role";
	public static final String LOCAL_IMAGE_PATH = "local_img_path";

	public static final String CREATE = "create table " + TEACHERS_TABLE + " ("
			+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ID_NUMBER
			+ " text not null, " + FIRST_NAME + " text not null, " + LAST_NAME
			+ " text not null, " + EMAIL + " text not null, " + ROLE
			+ " text not null, " + IMAGE_URL + " text not null, "
			+ LOCAL_IMAGE_PATH + "text not null" + ");";
}

class dbUpdate {
	public static final String UPDATES_TABLE = "updates";// הטבלה שם
	public static final String UID = "id";
	public static final String UPDATE_ID = "subject_id";
	public static final String UPDATE_SUBJECT = "subject";
	public static final String UPDATE_CONTENT = "content";// המשימה של הטקסט
	public static final String UPDATE_DATE = "date";// הטלפון מספר
	public static final String UPDATE_URL = "url";
	public final static String CREATE = "create table " + UPDATES_TABLE + " ("
			+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ UPDATE_ID
			+ " text not null, "  + UPDATE_SUBJECT
			+ " text not null, " + UPDATE_CONTENT + " text not null, "
			+ UPDATE_DATE + " text not null, " + UPDATE_URL + " text not null"
			+ ");";
}