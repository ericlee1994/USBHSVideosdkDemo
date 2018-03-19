package com.shgbit.android.heyshareuvc.json;

public class ReserveMeetingInfo {
	private String meetingId;
	private String meetingName;
	private String startTime;
	private String endTime;
	private String[] invitedUsers;
	private String sessionId;
	private String sessionType;
	
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getMeetingName() {
		return meetingName;
	}
	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String[] getInvitedUsers() {
		return invitedUsers;
	}
	public void setInvitedUsers(String[] invitedUsers) {
		this.invitedUsers = invitedUsers;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
}
