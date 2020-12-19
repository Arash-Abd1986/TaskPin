package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jobtick.R;
import android.annotation.SuppressLint;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SigInSigUpActivity extends ActivityBase {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_up)
    MaterialButton lytBtnSignup;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_in)
    MaterialButton lytBtnSingin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        ButterKnife.bind(this);

        lytBtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigInSigUpActivity.this, AuthActivity.class);
            intent.putExtra("type", "Signup");
            startActivity(intent);
        });

        lytBtnSingin.setOnClickListener(v -> {
            Intent intent = new Intent(SigInSigUpActivity.this, AuthActivity.class);
            intent.putExtra("type", "Signin");
            startActivity(intent);
        });
    }

}
