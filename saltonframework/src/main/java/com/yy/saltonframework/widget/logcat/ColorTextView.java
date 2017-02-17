package com.yy.saltonframework.widget.logcat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yy.saltonframework.R;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by munix on 20/12/16.
 */

public class ColorTextView extends ScrollView implements ColorTextViewListener {

    private int audioColor, videoColor, sdkColor, otherColor, signalColor, consoleColor;
    private TextView textView;
    private Queue<String> mLogQueue = new ConcurrentLinkedQueue<>();

    public ColorTextView(Context context) {
        super(context);
        init(null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setPadding(20, 20, 20, 20);
        addView(textView);

        textView.setTextColor(getContext().getResources().getColor(R.color.defaultTextColor));

        if (attrs != null) {
            TypedArray a = getContext().getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.ColorTextView, 0, 0);

            try {
                audioColor = a.getColor(R.styleable.ColorTextView_verboseColor, getContext().getResources()
                        .getColor(R.color.defaultVerboseColor));
                videoColor = a.getColor(R.styleable.ColorTextView_debugColor, getContext().getResources()
                        .getColor(R.color.defaultDebugColor));
                sdkColor = a.getColor(R.styleable.ColorTextView_errorColor, getContext().getResources()
                        .getColor(R.color.defaultErrorColor));
                otherColor = a.getColor(R.styleable.ColorTextView_infoColor, getContext().getResources()
                        .getColor(R.color.defaultInfoColor));
                signalColor = a.getColor(R.styleable.ColorTextView_warningColor, getContext().getResources()
                        .getColor(R.color.defaultWarningColor));

                consoleColor = a.getColor(R.styleable.ColorTextView_consoleColor, getContext().getResources()
                        .getColor(R.color.defaultConsoleColor));
            } finally {
                a.recycle();
            }
        } else {
            audioColor = getContext().getResources().getColor(R.color.defaultVerboseColor);
            videoColor = getContext().getResources().getColor(R.color.defaultDebugColor);
            sdkColor = getContext().getResources().getColor(R.color.defaultErrorColor);
            otherColor = getContext().getResources().getColor(R.color.defaultInfoColor);
            signalColor = getContext().getResources().getColor(R.color.defaultWarningColor);
            consoleColor = getContext().getResources().getColor(R.color.defaultConsoleColor);
        }

        setBackgroundColor(consoleColor);
        textView.setBackgroundColor(consoleColor);
    }

    public static final int AUDIO = 1;
    public static final int VIDEO = 2;
    public static final int SDK = 3;
    public static final int OTHER = 4;
    public static final int SIGNAL = 5;

    public void refreshLogcat(int TAG, String content) {
        StringBuilder log = new StringBuilder();
        int lineColor = audioColor;
        switch (TAG) {
            case AUDIO:
                lineColor = audioColor;
                break;
            case VIDEO:
                lineColor = videoColor;
                break;
            case SDK:
                lineColor = sdkColor;
                break;
            case OTHER:
                lineColor = otherColor;
                break;
            case SIGNAL:
                lineColor = signalColor;
                break;
        }
        log.append("<font color=\"#" + Integer.toHexString(lineColor)
                .toUpperCase()
                .substring(2) + "\">" + content + "</font><br><br>");
        if (mLogQueue.size() < 20) {
            mLogQueue.add(log.toString());
        } else {
            mLogQueue.poll();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (String msg : mLogQueue) {
            spannableStringBuilder.append(msg);
        }
//            System.out.println(sb.toString());
        textView.setText(Html.fromHtml(spannableStringBuilder.toString()));
    }

    @Override
    public void onTextCaptured(String logcat) {
        textView.append(Html.fromHtml(logcat));
    }

//    private void getLogcat(final ColorTextViewListener listener) {
//
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    String processId = Integer.toString(android.os.Process.myPid());
//                    String[] command = new String[]{
//                            "logcat",
//                            "-d",
//                            "-v",
//                            "threadtime"
//                    };
//                    Process process = Runtime.getRuntime().exec(command);
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process
//                            .getInputStream()));
//
//                    StringBuilder log = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        if (line.contains(processId)) {
//                            int lineColor = audioColor;
//
//                            if (line.contains(" I ")) {
//                                lineColor = otherColor;
//                            } else if (line.contains(" E ")) {
//                                lineColor = sdkColor;
//                            } else if (line.contains(" D ")) {
//                                lineColor = videoColor;
//                            } else if (line.contains(" W ")) {
//                                lineColor = signalColor;
//                            }
//
//                            log.append("<font color=\"#" + Integer.toHexString(lineColor)
//                                    .toUpperCase()
//                                    .substring(2) + "\">" + line + "</font><br><br>");
//                        }
//                    }
//                    listener.onTextCaptured(log.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }

//    private void getLogcatDul(final ColorTextViewListener listener) {
//
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    String processId = Integer.toString(android.os.Process.myPid());
//                    String[] command = new String[]{
//                            "logcat",
//                            "-d",
//                            "-v",
//                            "threadtime"
//                    };
//                    Process process = Runtime.getRuntime().exec(command);
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process
//                            .getInputStream()));
//
//                    StringBuilder log = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        if (line.contains(processId)) {
//                            int lineColor = audioColor;
//
//                            if (line.contains(" I ")) {
//                                lineColor = otherColor;
//                            } else if (line.contains(" E ")) {
//                                lineColor = sdkColor;
//                            } else if (line.contains(" D ")) {
//                                lineColor = videoColor;
//                            } else if (line.contains(" W ")) {
//                                lineColor = signalColor;
//                            }
//
//                            log.append("<font color=\"#" + Integer.toHexString(lineColor)
//                                    .toUpperCase()
//                                    .substring(2) + "\">" + line + "</font><br><br>");
//                        }
//                    }
//                    listener.onTextCaptured(log.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
}
