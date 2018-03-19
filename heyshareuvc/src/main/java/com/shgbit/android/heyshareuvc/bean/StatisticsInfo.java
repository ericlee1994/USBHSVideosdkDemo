package com.shgbit.android.heyshareuvc.bean;


/**
 * Created by Eric on 2017/7/6.
 */

public class StatisticsInfo {
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public People getPeople() {
        return people;
    }

    public void setPeople(People people) {
        this.people = people;
    }

    public Content content;
    public NetworkInfo networkInfo;
    public People people;
    public class Content{
        private AudioTxInfo audioTxInfo;
        private VideoRxInfo videoRxInfo;

        public AudioTxInfo getAudioTxInfo() {
            return audioTxInfo;
        }

        public void setAudioTxInfo(AudioTxInfo audioTxInfo) {
            this.audioTxInfo = audioTxInfo;
        }

        public VideoRxInfo getVideoRxInfo() {
            return videoRxInfo;
        }

        public void setVideoRxInfo(VideoRxInfo videoRxInfo) {
            this.videoRxInfo = videoRxInfo;
        }
    }
    public class NetworkInfo{
        private int rtt;

        private int rxDetectBw;

        private int rxJitter;

        private int rxLost;

        private int txDetectBw;

        private int txJitter;

        private int txLost;

        public int getRtt() {
            return rtt;
        }

        public void setRtt(int rtt) {
            this.rtt = rtt;
        }

        public int getRxDetectBw() {
            return rxDetectBw;
        }

        public void setRxDetectBw(int rxDetectBw) {
            this.rxDetectBw = rxDetectBw;
        }

        public int getRxJitter() {
            return rxJitter;
        }

        public void setRxJitter(int rxJitter) {
            this.rxJitter = rxJitter;
        }

        public int getRxLost() {
            return rxLost;
        }

        public void setRxLost(int rxLost) {
            this.rxLost = rxLost;
        }

        public int getTxDetectBw() {
            return txDetectBw;
        }

        public void setTxDetectBw(int txDetectBw) {
            this.txDetectBw = txDetectBw;
        }

        public int getTxJitter() {
            return txJitter;
        }

        public void setTxJitter(int txJitter) {
            this.txJitter = txJitter;
        }

        public int getTxLost() {
            return txLost;
        }

        public void setTxLost(int txLost) {
            this.txLost = txLost;
        }

    }
    public class People{
        private AudioTxInfo audioTxInfo;
        private VideoRxInfo videoRxInfo;

        public AudioTxInfo getAudioTxInfo() {
            return audioTxInfo;
        }

        public void setAudioTxInfo(AudioTxInfo audioTxInfo) {
            this.audioTxInfo = audioTxInfo;
        }

        public VideoRxInfo getVideoRxInfo() {
            return videoRxInfo;
        }

        public void setVideoRxInfo(VideoRxInfo videoRxInfo) {
            this.videoRxInfo = videoRxInfo;
        }
    }
}
