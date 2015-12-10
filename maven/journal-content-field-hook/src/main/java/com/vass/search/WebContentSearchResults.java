package com.vass.search;

import java.util.List;

public class WebContentSearchResults {
	
	private List<WebContentResult> results;
	private int total;
	
	// -----
	
	public WebContentSearchResults(List<WebContentResult> results, int total) {
		super();
		this.results = results;
		this.total = total;
	}
	
	// -----
	
	public List<WebContentResult> getResults() {
		return results;
	}
	public void setResults(List<WebContentResult> results) {
		this.results = results;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
}
