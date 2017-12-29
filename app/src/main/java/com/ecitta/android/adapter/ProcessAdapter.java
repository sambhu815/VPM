package com.ecitta.android.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.vpm.ProcessDetails_Activity;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Swapnil.Patel on 25-05-2017.
 */

public class ProcessAdapter extends BaseAdapter {
    Activity activity;
    String usertype;
    ArrayList<HashMap<String, String>> procerssList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    JSONArray jsonArray;
    String str_lang, str_doc, check_note;
    PrefManager manager;
    SharedPreferences pref;

    public ProcessAdapter(Activity activity, ArrayList<HashMap<String, String>> procerssList, String usertype) {
        this.activity = activity;
        this.procerssList = procerssList;
        this.usertype = usertype;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return procerssList.size();
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
        view = inflater.inflate(R.layout.row_process_list, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        LinearLayout lin_process = (LinearLayout) view.findViewById(R.id.lin_process);
        RelativeLayout rl_close = (RelativeLayout) view.findViewById(R.id.rl_close);
        RelativeLayout rl_interview = (RelativeLayout) view.findViewById(R.id.rl_interview);
        RelativeLayout rl_tracking_number = (RelativeLayout) view.findViewById(R.id.rl_tracking_number);

        TextView txt_nm = (TextView) view.findViewById(R.id.txt_nm);
        TextView txt_resi = (TextView) view.findViewById(R.id.txt_resi);
        TextView txt_pro = (TextView) view.findViewById(R.id.txt_pro);
        TextView txt_status = (TextView) view.findViewById(R.id.txt_status);
        TextView txt_date = (TextView) view.findViewById(R.id.txt_date);
        TextView txt_cdate = (TextView) view.findViewById(R.id.txt_cdate);
        TextView txt_idate = (TextView) view.findViewById(R.id.txt_idate);
        TextView txt_pn = (TextView) view.findViewById(R.id.txt_pn);
        TextView txt_tn = (TextView) view.findViewById(R.id.txt_tn);
        TextView txt_r = (TextView) view.findViewById(R.id.txt_r);
        TextView txt_tt = (TextView) view.findViewById(R.id.txt_tt);
        View view_line = (View) view.findViewById(R.id.view_line);

        TextView tv_doc = (TextView) view.findViewById(R.id.tv_doc);
        TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_type = (TextView) view.findViewById(R.id.tv_process);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        final TextView tv_cdate = (TextView) view.findViewById(R.id.tv_cdate);
        TextView tv_idate = (TextView) view.findViewById(R.id.tv_idate);
        TextView tv_residence = (TextView) view.findViewById(R.id.tv_residence);
        TextView tv_situation = (TextView) view.findViewById(R.id.tv_situation);
        TextView tv_process_num = (TextView) view.findViewById(R.id.tv_process_num);
        TextView tv_tracking_num = (TextView) view.findViewById(R.id.tv_tracking_num);
        TextView tv_status = (TextView) view.findViewById(R.id.tv_status);
        final TextView tv_note = (TextView) view.findViewById(R.id.tv_note);

        RelativeLayout rl_total = (RelativeLayout) view.findViewById(R.id.rl_total);
        RelativeLayout rl_reaming = (RelativeLayout) view.findViewById(R.id.rl_reaming);
        TextView tv_reaming = (TextView) view.findViewById(R.id.tv_reaming);
        TextView tv_total = (TextView) view.findViewById(R.id.tv_total);

        if (usertype.equals("Customer")) {
            tv_note.setVisibility(View.GONE);
        }

        if (str_lang.equals("0")) {
            txt_nm.setText("Nome : ");
            txt_resi.setText("Habitação : ");
            txt_pro.setText("Tipo de Processo : ");
            txt_status.setText("Status : ");
            txt_date.setText("Data : ");
            txt_cdate.setText("Encerramento : ");
            txt_idate.setText("Data de Entrevista : ");
            txt_pn.setText("Nr. Processo : ");
            txt_tn.setText("Código de Rastreamento : ");
            txt_r.setText("Pagamento Restante : ");
            txt_tt.setText("Valor Total : ");
            tv_note.setText("Observações : ");
            str_doc = " Documentos disponíveis";
        } else {
            str_doc = " Documents available";
        }

        resultp = procerssList.get(i);

        try {
            jsonArray = new JSONArray(resultp.get(AppConstant.TAG_documentlist));

            if (jsonArray.length() > 0) {
                tv_doc.setVisibility(View.VISIBLE);
                int size = jsonArray.length();
                tv_doc.setText(size + str_doc);
                tv_doc.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            } else {
                tv_doc.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_id.setText(resultp.get(AppConstant.TAG_cus_id));
        tv_name.setText(resultp.get(AppConstant.TAG_customername));
        tv_type.setText(resultp.get(AppConstant.TAG_typename));
        tv_date.setText(resultp.get(AppConstant.TAG_dateregistered));
        tv_residence.setText(resultp.get(AppConstant.TAG_residencename));
        tv_situation.setText(resultp.get(AppConstant.TAG_situationname));
        tv_process_num.setText(resultp.get(AppConstant.TAG_processnumber));
        tv_status.setText(resultp.get(AppConstant.TAG_processstatus));

        String close_date = resultp.get(AppConstant.TAG_dateofclosure);
        if (close_date.equals("null") || close_date == null || close_date.isEmpty()) {
            rl_close.setVisibility(View.GONE);
        } else {
            rl_close.setVisibility(View.VISIBLE);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = df.parse(close_date);

                df.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                close_date = sdf.format(d);
                tv_cdate.setText(close_date);
                tv_cdate.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String interview_date = resultp.get(AppConstant.TAG_interviewdate);
        if (interview_date.equals("null") || interview_date == null || interview_date.isEmpty()) {
            rl_interview.setVisibility(View.GONE);
        } else {
            rl_interview.setVisibility(View.VISIBLE);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = df.parse(interview_date);

                df.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                interview_date = sdf.format(d);
                tv_idate.setText(interview_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String trakicing = resultp.get(AppConstant.TAG_trackingnumber);
        if (trakicing.equals("null") || trakicing == null || trakicing.isEmpty()) {
            rl_tracking_number.setVisibility(View.GONE);
        } else {
            rl_tracking_number.setVisibility(View.VISIBLE);
            tv_tracking_num.setText(trakicing);
        }

        if (usertype.equals("Customer")) {
            rl_reaming.setVisibility(View.VISIBLE);
            rl_total.setVisibility(View.VISIBLE);

            tv_total.setText(resultp.get(AppConstant.TAG_totalpayment));
            tv_reaming.setText(resultp.get(AppConstant.TAG_amountremaining));
        } else {
            rl_reaming.setVisibility(View.GONE);
            rl_total.setVisibility(View.GONE);

            check_note = resultp.get(AppConstant.TAG_notes);

            if (check_note.equals("null") || check_note == null || check_note.isEmpty()) {
                tv_note.setVisibility(View.GONE);
            } else {
                tv_note.setVisibility(View.VISIBLE);
            }
        }

        tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = procerssList.get(i);

                final Dialog dialog = new Dialog(activity);
                Window window = dialog.getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_notes);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView tv_lable = (TextView) dialog.findViewById(R.id.tv_lable);
                TextView tv_notes = (TextView) dialog.findViewById(R.id.tv_notes);

                ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

                if (str_lang.equals("0")) {
                    tv_lable.setText("Observações");
                }
                check_note = resultp.get(AppConstant.TAG_notes);
                tv_notes.setText(check_note);

                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        lin_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = procerssList.get(i);

                try {
                    jsonArray = new JSONArray(resultp.get(AppConstant.TAG_documentlist));

                    if (jsonArray.length() > 0) {
                        Intent in = new Intent(activity, ProcessDetails_Activity.class);
                        in.putExtra("pro_num", resultp.get(AppConstant.TAG_processnumber));
                        in.putExtra("doclist", jsonArray.toString());
                        in.putExtra("usertype", usertype);
                        activity.startActivity(in);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
