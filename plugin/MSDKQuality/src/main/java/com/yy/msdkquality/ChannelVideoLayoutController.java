package com.yy.msdkquality;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.ycloud.live.YCConstant;
import com.ycloud.live.YCMedia;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.utils.YCLog;
import com.ycloud.live.video.YCSpVideoView;
import com.ycloud.live.video.YCVideoViewLayout;
import com.yy.msdkquality.widget.DoubleLayout;

import java.util.ArrayList;
import java.util.List;


public class ChannelVideoLayoutController {

	YCVideoViewLayout mViewLay;
	YCVideoViewLayout mViewLay2;
	DoubleLayout mDlo;
    private long mUserGroupId = 0;
    private long mView1Streamid = 0; //streamid that view1 linkto
    private long mView2Streamid = 0; //streamid that view2 linkto
     
    private static final String TAG = "ChannelVideoLayoutController";

    
    private List<YCMessage.VideoStreamInfo> mCurStreamList = new ArrayList<YCMessage.VideoStreamInfo>();
    
    
	private final Handler mUIHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) 
		{
			//YLog.info(this, "mUIHandler handleMessage is called,what:%d",msg.what);
			switch(msg.what)
			{
				case YCMessage.MsgType.onVideoStreamInfoNotify:
				{
					YCMessage.VideoStreamInfo streamInfo = (YCMessage.VideoStreamInfo) msg.obj;
					onVideoStreamInfoNotify(streamInfo);
				}
				break;
				
				case YCMessage.MsgType.onVideoliveBroadcastNotify:
				{
					YCMessage.VideoliveBroadcastInfo broadcastInfo  = (YCMessage.VideoliveBroadcastInfo)msg.obj;
					onVideoliveBroadcastNotify(broadcastInfo);
				}
				break;
				
				case YCMessage.MsgType.onVideoRenderInfoNotify:
				{
					YCMessage.VideoRenderInfo renderInfo = (YCMessage.VideoRenderInfo)msg.obj;
					onVideoRenderInfo(renderInfo);
				}
				break;
				
				case YCMessage.MsgType.onVideoDownlinkPlrNotify:
				{
					YCMessage.VideoDownlinkPlrInfo plrInfo = (YCMessage.VideoDownlinkPlrInfo)msg.obj;
					onVideoDownlinkPlr(plrInfo);
				}
				break;
	
				case YCMessage.MsgType.onAudioSpeakerInfoNotity:
				{
					YCMessage.AudioSpeakerInfo speakerInfo = (YCMessage.AudioSpeakerInfo)msg.obj;
					onAudioSpeakerInfo(speakerInfo);
				}
				break;
				
				case YCMessage.MsgType.onAudioStreamVolume:
				{
					YCMessage.AudioVolumeInfo audioVolumeInfo = (YCMessage.AudioVolumeInfo)msg.obj;
					onAudioVolumeArrived(audioVolumeInfo);
				}
				break;
				
				case YCMessage.MsgType.onVideoCodeRateNotify:
				{
					YCMessage.VideoCodeRateInfo info = (YCMessage.VideoCodeRateInfo)msg.obj;
					onVideoCodeRateNotify(info);
				}
				break;
			}//end of switch 
		} //end of handleMessage
	
	};
	
	private void onVideoStreamInfoNotify(YCMessage.VideoStreamInfo streamInfo)
	{
		switch(streamInfo.state)
		{
			case YCMessage.VideoStreamInfo.Arrive:
			{
				onVideoStreamArrive(streamInfo);
			}
			break;
			case YCMessage.VideoStreamInfo.Start:
			{
			}
			break;
			case YCMessage.VideoStreamInfo.Stop:
			{
				onVideoStreamStop(streamInfo);
			}
			break;
		}
	}

	private void onVideoStreamArrive(YCMessage.VideoStreamInfo streamInfo)
	{
		long userGroupId = streamInfo.userGroupId;
		long streamid = streamInfo.streamId;
		YCLog.info(TAG, "videoViewControler streamNotify recv userGroup:%d,streamId:%d", userGroupId, streamid);
		if (isStreamExist(streamid))
		{
			YCLog.info(TAG, "videoViewControler streamNotify streamId:%d exist,do nothing", streamid);
			return;
		}
		YCVideoViewLayout yl = getAvailLayout(streamid);
		if (yl == null) {
			return;
		}

		YCSpVideoView view = yl.clearAndCreateNewView();
		if(view != null)
		{
			view.setScaleMode(YCConstant.ScaleMode.AspectFit);
//			view.setScaleMode(YCConstant.ScaleMode.ClipToBounds);
			YCMedia.getInstance().requestMethod(new YCMediaRequest.YCAddSpVideoView(view));
			view.linkToStream(userGroupId, streamid);
			YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartSubscribeVideo(userGroupId, streamid));
			mCurStreamList.add(streamInfo);
			view.setVisibility(View.VISIBLE);
			YCLog.info(TAG, "videoViewControler streamNotify subscribe streamId:%d",streamid);
		}
		else
		{
			YCLog.info(TAG, "videoViewControler streamNotify no view for streamId:%d",streamid);
		}
		updateLayout(0);
	}

	public void updateLayout(long delay) {
		if (mView1Streamid != 0) {
			mViewLay.setVisibility(View.VISIBLE);
		}
		else {
			mViewLay.setVisibility(View.GONE);
		}
		if (mView2Streamid != 0) {
			mViewLay2.setVisibility(View.VISIBLE);
		}
		else {
			mViewLay2.setVisibility(View.GONE);
		}
		mDlo.postDelayed(new Runnable() {
			@Override
			public void run() {
				mDlo.reArrageLayout();
			}
		}, delay);
	}

	private YCVideoViewLayout getAvailLayout(long streamid) {
		if (streamid == mView1Streamid) {
			return mViewLay;
		}
		if (streamid == mView2Streamid) {
			return mViewLay2;
		}
		if (mView1Streamid == 0) {
			mView1Streamid = streamid;
			return mViewLay;
		}
		if (mView2Streamid == 0) {
			mView2Streamid = streamid;
			return mViewLay2;
		}
		return null;
	}

	private void onVideoStreamStop(YCMessage.VideoStreamInfo streamInfo)
	{
		long userGroup = streamInfo.userGroupId;
		long streamId = streamInfo.streamId;
		YCLog.info(TAG, "onVideoStreamStop userGroup:%d,streamId:%d",userGroup,streamId);

		YCVideoViewLayout yl = getAvailLayout(streamId);
		YCSpVideoView view = yl.getExistingView();
		if(view != null)
		{
			view.setVisibility(View.GONE); //不可见
			view.unLinkFromStream(userGroup, streamId);
			YCMedia.getInstance().requestMethod(new YCMediaRequest.YCRemoveSpVideoView(view));
			view.release();
			if (streamId == mView1Streamid) {
				mView1Streamid = 0;
			}
			if (streamId == mView2Streamid) {
				mView2Streamid = 0;
			}
		}

		removeStreamInfo(streamId);
		updateLayout(0);
	}
    
    private void onVideoliveBroadcastNotify(YCMessage.VideoliveBroadcastInfo broadcastInfo)
    {
 
    }

    private void onVideoRenderInfo(YCMessage.VideoRenderInfo renderInfo)
    {
    	//YLog.info(this, "videoRederInfo,state:%d",renderInfo.state);
    }
    
    private void onVideoDownlinkPlr(YCMessage.VideoDownlinkPlrInfo plrInfo)
    {
    	//YLog.info(this, "videoDownlinkPlrInfo,%d %d %f",plrInfo.appid,plrInfo.uid,plrInfo.plr);
    }
    
    private void onAudioSpeakerInfo(YCMessage.AudioSpeakerInfo speakerInfo)
    {
    	//YLog.info(this, "audioSpeakerInfo,%d %d",speakerInfo.uid,speakerInfo.state);
    }
    
    private void onAudioVolumeArrived(YCMessage.AudioVolumeInfo audioVolumeInfo)
    {
    	//YLog.info(this, "audioVolume uid:%d,volume:%d",audioVolumeInfo.uid,audioVolumeInfo.volume);
    }
  
	private void onVideoCodeRateNotify(YCMessage.VideoCodeRateInfo info)
	{
		YCLog.info(this, "onVideoCodeRateNotify codeRateSize:%d", info.codeRateList.size());
	}

    private boolean isStreamExist(Long streamId)
    {
    	for(YCMessage.VideoStreamInfo streamInfo : mCurStreamList)
    	{
    		if(streamInfo.streamId == streamId)
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    private void updateStreamState(long streamId,int state)
    {
    	for(int i=0;i<mCurStreamList.size();i++)
    	{
    		if(mCurStreamList.get(i).streamId == streamId)
    		{
    			mCurStreamList.get(i).state = state;
    			return;
    		}
    	}
    }
    
    private void removeStreamInfo(Long streamId)
    {
    	for(int i=0;i<mCurStreamList.size();i++)
    	{
    		if(mCurStreamList.get(i).streamId == streamId)
    		{
    			mCurStreamList.remove(i);
    			return;
    		}
    	}
    }
    
    public ChannelVideoLayoutController(YCVideoViewLayout viewLay, YCVideoViewLayout viewLay2, DoubleLayout dlo) {

		this.mViewLay = viewLay;
		this.mViewLay2 = viewLay2;
		mDlo = dlo;

        YCMedia.getInstance().addMsgHandler(mUIHandler);
    }


    
    public void destroy()  {
    	unsubscribeVideoStreams();
    	YCMedia.getInstance().removeMsgHandler(mUIHandler);
    }

    public void doVoiceAction() {
    }

    public void unsubscribeVideoStreams() {
    	stopSubscribeVideo();
    	mCurStreamList.clear();
    	mView1Streamid = 0;
    	mView2Streamid = 0;
    }

	/**
	 * 恢复订阅视频流
	 */
	public void resumeSubscribeVideo() {
		synchronized (this) {
			for (YCMessage.VideoStreamInfo streamInfo : mCurStreamList) {
				long userGroupId = streamInfo.userGroupId;
				long streamId = streamInfo.streamId;
				YCLog.info(this, "[call] ChannelVideoController resumeSubscribeVideo for userGroup:%d, streamId:%d", userGroupId, streamId);

				YCVideoViewLayout yl = getAvailLayout(streamId);
				YCSpVideoView view = yl.clearAndCreateNewView();
				if (view != null) {
					view.setScaleMode(YCConstant.ScaleMode.AspectFit);
//					view.setScaleMode(YCConstant.ScaleMode.ClipToBounds);
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCAddSpVideoView(view));
					view.linkToStream(userGroupId, streamId);
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartSubscribeVideo(userGroupId, streamId));
					view.setVisibility(View.VISIBLE);
					YCLog.info(this, "[call] ChannelVideoController resumeSubscribeVideo resume subscribe streamId:%d", streamId);
				} else {
					YCLog.info(this, "[call] ChannelVideoController resumeSubscribeVideo no view for streamId:%d", streamId);
				}
			}
			updateLayout(0);
		}
	}

	public void onPauseSubscribeVideo() {
		synchronized (this) {
			for (YCMessage.VideoStreamInfo streamInfo : mCurStreamList) {
				try {
					long userGroupId = streamInfo.userGroupId;
					long streamId = streamInfo.streamId;
					YCLog.info(this, "[call] ChannelVideoController pauseSubscribeVideo for userGroupId:%d, streamId:%d", userGroupId, streamId);

					YCVideoViewLayout yl = getAvailLayout(streamId);
					YCSpVideoView view = yl.getExistingView();
					view.setVisibility(View.GONE); //不可见
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopSubscribeVideo(userGroupId, streamId));
					view.unLinkFromStream(userGroupId, streamId);
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCRemoveSpVideoView(view));
					if (streamId == mView1Streamid) {
						mView1Streamid = 0;
					}
					if (streamId == mView2Streamid) {
						mView2Streamid = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			updateLayout(0);
		}
	}

	/**
	 * 暂停订阅视频流
	 */
	public void stopSubscribeVideo() {
		synchronized (this) {
			for (YCMessage.VideoStreamInfo streamInfo : mCurStreamList) {
				try {
					long userGroupId = streamInfo.userGroupId;
					long streamId = streamInfo.streamId;
					YCLog.info(this, "[call] ChannelVideoController pauseSubscribeVideo for userGroupId:%d, streamId:%d", userGroupId, streamId);

					YCVideoViewLayout yl = getAvailLayout(streamId);
					YCSpVideoView view = yl.getExistingView();
					view.setVisibility(View.GONE); //不可见
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopSubscribeVideo(mUserGroupId, streamId));
					view.unLinkFromStream(userGroupId, streamId);
					YCMedia.getInstance().requestMethod(new YCMediaRequest.YCRemoveSpVideoView(view));
					view.release();
					if (streamId == mView1Streamid) {
						mView1Streamid = 0;
					}
					if (streamId == mView2Streamid) {
						mView2Streamid = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			updateLayout(0);
		}
	}
}
