package com.vass.reindex;

import com.liferay.portal.kernel.util.StringPool;

public class ReindexForm {

	private boolean success = false;
	private boolean executed = false;
	private long executedGroup = -1;
	private String errMsg = StringPool.BLANK;
	
	// ----
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isExecuted() {
		return executed;
	}
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	public long getExecutedGroup() {
		return executedGroup;
	}
	public void setExecutedGroup(long executedGroup) {
		this.executedGroup = executedGroup;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}
