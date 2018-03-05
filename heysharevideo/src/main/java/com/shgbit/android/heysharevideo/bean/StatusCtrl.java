package com.shgbit.android.heysharevideo.bean;

/**
 * Created by Gxk on 2018/1/29.
 */

public class StatusCtrl {

	private boolean isMicMute = true;
	private boolean isSpeakerSetVol = false;
	private boolean isCameraMute = false;

	public StatusCtrl() {

	}

	public boolean isMicMute() {
		return isMicMute;
	}

	public void setMicMute(boolean micMute) {
		isMicMute = micMute;
	}

	public boolean isSpeakerSetVol() {
		return isSpeakerSetVol;
	}

	public void setSpeakerSetVol(boolean speakerSetVol) {
		isSpeakerSetVol = speakerSetVol;
	}

	public boolean isCameraMute() {
		return isCameraMute;
	}

	public void setCameraMute(boolean cameraMute) {
		isCameraMute = cameraMute;
	}
}
