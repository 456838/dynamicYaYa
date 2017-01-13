package com.yy.msdkquality.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class AdvProgressDlg extends android.app.ProgressDialog {
	private long mTimeOut = 0;// 默认timeOut为0即无限大
	private OnTimeOutListener mTimeOutListener = null;// timeOut后的处理器
	private Timer mTimer = null;// 定时器
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mTimeOutListener != null) {
				mTimeOutListener.onTimeOut(AdvProgressDlg.this);
				dismiss();
			}
		}
	};

	public AdvProgressDlg(Context context) {
		super(context);
	}

	/**
	 * 设置timeOut长度，和处理器
	 * 
	 * @param t
	 *            timeout时间长度
	 * @param timeOutListener
	 *            超时后的处理器
	 */
	public void setTimeOut(long t, OnTimeOutListener timeOutListener) {
		mTimeOut = t;
		if (timeOutListener != null) {
			this.mTimeOutListener = timeOutListener;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mTimeOut != 0) {
			mTimer = new Timer();
			TimerTask timerTast = new TimerTask() {
				@Override
				public void run() {
					Message msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);
				}
			};
			mTimer.schedule(timerTast, mTimeOut);
		}
	}

	/**
	 * 
	 * 处理超时的的接口
	 * 
	 */
	public interface OnTimeOutListener {
		/**
		 * 当progressDialog超时时调用此方法
		 */
		abstract public void onTimeOut(AdvProgressDlg dialog);
	}
}
