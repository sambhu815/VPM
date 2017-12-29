package com.ecitta.android.VPM_Customer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.R;
import com.ecitta.android.support.SupportUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Profile_Customer_Activity extends AppCompatActivity {
    public static final String TAG = Profile_Customer_Activity.class.getSimpleName();

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;
    RelativeLayout root_profile;

    String str_token, str_token_type;
    String str_name, str_add, str_username, str_telephone, str_country, str_profile, str_flag, str_address;
    String str_customerId, str_userType, str_loginId;
    String str_passport, str_ancient, str_airport, str_date, str_companyname, str_rs, str_currency;
    String str_gm, str_af, str_ge, str_gn, str_deviceId, str_weburl;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg, str_error;

    TextView tv_cname, tv_wish, txt_cname;
    ImageView iv_flag;

    EditText edt_name, edt_email, edt_phone, edt_passport, edt_country, edt_address,
            edt_acientName, edt_arrival, edt_time, edt_cname;

    TextInputLayout hint_cname, hint_time, hint_airport, hint_anci, hint_add, hint_passport,
            hint_country, hint_fn, hint_mail, hint_name;

    Button btn_edit;
    CircleImageView iv_profile;
    Toolbar toolbar;
    ProgressDialog pd;
    OkHttpClient client;
    Request request;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_customer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        firebase_pref = getSharedPreferences("firebase", 0);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        str_name = pref.getString(manager.PM_name, null);
        str_customerId = pref.getString(manager.PM_customerId, null);
        str_loginId = pref.getString(manager.PM_loginID, null);
        str_userType = pref.getString(manager.PM_userType, null);
        str_add = pref.getString(manager.PM_address, null);
        str_username = pref.getString(manager.PM_userName, null);
        str_telephone = pref.getString(manager.PM_telephone, null);
        str_country = pref.getString(manager.PM_country, null);
        str_profile = pref.getString(manager.PM_profilepic, null);
        str_flag = pref.getString(manager.PM_flag, null);
        str_weburl = pref.getString(manager.PM_image, null);
        str_passport = pref.getString(manager.PM_passport, null);
        str_ancient = pref.getString(manager.PM_ancientname, null);
        str_airport = pref.getString(manager.PM_arrivalairport, null);
        str_date = pref.getString(manager.PM_arrivaldatetime, null);
        str_companyname = pref.getString(manager.PM_companyname, null);
        str_rs = pref.getString(manager.PM_currencyname, null);

        str_token = pref.getString(manager.PM_access_token, null);
        str_token_type = pref.getString(manager.PM_token_type, null);

        str_deviceId = firebase_pref.getString("DeviceId", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Customer_Activity.this, Home_Customer_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_wish = (TextView) findViewById(R.id.tv_wish);

        hint_cname = (TextInputLayout) findViewById(R.id.hint_cname);
        hint_time = (TextInputLayout) findViewById(R.id.hint_time);
        hint_airport = (TextInputLayout) findViewById(R.id.hint_airport);
        hint_anci = (TextInputLayout) findViewById(R.id.hint_anci);
        hint_add = (TextInputLayout) findViewById(R.id.hint_add);
        hint_passport = (TextInputLayout) findViewById(R.id.hint_passport);
        hint_country = (TextInputLayout) findViewById(R.id.hint_country);
        hint_fn = (TextInputLayout) findViewById(R.id.hint_fn);
        hint_mail = (TextInputLayout) findViewById(R.id.hint_mail);
        hint_name = (TextInputLayout) findViewById(R.id.hint_name);

        root_profile = (RelativeLayout) findViewById(R.id.root_profile);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_country = (EditText) findViewById(R.id.edt_country);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_passport = (EditText) findViewById(R.id.edt_passport);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_acientName = (EditText) findViewById(R.id.edt_acientName);
        edt_arrival = (EditText) findViewById(R.id.edt_arrival);
        edt_time = (EditText) findViewById(R.id.edt_time);
        edt_cname = (EditText) findViewById(R.id.edt_cname);

        tv_cname = (TextView) findViewById(R.id.tv_cname);
        txt_cname = (TextView) findViewById(R.id.txt_cname);
        iv_profile = (CircleImageView) findViewById(R.id.iv_profile);
        iv_flag = (ImageView) findViewById(R.id.iv_flag);

        btn_edit = (Button) findViewById(R.id.btn_edit);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Olá " + str_name);
            str_gm = "Bom Dia!";
            str_af = "Boa tarde!";
            str_ge = "Boa noite!";
            str_gn = "Boa noite";
            txt_cname.setText("Empresa");
            hint_cname.setHint("Nome da Empresa");
            hint_time.setHint("Data e Hora de Chegada");
            hint_airport.setHint("Aeroporto de Chegada");
            hint_anci.setHint("Nome do Ascendente Estrangeiro");
            hint_add.setHint("Endereço");
            hint_passport.setHint("Passaporte");
            hint_country.setHint("País");
            hint_fn.setHint("Telefone");
            hint_mail.setHint("E-mail");
            hint_name.setHint("Nome");
            btn_edit.setText("EDITAR");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "Está bem";
        } else {
            getSupportActionBar().setTitle("Hello " + str_name);
            str_gm = "Good Morning !";
            str_af = "Good Afternoon !";
            str_ge = "Good Evening !";
            str_gn = "Good Night !";
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_yes = "Ok";
        }

        try {
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            if (timeOfDay >= 0 && timeOfDay < 12) {
                tv_wish.setText(str_gm);
            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                tv_wish.setText(str_af);
            } else if (timeOfDay >= 16 && timeOfDay < 21) {
                tv_wish.setText(str_ge);
            } else if (timeOfDay >= 21 && timeOfDay < 24) {
                tv_wish.setText(str_gn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        edt_name.setText(str_name);
        edt_email.setText(str_username);
        edt_country.setText(str_country);
        edt_phone.setText(str_telephone);
        edt_passport.setText(str_passport);
        edt_address.setText(str_add);
        edt_acientName.setText(str_ancient);
        edt_arrival.setText(str_airport);
        edt_time.setText(str_date);
        edt_cname.setText(str_companyname);

        tv_cname.setText(str_companyname);

        if (str_profile.equals("null") || str_profile == null || str_profile.isEmpty()) {
            iv_profile.setImageResource(R.drawable.ic_error);
        } else {
            String url = str_weburl + str_profile;
            Picasso.with(this)
                    .load(url)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_profile);
        }

        if (str_flag.equals("null") || str_flag == null || str_flag.isEmpty()) {
            iv_flag.setImageResource(R.drawable.ic_logo);
        } else {
            Picasso.with(this)
                    .load(str_flag)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_flag);
        }

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), Edit_Profile_Customer_Activity.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new customer_Dashborad().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private class customer_Dashborad extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_Customer_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.Customer_dashboard_details)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response responsed = client.newCall(request).execute();
                status = responsed.code();

                Log.e("Customer Dashboard ->", responsed.body().string());

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
                                    Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
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
                                            Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });
                                } else {
                                    str_name = jobj.getString(AppConstant.TAG_name);
                                    str_username = jobj.getString(AppConstant.TAG_email);
                                    str_telephone = jobj.getString(AppConstant.TAG_telephone);
                                    str_country = jobj.getString(AppConstant.TAG_country);
                                    str_passport = jobj.getString(AppConstant.TAG_passport);
                                    str_address = jobj.getString(AppConstant.TAG_add);
                                    str_ancient = jobj.getString(AppConstant.TAG_ancientname);
                                    str_airport = jobj.getString(AppConstant.TAG_airport);
                                    str_date = jobj.getString(AppConstant.TAG_date);
                                    str_companyname = jobj.getString(AppConstant.TAG_companyname);
                                    str_profile = jobj.getString(AppConstant.TAG_profilePic);
                                    str_currency = jobj.getString(AppConstant.TAG_currencyname);
                                    str_flag = jobj.getString(AppConstant.TAG_flaglogo);

                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date d = df.parse(str_date);
                                    df.setTimeZone(TimeZone.getDefault());

                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                    String fdate = sdf.format(d);
                                    Log.e("date :", fdate);

                                    manager.customerLogin(str_token, str_token_type, str_name, str_username, str_loginId, str_customerId
                                            , str_address, str_passport, str_profile, str_ancient, str_airport, str_userType, fdate
                                            , str_companyname, str_currency, str_flag, str_weburl, str_telephone, str_country);

                                    finish();
                                    startActivity(getIntent());
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

    private class InserErrorLog extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_Customer_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Customer_Activity.this);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile_Customer_Activity.this, Home_Customer_Activity.class);
        startActivity(intent);
        finish();
    }
}
