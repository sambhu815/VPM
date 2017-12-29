package com.ecitta.android.vpm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecitta.android.R;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.VPM_Customer.Home_Customer_Activity;
import com.ecitta.android.support.SupportUtil;

import java.util.Locale;

public class Splash_Activity extends AppCompatActivity {

    PrefManager manager;
    SupportUtil support;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RelativeLayout root_splash;
    ProgressBar progressBar;
    String token, android_id, str_internet, str_ok, str_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        root_splash = (RelativeLayout) findViewById(R.id.root_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        manager = new PrefManager(this);
        support = new SupportUtil(this);

        pref = getSharedPreferences("firebase", 0);
        editor = pref.edit();

        token = pref.getString("firebaseToken", null);

        if (Locale.getDefault().getDisplayLanguage().equals("English")) {
            str_internet = getResources().getString(R.string.internet);
            str_tag = "Please wait, Don't close e-Città";
            str_ok = "Ok";
        } else {
            str_internet = getResources().getString(R.string.internet_p);
            str_tag = "Favor aguardar, não feche o e-Città";
            str_ok = "Está bem";
        }
        if (support.checkInternetConnectivity()) {
            if (token == null) {
                Toast.makeText(getApplicationContext(), str_tag, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
                calltoken();
            } else {
                progressBar.setVisibility(View.GONE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);

                            Intent i;
                            if (manager.isLoggedIn_compnay()) {
                                i = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (manager.isLoggedIn_emp()) {
                                i = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (manager.isLoggedIn_customer()) {
                                i = new Intent(getApplicationContext(), Home_Customer_Activity.class);
                            } else {
                                i = new Intent(getApplicationContext(), Login_Activity.class);
                            }
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_in_right);
                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

            alertDialogBuilder.setTitle(str_internet);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        if (support.checkInternetConnectivity()) {
            if (token == null) {
                calltoken();
            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);

                            Intent i;
                            if (manager.isLoggedIn_compnay()) {
                                i = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (manager.isLoggedIn_emp()) {
                                i = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (manager.isLoggedIn_customer()) {
                                i = new Intent(getApplicationContext(), Home_Customer_Activity.class);
                            } else {
                                i = new Intent(getApplicationContext(), Login_Activity.class);
                            }
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_in_right);
                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

            alertDialogBuilder.setTitle(str_internet);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        super.onResume();
    }

    private void calltoken() {
        if (support.checkInternetConnectivity()) {

            android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("Android", "Android ID : " + android_id);

            LocalBroadcastManager.getInstance(this).registerReceiver(tokenrecriver, new IntentFilter("TokenReceiver"));
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

            alertDialogBuilder.setTitle(str_internet);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    BroadcastReceiver tokenrecriver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token = intent.getStringExtra("token");

            if (token != null) {
                editor.putString("firebaseToken", token);
                editor.putString("DeviceId", android_id);
                editor.commit();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);

                            Intent i;
                            if (manager.isLoggedIn_compnay()) {
                                i = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (manager.isLoggedIn_customer()) {
                                i = new Intent(getApplicationContext(), Home_Customer_Activity.class);
                            } else {
                                i = new Intent(getApplicationContext(), Login_Activity.class);
                            }
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_in_right);
                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    };
}
