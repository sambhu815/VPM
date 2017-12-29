package com.ecitta.android.VPM_Customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.R;
import com.ecitta.android.support.SupportUtil;

public class Home_Customer_Activity extends AppCompatActivity implements View.OnClickListener {

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref;
    Toolbar toolbar;
    boolean exit = false;
    TextView txt_profile, txt_pro, txt_resi;

    String str_name, str_lang, str_internet, str_dialog, str_back, str_yes, str_no, str_setting, str_mesg;

    LinearLayout lin_customer, lin_process, lin_residence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home__customer);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        str_name = pref.getString(AppConstant.TAG_name, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        txt_profile = (TextView) findViewById(R.id.txt_profile);
        txt_pro = (TextView) findViewById(R.id.txt_pro);
        txt_resi = (TextView) findViewById(R.id.txt_resi);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Bem Vindo, " + str_name + " !");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_back = "Clique VOLTAR de novo para sair.";
            txt_profile.setText("Perfil");
            txt_pro.setText("Processos");
            txt_resi.setText("Habitações");
        } else {
            getSupportActionBar().setTitle("Welcome, " + str_name + " !");
            str_mesg = getResources().getString(R.string.error);
            str_back = "Please click BACK again to exit.";
            str_internet = getResources().getString(R.string.internet);
        }


        lin_customer = (LinearLayout) findViewById(R.id.lin_customer);
        lin_process = (LinearLayout) findViewById(R.id.lin_process);
        lin_residence = (LinearLayout) findViewById(R.id.lin_residence);

        if (!support.checkInternetConnectivity()) {
            Snackbar.make(findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(str_setting, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }

        lin_process.setOnClickListener(this);
        lin_residence.setOnClickListener(this);
        lin_customer.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        Intent in = null;

        if (view == lin_customer) {
            in = new Intent(getApplicationContext(), Profile_Customer_Activity.class);
        } else if (view == lin_process) {
            in = new Intent(getApplicationContext(), Process_Customer_Activity.class);
        } else if (view == lin_residence) {
            in = new Intent(getApplicationContext(), Residence_Customer_Activity.class);
        }
        startActivity(in);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            if (str_lang.equals("0")) {
                str_dialog = "Tem certeza que deseja sair?";
                str_yes = "SIM";
                str_no = "NÃO";
                alert();
            } else {
                str_dialog = "Are you sure you want to logout? ";
                str_yes = "Yes";
                str_no = "No";
                alert();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Customer_Activity.this);

        alertDialogBuilder.setTitle(str_dialog);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.logOutUser();
                        finish();
                    }
                })
                .setNegativeButton(str_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!exit) {
            this.exit = true;
            Toast.makeText(this, str_back, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
        overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
    }

}
