package com.vass.search;

import java.util.List;

public class WebContentPagination {
	
	private List<Integer> deltas;
	private String currPos;
	private int total;
	private boolean isFirst;
	private boolean isLast;
	private List<WebContentPage> pageList;
	private int start;
	private int end;
	private int numPages;
	private String totalPages;
	private int delta;
	private int page;
	
	// -----
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<Integer> getDeltas() {
		return deltas;
	}
	public void setDeltas(List<Integer> deltas) {
		this.deltas = deltas;
	}
	public String getCurrPos() {
		return currPos;
	}
	public void setCurrPos(String currPos) {
		this.currPos = currPos;
	}
	public boolean isFirst() {
		return isFirst;
	}
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	public boolean isLast() {
		return isLast;
	}
	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
	public List<WebContentPage> getPageList() {
		return pageList;
	}
	public void setPageList(List<WebContentPage> pageList) {
		this.pageList = pageList;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getNumPages() {
		return numPages;
	}
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	public int getDelta() {
		return delta;
	}
	public void setDelta(int delta) {
		this.delta = delta;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(String totalPages) {
		this.totalPages = totalPages;
	}
	
}
