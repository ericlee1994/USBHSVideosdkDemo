package com.shgbit.android.heyshareuvc.interactmanager;

import android.text.format.Time;
import android.util.Log;

import com.shgbit.android.heysharevideo.json.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MeetingCeche {
	private final String TAG = "MeetingCeche";
	private static MeetingCeche instance;
	
	private ArrayList<Meeting> mMeetings;
	
	public MeetingCeche() {
	}
	
	public static MeetingCeche getInstance () {
		if (instance == null) {
			instance = new MeetingCeche();
		}
		return instance;
	}
	
	public void Finalize () {
		mMeetings = null;
	}
	
	public void setMeetings (Meeting[] meetings) {
		if (meetings == null || meetings.length <= 0) {
			mMeetings = null;
			return;
		}
		
		Arrays.sort(meetings,new ComparatorTime());

		if (mMeetings == null) {
			mMeetings = new ArrayList<>();
		} else {
			mMeetings.clear();
		}

		for (int i = 0; i < meetings.length; i++) {
			if (meetings[i] == null) {
				continue;
			}
			if (meetings[i].getStatus().equalsIgnoreCase("end") == true) {
				continue;
			} else {
				mMeetings.add(meetings[i]);
			}
		}
	}
	
	public Meeting getMeeting (String meetingId) {
		if (meetingId == null || meetingId.equals("")) {
			return null;
		}
		
		if (mMeetings == null || mMeetings.size() <= 0) {
			return null;
		}
		
		try {
			for (Meeting meeting : mMeetings) {
				if (meeting.getMeetingId().equals(meetingId)) {
					return meeting;
				}
			}
		} catch (Throwable e) {
			Log.e(TAG, "getMeeting Throwable: " + e.toString());
		}
		
		return null;
	}
	
	public ArrayList<Meeting> getDayOfMeetings (int year, int month, int day) {
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		if (mMeetings == null || mMeetings.size() <= 0) {
			return meetings;
		}
		
		for (Meeting meeting : mMeetings){
			if (meeting == null) {
				continue;
			}
			
			if (compareDate(meeting.getStartTime(), year, month, day) == true) {
				meetings.add(meeting);
			}
		}
		
		return meetings;
	}
	
	public boolean checkHasMeeting (int year, int month, int day) {
		if (mMeetings == null || mMeetings.size() <= 0) {
			return false;
		}
		
		for (Meeting meeting : mMeetings){
			if (meeting == null) {
				continue;
			}
			
			if (compareDate(meeting.getStartTime(), year, month, day) == true) {
				return true;
			}
		}
		return false;
	}
	
	private boolean compareDate (String timeString, int year, int month, int day) {
		Time nowTime = new Time ();
		nowTime.setToNow();
		Time meetingTime = new Time();
		meetingTime.set(getTimeMillis(timeString));
		
		if (year < nowTime.year) {
			return false;
		}
		
		if (month < nowTime.month) {
			return false;
		}
		
		if (day < nowTime.monthDay) {
			return false;
		}
		
		if (year == nowTime.year && month == nowTime.month && day == nowTime.monthDay) {
			if (Time.compare(nowTime, meetingTime) > 0) {
				return true;
			}
		} 
		
		if (meetingTime.year == year && meetingTime.month == month && meetingTime.monthDay == day) {
			return true;
		}
		return false;
	}

	public long getTimeMillis(String timeStr) {
		SimpleDateFormat sDateSDF;
		if (timeStr.contains("/") == true){
			sDateSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
		}else {
			sDateSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		}
		long l = 0;
		Date d;
		try {
			d = sDateSDF.parse(timeStr);
			l = d.getTime();

		} catch (Exception e) {

		}
		return l;
	}
}
