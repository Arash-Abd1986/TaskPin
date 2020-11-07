package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextMedium;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskBudgetFragment extends Fragment {


    TaskCreateActivity taskCreateActivity;
    View view;

    OperationsListener operationsListener;
    TaskModel task;
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @BindView(R.id.lyt_btn_date_time)
    LinearLayout lytBtnDateTime;
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @BindView(R.id.rb_hourly)
    RadioButton rbHourly;
    @BindView(R.id.rb_total)
    RadioButton rbTotal;
    @BindView(R.id.rg_hourly_total)
    RadioGroup rgHourlyTotal;
    @BindView(R.id.txt_hours)
    EditTextMedium txtHours;
    @BindView(R.id.img_btn_minus)
    ImageView imgBtnMinus;
    @BindView(R.id.img_btn_add)
    ImageView imgBtnAdd;
    @BindView(R.id.card_time)
    LinearLayout cardTime;
    @BindView(R.id.txt_estimated_budget_h)
    TextViewMedium txtEstimatedBudget;
    @BindView(R.id.txt_us_h)
    TextViewBold txtUs;
    @BindView(R.id.lyt_btn_back)
    LinearLayout lytBtnBack;
    @BindView(R.id.card_button)
    CardView cardButton;
    @BindView(R.id.lyt_btn_post_task)
    LinearLayout lytBtnPostTask;
    @BindView(R.id.lyt_button)
    LinearLayout lytButton;
    private LinearLayout cardBudgetT, cardBudgetH, estimatedH, estimatedT;
    private EditText edtBudgetT, edtBudgetH;
    private TextView txtBudgetT, txtBudgetH, txtDollerT, txtDollerH;

    public static TaskBudgetFragment newInstance(int budget, int hour_budget, int total_hours,
                                                 String payment_type, OperationsListener operationsListener) {


        Bundle args = new Bundle();
        args.putInt("BUDGET", budget);
        args.putInt("HOUR_BUDGET", hour_budget);
        args.putInt("TOTAL_HOURS", total_hours);
        args.putString("PAYMENT_TYPE", payment_type);
        TaskBudgetFragment fragment = new TaskBudgetFragment();
        fragment.operationsListener = operationsListener;
        fragment.setArguments(args);
        return fragment;
    }

    public TaskBudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_budget, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskCreateActivity = (TaskCreateActivity) getActivity();
        task = new TaskModel();
        cardBudgetT = view.findViewById(R.id.card_budgetT);
        edtBudgetT = view.findViewById(R.id.edt_budgetT);
        estimatedT = view.findViewById(R.id.card_estimated_t);
        txtBudgetT = view.findViewById(R.id.txt_budget_t);
        txtDollerT = view.findViewById(R.id.txt_doller_us_t);


        cardBudgetH = view.findViewById(R.id.card_budgetH);
        edtBudgetH = view.findViewById(R.id.edt_budgetH);
        estimatedH = view.findViewById(R.id.card_estimated_h);
        txtBudgetH = view.findViewById(R.id.txt_budget_h);
        txtDollerH= view.findViewById(R.id.txt_doller_us_h);

        radioBtnClick();
        edtText();

        task.setBudget(getArguments().getInt("BUDGET"));
        task.setHourlyRate(getArguments().getInt("HOUR_BUDGET"));
        task.setTotalHours(getArguments().getInt("TOTAL_HOURS"));
        task.setPaymentType(getArguments().getString("PAYMENT_TYPE"));
        if (task.getBudget() != 0) {
            edtBudgetH.setText(String.format("%d", task.getBudget()));
            edtBudgetT.setText(String.format("%d", task.getBudget()));
        }
        txtHours.setText(String.format("%d", task.getTotalHours()));

        if (task.getPaymentType() == null || task.getPaymentType().equalsIgnoreCase("fixed")) {
            rbTotal.setChecked(true);
            rbHourly.setChecked(false);
            //     rbHourly.setTextColor(taskCreateActivity.getResources().getColor(R.color.black));
            //     rbTotal.setTextColor(taskCreateActivity.getResources().getColor(R.color.white));
            cardTime.setVisibility(View.GONE);
            cardBudgetT.setVisibility(View.VISIBLE);
            cardBudgetH.setVisibility(View.GONE);
            estimatedH.setVisibility(View.GONE);
            estimatedT.setVisibility(View.VISIBLE);
            showEstimatedBudget();
        } else {
            rbHourly.setChecked(true);
            rbTotal.setChecked(false);
            //   rbHourly.setTextColor(taskCreateActivity.getResources().getColor(R.color.black));
            //    rbTotal.setTextColor(taskCreateActivity.getResources().getColor(R.color.white));
            cardTime.setVisibility(View.VISIBLE);
            cardBudgetT.setVisibility(View.GONE);
            cardBudgetH.setVisibility(View.VISIBLE);
            estimatedH.setVisibility(View.VISIBLE);
            estimatedT.setVisibility(View.GONE);
            showEstimatedBudget();
        }

        showEstimatedBudget();

        taskCreateActivity.setActionDraftTaskBudget(taskModel -> {
            if (edtBudgetH.getText().toString().trim().length() > 0) {
                taskModel.setBudget(rbTotal.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0);
                taskModel.setHourlyRate(rbHourly.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0);
                taskModel.setTotalHours(Integer.parseInt(txtHours.getText().toString().trim()));
                taskModel.setPaymentType(rbHourly.isChecked() ? Constant.TASK_PAYMENT_TYPE_HOURLY : Constant.TASK_PAYMENT_TYPE_FIXED);
            }
            operationsListener.draftTaskBudget(taskModel);
        });
    }


    private void edtText() {
        edtBudgetH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtDollerH.setTextColor(taskCreateActivity.getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0)
                    showEstimatedBudget();
            }
        });
        edtBudgetT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtDollerT.setTextColor(taskCreateActivity.getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0)
                    showEstimatedBudget();
            }
        });
    }

    private void radioBtnClick() {
        rgHourlyTotal.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb_btn = (RadioButton) view.findViewById(checkedId);
            if (rb_btn.getText().equals("Hourly")) {
                cardTime.setVisibility(View.VISIBLE);
                cardBudgetT.setVisibility(View.GONE);
                cardBudgetH.setVisibility(View.VISIBLE);
                estimatedH.setVisibility(View.VISIBLE);
                estimatedT.setVisibility(View.GONE);
                showEstimatedBudget();
            } else {
                cardTime.setVisibility(View.GONE);
                cardBudgetT.setVisibility(View.VISIBLE);
                cardBudgetH.setVisibility(View.GONE);
                estimatedH.setVisibility(View.GONE);
                estimatedT.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showEstimatedBudget() {
        int time = 1;
        if (rbHourly.isChecked()) {
            time = Integer.parseInt(txtHours.getText().toString().trim());
        }
        int budgetH = 0;
        if (edtBudgetH.getText().toString().trim().length() != 0)
            budgetH = Integer.parseInt(edtBudgetH.getText().toString().trim());
        txtBudgetH.setText(String.valueOf(time * budgetH));
        int budgetT = 0;
        if (edtBudgetT.getText().toString().trim().length() != 0)
            budgetT = Integer.parseInt(edtBudgetT.getText().toString().trim());
        txtBudgetT.setText(String.valueOf(budgetT));
    }

    @OnClick({R.id.lyt_btn_details, R.id.lyt_btn_date_time, R.id.lyt_btn_budget, R.id.img_btn_minus,
            R.id.img_btn_add, R.id.lyt_btn_back, R.id.lyt_btn_post_task})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_details:

                break;
            case R.id.lyt_btn_date_time:

                break;
            case R.id.lyt_btn_budget:
                break;
            case R.id.img_btn_minus:
                minusBtnClick();
                break;
            case R.id.img_btn_add:
                addBtnClick();
                break;
            case R.id.lyt_btn_back:
                if (edtBudgetH.getText().toString().trim().length() > 0) {
                    operationsListener.onBackClickBudget(
                            rbTotal.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0,
                            rbHourly.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0,
                            Integer.parseInt(txtHours.getText().toString().trim()),
                            rbHourly.isChecked() ? Constant.TASK_PAYMENT_TYPE_HOURLY : Constant.TASK_PAYMENT_TYPE_FIXED
                    );
                    operationsListener.onValidDataFilledBudgetBack();
                } else {
                    edtBudgetH.setError("Please enter your budget");
                    edtBudgetT.setError("Please enter your budget");
                }
                break;
            case R.id.lyt_btn_post_task:
                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onNextClickBudget(
                                rbTotal.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0,
                                rbHourly.isChecked() ? Integer.parseInt(edtBudgetH.getText().toString().trim()) : 0,
                                Integer.parseInt(txtHours.getText().toString().trim()),
                                rbHourly.isChecked() ? "hourly " : "fixed"
                        );
                        operationsListener.onValidDataFilledBudgetNext();
                        break;
                    case 1:
                        edtBudgetH.setError("Please enter your budget");
                        break;
                    case 2:
                        edtBudgetH.setError("Budget Greater or Equal than $10");
                        break;
                }
                break;
        }
    }

    private void addBtnClick() {
        int hours_value = Integer.parseInt(txtHours.getText().toString().trim());
        txtHours.setText(String.valueOf(hours_value + 1));
        showEstimatedBudget();
    }

    private void minusBtnClick() {
        int hours_value = Integer.parseInt(txtHours.getText().toString().trim());
        if (hours_value > 1) {
            txtHours.setText(String.valueOf(hours_value - 1));
            showEstimatedBudget();
        } else {
            txtHours.setText("1");
        }
    }

    private int getValidationCode() {
        if (TextUtils.isEmpty(edtBudgetH.getText().toString().trim())) {
            return 1;
        } else if (Integer.parseInt(edtBudgetH.getText().toString().trim()) < 10) {
            return 2;
        }
        return 0;
    }

    public interface OperationsListener {
        void onNextClickBudget(int budget, int hour_budget, int total_hours, String payment_type);

        void onBackClickBudget(int budget, int hour_budget, int total_hours, String payment_type);

        void onValidDataFilledBudgetNext();

        void onValidDataFilledBudgetBack();

        void draftTaskBudget(TaskModel taskModel);
    }

    @Override
    public void onDestroy() {
        Log.e("budget", "destory");
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        Log.e("budget", "destoryview");
        super.onDestroyView();

    }

}
