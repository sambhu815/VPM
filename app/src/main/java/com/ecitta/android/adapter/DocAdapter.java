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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 30-05-2017.
 */

public class DocAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> docList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    String str_pro_num, type;
    String str_lang;
    PrefManager manager;
    SharedPreferences pref;

    public DocAdapter(Activity activity, ArrayList<HashMap<String, String>> docList, String str_pro_num, String type) {
        this.activity = activity;
        this.docList = docList;
        this.str_pro_num = str_pro_num;
        this.type = type;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return docList.size();
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
        view = inflater.inflate(R.layout.row_doc_list, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        TextView txt_dname = (TextView) view.findViewById(R.id.txt_dname);
        TextView txt_date = (TextView) view.findViewById(R.id.txt_date);

        ImageView iv_doc = (ImageView) view.findViewById(R.id.iv_doc);

        TextView tv_docid = (TextView) view.findViewById(R.id.tv_doc_id);
        TextView tv_proic = (TextView) view.findViewById(R.id.tv_pro_id);
        TextView tv_doc = (TextView) view.findViewById(R.id.tv_docname);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);

        if (str_lang.equals("0")) {
            txt_date.setText("Data de Upload");
            txt_dname.setText("Nome do Documento");
        }

        resultp = docList.get(i);

        String ext = resultp.get("extension");

        if (ext.equalsIgnoreCase("txt")) {
            iv_doc.setImageResource(R.drawable.ic_text);
        } else if (ext.equalsIgnoreCase("pdf")) {
            iv_doc.setImageResource(R.drawable.ic_pdf);
        } else if (ext.equalsIgnoreCase("doc")) {
            iv_doc.setImageResource(R.drawable.ic_doc);
        } else if (ext.equalsIgnoreCase("png")) {
            iv_doc.setImageResource(R.drawable.ic_png);
        } else if (ext.equalsIgnoreCase("jpg")) {
            iv_doc.setImageResource(R.drawable.ic_jpg);
        } else {
            iv_doc.setImageResource(R.drawable.ic_text);
        }

        tv_docid.setText(resultp.get(AppConstant.TAG_doc_id));
        tv_proic.setText(resultp.get(AppConstant.TAG_process_id));
        tv_doc.setText(resultp.get(AppConstant.TAG_documentname));
        tv_date.setText(resultp.get(AppConstant.TAG_updateddate));

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();
        return view;
    }
}
