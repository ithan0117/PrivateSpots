package idv.william.privatespots.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class Spot implements Serializable {
	private Integer id;
	private String title;
	private Map<String, String> images;
	private String desc;
	private String createdDate;
	private double lat;
	private double lng;

	public Spot() {
	}

	public Spot(String title, Map<String, String> images, String desc, String createdDate) {
		this.title = title;
		this.images = images;
		this.desc = desc;
		this.createdDate = createdDate;
	}

	public Spot(Integer id, String title, Map<String, String> images, String desc, String createdDate) {
		this.id = id;
		this.title = title;
		this.images = images;
		this.desc = desc;
		this.createdDate = createdDate;
	}

	public Spot(Integer id, String title, Map<String, String> images, String desc, String createdDate, double lat, double lng) {
		this.id = id;
		this.title = title;
		this.images = images;
		this.desc = desc;
		this.createdDate = createdDate;
		this.lat = lat;
		this.lng = lng;
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

	public Map<String, String> getImages() {
		return images;
	}

	public void setImages(Map<String, String> images) {
		this.images = images;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
