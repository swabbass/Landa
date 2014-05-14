package ward.landa;

import java.util.HashMap;

public class Teacher {

	private int ID;
	private int imgId;
	private String imageUrl;
	private String imageLocalPath;
	private String last_name;
	private String id_number;
	private String first_name;
	private String email;
	private String position;
	private String faculty;
	public HashMap<String, Course> getCourses() {
		return courses;
	}

	public void setCourses(HashMap<String, Course> courses) {
		this.courses = courses;
	}

	private HashMap<String, Course> courses;
	public Teacher(int ID, int imgId, String name, String email, String phone,
			String pos, String faculty) {
		setID(ID);
		setImgId(imgId);
		setName(name);
		setEmail(email);
		setPosition(pos);
		setFaculty(faculty);
		courses = new HashMap<String, Course>();
	}
	public Teacher(String fname,String lname, String email, String id_number,
			String pos, String faculty) {

		setId_number(id_number);
		setName(fname);
		setLast_name(lname);
		setEmail(email);
		setPosition(pos);
		setFaculty(faculty);
		setImageUrl("http://nlanda.technion.ac.il/LandaSystem/pics/"+id_number+".jpg");
		courses = new HashMap<String, Course>();
	}
	public HashMap<String, String> getCourseDetailsToShow(String courseName)
	{
		Course c=getCourses().get(courseName);
		if(c!=null){
		HashMap< String, String> details=new HashMap<String, String>(4);
		details.put("day", c.getDateTime());
		details.put("from",c.getTimeFrom());
		details.put("to", c.getTimeTo());
		details.put("place", c.getPlace());
		return details;
		}
		else
		{
			throw new NullPointerException("Null Course");
		}		
	}
	public void addCourse(Course c) {
		if (courses != null) {
			courses.put(c.getName(), c);
		}
	}

	public void removeCourse(Course c) {
		if (courses.containsValue(c)) {
			courses.remove(c);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getName() {
		return first_name;
	}

	public void setName(String name) {
		this.first_name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageLocalPath() {
		return imageLocalPath;
	}

	public void setImageLocalPath(String imageLocalPath) {
		this.imageLocalPath = imageLocalPath;
	}
}
