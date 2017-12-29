package com.ecitta.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.vpm.TodoDetails_Activity;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 01-06-2017.
 */

public class TodoAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> todoList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    String str_lang;
    PrefManager manager;
    SharedPreferences pref;

    public TodoAdapter(Activity activity, ArrayList<HashMap<String, String>> todoList) {
        this.activity = activity;
        this.todoList = todoList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return todoList.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_todo_list, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        LinearLayout lin_todo = (LinearLayout) view.findViewById(R.id.lin_todo);

        TextView txt_date = (TextView) view.findViewById(R.id.txt_date);
        TextView txt_t = (TextView) view.findViewById(R.id.txt_t);
        TextView txt_status = (TextView) view.findViewById(R.id.txt_status);

        TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_com_id = (TextView) view.findViewById(R.id.tv_com_id);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_todo = (TextView) view.findViewById(R.id.tv_todo);
        TextView tv_status = (TextView) view.findViewById(R.id.tv_status);

        resultp = todoList.get(i);

        if (str_lang.equals("0")) {
            txt_date.setText("Data : ");
            txt_t.setText("Tarefa : ");
            txt_status.setText("Status : ");

            String value = resultp.get(AppConstant.TAG_todo_status);
            if (value.equals("Done")) {
                tv_status.setText("Resolvido");
            }
            if (value.equals("UnDone")) {
                tv_status.setText("Não Resolvido");
            }
        } else {
            tv_status.setText(resultp.get(AppConstant.TAG_todo_status));
        }

        tv_id.setText(resultp.get(AppConstant.TAG_todo_id));
        tv_com_id.setText(resultp.get(AppConstant.TAG_todo_companyId));
        tv_date.setText(resultp.get(AppConstant.TAG_calenderdate));
        tv_todo.setText(resultp.get(AppConstant.TAG_todo));

        lin_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = todoList.get(i);

                Intent in = new Intent(activity, TodoDetails_Activity.class);
                in.putExtra("todo", resultp.get(AppConstant.TAG_todo));
                in.putExtra("date", resultp.get(AppConstant.TAG_calenderdate));
                in.putExtra("status", resultp.get(AppConstant.TAG_todo_status));
                in.putExtra("info", resultp.get(AppConstant.TAG_description));
                activity.startActivity(in);
                activity.finish();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();

        return view;
    }
}
