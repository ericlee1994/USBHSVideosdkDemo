package com.shgbit.android.heysharevideo.json;

public class InviteMeetingInfo {
	private String meetingId;
	private String[] invitedUsers;
	private String userName;
	private String sessionId;
	private String sessionType;
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String[] getInvitedUsers() {
		return invitedUsers;
	}
	public void setInvitedUsers(String[] invitedUsers) {
		this.invitedUsers = invitedUsers;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
