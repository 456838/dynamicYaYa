package com.yy.msdkquality.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.msdkquality.R;
import com.yy.msdkquality.bean.ChatEntity;

import java.util.List;


public class ChatAdpter extends BaseAdapter {

    private List<ChatEntity> mChatList;
    Context mContext;

    public ChatAdpter(Context context, List<ChatEntity> chatList) {
        mContext = context;
        mChatList = chatList;
    }

    public void setAdpter(List<ChatEntity> chatList) {
        mChatList = chatList;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mChatList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.chat, null);
        }

        ChatEntity chatEntity = mChatList.get(position);
        TextView textViewMsg = (TextView) convertView.findViewById(R.id.textview_msg);
        TextView textViewNickName = (TextView) convertView.findViewById(R.id.textview_nickname);

        String nickName = new String();
        nickName = chatEntity.getSenderName();
        nickName += ":";

        textViewNickName.setText(nickName);
        textViewMsg.setText(chatEntity.getContext());

        return convertView;
    }

}