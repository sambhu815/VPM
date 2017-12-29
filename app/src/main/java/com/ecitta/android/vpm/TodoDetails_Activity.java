package com.ecitta.android.vpm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ecitta.android.R;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

public class TodoDetails_Activity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    PrefManager manager;
    SupportUtil support;
    SharedPreferences pref;

    EditText edt_todo, edt_date, edt_status, edt_info;
    TextInputLayout hint_todo, hint_date, hint_status, hint_mesg;

    String str_todo, str_date, str_status, str_info;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_todo_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manager = new PrefManager(this);
        support = new SupportUtil(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        intent = getIntent();

        str_todo = intent.getStringExtra("todo");
        str_date = intent.getStringExtra("date");
        str_status = intent.getStringExtra("status");
        str_info = intent.getStringExtra("info");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodoDetails_Activity.this, MainActivity.class);
                intent.putExtra("BackStack", "3");
                startActivity(intent);
                finish();
            }
        });

        str_lang = pref.getString(manager.PM_langID, null);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Detalhes da Tarefa");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "SIM";
        } else {
            getSupportActionBar().setTitle("TODO Details");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_yes = "Yes";
        }

        hint_todo = (TextInputLayout) findViewById(R.id.hint_todo);
        hint_date = (TextInputLayout) findViewById(R.id.hint_date);
        hint_status = (TextInputLayout) findViewById(R.id.hint_status);
        hint_mesg = (TextInputLayout) findViewById(R.id.hint_mesg);

        edt_todo = (EditText) findViewById(R.id.edt_todo);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_status = (EditText) findViewById(R.id.edt_status);
        edt_info = (EditText) findViewById(R.id.edt_info);

        if (str_lang.equals("0")) {
            hint_todo.setHint("Tarefa");
            hint_date.setHint("Data");
            hint_status.setHint("Status");
            hint_mesg.setHint("Detalhes");

            if (str_status.equals("Done")) {
                edt_status.setText("Resolvido");
            } else {
                edt_status.setText("Não Resolvido");
            }
        } else {
            edt_status.setText(str_status);
        }

        edt_todo.setText(str_todo);
        edt_date.setText(str_date);
        edt_info.setText(str_info);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TodoDetails_Activity.this, MainActivity.class);
        intent.putExtra("BackStack", "3");
        startActivity(intent);
        finish();
    }
}
