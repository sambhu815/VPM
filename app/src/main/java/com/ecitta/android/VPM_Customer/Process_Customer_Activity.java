package com.ecitta.android.VPM_Customer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.R;
import com.ecitta.android.adapter.ProcessAdapter;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Process_Customer_Activity extends AppCompatActivity {
    public static final String TAG = Process_Customer_Activity.class.getSimpleName();

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref, firebase_pref;

    ListView list_process;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout root_process, tv_lable;
    Toolbar toolbar;
    TextView txt_lable;

    String str_token_type, str_token, str_deviceId, str_error, str_usertype, str_mesg;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_list_mesg;
    int status;

    OkHttpClient client;
    Request request;

    ProcessAdapter processAdapter;
    ArrayList<HashMap<String, String>> procerssList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_process_customer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        procerssList = new ArrayList<>();
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);
        str_usertype = pref.getString(manager.PM_userType, null);

        firebase_pref = getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Process_Customer_Activity.this, Home_Customer_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_lable = (RelativeLayout) findViewById(R.id.tv_lable);
        txt_lable = (TextView) findViewById(R.id.txt_lable);
        root_process = (RelativeLayout) findViewById(R.id.root_process);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        list_process = (ListView) findViewById(R.id.list_process);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Processos");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_list_mesg = "Não há processos no sistema.";
            str_yes = "Está bem";
        } else {
            getSupportActionBar().setTitle("Process");

            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_list_mesg = "There is no any current process available.";
            str_yes = "Ok";
        }

        if (support.checkInternetConnectivity()) {
            new ProcessList().execute();
        } else {
            Snackbar.make(findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(str_setting, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                procerssList.clear();
                list_process.invalidate();
                list_process.refreshDrawableState();

                if (support.checkInternetConnectivity()) {
                    new ProcessList().execute();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
                            .setAction(str_setting, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(
                                            new Intent(Settings.ACTION_SETTINGS));
                                }
                            }).show();
                }
            }
        });
    }

    private class ProcessList extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Process_Customer_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.Customer_process)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Process Customer :", response.body().string());
                status = response.code();

                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd.isShowing()) {
                pd.dismiss();

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
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("Error")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_lable.setVisibility(View.VISIBLE);
                                                    txt_lable.setText(str_list_mesg);
                                                    list_process.setVisibility(View.GONE);
                                                    swipeRefreshLayout.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {
                                            tv_lable.setVisibility(View.GONE);
                                            list_process.setVisibility(View.VISIBLE);
                                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                                            JSONArray jsonArray = object.getJSONArray(AppConstant.TAG_processmodel);

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject r = jsonArray.getJSONObject(i);

                                                String id = r.getString(AppConstant.TAG_res_id);
                                                String name = r.getString(AppConstant.TAG_customername);
                                                String date = r.getString(AppConstant.TAG_dateregistered);
                                                String type = r.getString(AppConstant.TAG_typename);
                                                String res_name = r.getString(AppConstant.TAG_residencename);
                                                String situation = r.getString(AppConstant.TAG_situationname);
                                                String process_num = r.getString(AppConstant.TAG_processnumber);
                                                String process_status = r.getString(AppConstant.TAG_processstatus);
                                                String rs_total = r.getString(AppConstant.TAG_totalpayment);
                                                String rs_reaming = r.getString(AppConstant.TAG_amountremaining);
                                                String dateofclosure = r.getString(AppConstant.TAG_dateofclosure);
                                                String trackingnumber = r.getString(AppConstant.TAG_trackingnumber);
                                                String interviewdate = r.getString(AppConstant.TAG_interviewdate);

                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                Date d = df.parse(date);
                                                df.setTimeZone(TimeZone.getDefault());

                                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                                String fdate = sdf.format(d);
                                                Log.e("date :", fdate);

                                                JSONArray jarray = new JSONArray(r.getString(AppConstant.TAG_documentlist));

                                                HashMap<String, String> residence = new HashMap<String, String>();
                                                residence.put(AppConstant.TAG_res_id, id);
                                                residence.put(AppConstant.TAG_customername, name);
                                                residence.put(AppConstant.TAG_dateregistered, fdate);
                                                residence.put(AppConstant.TAG_typename, type);
                                                residence.put(AppConstant.TAG_totalpayment, rs_total);
                                                residence.put(AppConstant.TAG_amountremaining, rs_reaming);
                                                residence.put(AppConstant.TAG_residencename, res_name);
                                                residence.put(AppConstant.TAG_situationname, situation);
                                                residence.put(AppConstant.TAG_processnumber, process_num);
                                                residence.put(AppConstant.TAG_processstatus, process_status);
                                                residence.put(AppConstant.TAG_dateofclosure, dateofclosure);
                                                residence.put(AppConstant.TAG_trackingnumber, trackingnumber);
                                                residence.put(AppConstant.TAG_interviewdate, interviewdate);
                                                residence.put(AppConstant.TAG_documentlist, jarray.toString());

                                                procerssList.add(residence);
                                            }

                                            processAdapter = new ProcessAdapter(Process_Customer_Activity.this, procerssList, str_usertype);
                                            list_process.setAdapter(processAdapter);

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
                            });
                        }
                    }
                });
            }
        }
    }

    private class InserErrorLog extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Process_Customer_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Process_Customer_Activity.this);

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
                                                Toast.makeText(getApplicationContext(),
                                                        str_mesg + e.getMessage(),
                                                        Toast.LENGTH_LONG)
                                                        .show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Process_Customer_Activity.this, Home_Customer_Activity.class);
        startActivity(intent);
        finish();
    }
}
