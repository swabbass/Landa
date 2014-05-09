package ward.landa;

public class Event {
	private int id;
	private String info;
	private String smallInfo;
	private String dateTime;
	private String place;

	public Event(String info, String smallinfo, String dateTime, String place,
			int id) {
		setSmallInfo(smallinfo);
		setInfo(info);
		setPlace(place);
		setDateTime(dateTime);
		setId(id);

	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Event) {
			Event e = (Event) o;
			return e.getId() == this.getId();
		}
		return false;
	}

	@Override
	public String toString() {

		return getInfo() + "\n" + getDateTime() + "\n" + getPlace();
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getSmallInfo() {
		return smallInfo;
	}

	public void setSmallInfo(String smallInfo) {
		this.smallInfo = smallInfo;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
