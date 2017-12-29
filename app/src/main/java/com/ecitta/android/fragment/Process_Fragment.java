package com.ecitta.android.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.vpm.MainActivity;
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

/**
 * Created by Swapnil.Patel on 17-05-2017.
 */

public class Process_Fragment extends Fragment {
    public static final String TAG = Process_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref, firebase_pref;

    ListView list_process;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner sp_filter, sp_status;
    RelativeLayout root_process, rl_filter, tv_lable;
    LinearLayout lin_list;

    String str_token_type, str_token, str_deviceId, str_error, str_usertype, str_mesg, str_url;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_list_mesg;
    int status;
    TextView txt_lable;

    String[] filter;
    String[] statu;

    ProcessAdapter processAdapter;
    ArrayList<HashMap<String, String>> procerssList;

    OkHttpClient client;
    Request request;

    Dialog dialog;
    String str_status = "0";
    String str_filter = "Type";

    public Process_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_process, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        procerssList = new ArrayList<>();
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);

        str_lang = pref.getString(manager.PM_langID, null);

        if (str_lang.equals("0")) {
            ((MainActivity) getActivity()).setActionBarTitle("Processos");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_list_mesg = "Não há processos no sistema.";
            str_yes = "Está bem";
        } else {
            ((MainActivity) getActivity()).setActionBarTitle("Processes");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_list_mesg = "There is no any current process available.";
            str_yes = "Ok";
        }

        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);
        str_usertype = pref.getString(manager.PM_userType, null);

        firebase_pref = activity.getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        tv_lable = (RelativeLayout) rootView.findViewById(R.id.tv_lable);
        txt_lable = (TextView) rootView.findViewById(R.id.txt_lable);
        root_process = (RelativeLayout) rootView.findViewById(R.id.root_process);
        rl_filter = (RelativeLayout) rootView.findViewById(R.id.rl_filter);
        lin_list = (LinearLayout) rootView.findViewById(R.id.lin_list);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        list_process = (ListView) rootView.findViewById(R.id.list_process);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        if (support.checkInternetConnectivity()) {
            str_url = AppConstant.process_details + "?processFilter=" + str_status + "&sortBy=" + str_filter;
            new ProcessList().execute();
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
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
                    str_url = AppConstant.process_details + "?processFilter=" + str_status + "&sortBy=" + str_filter;
                    new ProcessList().execute();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Snackbar.make(activity.findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
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

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setVisible(false);
        MenuItem item_delete = menu.findItem(R.id.action_delete);
        item_delete.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_filter) {
            showdialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showdialog() {
        dialog = new Dialog(activity);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_todo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_lable = (TextView) dialog.findViewById(R.id.tv_lable);
        TextView tv_situation = (TextView) dialog.findViewById(R.id.tv_situation);
        TextView tv_order = (TextView) dialog.findViewById(R.id.tv_order);

        sp_status = (Spinner) dialog.findViewById(R.id.sp_status);
        sp_filter = (Spinner) dialog.findViewById(R.id.sp_filter);

        final Button btn_search = (Button) dialog.findViewById(R.id.btn_search);
        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        if (str_lang.equals("0")) {
            statu = getResources().getStringArray(R.array.status_p);
            filter = getResources().getStringArray(R.array.filter_p);
            tv_lable.setText("Opções de Busca");
            tv_situation.setText("Status");
            tv_order.setText("Alterar Ordem");
            btn_search.setText("BUSCAR");
        } else {
            statu = getResources().getStringArray(R.array.status);
            filter = getResources().getStringArray(R.array.filter);
            tv_lable.setText("Search Option");
            tv_situation.setText("Status");
            tv_order.setText("Change Order");
            btn_search.setText("Search");
        }

        ArrayAdapter<String> dishadapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_login, filter);
        dishadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(dishadapter);

        ArrayAdapter<String> filteradapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_login, statu);
        filteradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(filteradapter);

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                str_status = "" + adapterView.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("postion", "" + adapterView.getSelectedItemId());
                if (adapterView.getSelectedItemId() == 0) {
                    str_filter = "Type";
                } else if (adapterView.getSelectedItemId() == 1) {
                    str_filter = "Type";
                } else if (adapterView.getSelectedItemId() == 2) {
                    str_filter = "-Type";
                } else if (adapterView.getSelectedItemId() == 3) {
                    str_filter = "DateRegistered";
                } else if (adapterView.getSelectedItemId() == 4) {
                    str_filter = "-DateRegistered";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                str_url = AppConstant.process_details + "?processFilter=" + str_status + "&sortBy=" + str_filter;
                new ProcessList().execute();
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

    private class ProcessList extends AsyncTask<String, String, String> {

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
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(str_url)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Process List :", response.body().string());
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
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("Error")) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_lable.setVisibility(View.VISIBLE);
                                                    txt_lable.setText(str_list_mesg);
                                                    lin_list.setVisibility(View.GONE);
                                                    swipeRefreshLayout.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {
                                            tv_lable.setVisibility(View.GONE);
                                            lin_list.setVisibility(View.VISIBLE);
                                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                                            procerssList.clear();
                                            list_process.invalidate();
                                            list_process.refreshDrawableState();
                                            final JSONArray jsonArray = object.getJSONArray(AppConstant.TAG_processmodel);

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (jsonArray.length() == 0) {
                                                        tv_lable.setVisibility(View.VISIBLE);
                                                        txt_lable.setText(str_list_mesg);
                                                        lin_list.setVisibility(View.GONE);
                                                        swipeRefreshLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject r = jsonArray.getJSONObject(i);

                                                String id = r.getString(AppConstant.TAG_res_id);
                                                String name = r.getString(AppConstant.TAG_customername);
                                                String res_id = r.getString(AppConstant.TAG_residence);
                                                String date = r.getString(AppConstant.TAG_dateregistered);
                                                String type = r.getString(AppConstant.TAG_typename);
                                                String res_name = r.getString(AppConstant.TAG_residencename);
                                                String situation = r.getString(AppConstant.TAG_situationname);
                                                String process_num = r.getString(AppConstant.TAG_processnumber);
                                                String process_status = r.getString(AppConstant.TAG_processstatus);
                                                String dateofclosure = r.getString(AppConstant.TAG_dateofclosure);
                                                String trackingnumber = r.getString(AppConstant.TAG_trackingnumber);
                                                String interviewdate = r.getString(AppConstant.TAG_interviewdate);
                                                String notes = r.getString(AppConstant.TAG_notes);


                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                Date d = df.parse(date);
                                                df.setTimeZone(TimeZone.getDefault());

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                String fdate = sdf.format(d);
                                                Log.e("date :", fdate);

                                                JSONArray jarray = new JSONArray(r.getString(AppConstant.TAG_documentlist));

                                                HashMap<String, String> residence = new HashMap<String, String>();
                                                residence.put(AppConstant.TAG_res_id, id);
                                                residence.put(AppConstant.TAG_customername, name);
                                                residence.put(AppConstant.TAG_residence, res_id);
                                                residence.put(AppConstant.TAG_dateregistered, fdate);
                                                residence.put(AppConstant.TAG_typename, type);
                                                residence.put(AppConstant.TAG_residencename, res_name);
                                                residence.put(AppConstant.TAG_situationname, situation);
                                                residence.put(AppConstant.TAG_processnumber, process_num);
                                                residence.put(AppConstant.TAG_processstatus, process_status);
                                                residence.put(AppConstant.TAG_dateofclosure, dateofclosure);
                                                residence.put(AppConstant.TAG_trackingnumber, trackingnumber);
                                                residence.put(AppConstant.TAG_interviewdate, interviewdate);
                                                residence.put(AppConstant.TAG_notes, notes);
                                                residence.put(AppConstant.TAG_documentlist, jarray.toString());

                                                procerssList.add(residence);
                                            }

                                            processAdapter = new ProcessAdapter(activity, procerssList, str_usertype);
                                            list_process.setAdapter(processAdapter);

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
