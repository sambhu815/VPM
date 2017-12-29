package com.ecitta.android.vpm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.R;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Process_Activity extends AppCompatActivity {
    public static final String TAG = Process_Activity.class.getSimpleName();

    Intent in;
    PrefManager manager;
    SupportUtil support;
    SharedPreferences pref, firebase_pref;
    Toolbar toolbar;

    TextView tv_lable, tv_pro, txt_month, txt_year, txt_begin, txt_open;

    String str_token_type, str_token, str_deviceId, str_error;
    String str_year, str_moth, str_start, str_open;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg;
    int status;

    ProgressDialog pd;

    RelativeLayout root_process;
    EditText edt_total, edt_begin, edt_current_year, edt_current_month;

    OkHttpClient client;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_process);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manager = new PrefManager(this);
        support = new SupportUtil(this);
        in = getIntent();

        pref = getSharedPreferences(manager.PREF_NAME, 0);

        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);

        firebase_pref = getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);
        str_lang = pref.getString(manager.PM_langID, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Process_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        root_process = (RelativeLayout) findViewById(R.id.root_process);

        tv_lable = (TextView) findViewById(R.id.tv_lable);
        tv_pro = (TextView) findViewById(R.id.tv_pro);
        txt_month = (TextView) findViewById(R.id.txt_month);
        txt_year = (TextView) findViewById(R.id.txt_year);
        txt_begin = (TextView) findViewById(R.id.txt_begin);
        txt_open = (TextView) findViewById(R.id.txt_open);

        edt_total = (EditText) findViewById(R.id.edt_total);
        edt_begin = (EditText) findViewById(R.id.edt_begin);
        edt_current_year = (EditText) findViewById(R.id.edt_current_year);
        edt_current_month = (EditText) findViewById(R.id.edt_current_month);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Status do Processo");
            tv_lable.setText("Total de Processos");
            tv_pro.setText("Processos Abertos");
            txt_month.setText("Esse Mês");
            txt_year.setText("Esse ano");
            txt_begin.setText("Desde o Início");
            txt_open.setText("Processos Abertos");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "Está bem";
        } else {
            getSupportActionBar().setTitle("Process Status");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_yes = "Ok";
        }

        if (support.checkInternetConnectivity()) {
            new dashboard().execute();
        } else {
            Snackbar.make(root_process, str_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(str_setting, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }
    }

    private class dashboard extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Process_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.dashboard_details)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response responsed = client.newCall(request).execute();

                Log.e("Dashboard Details ->", responsed.body().string());
                status = responsed.code();

                return responsed.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(root_process, str_mesg, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (response != null) {
                            try {
                                String dash = response.body().string();
                                JSONObject jobj = new JSONObject(dash);

                                String status = jobj.getString(AppConstant.TAG_status);

                                if (status.equals("Error")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(root_process, str_mesg, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });
                                } else {

                                    str_year = jobj.getString(AppConstant.TAG_totalprocessesyear);
                                    str_moth = jobj.getString(AppConstant.TAG_totalprocessesmonth);
                                    str_start = jobj.getString(AppConstant.TAG_totalsincestarted);
                                    str_open = jobj.getString(AppConstant.TAG_openedprocesses);

                                    manager.process_status(str_year, str_moth, str_start, str_open);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            edt_begin.setText(str_start);
                                            edt_current_year.setText(str_year);
                                            edt_current_month.setText(str_moth);
                                            edt_total.setText(str_open);
                                        }
                                    });
                                }
                            } catch (final JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                        str_error = "Json parsing error: ";
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final SocketTimeoutException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final NullPointerException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            }
                        }
                    }
                });

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Process_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class InserErrorLog extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Process_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(AppConstant.error_log + "?DeviceName=Android_" + str_deviceId +
                                "&ModuleName=" + TAG +
                                "&ErrorMessage=" + str_error +
                                "&StatusCode=" + status)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Error Log :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("OK")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Process_Activity.this);

                                                    alertDialogBuilder.setTitle(str_mesg);

                                                    alertDialogBuilder
                                                            .setCancelable(true)
                                                            .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                }
                                                            });
                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.show();
                                                }
                                            });
                                        }
                                    } catch (final JSONException e) {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    } catch (SocketTimeoutException e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
