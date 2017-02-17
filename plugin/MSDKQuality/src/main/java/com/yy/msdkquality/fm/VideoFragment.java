package com.yy.msdkquality.fm;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.ycloud.live.utils.HwCodecConfig;
import com.yy.IFragmentListener;
import com.yy.msdkquality.R;
import com.yy.msdkquality.aty.ChannelTestLivingRoomActivity;
import com.yy.saltonframework.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/8/4 11:11
 * Time: 11:11
 * Description:
 */
public class VideoFragment extends Fragment {

    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.iv_audio_mute)
    ImageView ivAudioMute;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.start)
    ImageView start;
    @BindView(R.id.ll_video)
    LinearLayout llVideo;
    @BindView(R.id.ll_property)
    LinearLayout llProperty;
    @BindView(R.id.anchor)
    TextView anchor;
    @BindView(R.id.channel_id)
    TextView channelId;
    @BindView(R.id.online_count)
    TextView onlineCount;
    @BindView(R.id.channel_info)
    RelativeLayout channelInfo;
    @BindView(R.id.text_network)
    TextView textNetwork;
    @BindView(R.id.text_resolution)
    TextView textResolution;
    @BindView(R.id.text_bitrate)
    TextView textBitrate;
    @BindView(R.id.text_framerate)
    TextView textFramerate;
    @BindView(R.id.text_decode)
    TextView textDecode;
    @BindView(R.id.live_state)
    RelativeLayout liveState;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;



    IFragmentListener mIFragmentListener;
    @BindView(R.id.tv_openMic)
    AwesomeTextView tvOpenMic;
    @BindView(R.id.tv_muteaudio)
    AwesomeTextView tvMuteaudio;
    @BindView(R.id.tv_lvzhi)
    AwesomeTextView tvLvzhi;
    @BindView(R.id.tv_camera)
    AwesomeTextView tvCamera;
    @BindView(R.id.tv_word_test)
    AwesomeTextView tvWordTest;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }


    private void onAttachToContext(Context activity) {
        if (activity instanceof ChannelTestLivingRoomActivity) {
            mIFragmentListener = (IFragmentListener) activity;
        }
    }

    @Override
    public void onAttach(Context context) {
        onAttachToContext(context);
        super.onAttach(context);
    }

    private boolean isShow = false;

    public View mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_video, container, false);
        ButterKnife.bind(this, view);
        mContentView = view;
        return view;
    }

    ChannelTestLivingRoomActivity mActivity;

    private void initData() {
        mActivity = (ChannelTestLivingRoomActivity) getActivity();
        anchor.setText(mActivity.mUid);
        textDecode.setText(HwCodecConfig.isHw264DecodeEnabled() == true ? "解    码：硬解" : "解    码：软解");
    }

    @OnClick({R.id.tv_property, R.id.iv_audio_mute, R.id.iv_fullscreen, R.id.layout_bottom, R.id.start, R.id.ll_video, R.id.propertyFragment, R.id.ll_property, R.id.tv_openMic, R.id.tv_muteaudio, R.id.tv_lvzhi, R.id.tv_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_property:
                mIFragmentListener.setOnViewClick(view.getId(), view.getRootView());
                break;
            case R.id.iv_audio_mute:
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
            case R.id.iv_fullscreen:
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
            case R.id.layout_bottom:
                break;
            case R.id.start:
                LogUtils.e("start btn clicked!");
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
            case R.id.tv_word_test:
                LogUtils.e("start btn clicked!");
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
            case R.id.ll_video:
                if (isShow) {
                    showToolBar();
                } else {
                    hideToolBar();
                }
                isShow = !isShow;
                break;
            case R.id.propertyFragment:

                break;
            case R.id.tv_openMic:
                mIFragmentListener.setOnViewClick(view.getId(), view.getRootView());
                break;
            case R.id.tv_muteaudio:
                mIFragmentListener.setOnViewClick(view.getId(), view.getRootView());
                break;
            case R.id.tv_lvzhi:
                mIFragmentListener.setOnViewClick(view.getId(), view.getRootView());
                break;
            case R.id.tv_camera:
                mIFragmentListener.setOnViewClick(view.getId(), view.getRootView());
                break;
        }
    }

    private void hideToolBar() {
        //layout_top
        //start
        float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, distance);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layoutBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutBottom.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutBottom.startAnimation(animation);
    }

    private void showToolBar() {
        float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        TranslateAnimation animation = new TranslateAnimation(0, 0, distance, 0);
//		animation.setRepeatCount(1);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layoutBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutBottom.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);
//				pendAutoHideToolBar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutBottom.startAnimation(animation);
    }


    /**
     * 通过TextView的Id获取TextView
     *
     * @param viewId
     * @return
     */
    public TextView getTextView(@IdRes int viewId) {
        return getViewById(viewId);
    }

    /**
     * 设置对应id的控件的文本内容
     *
     * @param viewId
     * @param text
     * @return
     */
    public VideoFragment setText(@IdRes int viewId, CharSequence text) {
        if (text == null) {
            text = "";
        }
        getTextView(viewId).setText(text);
        return this;
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) mContentView.findViewById(id);
    }

}
