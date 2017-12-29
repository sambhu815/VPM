package com.ecitta.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.vpm.ResidenceDetails_Activity;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 24-05-2017.
 */

public class ResidenceAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> residenceList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    ArrayList<String> imgList = new ArrayList<>();
    JSONArray jsonArray;
    String img_url, type;
    String str_lang, str_weburl;
    PrefManager manager;
    SharedPreferences pref;

    public ResidenceAdapter(Activity activity, ArrayList<HashMap<String, String>> residenceList, String type) {
        this.activity = activity;
        this.residenceList = residenceList;
        this.type = type;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return residenceList.size();
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
        view = inflater.inflate(R.layout.row_residence_list, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);
        str_weburl = pref.getString(manager.PM_image, null);

        LinearLayout Li_select = (LinearLayout) view.findViewById(R.id.Li_select);
        ImageView iv_residence = (ImageView) view.findViewById(R.id.iv_residence);

        RelativeLayout rl_use = (RelativeLayout) view.findViewById(R.id.rl_use);
        RelativeLayout rl_place = (RelativeLayout) view.findViewById(R.id.rl_place);
        RelativeLayout rl_ready = (RelativeLayout) view.findViewById(R.id.rl_ready);

        TextView txt_nn = (TextView) view.findViewById(R.id.txt_nn);
        TextView txt_add = (TextView) view.findViewById(R.id.txt_add);
        TextView txt_use = (TextView) view.findViewById(R.id.txt_use);
        TextView txt_place = (TextView) view.findViewById(R.id.txt_place);
        TextView txt_ready = (TextView) view.findViewById(R.id.txt_ready);

        TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
        TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
        TextView tv_usable = (TextView) view.findViewById(R.id.tv_usable_places);
        TextView tv_places = (TextView) view.findViewById(R.id.tv_places);
        TextView tv_ready = (TextView) view.findViewById(R.id.tv_ready);

        if (str_lang.equals("0")) {
            txt_nn.setText("Apelido : ");
            txt_add.setText("Endereço : ");
            txt_use.setText("Vagas Disponíveis : ");
            txt_place.setText("Total de Vagas : ");
            txt_ready.setText("Disponível : ");
        }

        resultp = residenceList.get(i);

        try {
            jsonArray = new JSONArray(resultp.get(AppConstant.TAG_residenceimages));

            for (int img = 0; img < jsonArray.length(); img++) {
                JSONObject job = jsonArray.getJSONObject(0);

                img_url = str_weburl + job.getString(AppConstant.TAG_image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("ImageUrl : ", "" + img_url);

        if (jsonArray.length() > 0) {

            if (img_url.isEmpty()) {
                iv_residence.setImageResource(R.drawable.ic_error);
            } else {
                Picasso.with(activity)
                        .load(img_url)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv_residence);
            }
        } else {
            iv_residence.setImageResource(R.drawable.ic_logo);
        }

        if (type.equals("Customer")) {
            rl_place.setVisibility(View.GONE);
            rl_use.setVisibility(View.GONE);
            rl_ready.setVisibility(View.GONE);
        } else {
            rl_place.setVisibility(View.VISIBLE);
            rl_use.setVisibility(View.VISIBLE);
            rl_ready.setVisibility(View.VISIBLE);
            tv_usable.setText(resultp.get(AppConstant.TAG_usableplace));
            tv_places.setText(resultp.get(AppConstant.TAG_place));
        }

        tv_id.setText(resultp.get(AppConstant.TAG_res_id));
        tv_nickname.setText(resultp.get(AppConstant.TAG_nickname));
        tv_address.setText(resultp.get(AppConstant.TAG_street) + ", " + resultp.get(AppConstant.TAG_number) + ", " + resultp.get(AppConstant.TAG_apartment) + ", "
                + resultp.get(AppConstant.TAG_neighborhood) + ", " + resultp.get(AppConstant.TAG_city) + ", " + resultp.get(AppConstant.TAG_province));

        String str_value = resultp.get(AppConstant.TAG_ready);

        if (str_lang.equals("0")) {
            if (str_value.equals("true")) {
                tv_ready.setText("Sim");
            } else {
                tv_ready.setText("Não");
            }
        } else {
            if (str_value.equals("true")) {
                tv_ready.setText("Yes");
            } else {
                tv_ready.setText("No");
            }
        }


        Li_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = residenceList.get(i);

                try {
                    jsonArray = new JSONArray(resultp.get(AppConstant.TAG_residenceimages));

                    for (int img = 0; img < jsonArray.length(); img++) {
                        JSONObject job = jsonArray.getJSONObject(img);
                        String img_url = str_weburl + job.getString(AppConstant.TAG_image);
                        imgList.add(img_url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent in = new Intent(activity, ResidenceDetails_Activity.class);
                in.putExtra("map", resultp);
                in.putExtra("imageList", imgList);
                in.putExtra("user", type);
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
