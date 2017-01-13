package com.yy.msdkquality;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.List;

import com.ycloud.live.YCMessage.VideoStreamInfo;
import com.ycloud.live.video.YCVideoView;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.YCMedia;


public class ChannelVideoController {
	private static final String TAG = "ChannelVideoController";
    private YCVideoView mVideoView = null;
    private YCVideoView mVideoView2 = null;
    private long mView1Streamid = 0; //streamid that view1 linkto
    private long mView2Streamid = 0; //streamid that view2 linkto
    private List<VideoStreamInfo> mCurStreamList = new ArrayList<VideoStreamInfo>();
    
  
	public void onVideoStreamInfoNotify(VideoStreamInfo streamInfo)
	{
		switch(streamInfo.state)
		{
			case VideoStreamInfo.Arrive:
			{
				onVideoStreamArrive(streamInfo);
			}
			break;
			case VideoStreamInfo.Start:
			{
			}
			break;
			case VideoStreamInfo.Stop:
			{
				onVideoStreamStop(streamInfo);
			}
			break;
		}
	}
	
	public void onVideoStreamArrive(VideoStreamInfo streamInfo)
	{
		long userGroupId  = streamInfo.userGroupId;
    	long streamid = streamInfo.streamId;
		Log.d(TAG, String.format("videoViewControler streamNotify recv userGroup:%d,streamId:%d", userGroupId, streamid));
		if (isStreamExist(streamid))
		{
			Log.d(TAG, String.format("videoViewControler streamNotify streamId:%d exist,do nothing", streamid));
			return;
		}
		
		YCVideoView view = getAvailVideoViewForStream(streamid);
		if(view != null)
		{
			//绑定视频流和view
			YCMedia.getInstance().requestMethod(new YCMediaRequest.YCAddVideoView(view));
			view.linkToVideo(userGroupId, streamid);
			//订阅视频流
			YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartSubscribeVideo(userGroupId, streamid));
			mCurStreamList.add(streamInfo);
			Log.d(TAG, String.format("videoViewControler streamNotify subscribe streamId:%d", streamid));
		}
		else
		{
			Log.d(TAG, String.format("videoViewControler streamNotify no view for streamId:%d", streamid));
		}

		updateViewsVisibility();	
	}
	
	private YCVideoView getAvailVideoViewForStream(long streamid)
	{
		if(mView1Streamid == 0 && mVideoView != null)
		{
			mView1Streamid = streamid;
			return mVideoView;
		}
		
//		if(mView2Streamid == 0 && mVideoView2 != null)
//		{
//			mView2Streamid = streamid;
//			return mVideoView2;
//		}
		
		return null;
	}
	
	private void updateViewsVisibility()
	{
		if(mView1Streamid == 0)
		{
			mVideoView.setVisibility(View.GONE); //不可见
		}
		else
		{
			mVideoView.setVisibility(View.VISIBLE); //可见
		}
		
		if(mView2Streamid == 0)
		{
			mVideoView2.setVisibility(View.GONE); //不可见
		}
		else
		{
			mVideoView2.setVisibility(View.VISIBLE); //可见
		}
	}

    private void onVideoStreamStop(VideoStreamInfo streamInfo)
    {
    	long userGroup = streamInfo.userGroupId;
    	long streamId = streamInfo.streamId;
    	Log.d(TAG, String.format("onVideoStreamStop userGroup:%d,streamId:%d",userGroup,streamId));

		if(mView1Streamid == streamId && mVideoView != null)
		{
			mVideoView.unlinkFromVideo(userGroup, streamId);
			mVideoView.setVisibility(View.GONE); //不可见
			mView1Streamid = 0;
		}
		
		if(mView2Streamid == streamId && mVideoView2 != null)
		{
			mVideoView2.unlinkFromVideo(userGroup, streamId);
			mVideoView2.setVisibility(View.GONE); //不可见
			mView2Streamid = 0;
		}
		
		removeStreamInfo(streamId);
    }
    
    private boolean isStreamExist(Long streamId)
    {
    	for(VideoStreamInfo streamInfo : mCurStreamList)
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
    
    public ChannelVideoController(YCVideoView view,YCVideoView view2) {
 
        mVideoView = view;
        mVideoView2 = view2;

        makeSurface(true);
    }


    
    public void destroy()  {
    	unsubscribeVideoStreams();
    	if(mVideoView != null)
    	{
    		YCMedia.getInstance().requestMethod(new YCMediaRequest.YCRemoveVideoView(mVideoView));
    	}
    	if(mVideoView2 != null)
    	{
    		YCMedia.getInstance().requestMethod(new YCMediaRequest.YCRemoveVideoView(mVideoView2));
    	}
    }
    

    private void makeSurface(boolean hasVideo) {
 
        if(mVideoView != null) {
        	mVideoView.setKeepScreenOn(true);
        	if (hasVideo) {
                mVideoView.setVisibility(View.VISIBLE);
            }
        }
        
        if(mVideoView2 != null) {
        	mVideoView2.setKeepScreenOn(true);
        	if (hasVideo) {
                mVideoView2.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public void unsubscribeVideoStreams() {
    	for(int i=0;i<mCurStreamList.size();i++)
    	{
    		VideoStreamInfo streamInfo = mCurStreamList.get(i);
    		//取消订阅视频流
    		YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopSubscribeVideo(streamInfo.userGroupId, streamInfo.streamId));
    	}
    	
    	mView1Streamid = 0;
    	mView2Streamid = 0;
    }

}
