package com.ecitta.android.support;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ecitta.android.vpm.Login_Activity;

/**
 * Created by Swapnil.Patel on 8/31/2016.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "visa";

    public final String IS_LOGIN_company = "IsLoggedIn_company";
    public final String IS_LOGIN_customer = "IsLoggedIn_customer";
    public final String IS_LOGIN_emp = "IsLoggedIn_emp";

    public final String PM_access_token = "access_token";
    public final String PM_token_type = "token_type";

    public final String PM_loginID = "loginid";
    public final String PM_companyId = "companyid";
    public final String PM_customerId = "customerid";
    public final String PM_employeeId = "employeeid";
    public final String PM_userType = "usertype";
    public final String PM_profile = "logo";
    public final String PM_flag = "flagurl";
    public final String PM_image = "weburl";

    public final String PM_telephone = "telephone";
    public final String PM_country = "country";
    public final String PM_payment = "paymentdate";

    public final String PM_userName = "userName";
    public final String PM_name = "name";
    public final String PM_contactperson = "contactperson";
    public final String PM_pass = "password";

    public final String PM_month = "totalprocessesmonth";
    public final String PM_year = "totalprocessesyear";
    public final String PM_started = "totalsincestarted";
    public final String PM_open = "openedprocesses";

    public final String PM_address = "address";
    public final String PM_passport = "passport";
    public final String PM_ancientname = "ancientname";
    public final String PM_arrivalairport = "arrivalairport";
    public final String PM_arrivaldatetime = "arrivaldatetime";
    public final String PM_companyname = "companyname";
    public final String PM_currencyname = "currencyname";
    public final String PM_flagurl = "flagurl";
    public final String PM_profilepic = "profilepic";
    public final String PM_dob = "dob";
    public final String PM_role = "role";

    public final String PM_langID = "lang";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void lang(String language) {
        editor.putString(PM_langID, language);
        editor.commit();
    }


    public void employeeLogin(String token, String token_type, String loginId, String dob, String cname, String employeeId, String userType, String name, String username, String profilepic
            , String flagurl, String image, String role) {
        editor.putBoolean(IS_LOGIN_emp, true);
        editor.putString(PM_access_token, token);
        editor.putString(PM_token_type, token_type);
        editor.putString(PM_loginID, loginId);
        editor.putString(PM_companyname, cname);
        editor.putString(PM_dob, dob);
        editor.putString(PM_employeeId, employeeId);
        editor.putString(PM_userType, userType);
        editor.putString(PM_name, name);
        editor.putString(PM_userName, username);
        editor.putString(PM_profilepic, profilepic);
        editor.putString(PM_flagurl, flagurl);
        editor.putString(PM_image, image);
        editor.putString(PM_role, role);
        editor.commit();
    }

    public void customerLogin(String token, String token_type, String name, String username, String loginId, String customerId, String add,
                              String passport, String profilepic, String ancient, String airport, String userType, String date, String cname,
                              String rs, String flagurl, String image, String phone, String country) {
        editor.putBoolean(IS_LOGIN_customer, true);
        editor.putString(PM_access_token, token);
        editor.putString(PM_token_type, token_type);
        editor.putString(PM_name, name);
        editor.putString(PM_address, add);
        editor.putString(PM_userName, username);
        editor.putString(PM_loginID, loginId);
        editor.putString(PM_customerId, customerId);
        editor.putString(PM_passport, passport);
        editor.putString(PM_profilepic, profilepic);
        editor.putString(PM_userType, userType);
        editor.putString(PM_ancientname, ancient);
        editor.putString(PM_arrivalairport, airport);
        editor.putString(PM_arrivaldatetime, date);
        editor.putString(PM_companyname, cname);
        editor.putString(PM_currencyname, rs);
        editor.putString(PM_flagurl, flagurl);
        editor.putString(PM_image, image);
        editor.putString(PM_telephone, phone);
        editor.putString(PM_country, country);

        editor.commit();
    }

    public void createLogin(String token, String token_type, String name, String contactperson, String username, String loginId, String companyID,
                            String customerId, String employeeId, String userType, String profile, String flag, String image, String telephone, String country, String payment) {
        editor.putBoolean(IS_LOGIN_company, true);
        editor.putString(PM_access_token, token);
        editor.putString(PM_token_type, token_type);
        editor.putString(PM_name, name);
        editor.putString(PM_contactperson, contactperson);
        editor.putString(PM_userName, username);
        editor.putString(PM_loginID, loginId);
        editor.putString(PM_companyId, companyID);
        editor.putString(PM_customerId, customerId);
        editor.putString(PM_employeeId, employeeId);
        editor.putString(PM_userType, userType);
        editor.putString(PM_profile, profile);
        editor.putString(PM_flag, flag);
        editor.putString(PM_image, image);
        editor.putString(PM_telephone, telephone);
        editor.putString(PM_country, country);
        editor.putString(PM_payment, payment);

        editor.commit();
    }

    public void process_status(String year, String month, String start, String open) {
        editor.putString(PM_year, year);
        editor.putString(PM_month, month);
        editor.putString(PM_started, start);
        editor.putString(PM_open, open);
        editor.commit();
    }


    public boolean isLoggedIn_compnay() {
        return pref.getBoolean(IS_LOGIN_company, false);
    }

    public boolean isLoggedIn_customer() {
        return pref.getBoolean(IS_LOGIN_customer, false);
    }

    public boolean isLoggedIn_emp() {
        return pref.getBoolean(IS_LOGIN_emp, false);
    }


    public void logOutUser() {
        editor.clear();
        editor.commit();

        Intent welcome_intent = new Intent(_context, Login_Activity.class);
        welcome_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(welcome_intent);
    }
}
