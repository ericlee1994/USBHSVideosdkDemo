package com.shgbit.android.heyshareuvc.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VCUtils {
	private static String TAG = "VCUtils";
	
    public static long getTimeMillis(String timeStr) {
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
	
	public static String getTimeStr(long time) {
		SimpleDateFormat sTimeSDF = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return sTimeSDF.format(time);
    }
	
	public static String getTimeStr2(long time) {
		SimpleDateFormat sTimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sTimeSDF.format(time);
    }
    
	public static String getTimeStr3(long time) {
		SimpleDateFormat sTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
        return sTimeSDF.format(time);
    }
	
	public static boolean hideInputByRect(View v, MotionEvent event, Activity c) {

        if (needHideInput(event, v)) {
            return hideKeyboard(c, c.getCurrentFocus().getWindowToken());
        }

        return false;
    }
	
	 public static boolean needHideInput(MotionEvent event, View v) {
	        try {
	            if (v != null && (v instanceof EditText)) {
	                int[] l = {0, 0};
	                v.getLocationInWindow(l);
	                int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
	                        + v.getWidth();
	                if (event.getX() > left && event.getX() < right
	                        && event.getY() > top && event.getY() < bottom) {
	                    return false;
	                }
	            }
	            return true;
	        } catch (NullPointerException e) {
	            return false;
	        }
	    }
	 
	    public static boolean hideKeyboard(Context c, IBinder token) {
	        try {
	            if (token != null) {
	                InputMethodManager im = (InputMethodManager) (c
	                        .getSystemService(Context.INPUT_METHOD_SERVICE));
	                boolean hided = im.hideSoftInputFromWindow(token,
	                        InputMethodManager.HIDE_NOT_ALWAYS);
	                return hided;
	            }
	            return false;
	        } catch (NullPointerException e) {

	            e.printStackTrace();
	            return false;
	        }

	    }
	    
	    public static long[] get30days() {
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTimeInMillis(System.currentTimeMillis());
	        
	        long[] days = new long[30];
	        
	        for (int i = 0; i < days.length; i++) {
	        	days[i] = calendar.getTimeInMillis();
	        	calendar.add(Calendar.DATE, 1);
	        }
	        return days;
	    }
	    
	    public static String getWeekString (int week) {
	    	String weekday = "";
	    	if (week == 1) {
        		weekday = "周一";
        	}else if (week == 2) {
        		weekday = "周二";
        	}else if (week == 3) {
        		weekday = "周三";
        	}else if (week == 4) {
        		weekday = "周四";
        	}else if (week == 5) {
        		weekday = "周五";
        	}else if (week == 6) {
        		weekday = "周六";
        	} else if (week == 0) {
        		weekday = "周日";
        	} else {
        		weekday = "";
        	}
	    	return weekday;
	    }
	    
	    public static boolean isOverdueDay (long day, long today) {
	    	Time t1 = new Time();
	    	Time t2 = new Time();
	    	t1.set(day);
	    	t2.set(today);
	    	
	    	if (Time.compare(t1, t2) < 0) {
	    		return true;
	    	}
	    	
	    	return false;
	    }
	    public static boolean isDaySame (long day1, long day2) {
	    	Time t1 = new Time();
	    	Time t2 = new Time();
	    	t1.set(day1);
	    	t2.set(day2);
	    	if (t1.year == t2.year && t1.month == t2.month && t1.monthDay == t2.monthDay) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public static String getDepartment (String[] dep, int degree) {
	    	if (dep == null || dep.length <= 0) {
	    		return "";
	    	}
	    	
	    	String depString = "";
	    	for (String d : dep) {
	    		if (d == null) {
	    			continue;
	    		}
	    		if (d.contains(",") == true) {
	    			String[] ds = d.split(",");
	    			if (ds.length > degree) {
	    				depString += ds[ds.length - degree - 1] + ",";
	    			}
	    		}
	    		if(degree == 0){
	    			break;
	    		}
	    	}
	    	
	    	if (depString.length() > 0) {
	    		depString = depString.substring(0, depString.length()-1);
	    	}
	    	return depString;
	    }

	public static String CaughtException(Throwable ex) {

		String info = "";
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;

		try {
			baos = new ByteArrayOutputStream();

			printStream = new PrintStream(baos);
			ex.printStackTrace(printStream);

			byte[] data = baos.toByteArray();

			info = new String(data);
			data = null;

		} catch (Throwable e) {
			GBLog.e(TAG, "CaughtException 1st Throwable:" + e.toString());
			info = ex.toString();

		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Throwable e) {
				GBLog.e(TAG, "CaughtException 2nd Throwable:" + e.toString());
			}
		}

		return info;

	}

//	public static void zipToUpload(Context context, File fileZip, Handler UIHandler, int start, int success, int fail) {
//		try {
//			int DAY = 3; //保留3天日志
//			File fileLog = new File(Common.SDCARD_DIR + Common.LOG_DIR);
//			File fileXyu = new File(Common.SDCARD_DIR + Common.XiaoYu + context.getPackageName());
////			File fileZip = new File(FileUtils.getZipPath());
//			WAFile.deleteFile(fileZip, false);
//			List<File> fileList = new ArrayList<File>();
//			FileUtils.list(fileLog, fileList, DAY);
//			FileUtils.list(fileXyu, fileList, 0);
//			// 用zip压缩
//			ZipUtils.zipFiles(fileList, fileZip, UIHandler, start, success, fail);
//		} catch (Throwable e) {
//			GBLog.e(TAG, "zipToUpload Throwable:" + CaughtException(e));
//		}
//	}

}
