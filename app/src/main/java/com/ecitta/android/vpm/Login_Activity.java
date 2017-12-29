package com.ecitta.android.vpm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecitta.android.R;
import com.ecitta.android.VPM_Customer.Home_Customer_Activity;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity {
    public static final String TAG = Login_Activity.class.getSimpleName();

    Button btn_signin;
    EditText edt_username, edt_password;
    TextInputLayout hint_email, hint_pass;
    CheckBox chk;
    RelativeLayout root_login;

    Spinner sp_language;
    String str_lang = "0";

    ImageView iv_icon;
    TextView tv_text;

    String str_username, str_password, str_name;
    String str_token, str_token_type;
    String str_companyId, str_customerId, str_loginId, str_employeeId, str_userType, str_profile, str_flag, str_role, str_dob;
    String str_firebaseToken, str_deviceId, str_weburl;
    String str_contactperson, str_telephone, str_country, str_payment;
    String str_year, str_month, str_start, str_open, str_loding, str_loginerror;
    String str_address, str_passport, str_ancient, str_airport, str_date, str_companyname, str_currency;
    String str_permission, str_per_mesg, str_grant, str_cancel, str_internet, str_back;
    String str_error_mail, str_valid_mail, str_error_pass, str_setting, str_error_permission, str_ok;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref, firebase_pref, login_pref;
    SharedPreferences.Editor editor, fire_editor;

    ProgressDialog pd;
    boolean bool_check;
    int status, int_spin;
    String str_error, str_mesg;

    OkHttpClient client;
    Request request;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    String[] permissionsRequired = new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        manager.lang(str_lang);

        firebase_pref = getSharedPreferences("firebase", 0);

        login_pref = getSharedPreferences("save", 0);
        editor = login_pref.edit();

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (Locale.getDefault().getDisplayLanguage().equals("English")) {
            str_permission = "Need Multiple Permissions.";
            str_per_mesg = "This app needs Phone and Storage permissions.";
            str_internet = getResources().getString(R.string.internet);
            str_back = "Please click BACK again to exit.";
            str_grant = "Grant";
            str_cancel = "Cancel";
            str_ok = "Ok";
            CheckPermission();
        } else {
            str_permission = "Permissões são necessárias.";
            str_per_mesg = "Esse App precisa de permissões de armazenamento.";
            str_internet = getResources().getString(R.string.internet_p);
            str_back = "Clique VOLTAR de novo para sair.";
            str_grant = "Permitir";
            str_cancel = "Cancelar";
            str_ok = "Está bem";
            CheckPermission();
        }

        str_username = pref.getString(manager.PM_userName, null);
        str_password = pref.getString(manager.PM_pass, null);

        str_firebaseToken = firebase_pref.getString("firebaseToken", null);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        root_login = (RelativeLayout) findViewById(R.id.root_login);

        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);

        hint_email = (TextInputLayout) findViewById(R.id.hint_email);
        hint_pass = (TextInputLayout) findViewById(R.id.hint_pass);

        sp_language = (Spinner) findViewById(R.id.sp_language);

        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_text = (TextView) findViewById(R.id.tv_text);

        btn_signin = (Button) findViewById(R.id.btn_signin);

        String[] filter = getResources().getStringArray(R.array.language);

        ArrayAdapter<String> dishadapter = new ArrayAdapter<String>(this, R.layout.spinner_item_login, filter);
        dishadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_language.setAdapter(dishadapter);

        sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                str_lang = "" + adapterView.getSelectedItemId();
                manager.lang(str_lang);
                editor.putInt("value", adapterView.getSelectedItemPosition());
                editor.commit();

                if (str_lang.equals("0")) {
                    hint_pass.setHint("Senha");
                    chk.setText("Manter logado");
                    btn_signin.setText("ENTRAR");
                    str_loding = "Aguarde, carregando";
                    str_loginerror = "Usuário ou Senha incorretos.";
                    str_mesg = getResources().getString(R.string.error_port);
                    str_error_mail = "Por favor insira seu Email";
                    str_valid_mail = "Por favor insira um Email válido";
                    str_error_pass = "Por favor insira senha";
                    str_error_permission = "Não foi possível obter permissões";
                    str_setting = getResources().getString(R.string.setting_p);
                    str_back = "Clique VOLTAR de novo para sair.";
                } else {
                    hint_pass.setHint("Password");
                    chk.setText("Remember me");
                    btn_signin.setText("Sign In");
                    str_loding = "Loading , Please wait";
                    str_loginerror = "Incorrect username or password.";
                    str_mesg = getResources().getString(R.string.error);
                    str_error_mail = "Please Enter Email";
                    str_back = "Please click BACK again to exit.";
                    str_valid_mail = "Enter valid Email";
                    str_error_pass = "Enter Password";
                    str_error_permission = "Unable to get Permission";
                    str_setting = getResources().getString(R.string.setting_);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        str_username = login_pref.getString("id", null);
        str_password = login_pref.getString("pass", null);
        bool_check = login_pref.getBoolean("check", false);
        int_spin = login_pref.getInt("value", 0);

        edt_username.setText(str_username);
        edt_password.setText(str_password);
        sp_language.setSelection(int_spin);

        chk = (CheckBox) findViewById(R.id.chk);

        if (bool_check == true) {
            chk.setChecked(true);
        } else {
            chk.setChecked(false);
        }

        /*------set image and text accroding to screen size----------------*/

        LinearLayout.LayoutParams params_image = null;

        if (metrics.widthPixels >= 1080 || metrics.heightPixels >= 1920) {
            params_image = new AppBarLayout.LayoutParams(250, 250);
            params_image.gravity = Gravity.CENTER;
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        } else if (metrics.widthPixels >= 720 || metrics.heightPixels >= 1280) {
            params_image = new AppBarLayout.LayoutParams(200, 200);
            params_image.gravity = Gravity.CENTER;
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        } else if (metrics.widthPixels >= 540 || metrics.heightPixels >= 903) {
            params_image = new AppBarLayout.LayoutParams(160, 160);
            params_image.gravity = Gravity.CENTER;
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        } else if (metrics.widthPixels >= 480 || metrics.heightPixels >= 800) {
            //Toast.makeText(getApplicationContext(), "wq", Toast.LENGTH_SHORT).show();
            params_image = new AppBarLayout.LayoutParams(100, 100);
            params_image.gravity = Gravity.CENTER;
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        } else if (metrics.widthPixels >= 321 || metrics.heightPixels >= 480) {
            params_image = new AppBarLayout.LayoutParams(80, 80);
            params_image.gravity = Gravity.CENTER;
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        }
        iv_icon.setLayoutParams(params_image);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                str_username = edt_username.getText().toString().trim();
                str_password = edt_password.getText().toString().trim();

                if (chk.isChecked()) {
                    editor.putString("id", str_username);
                    editor.putString("pass", str_password);
                    editor.putBoolean("check", true);
                    editor.commit();
                } else {
                    editor.putString("id", "");
                    editor.putString("pass", "");
                    editor.putBoolean("check", false);
                    editor.commit();
                }

                if (str_username.equals("") || str_username.length() <= 0) {
                    edt_username.setError(str_error_mail);
                    edt_username.requestFocus();
                } else if (!support.isValidEmail(str_username)) {
                    edt_username.setError(str_valid_mail);
                    edt_username.requestFocus();
                } else if (str_password.equals("") || str_password.length() <= 0) {
                    edt_password.setError(str_error_pass);
                    edt_password.requestFocus();
                } else {
                    if (support.checkInternetConnectivity()) {
                        new calltoken().execute();
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
            }
        });
    }

    private class calltoken extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();

                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_Grant, "password")
                        .add(AppConstant.TAG_username, str_username)
                        .add(AppConstant.TAG_password, str_password);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.login).post(requestBody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);
                status = response.code();

                if (response.code() == 400) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content), str_loginerror, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    return response.body().string();
                }
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
                                String json = response.body().string();
                                JSONObject jsonObject = new JSONObject(json);
                                Log.e("Generate Token ->", "" + jsonObject);

                                str_token = jsonObject.getString(AppConstant.TAG_access_token);
                                str_token_type = jsonObject.getString(AppConstant.TAG_token_type);
                                str_username = jsonObject.getString(AppConstant.TAG_username);


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new calllogindetails().execute();
                                    }
                                });
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
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(android.R.id.content), str_loginerror, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private class calllogindetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.login_details + "?username=" + str_username +
                                "&password=" + str_password +
                                "&firebaseToken=" + str_firebaseToken +
                                "&deviceId=" + str_deviceId +
                                "&deviceName=Android")
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "f526573f-e255-ac37-e0c5-2c5a68ba9d5e")
                        .build();

                Response responsed = client.newCall(request).execute();
                status = responsed.code();

                Log.e("URL", AppConstant.login_details + "?username=" + str_username +
                        "&password=" + str_password +
                        "&firebaseToken=" + str_firebaseToken +
                        "&deviceId=" + str_deviceId +
                        "&deviceName=Android");
                Log.e("Getting User Details ->", responsed.body().string());

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
                                String details = response.body().string();
                                JSONObject jobj = new JSONObject(details);

                                str_loginId = jobj.getString(AppConstant.TAG_loginId);
                                str_companyId = jobj.getString(AppConstant.TAG_companyId);
                                str_customerId = jobj.getString(AppConstant.TAG_customerId);
                                str_employeeId = jobj.getString(AppConstant.TAG_empoyeeId);
                                str_userType = jobj.getString(AppConstant.TAG_userType);
                                str_name = jobj.getString(AppConstant.TAG_name);
                                str_profile = jobj.getString(AppConstant.TAG_profilePic);
                                str_role = jobj.getString(AppConstant.TAG_employeerole);
                                str_companyname = jobj.getString(AppConstant.TAG_companyname);
                                str_weburl = jobj.getString(AppConstant.TAG_weburl);

                                if (str_userType.equals("Employee")) {
                                    String fdate = jobj.getString(AppConstant.TAG_dob);

                                    String timestamp = fdate.replace("/Date(", "").replace(")/", "");
                                    String[] seperated = timestamp.split("-");

                                    String temp = seperated[0];
                                    long time = Long.parseLong(temp);
                                    Date d = new Date(time);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    str_dob = sdf.format(d);
                                    Log.e("date :", str_dob);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (str_userType.equals("Customer")) {
                                            new customer_Dashborad().execute();
                                        } else {
                                            new flaglogo().execute();
                                        }
                                    }
                                });
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

    private class flaglogo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.flag_logo)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "f526573f-e255-ac37-e0c5-2c5a68ba9d5e")
                        .build();

                Response responsed = client.newCall(request).execute();
                status = responsed.code();
                Log.e("Flag Logo ->", responsed.body().string());

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
                                String details = response.body().string();
                                JSONObject jobj = new JSONObject(details);

                                str_flag = jobj.getString(AppConstant.TAG_flaglogo);
                                manager.employeeLogin(str_token, str_token_type, str_loginId, str_dob, str_companyname, str_employeeId, str_userType, str_name, str_username, str_profile, str_flag, str_weburl, str_role);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new dashboard().execute();
                                    }
                                });
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

    private class dashboard extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
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
                status = responsed.code();

                Log.e("Dashboard Details ->", responsed.body().string());

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
                                    str_contactperson = jobj.getString(AppConstant.TAG_contactperson);
                                    str_telephone = jobj.getString(AppConstant.TAG_telephone);
                                    str_country = jobj.getString(AppConstant.TAG_country);
                                    str_payment = jobj.getString(AppConstant.TAG_paymentdate);
                                    str_profile = jobj.getString(AppConstant.TAG_profilelogo);

                                    str_year = jobj.getString(AppConstant.TAG_totalprocessesyear);
                                    str_month = jobj.getString(AppConstant.TAG_totalprocessesmonth);
                                    str_start = jobj.getString(AppConstant.TAG_totalsincestarted);
                                    str_open = jobj.getString(AppConstant.TAG_openedprocesses);

                                    if (str_userType.equals("Company")) {
                                        manager.createLogin(str_token, str_token_type, str_name, str_contactperson, str_username, str_loginId, str_companyId,
                                                str_customerId, str_employeeId, str_userType, str_profile, str_flag, str_weburl, str_telephone, str_country, str_payment);
                                    }
                                    manager.process_status(str_year, str_month, str_start, str_open);

                                    Intent i = new Intent(Login_Activity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_in_right);
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

    private class customer_Dashborad extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
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
                                    str_address = jobj.getString(AppConstant.TAG_add);
                                    str_telephone = jobj.getString(AppConstant.TAG_telephone);
                                    str_passport = jobj.getString(AppConstant.TAG_passport);
                                    str_profile = jobj.getString(AppConstant.TAG_profilePic);
                                    str_country = jobj.getString(AppConstant.TAG_country);
                                    str_ancient = jobj.getString(AppConstant.TAG_ancientname);
                                    str_airport = jobj.getString(AppConstant.TAG_airport);
                                    str_date = jobj.getString(AppConstant.TAG_date);
                                    str_companyname = jobj.getString(AppConstant.TAG_companyname);
                                    str_currency = jobj.getString(AppConstant.TAG_currencyname);
                                    str_flag = jobj.getString(AppConstant.TAG_flaglogo);

                                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    Date result1 = df1.parse(str_date);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    String fdate = sdf.format(result1);
                                    Log.e("date :", fdate);

                                    manager.customerLogin(str_token, str_token_type, str_name, str_username, str_loginId, str_customerId
                                            , str_address, str_passport, str_profile, str_ancient, str_airport, str_userType, fdate
                                            , str_companyname, str_currency, str_flag, str_weburl, str_telephone, str_country);

                                    Intent i = new Intent(Login_Activity.this, Home_Customer_Activity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_in_right);
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
            pd = new ProgressDialog(Login_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_Activity.this);

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
        overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
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
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[3])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle(str_permission);
                builder.setMessage(str_per_mesg);
                builder.setPositiveButton(str_grant, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(str_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle(str_permission);
                builder.setMessage(str_per_mesg);
                builder.setPositiveButton(str_grant, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        //  Toast.makeText(getBaseContext(), "Go to Permissions to Grant Phone and Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(str_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            proceedAfterPermission();
        }

    }

    private void proceedAfterPermission() {
        // Toast.makeText(getBaseContext(), "Permissions Granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[3])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle(str_permission);
                builder.setMessage(str_per_mesg);
                builder.setPositiveButton(str_grant, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(str_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), str_error_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
}
