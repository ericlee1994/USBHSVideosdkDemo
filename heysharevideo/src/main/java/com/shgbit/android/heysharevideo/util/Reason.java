package com.shgbit.android.heysharevideo.util;

/**
 * Created by Eric on 2017/6/29.
 */

public class Reason {
    public static String getReason(String s){
        if (s.equals("NOT_LOGGED_IN")){
            return format("系统未注册", s);
        }else if (s.equals("PEER_NOT_FOUND")){
            return format("对方不在线", s);
        }else if (s.equals("PEER_DEVICE_NOT_FOUND")){
            return format("对方不在线", s);
        }else if (s.equals("TIME_OUT")){
            return format("对方没有应答", s);
        }else if (s.equals("BUSY")){
            return format("对方忙", s);
        }else if (s.equals("INVALID_NEMONO")){
            return format("无效呼叫号码", s);
        }else if (s.equals("INVALID_NUMBER")){
            return format("无效呼叫号码", s);
        }else if (s.equals("INVALID_PASSWORD")){
            return format("密码错误", s);
        }else if (s.equals("LOCAL_NET_DISCONNECT")){
            return format("本地网络异常", s);
        }else if (s.equals("SIGNAL_TIMEOUT")){
            return format("本地网络异常", s);
        }else if (s.equals("MEDIA_TIMEOUT")){
            return format("本地网络异常", s);
        }else if (s.equals("PEER_NET_DISCONNECT")){
            return format("对方网络中断", s);
        }else if (s.equals("NOT_ALLOW_NEMO_CALL")){
            return format("对方不允许陌生人呼叫", s);
        }else if (s.equals("USER_NOT_ALLOWED")){
            return format("对方不允许拨打", s);
        }else if (s.equals("TEL_REACH_MAXTIME")){
            return format("已达最大呼叫时长", s);
        }else if (s.equals("CONFMGMT_KICKOUT")){
            return format("您已被管理员请出会议", s);
        }else if (s.equals("CONFMGMT_CONFOVER")){
            return format("主持人已结束会议", s);
        }else if (s.equals("MEETING_LOCKED")){
            return format("会议已被锁定，无法加入，请联系会议发起人解锁后进入", s);
        }else if (s.equals("SDP_INVALID")){
            return format("通讯失败，请升级到最新版本", s);
        }else if (s.equals("VERSION_STALE_LOCAL")){
            return format("版本过低，请升级后再试", s);
        }else if (s.equals("VERSION_STALE_REMOTE")){
            return format("对方版本过低，通话无法接通", s);
        }else if (s.equals("UNSUPPORT_CALL")){
            return format("您的终端目前无法支持此类呼叫", s);
        }else if (s.equals( "STREAM_RESOURCE_UNAVAILABLE")){
            return format("直播服务暂时不可用", s);
        }else if (s.equals("NO_UDP_PACKAGE")){
            return format("当前网络UDP被禁用", s);
        }else if (s.equals("APP_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("CONF_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("OFFICE_NEMO_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("NORMAL_CONF_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("LARGE_CONF_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("NORMAL_OFFICE_NEMO_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("NEN_NORMAL_CONF_SESSION_EXCEED")){
            return format("您呼叫的会议已达最大支持人数", s);
        }else if (s.equals("EN_CONF_SESSION_EXCEED_LOW_BALANCE")){
            return format("企业会议容量使用现在达到上限，无法加入会议。请联系管理员购买更多会议端口", s);
        }else if (s.equals("SERVICE_EXPIRED")){
            return format("服务已过期，请企业管理员更新服务许可证", s);
        }else if (s.equals("SERVICE_REACH_MAX_COUNT")){
            return format("当前在线呼叫数已达服务上限", s);
        }
        return "呼叫失败，请稍后重试";
    }

    private static String format(String zh, String en){

        return zh + "(" + en + ")";
    }
}
