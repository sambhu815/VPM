package com.ecitta.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.vpm.MainActivity;
import com.ecitta.android.R;
import com.ecitta.android.vpm.Edit_Profile_Company_Activity;
import com.ecitta.android.support.SupportUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Swapnil.Patel on 17-05-2017.
 */

public class Profile_Fragment extends Fragment {
    public static final String TAG = Profile_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;
    RelativeLayout root_profile;
    ScrollView scr_company, scr_emp;

    String str_token, str_token_type, str_deviceId;
    String str_name, str_contactperson, str_username, str_telephone, str_country, str_profile, str_flag, str_payment;
    String str_loginId, str_companyId, str_customerId, str_employeeId, str_userType, str_role, str_dob, str_profilepic;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg, str_error, str_weburl;

    EditText edt_name, edt_email, edt_phone, edt_country, edt_date;
    EditText edt_Ename, edt_Eemail, edt_erole, edt_edate;

    TextInputLayout hint_date, hint_con, hint_cno, hint_mail, hint_cp;
    TextInputLayout hint_ename, hint_email, hint_erole, hint_edate;

    Button btn_edit, btn_eedit;
    CircleImageView iv_profile;

    ProgressDialog pd;
    OkHttpClient client;
    Request request;
    int status;

    public Profile_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
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

        str_token = pref.getString(manager.PM_access_token, null);
        str_token_type = pref.getString(manager.PM_token_type, null);

        firebase_pref = activity.getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        scr_company = (ScrollView) rootView.findViewById(R.id.scr_company);
        scr_emp = (ScrollView) rootView.findViewById(R.id.scr_emp);

        iv_profile = (CircleImageView) rootView.findViewById(R.id.iv_profile);

        if (str_userType.equals("Company")) {
            scr_company.setVisibility(View.VISIBLE);
            scr_emp.setVisibility(View.GONE);

            hint_date = (TextInputLayout) rootView.findViewById(R.id.hint_date);
            hint_con = (TextInputLayout) rootView.findViewById(R.id.hint_con);
            hint_cno = (TextInputLayout) rootView.findViewById(R.id.hint_cno);
            hint_mail = (TextInputLayout) rootView.findViewById(R.id.hint_mail);
            hint_cp = (TextInputLayout) rootView.findViewById(R.id.hint_cp);

            root_profile = (RelativeLayout) rootView.findViewById(R.id.root_profile);
            edt_name = (EditText) rootView.findViewById(R.id.edt_name);
            edt_email = (EditText) rootView.findViewById(R.id.edt_email);
            edt_country = (EditText) rootView.findViewById(R.id.edt_country);
            edt_phone = (EditText) rootView.findViewById(R.id.edt_phone);
            edt_date = (EditText) rootView.findViewById(R.id.edt_date);

            edt_date.setText(str_payment);
            edt_phone.setText(str_telephone);
            edt_email.setText(str_username);
            edt_country.setText(str_country);
            edt_name.setText(str_contactperson);

            btn_edit = (Button) rootView.findViewById(R.id.btn_edit);

            if (str_profile.equals("null") || str_profile == null || str_profile.isEmpty()) {
                iv_profile.setImageResource(R.drawable.ic_logo);
            } else {
                String url = str_weburl + str_profile;
                Picasso.with(activity).load(url).into(iv_profile);
                Picasso.with(activity)
                        .load(url)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv_profile);
            }

            if (str_lang.equals("0")) {
                ((MainActivity) getActivity()).setActionBarTitle("Seu Perfil");
                hint_date.setHint("Dia de Pagamento");
                hint_con.setHint("País");
                hint_cno.setHint("Telefone de Contato");
                hint_mail.setHint("E-mail");
                hint_cp.setHint("Pessoa de Contato");
                btn_edit.setText("EDITAR");

                str_mesg = getResources().getString(R.string.error_port);
                str_internet = getResources().getString(R.string.internet_p);
                str_loding = getResources().getString(R.string.loading_p);
                str_setting = getResources().getString(R.string.setting_p);
                str_yes = "Está bem";
            } else {
                ((MainActivity) getActivity()).setActionBarTitle("Your Profile");
                str_mesg = getResources().getString(R.string.error);
                str_internet = getResources().getString(R.string.internet);
                str_loding = getResources().getString(R.string.loading);
                str_setting = getResources().getString(R.string.setting_);
                str_yes = "Ok";
            }

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(activity, Edit_Profile_Company_Activity.class);
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else {
            scr_company.setVisibility(View.GONE);
            scr_emp.setVisibility(View.VISIBLE);

            hint_ename = (TextInputLayout) rootView.findViewById(R.id.hint_ename);
            hint_email = (TextInputLayout) rootView.findViewById(R.id.hint_email);
            hint_erole = (TextInputLayout) rootView.findViewById(R.id.hint_erole);
            hint_edate = (TextInputLayout) rootView.findViewById(R.id.hint_edate);

            edt_Ename = (EditText) rootView.findViewById(R.id.edt_Ename);
            edt_Eemail = (EditText) rootView.findViewById(R.id.edt_Eemail);
            edt_erole = (EditText) rootView.findViewById(R.id.edt_erole);
            edt_edate = (EditText) rootView.findViewById(R.id.edt_edate);

            edt_Ename.setText(str_name);
            edt_Eemail.setText(str_username);
            edt_erole.setText(str_role);
            edt_edate.setText(str_dob);

            btn_eedit = (Button) rootView.findViewById(R.id.btn_eedit);

            if (str_profilepic.equals("null") || str_profilepic == null || str_profilepic.isEmpty()) {
                iv_profile.setImageResource(R.drawable.ic_logo);
            } else {
                String url = str_weburl + str_profilepic;
                Picasso.with(activity)
                        .load(url)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv_profile);
            }

            if (str_lang.equals("0")) {
                ((MainActivity) getActivity()).setActionBarTitle("Seu Perfil");
                btn_eedit.setText("EDITAR");
                str_mesg = getResources().getString(R.string.error_port);
                str_internet = getResources().getString(R.string.internet_p);
                str_loding = getResources().getString(R.string.loading_p);
                str_setting = getResources().getString(R.string.setting_p);
                str_yes = "Está bem";

                hint_ename.setHint("Nome");
                hint_email.setHint("E-mail");
                hint_erole.setHint("Perfil");
                hint_edate.setHint("Data de Nascimento");
            } else {
                ((MainActivity) getActivity()).setActionBarTitle("Your Profile");
                str_mesg = getResources().getString(R.string.error);
                str_internet = getResources().getString(R.string.internet);
                str_loding = getResources().getString(R.string.loading);
                str_setting = getResources().getString(R.string.setting_);
                str_yes = "Ok";
            }

            btn_eedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(activity, Edit_Profile_Company_Activity.class);
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_filter);
        item.setVisible(false);
        MenuItem item_delete = menu.findItem(R.id.action_delete);
        item_delete.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new flaglogo().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class flaglogo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(activity);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
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

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new dashboard().execute();
                                    }
                                });
                            } catch (final JSONException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                        str_error = "Json parsing error: ";
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final SocketTimeoutException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final IOException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final NullPointerException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final Exception e) {
                                activity.runOnUiThread(new Runnable() {
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
            pd = new ProgressDialog(activity);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
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
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });
                                } else {
                                    str_name = jobj.getString(AppConstant.TAG_name);
                                    str_profile = jobj.getString(AppConstant.TAG_profilelogo);
                                    str_contactperson = jobj.getString(AppConstant.TAG_contactperson);
                                    str_username = jobj.getString(AppConstant.TAG_email);
                                    str_telephone = jobj.getString(AppConstant.TAG_telephone);
                                    str_country = jobj.getString(AppConstant.TAG_country);
                                    str_payment = jobj.getString(AppConstant.TAG_paymentdate);


                                    manager.createLogin(str_token, str_token_type, str_name, str_contactperson, str_username, str_loginId, str_companyId,
                                            str_customerId, str_employeeId, str_userType, str_profile, str_flag, str_weburl, str_telephone, str_country, str_payment);

                                    Intent i = activity.getIntent();
                                    startActivity(i);
                                    activity.finish();

                                }
                            } catch (final JSONException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                        str_error = "Json parsing error: ";
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final SocketTimeoutException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final IOException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final NullPointerException e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        str_error = e.getMessage();
                                        new InserErrorLog().execute();
                                    }
                                });
                            } catch (final Exception e) {
                                activity.runOnUiThread(new Runnable() {
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
            pd = new ProgressDialog(activity);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("OK")) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    } catch (SocketTimeoutException e) {
                                        e.printStackTrace();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
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
