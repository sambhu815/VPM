package com.ecitta.android.VPM_Customer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.R;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Edit_Profile_Customer_Activity extends AppCompatActivity {
    public static final String TAG = Edit_Profile_Customer_Activity.class.getSimpleName();

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;
    RelativeLayout root_profile;
    Toolbar toolbar;

    String str_token, str_token_type, str_deviceId, str_error;
    String str_name, str_add, str_username, str_telephone, str_country, str_profile, str_flag, cdate;
    String str_customerId, str_userType, str_loginId, date, time;
    String str_passport, str_ancient, str_airport, str_date, str_companyname, str_rs;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg, str_ok, str_weburl;
    int status;
    int mYear, mMonth, mDay, mHour, mMinute;

    TextView tv_cname;
    ImageView iv_flag;
    EditText edt_phone, edt_arrival, edt_time, edt_address, edt_acientName;

    TextInputLayout hint_time, hint_airport, hint_anci, hint_add, hint_fn;
    Button btn_update;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    OkHttpClient client;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
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

        firebase_pref = getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Edit_Profile_Customer_Activity.this, Profile_Customer_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        hint_time = (TextInputLayout) findViewById(R.id.hint_time);
        hint_airport = (TextInputLayout) findViewById(R.id.hint_airport);
        hint_anci = (TextInputLayout) findViewById(R.id.hint_anci);
        hint_add = (TextInputLayout) findViewById(R.id.hint_add);
        hint_fn = (TextInputLayout) findViewById(R.id.hint_fn);

        root_profile = (RelativeLayout) findViewById(R.id.root_profile);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_arrival = (EditText) findViewById(R.id.edt_arrival);
        edt_time = (EditText) findViewById(R.id.edt_time);
        edt_acientName = (EditText) findViewById(R.id.edt_acientName);
        edt_address = (EditText) findViewById(R.id.edt_address);

        tv_cname = (TextView) findViewById(R.id.tv_cname);
        iv_flag = (ImageView) findViewById(R.id.iv_flag);

        btn_update = (Button) findViewById(R.id.btn_update);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Alterar Perfil");

            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "SIM";
            str_ok = "Está bem";
            hint_time.setHint("Data e Hora de Chegada");
            hint_airport.setHint("Aeroporto de Chegada");
            hint_anci.setHint("Nome do Ascendente Estrangeiro");
            hint_add.setHint("Endereço");
            hint_fn.setHint("Telefone");
            btn_update.setText("ALTERAR");
        } else {
            getSupportActionBar().setTitle("Edit Profile");

            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_yes = "Yes";
            str_ok = "Ok";
        }


        edt_phone.setText(str_telephone);
        edt_arrival.setText(str_airport);
        edt_time.setText(str_date);
        edt_acientName.setText(str_ancient);
        edt_address.setText(str_add);

        String[] splite = str_date.split(" ");
        date = splite[0];
        time = splite[1];

        edt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Edit_Profile_Customer_Activity.this);
                Window window = dialog.getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_date_time);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final EditText tv_date = (EditText) dialog.findViewById(R.id.tv_date);
                final EditText tv_time = (EditText) dialog.findViewById(R.id.tv_time);
                TextView txt_date = (TextView) dialog.findViewById(R.id.txt_date);
                TextView txt_time = (TextView) dialog.findViewById(R.id.txt_time);
                TextView tv_lable = (TextView) dialog.findViewById(R.id.tv_lable);

                Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

                if (str_lang.equals("0")) {
                    tv_lable.setText(" Data e Hora");
                    txt_date.setText("Selecione a data");
                    txt_time.setText("Selecione o tempo");
                }
                tv_date.setText(date);
                tv_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR); // current year
                        mMonth = c.get(Calendar.MONTH); // current month
                        mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Edit_Profile_Customer_Activity.this, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String str_temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                try {
                                    Date sdfdate = sdf.parse(str_temp);
                                    str_date = sdf.format(sdfdate);
                                    tv_date.setText(str_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    }
                });

                tv_time.setText(time);

                tv_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit_Profile_Customer_Activity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        view.setIs24HourView(true);
                                        tv_time.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = tv_date.getText().toString();
                        String time = tv_time.getText().toString();
                        str_date = date + " " + time;
                        edt_time.setText(str_date);
                        dialog.dismiss();
                    }
                });
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                str_airport = edt_arrival.getText().toString();
                str_telephone = edt_phone.getText().toString();
                str_add = edt_address.getText().toString();
                str_ancient = edt_acientName.getText().toString();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    SimpleDateFormat sdf_new = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    str_date = edt_time.getText().toString();
                    Date date = sdf.parse(str_date);
                    cdate = sdf_new.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (support.checkInternetConnectivity()) {
                    new UpdateProfile().execute();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_LONG)
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

    private class UpdateProfile extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Edit_Profile_Customer_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.Customer_update_details + "?Telephone=" + str_telephone +
                                "&arrivalairport=" + str_airport +
                                "&arrivaldatetime=" + cdate +
                                "&address=" + str_add +
                                "&ancient=" + str_ancient)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();
                Log.d("URL : ", AppConstant.Customer_update_details + "?Telephone=" + str_telephone +
                        "&arrivalairport=" + str_airport +
                        "&arrivaldatetime=" + cdate +
                        "&address=" + str_add +
                        "&ancient=" + str_ancient);

                Response response = client.newCall(request).execute();
                Log.e("Update Profile :", response.body().string());
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
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("Error")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        } else {

                                            str_airport = object.getString(AppConstant.TAG_airport);
                                            String date = object.getString(AppConstant.TAG_date);

                                            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            Date d = df1.parse(date);
                                            df1.setTimeZone(TimeZone.getDefault());

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            str_date = sdf.format(d);
                                            Log.e("date :", str_date);

                                            manager.customerLogin(str_token, str_token_type, str_name, str_username, str_loginId, str_customerId
                                                    , str_add, str_passport, str_profile, str_ancient, str_airport, str_userType, str_date
                                                    , str_companyname, str_rs, str_flag, str_weburl, str_telephone, str_country);

                                            Intent in = new Intent(getApplicationContext(), Profile_Customer_Activity.class);
                                            startActivity(in);
                                            finish();
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
                                                str_mesg = getResources().getString(R.string.error);
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
            pd = new ProgressDialog(Edit_Profile_Customer_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Edit_Profile_Customer_Activity.this);

                                                    alertDialogBuilder.setTitle(str_mesg);

                                                    alertDialogBuilder
                                                            .setCancelable(true)
                                                            .setPositiveButton(str_ok, new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(Edit_Profile_Customer_Activity.this, Profile_Customer_Activity.class);
        startActivity(intent);
        finish();
    }
}
