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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.vpm.MainActivity;
import com.ecitta.android.R;
import com.ecitta.android.adapter.ResidenceAdapter;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;

/**
 * Created by Swapnil.Patel on 17-05-2017.
 */

public class Residence_Fragment extends Fragment {
    public static final String TAG = Residence_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref;
    SharedPreferences firebase_pref;

    ListView list_residence;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tv_lable;
    RelativeLayout root_residence;
    String str_token_type, str_token, str_usertype, str_mesg;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_list_mesg;

    ResidenceAdapter residenceAdapter;
    ArrayList<HashMap<String, String>> residenceList;
    ArrayList<String> imageUrlList;

    int status;
    String str_error, str_deviceId;

    OkHttpClient client;
    Request request;

    public Residence_Fragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_residence, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        residenceList = new ArrayList<>();
        imageUrlList = new ArrayList<>();
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);

        str_lang = pref.getString(manager.PM_langID, null);

        if (str_lang.equals("0")) {
            ((MainActivity) getActivity()).setActionBarTitle("Habitação");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_list_mesg = "Não há residências cadastradas.";
            str_yes = "Está bem";
        } else {
            ((MainActivity) getActivity()).setActionBarTitle("Residence");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_list_mesg = "There is no any Residence available.";
            str_yes = "Ok";
        }
        firebase_pref = activity.getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);
        str_usertype = pref.getString(manager.PM_userType, null);

        root_residence = (RelativeLayout) rootView.findViewById(R.id.root_residence);
        tv_lable = (TextView) rootView.findViewById(R.id.tv_lable);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        list_residence = (ListView) rootView.findViewById(R.id.list_residence);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


        if (support.checkInternetConnectivity()) {
            new ResidenceList().execute();
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
                residenceList.clear();
                list_residence.invalidate();
                list_residence.refreshDrawableState();

                if (support.checkInternetConnectivity()) {
                    new ResidenceList().execute();
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
        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setVisible(false);
        MenuItem item_delete = menu.findItem(R.id.action_delete);
        item_delete.setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ResidenceList extends AsyncTask<String, String, String> {

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
                Builder b = new Builder();
                b.readTimeout(15, TimeUnit.SECONDS);
                b.writeTimeout(15, TimeUnit.SECONDS);

                client = b.build();

                request = new Request.Builder()
                        .url(AppConstant.residence_details)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e(TAG, response.body().string());
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
                        e.printStackTrace();
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
                                                    list_residence.setVisibility(View.GONE);
                                                    swipeRefreshLayout.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {
                                            tv_lable.setVisibility(View.GONE);
                                            list_residence.setVisibility(View.VISIBLE);
                                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                                            final JSONArray jsonArray = object.getJSONArray(AppConstant.TAG_residentlist);

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (jsonArray.length() == 0) {
                                                        tv_lable.setVisibility(View.VISIBLE);
                                                        tv_lable.setText(str_list_mesg);
                                                        list_residence.setVisibility(View.GONE);
                                                        swipeRefreshLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject r = jsonArray.getJSONObject(i);

                                                String id = r.getString(AppConstant.TAG_res_id);
                                                String nickname = r.getString(AppConstant.TAG_nickname);
                                                String street = r.getString(AppConstant.TAG_street);
                                                String number = r.getString(AppConstant.TAG_number);
                                                String apartment = r.getString(AppConstant.TAG_apartment);
                                                String neigh = r.getString(AppConstant.TAG_neighborhood);
                                                String city = r.getString(AppConstant.TAG_city);
                                                String province = r.getString(AppConstant.TAG_province);
                                                String place = r.getString(AppConstant.TAG_place);
                                                String ready = r.getString(AppConstant.TAG_ready);
                                                String usable = r.getString(AppConstant.TAG_usableplace);
                                                String available = r.getString(AppConstant.TAG_available);

                                                JSONArray jarray = new JSONArray(r.getString(AppConstant.TAG_residenceimages));

                                                HashMap<String, String> residence = new HashMap<String, String>();
                                                residence.put(AppConstant.TAG_res_id, id);
                                                residence.put(AppConstant.TAG_nickname, nickname);
                                                residence.put(AppConstant.TAG_street, street);
                                                residence.put(AppConstant.TAG_number, number);
                                                residence.put(AppConstant.TAG_apartment, apartment);
                                                residence.put(AppConstant.TAG_neighborhood, neigh);
                                                residence.put(AppConstant.TAG_city, city);
                                                residence.put(AppConstant.TAG_province, province);
                                                residence.put(AppConstant.TAG_place, place);
                                                residence.put(AppConstant.TAG_ready, ready);
                                                residence.put(AppConstant.TAG_available, available);
                                                residence.put(AppConstant.TAG_usableplace, usable);
                                                residence.put(AppConstant.TAG_residenceimages, jarray.toString());

                                                residenceList.add(residence);
                                            }

                                            residenceAdapter = new ResidenceAdapter(activity, residenceList, str_usertype);
                                            list_residence.setAdapter(residenceAdapter);
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
