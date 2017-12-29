package com.ecitta.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.vpm.MainActivity;
import com.ecitta.android.vpm.Process_Activity;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

/**
 * Created by Swapnil.Patel on 17-05-2017.
 */

public class Dashbroad_Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = Dashbroad_Fragment.class.getSimpleName();
    private AppCompatActivity mActivity;

    LinearLayout lin_customer, lin_process, lin_residence, lin_calender;
    RelativeLayout rl_process;
    TextView tv_total, tv_open, tv_tt, tv_op, tv_customer, tv_process, tv_residence, tv_calendar;
    String str_year, str_month, str_start, str_open, str_name, str_lang;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref;

    public Dashbroad_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(mActivity);
        manager = new PrefManager(mActivity);
        pref = mActivity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);
        str_name = pref.getString(manager.PM_name, null);

        str_year = pref.getString(manager.PM_year, null);
        str_month = pref.getString(manager.PM_month, null);
        str_start = pref.getString(manager.PM_started, null);
        str_open = pref.getString(manager.PM_open, null);

        rl_process = (RelativeLayout) rootView.findViewById(R.id.rl_process);

        tv_open = (TextView) rootView.findViewById(R.id.tv_open);
        tv_total = (TextView) rootView.findViewById(R.id.tv_total);
        tv_tt = (TextView) rootView.findViewById(R.id.tv_tt);
        tv_op = (TextView) rootView.findViewById(R.id.tv_op);
        tv_customer = (TextView) rootView.findViewById(R.id.tv_customer);
        tv_process = (TextView) rootView.findViewById(R.id.tv_process);
        tv_residence = (TextView) rootView.findViewById(R.id.tv_residence);
        tv_calendar = (TextView) rootView.findViewById(R.id.tv_calendar);

        lin_customer = (LinearLayout) rootView.findViewById(R.id.lin_customer);
        lin_process = (LinearLayout) rootView.findViewById(R.id.lin_process);
        lin_residence = (LinearLayout) rootView.findViewById(R.id.lin_residence);
        lin_calender = (LinearLayout) rootView.findViewById(R.id.lin_calender);

        lin_process.setOnClickListener(this);
        lin_residence.setOnClickListener(this);
        lin_calender.setOnClickListener(this);
        lin_customer.setOnClickListener(this);

        rl_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mActivity, Process_Activity.class);
                mActivity.startActivity(in);
                mActivity.finish();
            }
        });
        makeTotal();

        if (str_lang.equals("0")) {
            ((MainActivity) getActivity()).setActionBarTitle("Bem Vindo, " + str_name);
            tv_tt.setText("Total Proc.");
            tv_op.setText("Proc. Abertos.");
            tv_customer.setText("Clientes");
            tv_process.setText("Processos");
            tv_residence.setText("Habitações");
            tv_calendar.setText("Tarefas");
        } else {
            ((MainActivity) getActivity()).setActionBarTitle("Welcome, " + str_name);
        }

        return rootView;
    }

    private void makeTotal() {
        int int_year, int_month, int_start;

        int_year = Integer.parseInt(str_year);
        int_month = Integer.parseInt(str_month);
        int_start = Integer.parseInt(str_start);


        if (int_start < 10) {
            tv_total.setText("0" + int_start);
        } else {
            tv_total.setText("" + int_start);
        }

        int int_open = Integer.parseInt(str_open);
        if (int_open < 10) {
            tv_open.setText("0" + int_open);
        } else {
            tv_open.setText("" + int_open);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setVisible(false);
        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setVisible(false);
        MenuItem item_delete = menu.findItem(R.id.action_delete);
        item_delete.setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

        Fragment fragment = null;
        String tag = null;
        String title = getString(R.string.app_name);

        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().show();
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        switch (view.getId()) {
            case R.id.lin_customer:
                fragment = new Customer_Fragment();
                tag = Customer_Fragment.TAG;
                title = "Customer";
                break;

            case R.id.lin_process:
                fragment = new Process_Fragment();
                tag = Process_Fragment.TAG;
                title = "Process";
                break;

            case R.id.lin_residence:
                fragment = new Residence_Fragment();
                tag = Residence_Fragment.TAG;
                title = "Residence";
                break;

            case R.id.lin_calender:
                fragment = new Calender_Fragment();
                tag = Calender_Fragment.TAG;
                title = "TODO List";
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();

            mActivity.getSupportActionBar().setTitle(title);
        }
    }
}
