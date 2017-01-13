package com.yy.msdkquality.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;

public class ViewContainer {

	private Activity mActivity;
	private LayoutInflater mFactory;

	private View mView;
	private ViewGroup mViewRoot;

	private boolean mIsShow;
	public Object mRoot;

	ArrayList<ViewContainer> mChildren = new ArrayList<ViewContainer>();
	ViewContainer mParent = null;
	
	public ViewContainer(Activity c) {
		mActivity = c;
		mFactory = LayoutInflater.from(mActivity);

		mRoot = c;
	}

	public ViewContainer(Activity c, int resId) {
		this(c, resId, null);
	}

	public ViewContainer(Activity context, int resId, ViewGroup root) {
		this(context);
		mViewRoot = root;

		inflater(resId, root);
	}

	public ViewContainer(Activity context, View v) {
		mActivity = context;
		mView = v;
	}

	public ViewContainer(ViewContainer parent, View v) {
		mActivity = parent.getAct();
		mFactory = LayoutInflater.from(mActivity);

		mView = v;
		parent.mChildren.add(this);
		mParent = parent;

		mRoot = parent.mRoot;
	}

	public ViewContainer(ViewContainer parent, int resId) {
		mActivity = parent.getAct();
		mFactory = LayoutInflater.from(mActivity);

		mView = inflater(resId);
		parent.mChildren.add(this);
		mParent = parent;

		mRoot = parent.mRoot;
	}

	public ViewContainer(ViewContainer parent, int resId, ViewGroup root) {
		mActivity = parent.getAct();
		mFactory = LayoutInflater.from(mActivity);
		mViewRoot = root;

		mView = inflater(resId, root);
		parent.mChildren.add(this);
		mParent = parent;

		mRoot = parent.mRoot;
	}

	public void removeFromParent() {
		if (mParent == null) {
			return;
		}

		mParent.mChildren.remove(this);
	}

	public View inflater(int resId) {
		return inflater(resId, mViewRoot);
	}

	public View inflater(int resId, ViewGroup root) {
		mView = mFactory.inflate(resId, null);
		if (mView == null) {
			throw new RuntimeException();
		}
		if (root != null) {
			root.addView(mView);
		}		
		return mView;
	}

	public final View findViewById(int id) {
		return mView.findViewById(id);
	}

	public final View findViewWithTag(String tag) {
		return mView.findViewWithTag(tag);
	}

	public Context getContext() {
		return mActivity;
	}

	public Activity getAct() {
		return mActivity;
	}

	public Object getRoot() {
		return mRoot;
	}

	public ViewContainer getParent() {
		return mParent;
	}

	public View getView() {
		return mView;
	}

	public void setView(View v) {
		mView = v;
	}

	public abstract class RunOnUI implements Runnable {
		public RunOnUI() {
			mActivity.runOnUiThread(this);
		}
	}

	public Drawable getDrawable(int id) {
		return mActivity.getResources().getDrawable(id);
	}

	public String getString(int id) {
		return mActivity.getString(id);
	}

	public String getString(int id, Object... formatArgs) {
		return mActivity.getString(id, formatArgs);
	}

	public void exchangeData(View v, Object data) {
		ViewValSetter.setViewVal(v, data);
	}

	public void exchangeData(int id, Object data) {
		exchangeData(findViewById(id), data);
	}

	public void onResume() {
		mIsShow = true;

		for (ViewContainer v : mChildren) {
			v.onResume();
		}
	}

	public void onPause() {
		mIsShow = false;

		for (ViewContainer v : mChildren) {
			v.onPause();
		}
	}

	public boolean isActive() {
		return mIsShow;
	}

	// OnClickListener
	public void setOnClickListener(int id, OnClickListener l) {
		mView.findViewById(id).setOnClickListener(l);
	}

	// OnKeyListener
	public void setOnKeyListener(int id, OnKeyListener l) {
		mView.findViewById(id).setOnKeyListener(l);
	}

	// OnCheckedChangeListener
	public void setOnCheckedChangeListener(int id, OnCheckedChangeListener l) {
		((CompoundButton) mView.findViewById(id))
				.setOnCheckedChangeListener(l);
	}

	// OnItemClickListener
	public void setOnItemClickListener(AdapterView<?> v, OnItemClickListener l) {
		v.setOnItemClickListener(l);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		return false;
	}
		
	public boolean onBackPressed(){
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
}
