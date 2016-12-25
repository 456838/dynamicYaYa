package com.yy.saltonframework.base;

import android.util.SparseArray;
import android.view.View;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/1/19 10:11
 * Time: 10:11
 * Description:
 * Update:ViewHolder简化
 */

@SuppressWarnings("unchecked")
public class ViewHolder {
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
