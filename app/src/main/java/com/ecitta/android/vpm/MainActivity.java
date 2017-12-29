package com.ecitta.android.vpm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ecitta.android.R;
import com.ecitta.android.fragment.Dashbroad_Fragment;
import com.ecitta.android.fragment.Calender_Fragment;
import com.ecitta.android.fragment.Message_Fragment;
import com.ecitta.android.fragment.Process_Fragment;
import com.ecitta.android.fragment.Profile_Fragment;
import com.ecitta.android.fragment.Residence_Fragment;
import com.ecitta.android.leftdrawer.FragmentDrawer;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.support.SupportUtil;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private FragmentDrawer drawerFragment;

    private Toolbar toolbar;

    PrefManager manager;
    SupportUtil support;
    Intent intent;
    SharedPreferences pref;
    String str_name, str_lang, str_dialog, str_yes, str_no, str_back, str_internet, str_setting;

    String str_backstack;
    boolean exit = false;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_name = pref.getString(manager.PM_name, null);
        str_lang = pref.getString(manager.PM_langID, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (str_lang.equals("0")) {
            str_back = "Clique VOLTAR de novo para sair.";
            str_internet = getResources().getString(R.string.internet_p);
            str_setting = getResources().getString(R.string.setting_);
            getSupportActionBar().setTitle("Bem Vindo, " + str_name);
        } else {
            str_back = "Please click BACK again to exit.";
            str_internet = getResources().getString(R.string.internet);
            str_setting = getResources().getString(R.string.setting_);
            getSupportActionBar().setTitle("Welcome, " + str_name);
        }

        if (!support.checkInternetConnectivity()) {
            Snackbar.make(findViewById(android.R.id.content), str_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(str_setting, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        // drawerFragment.setEventListener(this);


        /*-----------for get back from activity to fragment drawer----------------*/
        intent = getIntent();
        Fragment fragment = null;

        if (intent != null) {
            str_backstack = intent.getStringExtra("BackStack");

            if (str_backstack != null) {
                if (str_backstack.equals("1")) {
                    str_backstack = "";
                    fragment = new Residence_Fragment();
                } else if (str_backstack.equals("2")) {
                    str_backstack = "";
                    fragment = new Process_Fragment();
                } else if (str_backstack.equals("3")) {
                    str_backstack = "";
                    fragment = new Calender_Fragment();
                } else if (str_backstack.equals("4")) {
                    str_backstack = "";
                    fragment = new Profile_Fragment();
                }
            } else {
                fragment = new Dashbroad_Fragment();
            }
        } else {
            fragment = new Dashbroad_Fragment();
        }

        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String tag = null;
        String title = getString(R.string.app_name);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        switch (position) {
            case 0:
                fragment = new Dashbroad_Fragment();
                tag = Profile_Fragment.TAG;
                title = "Welcome, " + str_name;
                break;
            case 1:
                fragment = new Profile_Fragment();
                tag = Profile_Fragment.TAG;
                title = "User Profile";
                break;
            case 2:
                fragment = new Message_Fragment();
                tag = Message_Fragment.TAG;
                title = "Messages";
                break;
            case 3:
                fragment = new Residence_Fragment();
                tag = Residence_Fragment.TAG;
                title = "Residence";
                break;
            case 4:
                if (str_lang.equals("0")) {
                    str_dialog = "Tem certeza que deseja sair?";
                    str_yes = "SIM";
                    str_no = "NÃO";
                    alert();
                } else {
                    str_dialog = "Are you sure you want to logout? ";
                    str_yes = "Yes";
                    str_no = "No";
                    alert();
                }
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle(str_dialog);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.logOutUser();
                        finish();
                    }
                })
                .setNegativeButton(str_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!exit) {
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
        overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
    }
}
