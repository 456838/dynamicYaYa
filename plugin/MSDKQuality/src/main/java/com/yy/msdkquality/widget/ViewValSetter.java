package com.yy.msdkquality.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewValSetter {

	public static void setViewVal(final View v, final Object val) {

		String text = (val == null ? "" : val.toString());
		text = (text == null ? "" : text);

		// Checkable
		if (v instanceof Checkable) {
			if (val instanceof Boolean) {
				((Checkable) v).setChecked((Boolean) val);
			} else if (v instanceof TextView) {
				// Note: keep the instanceof TextView check at the bottom of
				// these
				// ifs since a lot of views are TextViews (e.g. CheckBoxes).
				setViewText((TextView) v, text);
			} else {
				throw new IllegalStateException(v.getClass().getName()
						+ " should be bound to a Boolean, not a "
						+ (val == null ? "<unknown type>" : val.getClass()));
			}
		}
		// TextView
		else if (v instanceof TextView) {
			if (val instanceof CharSequence) {
				setViewText((TextView) v, (CharSequence) val);
			} else if (val instanceof Integer) {
				setViewText((TextView) v, (Integer) val);
			} else {
				setViewText((TextView) v, text);
			}
		}
		// ImageView
		else if (v instanceof ImageView) {
			if (val instanceof Drawable) {
				setViewImage((ImageView) v, (Drawable) val);
			} else if (val instanceof Bitmap) {
				setViewImage((ImageView) v, (Bitmap) val);
			} else if (val instanceof Integer) {
				setViewImage((ImageView) v, (Integer) val);
			} else {
				throw new IllegalStateException(v.getClass().getName()
						+ " is not a "
						+ " view that can be bounds by this SimpleAdapter");
//				setViewImage((ImageView) v, text);
			}
		} else {
			throw new IllegalStateException(v.getClass().getName()
					+ " is not a "
					+ " view that can be bounds by this SimpleAdapter");
		}
	}

	public static void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public static void setViewImage(ImageView v, Drawable value) {
		v.setImageDrawable(value);
	}

	public static void setViewImage(ImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}

//	public static void setViewImage(ImageView v, String value) {
//		try {
//			v.setImageResource(Integer.parseInt(value));
//		} catch (NumberFormatException nfe) {
//			v.setImageURI(Uri.parse(value));
//		}
//	}

	public static void setViewText(TextView v, CharSequence text) {
		v.setText(text);
	}

	public static void setViewText(TextView v, Integer id) {
		v.setText(id);
	}

	public static interface ViewBinder {
		boolean setViewValue(View view, Object data, String textRepresentation);
	}
}
