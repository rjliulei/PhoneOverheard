package cn.linving.girls.bean;

import java.io.Serializable;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * 用于收藏
 * 
 * @author ving
 *
 */

@DatabaseTable(tableName = "RowImage")
public class RowImage implements Serializable {
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String desc;
	private List<String> tags;
	@DatabaseField
	private String fromPageTitle;
	@DatabaseField
	private String column;
	@DatabaseField
	private String parentTag;
	@DatabaseField
	private String date;
	@DatabaseField
	private String downloadUrl;
	@DatabaseField
	private String imageUrl;
	@DatabaseField
	private int imageWidth;
	@DatabaseField
	private int imageHeight;
	@DatabaseField
	private String thumbnailUrl;
	@DatabaseField
	private int thumbnailWidth;
	@DatabaseField
	private int thumbnailHeight;
	@DatabaseField
	private String thumbLargeUrl;
	@DatabaseField
	private int thumbLargeWidth;
	@DatabaseField
	private int thumbLargeHeight;
	@DatabaseField
	private String albumName;
	@DatabaseField
	private String objTag;
	@DatabaseField
	private String title;
	@DatabaseField
	private String fromUrl;

	/**
	 * ����ר�����
	 * 
	 * @return
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * ��������
	 * 
	 * @return
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * ʱ��
	 * 
	 * @return
	 */
	public String getDate() {
		return date;
	}

	public String getDesc() {
		return desc;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public String getFromPageTitle() {
		return fromPageTitle;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public String getId() {
		return id;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public String getObjTag() {
		return objTag;
	}

	public String getParentTag() {
		return parentTag;
	}

	public List<String> getTags() {
		return tags;
	}

	public int getThumbLargeHeight() {
		return thumbLargeHeight;
	}

	public String getThumbLargeUrl() {
		return thumbLargeUrl;
	}

	public int getThumbLargeWidth() {
		return thumbLargeWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public void setFromPageTitle(String fromPageTitle) {
		this.fromPageTitle = fromPageTitle;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	/**
	 * ID
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setObjTag(String objTag) {
		this.objTag = objTag;
	}

	public void setParentTag(String parentTag) {
		this.parentTag = parentTag;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setThumbLargeHeight(int thumbLargeHeight) {
		this.thumbLargeHeight = thumbLargeHeight;
	}

	public void setThumbLargeUrl(String thumbLargeUrl) {
		this.thumbLargeUrl = thumbLargeUrl;
	}

	public void setThumbLargeWidth(int thumbLargeWidth) {
		this.thumbLargeWidth = thumbLargeWidth;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "RowImage [id=" + id + ", desc=" + desc + ", tags=" + tags
				+ ", fromPageTitle=" + fromPageTitle + ", column=" + column
				+ ", parentTag=" + parentTag + ", date=" + date
				+ ", downloadUrl=" + downloadUrl + ", imageUrl=" + imageUrl
				+ ", imageWidth=" + imageWidth + ", imageHeight=" + imageHeight
				+ ", thumbnailUrl=" + thumbnailUrl + ", thumbnailWidth="
				+ thumbnailWidth + ", thumbnailHeight=" + thumbnailHeight
				+ ", thumbLargeUrl=" + thumbLargeUrl + ", thumbLargeWidth="
				+ thumbLargeWidth + ", thumbLargeHeight=" + thumbLargeHeight
				+ ", albumName=" + albumName + ", objTag=" + objTag
				+ ", title=" + title + ", fromUrl=" + fromUrl + "]";
	}

}
