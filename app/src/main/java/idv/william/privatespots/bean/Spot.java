package idv.william.privatespots.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Spot implements Serializable {
	private Integer id;
	private String title;
	private List<String> images;
	private String content;
	private Timestamp createdDateTime;

	public Spot() {
	}

	public Spot(Integer id, String title, List<String> images, String content, Timestamp createdDateTime) {
		this.id = id;
		this.title = title;
		this.images = images;
		this.content = content;
		this.createdDateTime = createdDateTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
}
