package com.shgbit.android.heysharevideo.bean;

public class Command {
	
	public enum Cmd {
		Exchange, PopUp, PopDown, AudioMute, VideoMute, StateChange, ModeChange, GetUnjoinedInfo,PopUpDown,ModeVoice,PicShare,CommentStart,CommentEnd,CommentModeChange,LocalChange
	}

	private Cmd name;
	
	private Object [] args;

	public Cmd getName() {
		return name;
	}

	public void setName(Cmd name) {
		this.name = name;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	
}
