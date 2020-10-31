package com.jobtick.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.payment.AddBillingAddress;
import com.jobtick.payment.AddBillingAddressImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillingAddressActivity extends ActivityBase {

/*    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/

    @BindView(R.id.edt_address_line_1)
    EditText edtAddressLine1;

    @BindView(R.id.edt_address_line_2)
    EditText edtAddressLine2;

    @BindView(R.id.edt_suburs)
    EditText edtSuburs;

    @BindView(R.id.edt_state)
    EditText edtState;

    @BindView(R.id.edt_postcode)
    EditText edtPostcode;

    @BindView(R.id.edt_Country)
    EditText edtCountry;

    @BindView(R.id.lyt_btn_change_billing_address)
    MaterialButton lytBtnChangeBillingAddress;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    private AddBillingAddress addBillingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_address);
        ButterKnife.bind(this);
        initToolbar();

        addBillingAddress = new AddBillingAddressImpl(this, sessionManager) {
            @Override
            public void onSuccess() {
                showToast("Update Successfully.", BillingAddressActivity.this);
                hideProgressDialog();
            }

            @Override
            public void onError(Exception e) {
                showToast(e.getMessage(), BillingAddressActivity.this);
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                if (errorType == ErrorType.UnAuthenticatedUser)
                    unauthorizedUser();
                else
                    showToast(message, BillingAddressActivity.this);
            }
        };
    }


    private void initToolbar() {

        ivBack.setOnClickListener(v -> {
            finish();
        });
      /*  toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Billing Address");*/
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {

        if (!validation()) return;
        showProgressDialog();
        addBillingAddress.add(edtAddressLine1.getText().toString(),
                edtAddressLine2.getText().toString(),
                edtSuburs.getText().toString(),
                edtState.getText().toString(),
                edtPostcode.getText().toString(),
                edtCountry.getText().toString());

    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtAddressLine1.getText().toString().trim())) {
            edtAddressLine1.setError("Address is mandatory");
            return false;
        }

        if (TextUtils.isEmpty(edtAddressLine2.getText().toString().trim())) {
            edtAddressLine2.setError("Address is mandatory");
            return false;
        }


        if (TextUtils.isEmpty(edtSuburs.getText().toString().trim())) {
            edtSuburs.setError("Please enter Suburb");
            return false;
        }
        if (TextUtils.isEmpty(edtState.getText().toString().trim())) {
            edtState.setError("Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(edtPostcode.getText().toString().trim())) {
            edtPostcode.setError("Please enter Passcode");
            return false;
        }
        if (edtPostcode.getText().toString().length() != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode");
            return false;
        }
        if (TextUtils.isEmpty(edtCountry.getText().toString().trim())) {
            edtCountry.setError("Please Enter Country");
            return false;
        }
        return true;
    }
}
