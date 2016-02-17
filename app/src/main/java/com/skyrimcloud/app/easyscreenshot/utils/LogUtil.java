package com.skyrimcloud.app.easyscreenshot.utils;

import android.util.Log;

/**
 *
 * @ClassName: LogUtil
 * @Description: 处理log的工具类
 * @date 2014-11-24 下午6:18:30
 */
public class LogUtil {
	public static boolean DEBUG = true;// 是否开启log
	public static boolean SHOW_LINE_NUMBER_IN_LOG = true;// 是否在log中显示行号

	public static boolean TEST_MODE=false;

	static String TAG = "LogUtil";

	public final static boolean SHOW_JSON_IN_LOG=true;//是否显示json数据

	public static void LOGI(String content) {
		LOG(TAG, content, Log.INFO,1,false);
	}

	/**
	 * 查看调用者的函数信息
	 * @param content
	 */
	public static void LOGICaller(String content) {
		LOG(TAG, content, Log.INFO, 1,true);
	}
	public static void LOGECaller(String content) {
		LOG(TAG, content, Log.ERROR, 1,true);
	}

	public static void LOGI(String tag, String content) {
		LOG(tag, content, Log.INFO,1,false);
	}

	public static void LOGE(String content) {
		LOG(TAG, content, Log.ERROR,1,false);
	}

	public static void LOGE(String tag, String content) {
		LOG(tag, content, Log.ERROR,1,false);

	}

	/**
	 * 使用assert
	 *
	 * @param flag
	 */
	public static void assertTrue(boolean flag) {
		if (DEBUG && !flag) {
			throw new AssertionError();
		}
	}
	public static void assertEquals(int a,int b){
		if (DEBUG && a!=b) {
			throw new AssertionError("a="+a+",b="+b);
		}
	}
	public static void assertEquals(long a,long b){
		if (DEBUG && a!=b) {
			throw new AssertionError("a="+a+",b="+b);
		}
	}
	/**
	 * a和b必须都不为null且相等
	 * @param a
	 * @param b
	 */
	public static void assertEquals(String a,String b){
		if (DEBUG && !(
				a!=null&& b!=null&& a.equals(b))) {
			throw new AssertionError("a="+a+",b="+b);
		}
	}

	/**
	 *
	 * @param tag
	 * 			  log的tag
	 * @param content
	 *            log的内容
	 * @param logType
	 *            log的类型,如Log.INFO,Log.DEBUG等
	 * @param methodDepth
	 *
	 * @param showCaller 是否显示调用者的方法名和行号等
	 */
	private static void LOG(String tag, String content, int logType,int methodDepth,boolean showCaller) {
		if (DEBUG) {
			if (SHOW_LINE_NUMBER_IN_LOG) {
				Throwable throwable = new Throwable();

				methodDepth++;

				if (throwable.getStackTrace().length-1> methodDepth){
					StackTraceElement element = throwable.getStackTrace()[methodDepth];


					StringBuffer stringBuffer=new StringBuffer();
					if (showCaller&& throwable.getStackTrace().length-1> methodDepth+1){
						StackTraceElement elementCaller = throwable.getStackTrace()[methodDepth+1];



						stringBuffer.append("[")
								.append(elementCaller.getFileName())
								.append(":")
								.append(elementCaller.getMethodName())
								.append("():")
								.append(elementCaller.getLineNumber())
								.append("]");

						stringBuffer.append("->");

						stringBuffer.append("[")
								.append(element.getFileName())
								.append(":")
								.append(element.getMethodName())
								.append("():")
								.append(element.getLineNumber())
								.append("]");


						stringBuffer.append(""+content);

						stringBuffer.append(" (")
								.append(element.getFileName())
								.append(":")
								.append(element.getLineNumber())
								.append(") ");



					}else{

						stringBuffer.append("[")
								.append(element.getFileName())
								.append(":")
								.append(element.getMethodName())
								.append("():")
								.append(element.getLineNumber())
								.append("]")
								.append(content);

						stringBuffer.append(" (")
								.append(element.getFileName())
								.append(":")
								.append(element.getLineNumber())
								.append(")");


					}
					content = stringBuffer.toString();

				}


			}

			switch (logType) {
				case Log.INFO:
					Log.i(tag, content);
					break;
				case Log.DEBUG:
					Log.d(tag, content);
					break;
				case Log.ERROR:
					Log.e(tag, content);
					break;

				default:
					break;
			}

		}
	}
}
