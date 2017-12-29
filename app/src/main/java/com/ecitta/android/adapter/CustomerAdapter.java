package com.ecitta.android.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Swapnil.Patel on 26-05-2017.
 */

public class CustomerAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> customerList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    String str_lang;
    PrefManager manager;
    SharedPreferences pref;
    String img_url, str_weburl;

    public CustomerAdapter(Activity activity, ArrayList<HashMap<String, String>> customerList) {
        this.activity = activity;
        this.customerList = customerList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return customerList.size();
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

        view = inflater.inflate(R.layout.row_customer_list, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);
        str_weburl = pref.getString(manager.PM_image, null);

        ImageView iv_customer = (ImageView) view.findViewById(R.id.iv_customer);

        TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tv_email = (TextView) view.findViewById(R.id.tv_email);
        TextView tv_passport = (TextView) view.findViewById(R.id.tv_passport);
        TextView tv_country = (TextView) view.findViewById(R.id.tv_country);

        TextView tv_nm = (TextView) view.findViewById(R.id.txt_nm);
        TextView tv_mail = (TextView) view.findViewById(R.id.txt_mail);
        TextView tv_tel = (TextView) view.findViewById(R.id.txt_tel);
        TextView tv_pas = (TextView) view.findViewById(R.id.txt_pas);
        TextView tv_con = (TextView) view.findViewById(R.id.txt_con);

        if (str_lang.equals("0")) {
            tv_nm.setText("Nome : ");
            tv_mail.setText("E-mail : ");
            tv_tel.setText("Telefone : ");
            tv_pas.setText("Nr Passaporte : ");
            tv_con.setText("País : ");
        }

        resultp = customerList.get(i);

        img_url = resultp.get(AppConstant.TAG_profilePic);
        img_url = img_url.replaceAll(" ", "%20");

        if (img_url.equals("null") || img_url == null || img_url.isEmpty()) {
            iv_customer.setImageResource(R.drawable.ic_error);
        } else {
            String url = str_weburl + img_url;
            Picasso.with(activity)
                    .load(url)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_customer);
        }

        tv_id.setText(resultp.get(AppConstant.TAG_cus_id));
        tv_name.setText(resultp.get(AppConstant.TAG_name));
        tv_phone.setText(resultp.get(AppConstant.TAG_telephone));
        tv_email.setText(resultp.get(AppConstant.TAG_email));
        tv_passport.setText(resultp.get(AppConstant.TAG_passport));
        tv_country.setText(resultp.get(AppConstant.TAG_country));

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();

        return view;
    }
}
