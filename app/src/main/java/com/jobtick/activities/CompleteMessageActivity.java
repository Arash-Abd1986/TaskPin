package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.text_view.TextViewBold;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.activities.TaskDetailsActivity.requestAcceptListener;
import static com.jobtick.utils.ConstantKey.RESULTCODE_CREATE_TASK;
import static com.jobtick.utils.ConstantKey.RESULTCODE_INCREASE_BUDGET;
import static com.jobtick.utils.ConstantKey.RESULTCODE_MAKEANOFFER;

public class CompleteMessageActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextViewBold txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_subtitle)
    TextViewRegular txtSubtitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_finish)
    LinearLayout lytBtnFinish;

    public int from = 0;

    private TaskModel taskModel;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linearTaskCompleted)
    LinearLayout linearTaskCompleted;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cardFinish)
    CardView cardFinish;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_new_job)
    LinearLayout lytBtnNewJob;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_view_your_job)
    LinearLayout lytBtnViewYourJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_message);
        ButterKnife.bind(this);
        initToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE) != null) {
                txtTitle.setText(bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE));
            }
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE) != null) {
                txtSubtitle.setText(bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE));
            }
            if (bundle.containsKey(ConstantKey.COMPLETES_MESSAGE_FROM)) {
                from = bundle.getInt(ConstantKey.COMPLETES_MESSAGE_FROM, 0);
            }
            if(bundle.containsKey(ConstantKey.TASK)){
                taskModel = bundle.getParcelable(ConstantKey.TASK);
            }
        }

        if (from==RESULTCODE_CREATE_TASK)
        {
            linearTaskCompleted.setVisibility(View.VISIBLE);
            cardFinish.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Completed");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick({R.id.cardFinish,R.id.lyt_btn_view_your_job,R.id.lyt_btn_new_job})
    public void onViewClicked(View view) {

        switch (view.getId())
        {
            case R.id.cardFinish:
                if (from == RESULTCODE_INCREASE_BUDGET) {
                    if (requestAcceptListener != null) {
                        requestAcceptListener.onRequestAccept();
                    }
                    onBackPressed();

                } else if (from == RESULTCODE_MAKEANOFFER) {
                    if (requestAcceptListener != null) {
                        requestAcceptListener.onMakeAnOffer();
                    }
                    onBackPressed();

                } else {
                    onBackPressed();

                }

                break;
            case R.id.lyt_btn_view_your_job:
                //TODO: go to my job fragment
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra(ConstantKey.GO_TO_MY_JOBS, true);
                startActivity(intent);
                break;
            case R.id.lyt_btn_new_job:

                Intent creating_task = new Intent(CompleteMessageActivity.this, CategoryListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("category", "");
                creating_task.putExtras(bundle);
                startActivity(creating_task);
                finish();
                break;

        }
    }
}
