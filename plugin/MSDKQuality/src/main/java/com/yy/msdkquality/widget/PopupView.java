package com.yy.msdkquality.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

public abstract class PopupView extends ViewContainer {

	public PopupView(Activity context) {
		super(context);
	}

	protected void createPopup() {
		mPopup = new PopupWindow(getContext());
		mPopup.setWidth(LayoutParams.FILL_PARENT);
		mPopup.setHeight(LayoutParams.WRAP_CONTENT);

		mPopup.setContentView(getView());

		mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		mPopup.setFocusable(true);

		mPopup.setOutsideTouchable(true);
		mPopup.setTouchable(true);
		mPopup.setAnimationStyle(0);
		
		mPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public void show(View anchor) {
		if (mPopup == null) {
			createPopup();
		}
		mPopupShown = true;

		mAnchor = anchor;
		mPopup.showAsDropDown(anchor);
	}

	public void showAbove(View anchor) {
		if (mPopup == null) {
			createPopup();
		}
		mAnchor = anchor;
		mPopupShown = true;

		int pos[] = new int[2];
		anchor.getLocationInWindow(pos);

		mPopup.showAtLocation(anchor, Gravity.TOP | Gravity.LEFT, pos[0],
				pos[1] - getViewHeight());
	}

	public void showInCenter(View anchor) {
		if (mPopup == null) {
			createPopup();
		}
		mAnchor = anchor;
		mPopupShown = true;

		int pos[] = new int[2];
		anchor.getLocationInWindow(pos);

		mPopup.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	}

	public void dismiss() {
		if (mPopup == null) {
			return;
		}
		mPopupShown = false;

		mPopup.dismiss();
	}

	public void setAnim(int n) {
		mPopup.setAnimationStyle(n);
	}

	public boolean isPopupShown() {
		return mPopupShown;
	}

	public View getAnchor() {
		return mAnchor;
	}

	protected int getViewHeight() {
		int h = getView().getHeight();
		if (h == 0) {

			DisplayMetrics metrics = new DisplayMetrics();

			((WindowManager) getView().getContext().getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
					metrics);

			//try{
			getView().measure(
					MeasureSpec.makeMeasureSpec(metrics.widthPixels,
							MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(metrics.heightPixels,
							MeasureSpec.AT_MOST));
			//}catch(Exception e){}

			h = getView().getMeasuredHeight();
		}
		return h;
	}

	protected boolean mPopupShown = false;
	protected View mAnchor;
	protected PopupWindow mPopup;
}
