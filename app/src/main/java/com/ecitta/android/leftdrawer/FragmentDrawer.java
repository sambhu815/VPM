package com.ecitta.android.leftdrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ecitta.android.support.AppConstant;
import com.ecitta.android.support.PrefManager;
import com.ecitta.android.R;
import com.ecitta.android.support.SupportUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by parth.lad on 4/13/2016.
 */
public class FragmentDrawer extends Fragment {
    private static String TAG = FragmentDrawer.class.getSimpleName();
    private AppCompatActivity activity;


    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;

    private static String[] titles = null;
    private static int[] icons = {R.drawable.ic_home, R.drawable.ic_person, R.drawable.ic_sms,
            R.drawable.ic_residence, R.drawable.ic_logout};

    private FragmentDrawerListener drawerListener;

    SharedPreferences pref;
    PrefManager manager;
    SupportUtil support;
    Intent intent;

    private LogoutListener logoutListener;
    TextView tv_email, tv_name;
    CircleImageView iv_profile;
    ImageView iv_flag;

    String str_name, str_usrname, str_profile, str_flag, str_lang, str_pic, str_type, str_cname, str_weburl;

    public FragmentDrawer() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public void setEventListener(LogoutListener listener) {
        this.logoutListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setIcon(icons[i]);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        //titles = getActivity().getResources().getStringArray(R.array.list_items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.nav_drawer, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);

        str_name = pref.getString(manager.PM_name, null);
        str_cname = pref.getString(manager.PM_companyname, null);
        str_usrname = pref.getString(manager.PM_userName, null);
        str_profile = pref.getString(manager.PM_profile, null);
        str_pic = pref.getString(manager.PM_profilepic, null);
        str_flag = pref.getString(manager.PM_flag, null);
        str_type = pref.getString(manager.PM_userType, null);
        str_lang = pref.getString(manager.PM_langID, null);
        str_weburl = pref.getString(manager.PM_image, null);


        if (str_lang.equals("0")) {
            titles = getActivity().getResources().getStringArray(R.array.list_items_port);
        } else {
            titles = getActivity().getResources().getStringArray(R.array.list_items);
        }

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        iv_profile = (CircleImageView) layout.findViewById(R.id.iv_profile);
        iv_flag = (ImageView) layout.findViewById(R.id.iv_flag);
        tv_email = (TextView) layout.findViewById(R.id.tv_email);
        tv_name = (TextView) layout.findViewById(R.id.tv_name);


        //tv_email.setText(str_usrname);

        if (str_type.equals("Company")) {
            tv_name.setText(str_name);
            String url = str_weburl + str_profile;

            if (url.isEmpty()) {
                iv_profile.setImageResource(R.drawable.ic_error);
            } else {
                Picasso.with(getActivity())
                        .load(url)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv_profile);
            }
        } else {
            tv_name.setText(str_cname);
            String url = str_weburl + str_pic;

            if (url.isEmpty()) {
                iv_profile.setImageResource(R.drawable.ic_error);
            } else {
                Picasso.with(getActivity())
                        .load(url)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv_profile);
            }
        }

        if (str_flag.isEmpty()) {
            iv_flag.setImageResource(R.drawable.ic_profile);
        } else {
            Picasso.with(getActivity())
                    .load(str_flag)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_flag);
        }

       /* tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutListener.onLogoutClicked();
                mDrawerLayout.closeDrawer(containerView);
            }
        });
*/
        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    public interface LogoutListener {
        void onLogoutClicked();
    }
}
