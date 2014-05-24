package ward.landa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2267973230480147607L;
	private String name;
	private String day;
	private String timeFrom;
	private String timeTo;
	private String place;
	private String Teacher;
	private int imgID;
	private int courseID;
	private long course_db_id;
	private float rating;
	private String tutor_id;
	private int notify;
	
	public Course(String name,String day,String timeFrom,String timeTo,String place)
	{
		this.name=name;
		this.day=day;
		this.timeFrom=timeFrom;
		this.timeTo=timeTo;
		this.place=place;
		this.notify=1;
	}
	//private List<Teacher> teachers;
	private int toShow;
	public Course(int courseID,String name ,String dateString,String teacher,int imgId,float rate) {
		setCourseID(courseID);
		setName(name);
		setDateTime(dateString);
		setTeacher(teacher);
		setImgID(imgId);
		setRating(rate);
		setToShow(1);
	//	teachers=new ArrayList<Teacher>();
	
	}
	public Course(int courseID,String name ,String dateString,String timeFrom,String timeTo,String place,String tutor_id) {
		setCourseID(courseID);
		setName(name);
		setDateTime(dateString);
		setTimeFrom(timeFrom);
		setTimeTo(timeTo);
		setPlace(place);
		setImgID(0);
		setRating(0);
		setTutor_id(tutor_id);
	//	teachers=new ArrayList<Teacher>();
	
	}
	/*public void addTeacher(Teacher t)
	{
		if(t!=null)
		{
			for(Teacher tmp :teachers)
			{
				if(tmp.equals(t))
				{
					return ;
				}
			}
			teachers.add(t);
		}
	}
	public void removeTeacher(Teacher t)
	{

		if(teachers.contains(t))
		{
			teachers.remove(t);
		}
	}*/
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof Course)
		{
			Course tmp=(Course)o;
			return tmp.getName().equals(this.getName());
		}
		return false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDateTime() {
		return day;
	}
	public void setDateTime(String dateTime) {
		this.day = dateTime;
	}
	public String getTeacher() {
		return Teacher;
	}
	public void setTeacher(String teacher) {
		Teacher = teacher;
	}
	public int getImgID() {
		return imgID;
	}
	public void setImgID(int imgID) {
		this.imgID = imgID;
	}
	public int getCourseID() {
		return courseID;
	}
	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public int getToShow() {
		return toShow;
	}
	public void setToShow(int toShow) {
		this.toShow = toShow;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}
	public String getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}
	public String getTutor_id() {
		return tutor_id;
	}
	public void setTutor_id(String tutor_id) {
		this.tutor_id = tutor_id;
	}
	public int getNotify() {
		return notify;
	}
	public void setNotify(int notify) {
		this.notify = notify;
	}
	public long getCourse_db_id() {
		return course_db_id;
	}
	public void setCourse_db_id(long course_db_id) {
		this.course_db_id = course_db_id;
	}
	
	
	
}
