package ward.landa;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import utils.Utilities;

import android.text.Html;

public class Update implements Serializable, Comparable<Update> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4704709704844438730L;
	private String text;
	private String update_id;
	private boolean active;
	private String subject;
	private String dateTime;
	private String url;
	private String urlToJason;
	private boolean pinned;
	private boolean popUpOpend;

	public Update(String subject, String dateTime, String text) {
		setSubject(subject);
		setDateTime(dateTime);
		setText(text);

	}

	public Update(String id, String urlToJSon) {
		this.update_id = id;
		this.setUrlToJason(urlToJSon);
	}

	public Update(String id, String subject, String dateTime, String text,
			boolean pinned) {
		setUpdate_id(id);
		setSubject(subject);
		setDateTime(dateTime);
		setText(text);
		this.pinned = false;
		this.setPopUpOpend(false);

	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Update) {
			Update t = (Update) o;
			return t.update_id.equals(update_id);
		}
		return false;
	}

	public String getText() {
		return text;
	}

	public boolean isToobig() {
		return text.length() > 300;
	}

	public void setText(String text) {
		String tmp = Utilities.removeTableFirstTrHtml(text);
		tmp=Utilities.html2Text(tmp == null ? text : tmp);
		this.text =tmp ;

		// Html.fromHtml(text).toString();
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

		String mamn = Jsoup.parse(subject).text();
		this.subject = Utilities.replacer(new StringBuffer(mamn));

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

	public String getUrlToJason() {
		return urlToJason;
	}

	public void setUrlToJason(String urlToJason) {
		this.urlToJason = urlToJason;
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	@Override
	public int compareTo(Update another) {
		if (isPinned() && !another.isPinned()) {
			return -1;
		} else if (!isPinned() && another.isPinned()) {
			return 1;
		} else
			return another.getDateTime().compareTo(dateTime);
	}

	public boolean isPopUpOpend() {
		return popUpOpend;
	}

	public void setPopUpOpend(boolean popUpOpend) {
		this.popUpOpend = popUpOpend;
	}

}
