package com.vass.search;


public class WebContentPage {

	private int page;
	private String name;
	private boolean disabled;
	
	// ---
	
	public WebContentPage(int page, String name, boolean disabled) {
		this.page = page;
		this.name = name;
		this.disabled = disabled;
	}
	
	public WebContentPage() {
	}
	
	// ---
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
}
