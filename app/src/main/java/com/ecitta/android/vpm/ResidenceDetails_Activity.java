package com.ecitta.android.vpm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ecitta.android.R;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.VPM_Customer.Residence_Customer_Activity;
import com.ecitta.android.adapter.Galleyadapter;
import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.SupportUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ResidenceDetails_Activity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    PrefManager manager;
    SupportUtil support;
    SharedPreferences pref;

    EditText edt_nick, edt_place, edt_usable, edt_ready;
    EditText edt_street, edt_number, edt_apart, edt_nei, edt_city, edt_pro;
    TextInputLayout card_place, card_use, card_name, card_street, card_no, card_apart, card_nei, card_city, card_pro, card_ready;

    String str_nick, str_place, str_usable, str_ready, str_usertype;
    String str_lang, str_internet, str_yes, str_loding, str_setting, str_mesg;

    ArrayList<String> imageUrl;

    HashMap<String, String> resultp;


    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_residence_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manager = new PrefManager(this);
        support = new SupportUtil(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_lang = pref.getString(manager.PM_langID, null);

        intent = getIntent();
        str_usertype = intent.getStringExtra("user");
        resultp = (HashMap<String, String>) intent.getSerializableExtra("map");

        str_nick = resultp.get(AppConstant.TAG_nickname);

        imageUrl = (ArrayList<String>) getIntent().getSerializableExtra("imageList");
        Gallery();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (str_usertype.equals("Customer")) {
                    Intent intent = new Intent(ResidenceDetails_Activity.this, Residence_Customer_Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ResidenceDetails_Activity.this, MainActivity.class);
                    intent.putExtra("BackStack", "1");
                    startActivity(intent);
                    finish();
                }
            }
        });

        card_place = (TextInputLayout) findViewById(R.id.card_place);
        card_use = (TextInputLayout) findViewById(R.id.card_use);
        card_name = (TextInputLayout) findViewById(R.id.card_name);
        card_street = (TextInputLayout) findViewById(R.id.card_street);
        card_no = (TextInputLayout) findViewById(R.id.card_no);
        card_apart = (TextInputLayout) findViewById(R.id.card_apart);
        card_nei = (TextInputLayout) findViewById(R.id.card_nei);
        card_city = (TextInputLayout) findViewById(R.id.card_city);
        card_pro = (TextInputLayout) findViewById(R.id.card_pro);
        card_ready = (TextInputLayout) findViewById(R.id.card_ready);

        edt_street = (EditText) findViewById(R.id.edt_street);
        edt_number = (EditText) findViewById(R.id.edt_number);
        edt_apart = (EditText) findViewById(R.id.edt_apart);
        edt_nei = (EditText) findViewById(R.id.edt_nei);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_pro = (EditText) findViewById(R.id.edt_pro);

        edt_nick = (EditText) findViewById(R.id.edt_name);
        edt_place = (EditText) findViewById(R.id.edt_place);
        edt_usable = (EditText) findViewById(R.id.edt_usaplace);
        edt_ready = (EditText) findViewById(R.id.edt_ready);

        if (str_lang.equals("0")) {
            getSupportActionBar().setTitle("Detalhes da Habitação");
            str_mesg = getResources().getString(R.string.error_port);
            str_internet = getResources().getString(R.string.internet_p);
            str_loding = getResources().getString(R.string.loading_p);
            str_setting = getResources().getString(R.string.setting_p);
            str_yes = "SIM";

            card_place.setHint("Total de Vagas");
            card_use.setHint("Vagas Disponíveis");
            card_name.setHint("Apelido");
            card_street.setHint("Logradouro");
            card_no.setHint("Número");
            card_apart.setHint("Apartamento");
            card_nei.setHint("Bairro");
            card_city.setHint("Cidade");
            card_pro.setHint("Estado ou Província");
            card_ready.setHint("Disponível");

        } else {
            getSupportActionBar().setTitle("Residence Details");
            str_mesg = getResources().getString(R.string.error);
            str_internet = getResources().getString(R.string.internet);
            str_loding = getResources().getString(R.string.loading);
            str_setting = getResources().getString(R.string.setting_);
            str_yes = "Yes";
        }

        if (str_usertype.equals("Customer")) {
            edt_usable.setVisibility(View.GONE);
            edt_place.setVisibility(View.GONE);
            edt_ready.setVisibility(View.GONE);

            card_place.setVisibility(View.GONE);
            card_use.setVisibility(View.GONE);
        } else {
            card_place.setVisibility(View.VISIBLE);
            card_use.setVisibility(View.VISIBLE);

            edt_usable.setVisibility(View.VISIBLE);
            edt_place.setVisibility(View.VISIBLE);
            edt_ready.setVisibility(View.VISIBLE);

            str_place = resultp.get(AppConstant.TAG_place);
            str_usable = resultp.get(AppConstant.TAG_usableplace);
            str_ready = resultp.get(AppConstant.TAG_ready);

            if (str_lang.equals("0")) {
                if (str_ready.equals("true")) {
                    edt_ready.setText("Sim");
                } else {
                    edt_ready.setText("Não");
                }

            } else {
                if (str_ready.equals("true")) {
                    edt_ready.setText("Yes");
                } else {
                    edt_ready.setText("No");
                }
            }

            edt_place.setText(str_place);
            edt_usable.setText(str_usable);
        }

        edt_nick.setText(str_nick);
        edt_street.setText(resultp.get(AppConstant.TAG_street));
        edt_number.setText(resultp.get(AppConstant.TAG_number));
        edt_apart.setText(resultp.get(AppConstant.TAG_apartment));
        edt_nei.setText(resultp.get(AppConstant.TAG_neighborhood));
        edt_city.setText(resultp.get(AppConstant.TAG_city));
        edt_pro.setText(resultp.get(AppConstant.TAG_province));
    }

    private void Gallery() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new Galleyadapter(ResidenceDetails_Activity.this, imageUrl));

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        NUM_PAGES = imageUrl.size();

        //For Auto Start
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipetime = new Timer();
        swipetime.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);

        //pagechanger on indicater

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (str_usertype.equals("Customer")) {
            Intent intent = new Intent(ResidenceDetails_Activity.this, Residence_Customer_Activity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ResidenceDetails_Activity.this, MainActivity.class);
            intent.putExtra("BackStack", "1");
            startActivity(intent);
            finish();
        }
    }
}
