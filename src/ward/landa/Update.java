package ward.landa;

public class Update {
	private String text;
	private String update_id;
	private boolean active;
	private String subject;
	private String dateTime;
	private String url;

	public Update(String subject, String dateTime, String text) {
		setSubject(subject);
		setDateTime(dateTime);
		setText(text);

	}

	public Update(String id, String subject, String dateTime, String text) {
		setUpdate_id(id);
		setSubject(subject);
		setDateTime(dateTime);
		setText(text);

	}

	public String getText() {
		return text;
	}

	public String getSmallText() {
		if (text.length() > 200) {
			for (int i = 200; i < text.length(); i++) {
				if (text.charAt(i) == ' ')
					return new String(text.substring(0, i) + "...");
			}
		}
		return text;
	}

	public boolean isToobig() {
		return text.length() > 300;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUpdate_id() {
		return update_id;
	}

	public void setUpdate_id(String update_id) {
		this.update_id = update_id;
	}

}
