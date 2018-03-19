package com.shgbit.android.heyshareuvc.bean;

/**
 * Created by Eric on 2017/7/12.
 */

public class VideoRxInfo {
    private String codecType;
    private String disName;
    private String resolution;
    private int actBw;
    private int frameRate;
    private int jitter;
    private int lostRate;
    private int packageLost;
    private boolean encrypt;

    public String getCodecType() {
        return codecType;
    }

    public void setCodecType(String codecType) {
        this.codecType = codecType;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getActBw() {
        return actBw;
    }

    public void setActBw(int actBw) {
        this.actBw = actBw;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getJitter() {
        return jitter;
    }

    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    public int getLostRate() {
        return lostRate;
    }

    public void setLostRate(int lostRate) {
        this.lostRate = lostRate;
    }

    public int getPackageLost() {
        return packageLost;
    }

    public void setPackageLost(int packageLost) {
        this.packageLost = packageLost;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
}
