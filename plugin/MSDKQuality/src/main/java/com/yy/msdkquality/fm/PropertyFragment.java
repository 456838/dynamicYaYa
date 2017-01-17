package com.yy.msdkquality.fm;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.IFragmentListener;
import com.yy.msdkquality.R;
import com.yy.msdkquality.aty.ChannelTestLivingRoomActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/8/1 14:16
 * Time: 14:16
 * Description:
 */
public class PropertyFragment extends Fragment {


    @BindView(R.id.tv_temp_audio_quality)
    TextView tvTempAudioQuality;
    @BindView(R.id.tv_audio_quality)
    TextView tvAudioQuality;
    @BindView(R.id.re_audio_quality)
    RelativeLayout reAudioQuality;
    @BindView(R.id.tv_temp_audio_low_flow)
    TextView tvTempAudioLowFlow;
    @BindView(R.id.tv_audio_low_flow)
    TextView tvAudioLowFlow;
    @BindView(R.id.re_audio_low_flow)
    RelativeLayout reAudioLowFlow;
    @BindView(R.id.tv_temp_fastplay)
    TextView tvTempFastplay;
    @BindView(R.id.tv_fastplay)
    TextView tvFastplay;
    @BindView(R.id.re_fastplay)
    RelativeLayout reFastplay;
    @BindView(R.id.tv_temp_hardware_decode)
    TextView tvTempHardwareDecode;
    @BindView(R.id.tv_hardware_decode)
    TextView tvHardwareDecode;
    @BindView(R.id.re_hardware_decode)
    RelativeLayout reHardwareDecode;
    @BindView(R.id.tv_temp_hardware_encode)
    TextView tvTempHardwareEncode;
    @BindView(R.id.tv_hardware_encode)
    TextView tvHardwareEncode;
    @BindView(R.id.re_hardware_encode)
    RelativeLayout reHardwareEncode;
    @BindView(R.id.tv_temp_code_rate)
    TextView tvTempCodeRate;
    @BindView(R.id.tv_code_rate)
    TextView tvCodeRate;
    @BindView(R.id.re_code_rate)
    RelativeLayout reCodeRate;
    @BindView(R.id.tv_temp_sex)
    TextView tvTempSex;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_temp_open_mic)
    TextView tvTempOpenMic;
    @BindView(R.id.tv_temp_water_mark)
    TextView tvTempWaterMark;
    @BindView(R.id.tv_temp_region)
    TextView tvTempRegion;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_temp_sign)
    TextView tvTempSign;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.rl_publish_video)
    RelativeLayout rlPublishVideo;
    @BindView(R.id.rl_open_mic)
    RelativeLayout rlOpenMic;

    IFragmentListener mIFragmentListener;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_property, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick({R.id.rl_publish_video, R.id.rl_open_mic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_publish_video:
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
            case R.id.rl_open_mic:
                mIFragmentListener.setOnViewClick(view.getId(), view);
                break;
        }
    }
}
