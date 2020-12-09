package com.jobtick.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.jobtick.R;
import com.jobtick.fragments.ProfileFragment;
import com.jobtick.fragments.ProfileViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends ActivityBase {
    @BindView(R.id.btnBack)
    LinearLayout btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porfile);

        ButterKnife.bind(this);
        btnBack.setOnClickListener(v -> ProfileActivity.super.onBackPressed());

        if(getIntent().getIntExtra("id",-1)!=-1){
            Bundle b = new Bundle();
            b.putInt("userId",getIntent().getIntExtra("id",-1));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ProfileViewFragment profileFragment = new ProfileViewFragment();
            profileFragment.setArguments(b);
            ft.replace(R.id.profile, profileFragment);
            ft.commit();
        }
    }
}