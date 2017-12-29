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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.vpm.MainActivity;
import com.ecitta.android.R;
import com.ecitta.android.adapter.TodoAdapter;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class Calender_Fragment extends Fragment {
    public static final String TAG = Calender_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;

    TextView tv_lable;
    ListView list_calender;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout rl_search, root_todo;
    TextView tv_head;

    Dialog dialog;
    String str_sdate = null;
    String str_edate = null;
    String str_token_type, str_token;
    String str_filter = "CalenderDate";
    String str_status = "UnDone";
    String str_url, str_deviceId, str_error, str_mesg;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_list_mesg;

    int status;

    TodoAdapter todoAdapter;
    ArrayList<HashMap<String, String>> todoList;
    String[] statu;
    String[] filter;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    OkHttpClient client;
    Request request;

    public Calender_Fragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_calender, container, false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);

        firebase_pref = activity.getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        todoList = new ArrayList<>();

        str_lang = pref.getString(manager.PM_langID, null);

        if (str_lang.equals("0")) {
            ((MainActivity) getActivity()).setActionBarTitle("Suas Tarefas");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_list_mesg = "Não há tarefas agendadas.";
            str_yes = "Está bem";
        } else {
            ((MainActivity) getActivity()).setActionBarTitle("Your TO DO List");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_list_mesg = "There is no any TODO List available.";
            str_yes = "Ok";
        }

        rl_search = (RelativeLayout) rootView.findViewById(R.id.rl_search);
        root_todo = (RelativeLayout) rootView.findViewById(R.id.root_todo);

        tv_lable = (TextView) rootView.findViewById(R.id.tv_lable);

        list_calender = (ListView) rootView.findViewById(R.id.list_calender);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        currentdeate();
        nextdate();
        str_url = AppConstant.todolist;

        if (support.checkInternetConnectivity()) {
            new TodoList().execute();
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
                todoList.clear();
                list_calender.invalidate();
                list_calender.refreshDrawableState();

                if (support.checkInternetConnectivity()) {
                    new TodoList().execute();
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

    private void nextdate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date date = calendar.getTime();
        str_edate = sdf.format(date);
    }

    private void currentdeate() {
        str_sdate = sdf.format(Calendar.getInstance().getTime());
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

        final Spinner sp_status = (Spinner) dialog.findViewById(R.id.sp_status);
        final Spinner sp_filter = (Spinner) dialog.findViewById(R.id.sp_filter);

        final Button btn_search = (Button) dialog.findViewById(R.id.btn_search);
        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        if (str_lang.equals("0")) {
            statu = getResources().getStringArray(R.array.search_p);
            filter = getResources().getStringArray(R.array.search_filter_p);
            tv_lable.setText("Opções de Busca");
            tv_situation.setText("Status");
            tv_order.setText("Alterar Ordem");
            btn_search.setText("BUSCAR");
        } else {
            statu = getResources().getStringArray(R.array.search);
            filter = getResources().getStringArray(R.array.search_filter);
            tv_lable.setText("Search Option");
            tv_situation.setText("Status");
            tv_order.setText("Change Order");
            btn_search.setText("Search");
        }

        ArrayAdapter<String> dishadapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_login, statu);
        dishadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(dishadapter);

        ArrayAdapter<String> filteradapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_login, filter);
        filteradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(filteradapter);

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemId() == 0) {
                    str_status = "Undone";
                } else if (adapterView.getSelectedItemId() == 1) {
                    str_status = "All";
                } else if (adapterView.getSelectedItemId() == 2) {
                    str_status = "Done";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemId() == 0) {
                    str_filter = "CalenderDate";
                } else if (adapterView.getSelectedItemId() == 1) {
                    str_filter = "-CalenderDate";
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
                str_url = AppConstant.todolist + "?status=" + str_status + "&startDate=" + str_sdate
                        + "&endDate=" + str_edate + "&filter=" + str_filter;
                new TodoList().execute();
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

    private class TodoList extends AsyncTask<String, String, String> {

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
                Log.e("Todo List :", response.body().string());
                status = response.code();

                return response.body().string();
            } catch (
                    Exception e) {
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
                                                    tv_lable.setText(str_list_mesg);
                                                    list_calender.setVisibility(View.GONE);
                                                    swipeRefreshLayout.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {
                                            tv_lable.setVisibility(View.GONE);
                                            list_calender.setVisibility(View.VISIBLE);
                                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                                            todoList.clear();
                                            list_calender.invalidate();
                                            list_calender.refreshDrawableState();

                                            final JSONArray jsonArray = object.getJSONArray(AppConstant.TAG_calendermodel);

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (jsonArray.length() == 0) {
                                                        tv_lable.setVisibility(View.VISIBLE);
                                                        tv_lable.setText(str_list_mesg);
                                                        list_calender.setVisibility(View.GONE);
                                                        swipeRefreshLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject r = jsonArray.getJSONObject(i);

                                                String id = r.getString(AppConstant.TAG_todo_id);
                                                String comId = r.getString(AppConstant.TAG_todo_companyId);
                                                String todo = r.getString(AppConstant.TAG_todo);
                                                String date = r.getString(AppConstant.TAG_calenderdate);
                                                String todo_status = r.getString(AppConstant.TAG_todo_status);
                                                String info = r.getString(AppConstant.TAG_description);

                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                Date d = df.parse(date);
                                                df.setTimeZone(TimeZone.getDefault());

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                String fdate = sdf.format(d);
                                                Log.e("date :", fdate);

                                                HashMap<String, String> residence = new HashMap<String, String>();
                                                residence.put(AppConstant.TAG_todo_id, id);
                                                residence.put(AppConstant.TAG_todo_companyId, comId);
                                                residence.put(AppConstant.TAG_todo, todo);
                                                residence.put(AppConstant.TAG_calenderdate, fdate);
                                                residence.put(AppConstant.TAG_todo_status, todo_status);
                                                residence.put(AppConstant.TAG_description, info);

                                                todoList.add(residence);
                                            }
                                            todoAdapter = new TodoAdapter(activity, todoList);
                                            list_calender.setAdapter(todoAdapter);
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

    @Override
    public void onDetach() {
        super.onDetach();
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
