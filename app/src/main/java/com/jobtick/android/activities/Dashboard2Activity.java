package com.jobtick.android.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.NotificationListAdapter;
import com.jobtick.android.adapers.SectionsPagerAdapter;
import com.jobtick.android.fragments.Dashboard2PosterFragment;
import com.jobtick.android.fragments.Dashboard2TickerFragment;
import com.jobtick.android.models.ConversationModel;
import com.jobtick.android.models.notification.NotifDatum;
import com.jobtick.android.models.notification.PushNotificationModel2;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.widget.ContentWrappingViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class Dashboard2Activity extends ActivityBase implements NotificationListAdapter.OnItemClickListener, ViewPager.OnPageChangeListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_as_ticker)
    RadioButton rbAsATicker;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_as_poster)
    RadioButton rbAsAPoster;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_notifications_container)
    LinearLayout noNotifications;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ticker_poster_view_pager)
    ContentWrappingViewPager viewPager;

    NotificationListAdapter notificationListAdapter;
    private PushNotificationModel2 pushNotificationModel2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        ButterKnife.bind(this);
        initToolbar();
        initComponent();
        initNotificationList();
    }

    private void initToolbar() {
        toolbar.setTitle("Dashboard");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //we just get last 10 notifications
    private void getNotificationList() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=1",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            String jsonString = jsonObject.toString(); //http request
                            Gson gson = new Gson();
                            pushNotificationModel2 = gson.fromJson(jsonString, PushNotificationModel2.class);
                        } else {
                            showToast("something went wrong.", this);
                            checkList();
                            return;
                        }

                        notificationListAdapter.addItems(pushNotificationModel2.getData());
                        checkList();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkList();
                    }
                },
                error -> {
                    checkList();
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard2Activity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }

    private void checkList() {
        if (notificationListAdapter.getItemCount() <= 0) {
            noNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noNotifications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initNotificationList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Dashboard2Activity.this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        notificationListAdapter = new NotificationListAdapter(new ArrayList<>());
        notificationListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(notificationListAdapter);

        getNotificationList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rbAsATicker.setChecked(true);
                rbAsAPoster.setChecked(false);
                break;
            case 1:
                rbAsATicker.setChecked(false);
                rbAsAPoster.setChecked(true);
                break;
            case 2:

                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        clickEvent();
    }

    private void clickEvent() {
        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_as_ticker:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.rb_as_poster:
                    viewPager.setCurrentItem(1);
                    break;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Dashboard2TickerFragment.newInstance(), "User as Ticker");
        adapter.addFragment(Dashboard2PosterFragment.newInstance(), "User as Poster");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, NotifDatum obj, int position, String action) {


        if (obj.getData() != null && obj.getData().getTrigger() != null) {
            if(obj.getData().getTrigger().equals("task") || obj.getData().getTrigger().equals("comment")) {
                Intent intent = new Intent(Dashboard2Activity.this, TaskDetailsActivity.class);
                Bundle bundleIntent = new Bundle();
                bundleIntent.putString(ConstantKey.SLUG, obj.getData().getTaskSlug());
                //TODO: need to put poster id to this, but is has to be implemented at taskDetailsActivity not from outside
                //bundleIntent.putInt(ConstantKey.USER_ID, obj.getUser().getId());
                intent.putExtras(bundleIntent);
                startActivity(intent);
            }else if(obj.getData().getTrigger().equals("conversation")) {

                ConversationModel model = new ConversationModel(obj.getData().getConversation().getId(),
                        obj.getUserAccountModel().getName(), obj.getData().getTaskId(), null, null, obj.getCreatedAt(),
                        sessionManager.getUserAccount(),
                        obj.getUserAccountModel(),
                        obj.getData().getTaskSlug(), obj.getData().getTaskStatus(), null);

                Intent intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                ChatActivity.conversationModel = model;
                //    bundle.putParcelable(ConstantKey.CONVERSATION, model);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }


    }
}