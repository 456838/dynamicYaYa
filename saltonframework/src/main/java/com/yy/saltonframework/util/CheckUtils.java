package com.yy.saltonframework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

	/**
	 * 判断两个string是否相等
	 */
	public static boolean checkEquels(Object strObj0, Object strObj1) {
		String str0 = strObj0 + "";
		String str1 = strObj1 + "";
		return str0.equals(str1);
	}
	
	/**
	 * 同时�?��多个参数是否为空
	 * @param strArray
	 * @return
	 */
	public static boolean isNull(Object... strArray){
		for(Object obj:strArray){
			if (!"".equals(obj + "")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �?��是否为空
	 */
	public static boolean isNull(Object strObj) {
		String str = strObj + "";
		return !(!"".equals(str) && !"null".equals(str));
	}
	
	/**
	 * �?��邮箱
	 * @param strObj
	 * @return
	 */
	public static boolean checkEmail(Object strObj){
		String str = strObj + "";
		String match = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern pattern = Pattern.compile(match);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * �?��是否为电话号�?
	 */
	public static boolean checkNumber(Object phoneNumber) {
		boolean isValid = false;
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber + "";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * �?��str的长度是否达到要�?
	 * @param strObj
	 * @param length
	 * @return
	 */
	public static boolean checkLength(Object strObj,int length){
		String str = strObj + "";
		return str.length() >= length;
	}
	
	/**
	 * 检查字符串的长度
	 * @param strObj
	 * @param length
	 * @return
	 */
	public static boolean checkLengthEq(Object strObj,int length){
		String str = strObj + "";
		return str.length() == length;
	}
	
	public static boolean checkLength(Object strObj,int start,int end){
		String str = strObj + "";
		return str.length() >= start && str.length() <= end;
	}
	
	/**
	 * �?��请求返回是否正确
	 */
	public static boolean checkStatusOk(String status){
		return "1".equals(status);
	}
	
	public static boolean checkStatusOk(int status){
		return 2000000 == status;
	}
	
}
