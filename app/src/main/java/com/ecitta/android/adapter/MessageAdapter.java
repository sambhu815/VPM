package com.ecitta.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.support.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 05-06-2017.
 */

public class MessageAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> messageList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public MessageAdapter(Activity activity, ArrayList<HashMap<String, String>> messageList) {
        this.activity = activity;
        this.messageList = messageList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_message_list, viewGroup, false);

        TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);

        resultp = messageList.get(i);

        tv_id.setText(resultp.get(AppConstant.TAG_msg_id));
        tv_date.setText(resultp.get(AppConstant.TAG_msg_createdon));
        tv_message.setText(resultp.get(AppConstant.TAG_message));

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();
        return view;
    }
}
