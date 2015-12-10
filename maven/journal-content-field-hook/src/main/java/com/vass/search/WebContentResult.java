package com.vass.search;


public class WebContentResult {

	private String contentId;
	private String title;
	private String urlTitle;
	private Long groupId;
	private Long strucutreId;
	private String groupName;
	private String strucutreName;
	private String user;
	private String modifiedDateStr;
	
	// ------
	
	public WebContentResult(String contentId, String title, String urlTitle, Long groupId,
			Long strucutreId, String groupName, String strucutreName,
			String user, String modifiedDateStr) {
		this.contentId = contentId;
		this.title = title;
		this.urlTitle = urlTitle;
		this.groupId = groupId;
		this.strucutreId = strucutreId;
		this.groupName = groupName;
		this.strucutreName = strucutreName;
		this.user = user;
		this.modifiedDateStr = modifiedDateStr;
	}
	
	// BASE METHODS
	
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStrucutreName() {
		return strucutreName;
	}
	public void setStrucutreName(String strucutreName) {
		this.strucutreName = strucutreName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Long getStrucutreId() {
		return strucutreId;
	}
	public void setStrucutreId(Long strucutreId) {
		this.strucutreId = strucutreId;
	}
	public String getModifiedDateStr() {
		return modifiedDateStr;
	}
	public void setModifiedDateStr(String modifiedDateStr) {
		this.modifiedDateStr = modifiedDateStr;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public long getGroupId() {
		return this.groupId;
	}
	public String getUrlTitle() {
		return urlTitle;
	}
	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}
	
}
