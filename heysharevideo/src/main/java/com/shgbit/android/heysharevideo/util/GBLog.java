package com.shgbit.android.heysharevideo.util;

import android.text.format.Time;
import android.util.Log;

import com.wa.util.WAFile;

import java.util.ArrayList;

public class GBLog {
	
	public static String filepath = null;
	
	private static ArrayList<String> list = new ArrayList<String>();
	
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	
	public static int LogMaxLen = 100;
	private static int mLevel = INFO;
	
	public static void setWriteLevel (int level) {
		mLevel = level;
	}
	
	
	public static void v(String tag, String msg) {
		Log.v(tag, msg);
		WriteFile(VERBOSE, tag, msg);
	}

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
		WriteFile(DEBUG, tag, msg);
	}
	
	public static void i(String tag, String msg) {
		Log.i(tag, msg);
		WriteFile(INFO, tag, msg);
	}
	
	public static void e(String tag, String msg) {
		Log.e(tag, msg);
		WriteFile(ERROR, tag, msg);
	}
	
	public static void w(String tag, String msg) {
		Log.w(tag, msg);
		WriteFile(WARN, tag, msg);
	}
	
	private static void WriteFile(int level, String tag, String msg) {
		if (filepath == null) {
			return;
		}
		
		String strLevel = "";
		
		switch(level) {
			case VERBOSE:
				strLevel = "[VERBOSE]";
				break;
			case DEBUG:
				strLevel = "[DEBUG]  ";
				break;
			case INFO:
				strLevel = "[INFO]   ";
				break;
			case WARN:
				strLevel = "[WARN]   ";
				break;
			case ERROR:
				strLevel = "[ERROR]  ";
				break;
			default:
				strLevel = "[UNKNOWN]";
				break;
		}
		
		Time now = new Time();
		now.setToNow();
		
		String log = now.format3339(false) + " " + strLevel + " " + tag + " " + msg;
		
		try {
			
			if (level >= mLevel) {
			
				String filename = filepath + "/log-" + now.format("%Y%m%d") + ".txt";
				
				WAFile.write(filename, true, log + "\r\n");
			
			}
			
			list.add(log);
			if (LogMaxLen > 0) {
				while (list.size() > LogMaxLen) {
					list.remove(0);
				}
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public static String[] ReadFile(int lines) {
		
		if (list == null) {
			return null;
		}
		
		int len = list.size() > lines ? lines : list.size();
		
		String [] log = new String[len];
		
		for (int i = 0; i < len; i++) {
			
			log[i] = "null";
			
			try {
				log[i] = list.get(list.size() - i - 1);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return log;
	}
	
}