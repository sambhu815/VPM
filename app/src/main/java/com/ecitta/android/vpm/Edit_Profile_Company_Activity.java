package com.ecitta.android.vpm;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ecitta.android.R;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Edit_Profile_Company_Activity extends AppCompatActivity {
    public static final String TAG = Edit_Profile_Company_Activity.class.getSimpleName();

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;
    RelativeLayout root_profile;

    String str_token, str_token_type, str_deviceId, str_error;
    String str_name, str_contactperson, str_username, str_telephone, str_country, str_profile, str_flag, str_payment, str_role, str_dob;
    String str_loginId, str_companyId, str_customerId, str_employeeId, str_userType, str_profilepic, dob;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg, str_cname, str_con_mesg, str_weburl;
    int status;

    EditText edt_name, edt_phone, edt_ename, edt_dob;
    TextInputLayout hint_con, hint_cno, hint_cp, hint_name, hint_dob;
    AutoCompleteTextView edt_country;
    Button btn_edit, btn_update;
    CircleImageView iv_profile;

    String[] country = {"Afghanistan", "Albania", "Algeria", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahrain", "Bangladesh", "Belarus", "Belgium",
            "Belize", "Bolivarian Republic of Venezuela", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei Darussalam", "Bulgaria", "Cambodia", "Cameroon", "Canada", "Caribbean", "Chile",
            "China", "Colombia", "Congo [DRC]", "Costa Rica", "Croatia", "Czech Republic", "Denmark", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Eritrea", "Estonia", "Ethiopia",
            "Faroe Islands", "Finland", "France", "Georgia", "Germany", "Greece", "Greenland", "Guatemala", "Haiti", "Honduras", "Hong Kong", "Hong Kong SAR", "Hungary",
            "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Korea",
            "Kuwait", "Kyrgyzstan", "Lao PDR", "Latin America", "Latvia", "Lebanon", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao SAR", "Macedonia (Former Yugoslav Republic of Macedonia)", "Malaysia",
            "Maldives", "Mali", "Malta", "Mexico", "Moldova", "Mongolia", "Montenegro", "Morocco", "Myanmar", "Nepal", "Netherlands", "New Zealand", "Nicaragua",
            "Nigeria", "Norway", "Oman", "Pakistan", "Panama", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Principality of Monaco", "Puerto Rico", "Qatar",
            "Réunion", "Romania", "Russia", "Rwanda", "Saudi Arabia", "Senegal", "Serbia", "Serbia and Montenegro (Former)", "Singapore", "Slovakia", "Slovenia", "Somalia", "South Africa",
            "Spain", "Sri Lanka", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Thailand", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "U.A.E.", "Ukraine",
            "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vietnam", "Yemen", "Zimbabwe"};

    OkHttpClient client;
    Request request;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_profile_company);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        str_name = pref.getString(manager.PM_name, null);
        str_loginId = pref.getString(manager.PM_loginID, null);
        str_companyId = pref.getString(manager.PM_companyId, null);
        str_customerId = pref.getString(manager.PM_customerId, null);
        str_employeeId = pref.getString(manager.PM_employeeId, null);
        str_userType = pref.getString(manager.PM_userType, null);
        str_contactperson = pref.getString(manager.PM_contactperson, null);
        str_username = pref.getString(manager.PM_userName, null);
        str_telephone = pref.getString(manager.PM_telephone, null);
        str_country = pref.getString(manager.PM_country, null);
        str_profile = pref.getString(manager.PM_profile, null);
        str_profilepic = pref.getString(manager.PM_profilepic, null);
        str_flag = pref.getString(manager.PM_flag, null);
        str_weburl = pref.getString(manager.PM_image, null);
        str_payment = pref.getString(manager.PM_payment, null);
        str_role = pref.getString(manager.PM_role, null);
        str_dob = pref.getString(manager.PM_dob, null);
        str_cname = pref.getString(manager.PM_companyname, null);


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
                Intent intent = new Intent(Edit_Profile_Company_Activity.this, MainActivity.class);
                intent.putExtra("BackStack", "4");
                startActivity(intent);
                finish();
            }
        });

        hint_con = (TextInputLayout) findViewById(R.id.hint_con);
        hint_cno = (TextInputLayout) findViewById(R.id.hint_cno);
        hint_cp = (TextInputLayout) findViewById(R.id.hint_cp);
        hint_name = (TextInputLayout) findViewById(R.id.hint_name);
        hint_dob = (TextInputLayout) findViewById(R.id.hint_dob);


        root_profile = (RelativeLayout) findViewById(R.id.root_profile);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_country = (AutoCompleteTextView) findViewById(R.id.edt_country);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_ename = (EditText) findViewById(R.id.edt_ename);
        edt_dob = (EditText) findViewById(R.id.edt_dob);

        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_update = (Button) findViewById(R.id.btn_update);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Alterar Perfil");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "Está bem";
            hint_con.setHint("País");
            hint_cno.setHint("Telefone de Contato");
            hint_cp.setHint("Pessoa de Contato");
            hint_name.setHint("Nome");
            hint_dob.setHint("Data de Nascimento");
            btn_update.setText("ALTERAR");
            str_con_mesg = "Favor escolher um país válido.";
        } else {
            getSupportActionBar().setTitle("Edit Profile");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_con_mesg = "Please Enter valid country name.";
            str_yes = "Ok";
        }

        iv_profile = (CircleImageView) findViewById(R.id.iv_profile);

        if (str_userType.equals("Company")) {
            hint_name.setVisibility(View.GONE);
            hint_dob.setVisibility(View.GONE);

            edt_ename.setVisibility(View.GONE);
            edt_dob.setVisibility(View.GONE);

            edt_phone.setText(str_telephone);
            edt_country.setText(str_country);
            edt_name.setText(str_contactperson);
        } else {
            hint_con.setVisibility(View.GONE);
            hint_cno.setVisibility(View.GONE);
            hint_cp.setVisibility(View.GONE);

            edt_phone.setVisibility(View.GONE);
            edt_country.setVisibility(View.GONE);
            edt_name.setVisibility(View.GONE);

            edt_ename.setText(str_name);
            edt_dob.setText(str_dob);

            edt_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Edit_Profile_Company_Activity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // set day of month , month and year value in the edit text
                            String str_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                            try {
                                Date sdfdate = sdf.parse(str_date);
                                str_dob = sdf.format(sdfdate);
                                edt_dob.setText(str_dob);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                }
            });
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        edt_country.setThreshold(1);
        edt_country.setAdapter(adapter);

        edt_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_country.getWindowToken(), 0);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (support.checkInternetConnectivity()) {
                    if (str_userType.equals("Company")) {
                        str_contactperson = edt_name.getText().toString();
                        str_telephone = edt_phone.getText().toString();
                        str_country = edt_country.getText().toString().trim();

                        /*to check the conutry name from the country array for correct value*/
                        if (Arrays.asList(country).contains(str_country)) {
                            new UpdateProfile().execute();
                        } else {
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Edit_Profile_Company_Activity.this);

                            alertDialogBuilder.setTitle(str_con_mesg);

                            alertDialogBuilder
                                    .setCancelable(true)
                                    .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    } else {
                        str_name = edt_ename.getText().toString();
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat sdf_new = new SimpleDateFormat("yyyy-MM-dd");
                            str_dob = edt_dob.getText().toString();
                            Date date = sdf.parse(str_dob);
                            dob = sdf_new.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        new UpdateEmpProfile().execute();
                    }
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
            pd = new ProgressDialog(Edit_Profile_Company_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.update_profile + "?telephone=" + str_telephone +
                                "&country=" + str_country +
                                "&contactPerson=" + str_contactperson)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

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
                                            str_contactperson = object.getString(AppConstant.TAG_contactperson);
                                            str_country = object.getString(AppConstant.TAG_country);
                                            str_telephone = object.getString(AppConstant.TAG_telephone);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new flaglogo().execute();
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
                            });
                        }
                    }
                });
            }
        }
    }

    private class UpdateEmpProfile extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Edit_Profile_Company_Activity.this);
            pd.setMessage(str_loding);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.updateEmp_profile + "?name=" + str_name +
                                "&dob=" + dob)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Update Emp Profile :", response.body().string());
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
                                            str_name = object.getString(AppConstant.TAG_name);
                                            String str = object.getString(AppConstant.TAG_dob);

                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            Date d = df.parse(str);
                                            df.setTimeZone(TimeZone.getDefault());

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            str_dob = sdf.format(d);
                                            Log.e("date :", str_dob);

                                            manager.employeeLogin(str_token, str_token_type, str_loginId, str_dob, str_cname, str_employeeId,
                                                    str_userType, str_name, str_username, str_profilepic, str_flag, str_weburl, str_role);

                                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                            in.putExtra("BackStack", "4");
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

    private class flaglogo extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Edit_Profile_Company_Activity.this);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (str_userType.equals("Company")) {
                                            manager.createLogin(str_token, str_token_type, str_name, str_contactperson, str_username, str_loginId, str_companyId,
                                                    str_customerId, str_employeeId, str_userType, str_profile, str_flag, str_weburl, str_telephone, str_country, str_payment);

                                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                            in.putExtra("BackStack", "4");
                                            startActivity(in);
                                            finish();
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

    private class InserErrorLog extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Edit_Profile_Company_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Edit_Profile_Company_Activity.this);

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
        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        in.putExtra("BackStack", "4");
        startActivity(in);
        finish();
    }
}
