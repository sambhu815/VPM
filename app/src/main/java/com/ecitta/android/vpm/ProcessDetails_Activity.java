package com.ecitta.android.vpm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecitta.android.R;
import com.ecitta.android.VPM_Customer.Process_Customer_Activity;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.adapter.DocAdapter;
import com.ecitta.android.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProcessDetails_Activity extends AppCompatActivity {
    public static final String TAG = ProcessDetails_Activity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 1;

    Intent intent;
    PrefManager manager;
    SupportUtil support;
    SharedPreferences pref, firebase_pref;

    ListView list_doc;
    JSONArray jsonArray;
    ArrayList<HashMap<String, String>> docList;
    DocAdapter docAdapter;

    Toolbar toolbar;
    String temp, str_pro_num, str_usertype;
    String str_token_type, str_token, str_deviceId, str_error, str_mesg;

    String str_docid, str_proid, str_file_url, str_downloadfile_name;
    String str_file_name, str_file_byte, str_mime_type, str_permission, str_doc, str_install;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_download;

    File file, dir, document;

    OkHttpClient client;
    Request request;
    int status;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_process_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manager = new PrefManager(this);
        support = new SupportUtil(this);
        intent = getIntent();
        docList = new ArrayList<>();

        Bundle b = intent.getExtras();
        temp = b.getString("doclist");
        str_pro_num = intent.getStringExtra("pro_num");
        str_usertype = intent.getStringExtra("usertype");

        pref = getSharedPreferences(manager.PREF_NAME, 0);

        str_token_type = pref.getString(manager.PM_token_type, null);
        str_token = pref.getString(manager.PM_access_token, null);
        str_lang = pref.getString(manager.PM_langID, null);

        firebase_pref = getSharedPreferences("firebase", 0);
        str_deviceId = firebase_pref.getString("DeviceId", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (str_usertype.equals("Customer")) {
                    Intent intent = new Intent(ProcessDetails_Activity.this, Process_Customer_Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ProcessDetails_Activity.this, MainActivity.class);
                    intent.putExtra("BackStack", "2");
                    startActivity(intent);
                    finish();
                }
            }
        });

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Lista de Documentos");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_doc = " Documentos";
            str_install = "Por favor instale o aplicativo necessário para esse arquivo.";
            str_download = "Realizando download, favor aguardar.";
            str_permission = "Permissão negada, favor autorizar para continuar.";
            str_yes = "Está bem";
        } else {
            getSupportActionBar().setTitle("Documents List");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_doc = "Documents";
            str_download = "Downloading File, Please Wait!";
            str_install = "Please install application for open this file document.";
            str_permission = "Permission Denied, Please allow to proceed !";
            str_yes = "Ok";
        }

        list_doc = (ListView) findViewById(R.id.list_doc);

        list_doc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                str_proid = ((TextView) view.findViewById(R.id.tv_pro_id)).getText().toString();
                str_docid = ((TextView) view.findViewById(R.id.tv_doc_id)).getText().toString();
                str_file_name = ((TextView) view.findViewById(R.id.tv_docname)).getText().toString();

                String[] temp_ex = str_file_name.split("\\.");
                String extention = temp_ex[temp_ex.length - 1];

                str_mime_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);

                file = Environment.getExternalStorageDirectory();
                dir = new File(file.getAbsolutePath() + "/e-Città/" + str_doc + "/" + str_pro_num);

                str_downloadfile_name = "Doc_" + str_docid + "_" + str_proid + "_" + str_file_name;
                document = new File(dir, str_downloadfile_name);

                if (!document.exists()) {
                    if (checkpermission()) {
                        str_file_url = AppConstant.doc_details + "?processId=" + str_proid + "&docId=" + str_docid;
                        if (support.checkInternetConnectivity()) {
                            new DoumentbyID().execute();
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
                    } else {
                        requestPermission();
                    }
                } else {

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/e-Città/" + str_doc + "/" + str_pro_num + "/" + str_downloadfile_name);
                    Uri uri = Uri.fromFile(file);

                    Intent in = new Intent(Intent.ACTION_VIEW);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.setDataAndType(uri, str_mime_type);
                    try {
                        startActivity(in);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), str_install, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        GetDoc();
    }

    private void GetDoc() {
        try {
            jsonArray = new JSONArray(temp);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String pro_id = jsonObject.getString(AppConstant.TAG_process_id);
                String doc_id = jsonObject.getString(AppConstant.TAG_doc_id);
                String doc_name = jsonObject.getString(AppConstant.TAG_documentname);
                String doc_date = jsonObject.getString(AppConstant.TAG_updateddate);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = df.parse(doc_date);
                df.setTimeZone(TimeZone.getDefault());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String fdate = sdf.format(d);
                Log.e("date :", fdate);

                String[] temp_ex = doc_name.split("\\.");
                String extention = temp_ex[temp_ex.length - 1];
                Log.e("extemstion :", extention);

                HashMap<String, String> doc = new HashMap<String, String>();
                doc.put(AppConstant.TAG_process_id, pro_id);
                doc.put(AppConstant.TAG_doc_id, doc_id);
                doc.put(AppConstant.TAG_documentname, doc_name);
                doc.put(AppConstant.TAG_updateddate, fdate);
                doc.put("extension", extention);

                docList.add(doc);
            }
            docAdapter = new DocAdapter(ProcessDetails_Activity.this, docList, str_pro_num, str_usertype);
            list_doc.setAdapter(docAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class DoumentbyID extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(ProcessDetails_Activity.this);
            pd.setMessage(str_download);
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient.Builder b = new OkHttpClient.Builder();
                b.readTimeout(15, TimeUnit.SECONDS);
                b.writeTimeout(15, TimeUnit.SECONDS);

                client = b.build();

                request = new Request.Builder()
                        .url(str_file_url)
                        .get()
                        .addHeader(AppConstant.TAG_aurtho, str_token_type + " " + str_token)
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Document :", response.body().string());
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

                                    final String status = object.getString(AppConstant.TAG_status);

                                    if (status.equals("Error")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (pd.isShowing()) {
                                                    pd.dismiss();
                                                    Snackbar.make(findViewById(android.R.id.content), str_mesg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }
                                        });
                                    } else {
                                        str_file_name = object.getString(AppConstant.TAG_documentname);
                                        str_file_byte = object.getString(AppConstant.TAG_documentbinary);
                                        str_mime_type = object.getString(AppConstant.TAG_mimetype);

                                        try {
                                            byte[] decodestring = Base64.decode(str_file_byte, Base64.DEFAULT);

                                            file = Environment.getExternalStorageDirectory();
                                            dir = new File(file.getAbsolutePath() + "/e-Città/" + str_doc + "/" + str_pro_num);
                                            if (!dir.exists()) {
                                                dir.mkdirs();
                                            }

                                            str_downloadfile_name = "Doc_" + str_docid + "_" + str_proid + "_" + str_file_name;
                                            document = new File(dir, str_downloadfile_name);

                                            if (!document.exists()) {
                                                FileOutputStream fos = new FileOutputStream(document.getPath());
                                                fos.write(decodestring);
                                                fos.close();
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (pd.isShowing()) {
                                                        pd.dismiss();

                                                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                                                + "/e-Città/" + str_doc + "/" + str_pro_num + "/" + str_downloadfile_name);
                                                        Uri uri = Uri.fromFile(file);

                                                        Intent in = new Intent(Intent.ACTION_VIEW);
                                                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        in.setDataAndType(uri, str_mime_type);
                                                        try {
                                                            startActivity(in);
                                                        } catch (ActivityNotFoundException e) {
                                                            Toast.makeText(getApplicationContext(), str_install, Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            });

                                        } catch (final Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.e(TAG, "error: " + e.getMessage());
                                                }
                                            });
                                        }
                                    }
                                } catch (final JSONException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                                            str_error = "Json parsing error: ";
                                            if (pd.isShowing()) {
                                                pd.dismiss();
                                                new InserErrorLog().execute();
                                            }
                                        }
                                    });
                                } catch (final SocketTimeoutException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            e.printStackTrace();
                                            str_error = e.getMessage();
                                            if (pd.isShowing()) {
                                                pd.dismiss();
                                                new InserErrorLog().execute();
                                            }
                                        }
                                    });
                                } catch (final IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            e.printStackTrace();
                                            str_error = e.getMessage();
                                            if (pd.isShowing()) {
                                                pd.dismiss();
                                                new InserErrorLog().execute();
                                            }
                                        }
                                    });
                                } catch (final NullPointerException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_error = e.getMessage();
                                            if (pd.isShowing()) {
                                                pd.dismiss();
                                                new InserErrorLog().execute();
                                            }
                                        }
                                    });
                                } catch (final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_error = e.getMessage();
                                            if (pd.isShowing()) {
                                                pd.dismiss();
                                                new InserErrorLog().execute();
                                            }
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

    private class InserErrorLog extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(ProcessDetails_Activity.this);
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
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessDetails_Activity.this);

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

    private boolean checkpermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DoumentbyID().execute();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), str_permission, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (str_usertype.equals("Customer")) {
            Intent intent = new Intent(ProcessDetails_Activity.this, Process_Customer_Activity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ProcessDetails_Activity.this, MainActivity.class);
            intent.putExtra("BackStack", "2");
            startActivity(intent);
            finish();
        }
    }
}
