package com.jobtick.cancellations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.utils.ConstantKey;

import butterknife.BindView;
import butterknife.ButterKnife;

public class cancellationSubmittedActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.btn_done)
    MaterialButton btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_submited);
        ButterKnife.bind(this);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            throw new IllegalStateException("there is no bundle attached.");

        String desc = bundle.getString(ConstantKey.CANCELLATION_SUBMITTED);
        if(desc != null && !desc.equals(""))
            txtTitle.setText(desc);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation Request");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void finishActivity(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantKey.CANCELLATION, true);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}