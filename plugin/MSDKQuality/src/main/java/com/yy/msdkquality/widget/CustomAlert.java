package com.yy.msdkquality.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yy.msdkquality.R;


public class CustomAlert extends Dialog {

    private TextView mTitle;
    private View mTitleDivider;
    private TextView mMessage;
    private View mMessageDivider;
    private Button mButtonNegative;
    private View mButtonDivider;
    private Button mButtonPositive;
    private FrameLayout mCustomView;

    private OnClickListener mListener;

    private CustomAlert(Context context) {
        this(context, 0);
    }

    private CustomAlert(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.custom_alert);
        initView();
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.title);
        mTitleDivider = findViewById(R.id.title_divider);
        mMessage = (TextView) findViewById(R.id.message);
        mMessageDivider = findViewById(R.id.message_divider);
        mButtonNegative = (Button) findViewById(R.id.button_negative);
        mButtonDivider = findViewById(R.id.button_divider);
        mButtonPositive = (Button) findViewById(R.id.button_positive);
        mCustomView = (FrameLayout) findViewById(R.id.custom_view);

        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        mButtonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClick(CustomAlert.this, BUTTON_NEGATIVE);
                }
            }
        });
        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClick(CustomAlert.this, BUTTON_POSITIVE);
                }
            }
        });
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    public void setMessage(CharSequence message) {
        mMessage.setText(message);
    }

    public void setNegative(CharSequence negative) {
        mButtonNegative.setText(negative);
    }

    public void setPositive(CharSequence positive) {
        mButtonPositive.setText(positive);
    }

    public void setOnClickListener(final OnClickListener listener) {
        mListener = listener;
    }

    public void setCustomView(View view) {
        mCustomView.removeAllViews();
        mCustomView.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCustomView.setVisibility(View.VISIBLE);
        mMessageDivider.setVisibility(View.VISIBLE);
    }

    public static class Builder {
        private Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mNegative;
        private CharSequence mPositive;
        private OnClickListener mListener;
        private OnCancelListener mCancelListener;
        private View mCustomView;
        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder title(int resId) {
            title(mContext.getString(resId));
            return this;
        }

        public Builder title(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder message(int resId) {
            message(mContext.getString(resId));
            return this;
        }

        public Builder message(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder negative(int resId) {
            negative(mContext.getString(resId));
            return this;
        }

        public Builder negative(CharSequence title) {
            mNegative = title;
            return this;
        }

        public Builder positive(int resId) {
            positive(mContext.getString(resId));
            return this;
        }

        public Builder positive(CharSequence title) {
            mPositive = title;
            return this;
        }

        public Builder onClickListener(OnClickListener listener) {
            mListener = listener;
            return this;
        }

        public Builder onCancelListener(OnCancelListener listener) {
            mCancelListener = listener;
            return this;
        }

        public Builder customView(View customView) {
            mCustomView = customView;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public CustomAlert create() {
        	CustomAlert alert = new CustomAlert(mContext, R.style.Theme_Dialog_Custom);

            if (mTitle == null) {
                alert.mTitle.setVisibility(View.GONE);
                alert.mTitleDivider.setVisibility(View.GONE);
            } else {
                alert.setTitle(mTitle);
            }

            if (mMessage == null) {
                alert.mMessage.setVisibility(View.GONE);
                alert.mMessageDivider.setVisibility(View.GONE);
            } else {
                alert.setMessage(mMessage);
            }

            if (mNegative == null) {
                alert.mButtonNegative.setVisibility(View.GONE);
                alert.mButtonDivider.setVisibility(View.GONE);
            } else {
                alert.setNegative(mNegative);
            }

            if (mPositive == null) {
                alert.mButtonPositive.setVisibility(View.GONE);
                alert.mButtonDivider.setVisibility(View.GONE);
            } else {
                alert.setPositive(mPositive);
            }

            alert.setOnClickListener(mListener);
            alert.setOnCancelListener(mCancelListener);
            alert.setCancelable(mCancelable);

            if (mCustomView != null) {
                alert.setCustomView(mCustomView);
            }

            if (!(mContext instanceof Activity)) {
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            return alert;
        }

        public CustomAlert show() {
        	CustomAlert alert = create();
            alert.show();
            return alert;
        }
    }
}
