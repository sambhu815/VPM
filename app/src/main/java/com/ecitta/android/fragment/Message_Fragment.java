package com.ecitta.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.vpm.MainActivity;
import com.ecitta.android.R;
import com.ecitta.android.adapter.MessageAdapter;
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

public class Message_Fragment extends Fragment {
    public static final String TAG = Message_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    PrefManager manager;
    SharedPreferences pref, firebase_pref;
    SupportUtil support;
    RelativeLayout root_message;
    int status;

    String str_token, str_token_type, str_userId, str_messageId, str_message, str_id, str_type, str_role, str_date;
    String str_deviceId, str_error, str_mesg, str_url, str_load, str_lable, str_ok;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_dialog, str_no;
    int int_index = 1;

    ListView list_message;
    SwipeRefreshLayout swipeRefreshLayout;
    Button btn_loadmore;
    TextView tv_lable;

    ArrayList<HashMap<String, String>> messageList;
    MessageAdapter messageAdapter;

    OkHttpClient client;
    Request request;
    JSONArray jsonArray;
    int total_mesg = 0;
    MenuItem item_delete;

    public Message_Fragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        messageList = new ArrayList<>();
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        str_type = pref.getString(manager.PM_userType, null);
        str_token = pref.getString(manager.PM_access_token, null);
        str_token_type = pref.getString(manager.PM_token_type, null);
        str_userId = pref.getString(manager.PM_loginID, null);

        firebase_pref = activity.getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        root_message = (RelativeLayout) rootView.findViewById(R.id.root_message);
        list_message = (ListView) rootView.findViewById(R.id.list_message);
        tv_lable = (TextView) rootView.findViewById(R.id.tv_lable);

        if (str_lang.equals("0")) {
            ((MainActivity) getActivity()).setActionBarTitle("Notificação");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_ok = "Está bem";
            str_load = "Próximas";
            str_lable = "Não há mensagens no sistema.";
        } else {
            ((MainActivity) getActivity()).setActionBarTitle("Notification");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_ok = "Ok";
            str_load = "Load More";
            str_lable = "There is no any messages available.";
        }

        btn_loadmore = new Button(activity);
        btn_loadmore.setText(str_load);
        btn_loadmore.setTextColor(activity.getResources().getColor(R.color.white));
        btn_loadmore.setBackground(activity.getResources().getDrawable(R.drawable.loginbtn));
        list_message.addFooterView(btn_loadmore);

        if (support.checkInternetConnectivity()) {
            str_url = AppConstant.message_list + "?UserId=" + str_userId + "&pageindex=" + int_index + "&pagesize=";
            new MessageList().execute();
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
                messageList.clear();
                list_message.invalidate();
                list_message.refreshDrawableState();

                if (support.checkInternetConnectivity()) {
                    int_index = 1;
                    str_url = AppConstant.message_list + "?UserId=" + str_userId + "&pageindex=" + int_index + "&pagesize=";
                    new MessageList().execute();
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

        list_message.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {

                str_id = ((TextView) view.findViewById(R.id.tv_id)).getText().toString();
                if (str_lang.equals("0")) {
                    str_dialog = "Você realmente deseja excluir todas as mensagens?";
                    str_yes = "SIM";
                    str_no = "NÃO";
                    alert();
                } else {
                    str_dialog = "Do you really want to delete this message? ";
                    str_yes = "Yes";
                    str_no = "No";
                    alert();
                }
                return true;
            }
        });

        btn_loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int size = messageList.size();
                if (size == total_mesg) {
                    btn_loadmore.setVisibility(View.GONE);
                } else {
                    int_index++;
                    str_url = AppConstant.message_list + "?UserId=" + str_userId + "&pageindex=" + int_index + "&pagesize=";
                    new MessageList().execute();
                }
            }
        });
        return rootView;
    }

    private void alert() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);

                alertDialogBuilder.setTitle(str_dialog);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new MessageDelete().execute();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(str_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private class MessageList extends AsyncTask<String, String, String> {

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
                Log.e("Message List :", response.body().string());
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
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String list = response.body().string();
                                        jsonArray = new JSONArray(list);

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (jsonArray.length() == 0) {
                                                    tv_lable.setVisibility(View.VISIBLE);
                                                    swipeRefreshLayout.setVisibility(View.GONE);
                                                    list_message.setVisibility(View.GONE);
                                                    tv_lable.setText(str_lable);
                                                    item_delete.setVisible(false);
                                                } else {
                                                    item_delete.setVisible(true);
                                                    try {
                                                        for (int is = 0; is < jsonArray.length(); is++) {

                                                            JSONObject object = jsonArray.getJSONObject(0);
                                                            total_mesg = object.getInt("totalmessages");
                                                        }

                                                        if (total_mesg <= 25) {
                                                            btn_loadmore.setVisibility(View.GONE);
                                                        } else {
                                                            btn_loadmore.setVisibility(View.VISIBLE);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object = jsonArray.getJSONObject(i);

                                            str_message = object.getString(AppConstant.TAG_message);
                                            str_messageId = object.getString(AppConstant.TAG_msg_id);
                                            str_date = object.getString(AppConstant.TAG_msg_createdon);

                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            Date d = df.parse(str_date);
                                            df.setTimeZone(TimeZone.getDefault());

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            str_date = sdf.format(d);
                                            Log.e("date :", str_date);

                                            HashMap<String, String> residence = new HashMap<String, String>();
                                            residence.put(AppConstant.TAG_message, str_message);
                                            residence.put(AppConstant.TAG_msg_id, str_messageId);
                                            residence.put(AppConstant.TAG_msg_createdon, str_date);

                                            messageList.add(residence);
                                        }

                                        int currentposition = list_message.getFirstVisiblePosition();

                                        messageAdapter = new MessageAdapter(activity, messageList);
                                        list_message.setAdapter(messageAdapter);

                                        list_message.setSelectionFromTop(currentposition + 1, 0);

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

    private class MessageDelete extends AsyncTask<String, String, String> {

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
                        .url(AppConstant.message_delete + "/" + str_userId + "/" + str_id)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Message Delete :", response.body().string());
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
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        messageList.clear();
                                        list_message.invalidate();
                                        list_message.refreshDrawableState();

                                        String list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("true")) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_url = AppConstant.message_list + "?UserId=" + str_userId + "&pageindex=" + int_index + "&pagesize=";
                                                    new MessageList().execute();
                                                }
                                            });
                                        } else {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
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
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setVisible(false);
        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setVisible(false);
        item_delete = menu.findItem(R.id.action_delete);
        item_delete.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            if (str_type.equals("Employee")) {
                str_role = pref.getString(manager.PM_role, null);
                if (str_role.equals("Editor")) {
                    if (str_lang.equals("0")) {
                        str_dialog = "Confirma a exclusão das mensagens?";
                        str_yes = "SIM";
                        str_no = "NÃO";
                        deteleall();
                    } else {
                        str_dialog = "Do you really want to delete all messages? ";
                        str_yes = "Yes";
                        str_no = "No";
                        deteleall();
                    }
                } else {
                    if (str_lang.equals("0")) {
                        str_dialog = "Você não possui permissão para excluir mensagens.";
                        showdialog();
                    } else {
                        str_dialog = "You don't have any permission to delete all messages.";
                        showdialog();
                    }
                }
            } else {
                if (str_lang.equals("0")) {
                    str_dialog = "Confirma a exclusão das mensagens?";
                    str_yes = "SIM";
                    str_no = "NÃO";
                    deteleall();
                } else {
                    str_dialog = "Do you really want to delete all messages? ";
                    str_yes = "Yes";
                    str_no = "No";
                    deteleall();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showdialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);

                alertDialogBuilder.setTitle(str_dialog);
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton(str_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void deteleall() {
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);

        alertDialogBuilder.setTitle(str_dialog);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new MessageDeleteAll().execute();
                    }
                })
                .setNegativeButton(str_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class MessageDeleteAll extends AsyncTask<String, String, String> {

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
                        .url(AppConstant.message_delete_All + "/" + str_userId + "/true")
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Message Delete All:", response.body().string());
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
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        messageList.clear();
                                        list_message.invalidate();
                                        list_message.refreshDrawableState();

                                        String list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        String status = object.getString(AppConstant.TAG_status);

                                        if (status.equals("true")) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_url = AppConstant.message_list + "?UserId=" + str_userId + "&pageindex=" + int_index + "&pagesize=";
                                                    new MessageList().execute();
                                                }
                                            });
                                        } else {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Snackbar.make(activity.findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
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
