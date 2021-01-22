package com.jobtick.android.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.payment.AddCreditCard;
import com.jobtick.android.payment.AddCreditCardImpl;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.StringUtils;
import com.jobtick.android.widget.ExtendedEntryText;

import butterknife.BindView;

public class CreditCardReqFragment extends Fragment implements TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_add_card)
    MaterialButton btnAddCard;

    ExtendedEntryText edtFullName;
    ExtendedEntryText edtCardNumber;
    ExtendedEntryText edtExpiryDate;
    ExtendedEntryText edtSecurityNumber;

    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;
    private SessionManager sessionManager;

    public CreditCardReqFragment() {
    }

    public static CreditCardReqFragment newInstance() {
        return new CreditCardReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnAddCard = view.findViewById(R.id.btn_add_card);
        btnAddCard.setOnClickListener(v -> {
            if(validation()){
                setExpiryDate(edtExpiryDate.getText());
                ((ActivityBase) requireActivity()).showProgressDialog();
                addCreditCard.getToken(edtCardNumber.getText(),
                        expMonth, expYear,
                        edtSecurityNumber.getText(),
                        edtFullName.getText());
            }
        });

        edtFullName = view.findViewById(R.id.edt_full_name);
        edtCardNumber = view.findViewById(R.id.edt_card_number);
        edtExpiryDate = view.findViewById(R.id.edt_expiry_date);
        edtSecurityNumber = view.findViewById(R.id.edt_security_number);

        edtFullName.addTextChangedListener(this);
        edtCardNumber.addTextChangedListener(this);
        edtSecurityNumber.addTextChangedListener(this);
        edtExpiryDate.addTextChangedListener(this);


        addCreditCard = new AddCreditCardImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                ((ActivityBase) requireActivity()).showToast(getString(R.string.credit_card_error), requireContext());
            }

            @Override
            public void onNetworkResponseError(NetworkResponse networkResponse) {
                ((ActivityBase) requireActivity()).errorHandle1(networkResponse);
                ((ActivityBase) requireActivity()).hideProgressDialog();
            }

            @Override
            public void onValidationError(ValidationErrorType validationErrorType, String message) {
                ((ActivityBase) requireActivity()).showToast(message, requireContext());
                ((ActivityBase) requireActivity()).hideProgressDialog();
            }
        };
        btnAddCard.setEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_card_req, container, false);
    }

    private void goNext() {
        ((PosterRequirementsBottomSheet) getParentFragment()).changeFragment(1);
    }

    private void setExpiryDate(String expiryDate){
        expMonth = Integer.parseInt(expiryDate.substring(0, 2));
        expYear = Integer.parseInt(expiryDate.substring(3));
    }

    private boolean validation(){
        if(edtFullName.getText().isEmpty()){
            edtFullName.setError("The card name must be filled.");
            return false;
        }
        else if(edtCardNumber.getText().isEmpty()){
            edtCardNumber.setError("The card number must be filled.");
            return false;
        }
        else if(edtExpiryDate.getText().toString() == null || edtExpiryDate.getText().isEmpty()
                || edtExpiryDate.getText().length() != 7){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(!StringUtils.checkCreditCardExpiryFormat(edtExpiryDate.getText().toString())){
            edtExpiryDate.setError("The card expiry date is not correct.");
            return false;
        }
        else if(Integer.parseInt(edtExpiryDate.getText().substring(0, 2)) > 12){
            edtExpiryDate.setError("The card expiry date is not correct.");
        }
        else if(edtSecurityNumber.getText().isEmpty()){
            edtSecurityNumber.setError("The card CVC must be filled.");
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean btnEnabled = edtCardNumber.getText().length() > 0 &&
                edtFullName.getText().length() > 0 &&
                edtSecurityNumber.getText().length() > 0 &&
                edtExpiryDate.getText().length() > 0;
            btnAddCard.setEnabled(btnEnabled);
    }
}