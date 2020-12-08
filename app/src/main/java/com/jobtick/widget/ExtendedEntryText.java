package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jobtick.R;

import static android.text.InputType.TYPE_CLASS_TEXT;


public class ExtendedEntryText extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private String eTitle;
    private String eContent;
    private String eHint;
    private TextView textView;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView errorView;
    private TextView dollar;
    private EditText editText;
    private EditText secondEditText;
    private ImageView imageView;
    private int eBoxSize = 0;
    private boolean eIsPassword;
    private boolean eStartFocus;
    private int eInputType;
    private int eImeOptions;
    private boolean password_hide = true;
    private boolean eIsEnable = true;
    private ExtendedViewOnClickListener extendedViewOnClickListener;


    public ExtendedEntryText(Context context) {
        this(context, null);
    }

    public ExtendedEntryText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedEntryText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedEntryText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedEntryText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedEntryText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedEntryText_eHint);
            eIsEnable = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eIsEnable, true);
            eStartFocus = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eStartFocus, false);
            String inputType = sharedAttribute.getString(R.styleable.ExtendedEntryText_eInputType);
            if (inputType != null && !inputType.isEmpty())
                eInputType = Integer.parseInt(inputType);

            String boxSize = sharedAttribute.getString(R.styleable.ExtendedEntryText_eBoxSize);
            if (boxSize != null && !boxSize.isEmpty())
                eBoxSize = Integer.parseInt(boxSize);

            String imeOptions = sharedAttribute.getString(R.styleable.ExtendedEntryText_eImeOptions);
            if (imeOptions != null && !imeOptions.isEmpty())
                eImeOptions = Integer.parseInt(imeOptions);

        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        if(eBoxSize == EBoxSize.NORMAL)
            LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);
        else if(eBoxSize == EBoxSize.SMALL)
            LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text_small, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.content_auto_complete);
        if(eInputType == EInputType.AUTOCOMPLETE){
            editText = (EditText) autoCompleteTextView;
            secondEditText = (EditText) findViewById(R.id.content);
        }
        else
            editText = (EditText) findViewById(R.id.content);

        textView = (TextView) findViewById(R.id.title);
        errorView = (TextView) findViewById(R.id.error);
        imageView = (ImageView) findViewById(R.id.img_btn_password_toggle);
        dollar = (TextView) findViewById(R.id.dollar);

        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eHint);
        editText.setEnabled(eIsEnable);

        editText.setOnFocusChangeListener(this);
        editText.setOnEditorActionListener(this);
        setInputType();
        setImeOptions();
        setListeners();

        if(eStartFocus){
            editText.requestFocus();
            showKeyboard(editText);
        }
    }

    @Override
    public void onClick(View v) {
        if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR || eInputType == EInputType.SPINNER) {
            if (extendedViewOnClickListener == null)
                throw new IllegalStateException(eInputType + " type selected, but ExtendedViewOnClickListener is not implemented.");

            extendedViewOnClickListener.onClick();
            return;
        }


        editText.requestFocus();
        editText.performClick();

        showKeyboard(editText);
    }

    private void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        });
    }

    private void setInputType() {
        imageView.setVisibility(GONE);

        if (eInputType == EInputType.INTEGER) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (eInputType == EInputType.EMAIL)
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        else if (eInputType == EInputType.PASSWORD) {
            eIsPassword = true;
            imageView.setVisibility(VISIBLE);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        } else if (eInputType == EInputType.PHONE)
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        else if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR || eInputType == EInputType.SPINNER) {
            editText.setFocusable(false);
            editText.setOnClickListener(v -> {
                extendedViewOnClickListener.onClick();
            });
            editText.setInputType(InputType.TYPE_NULL);
            editText.setClickable(true);
        } else
            editText.setInputType(TYPE_CLASS_TEXT);


        if (eInputType == EInputType.SUBURB) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_pin_blue), null);
        }
        if (eInputType == EInputType.CALENDAR) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_blue), null);
        }
        if (eInputType == EInputType.SPINNER) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_down_black), null);
        }
        if (eInputType == EInputType.BUDGET) {
            dollar.setText("$ ");
            dollar.setVisibility(View.VISIBLE);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if(eInputType == EInputType.AUTOCOMPLETE){
            secondEditText.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.VISIBLE);
            editText.setInputType(TYPE_CLASS_TEXT);
        }

    }

    public void setAdapter(String[] items){
        if(eInputType != EInputType.AUTOCOMPLETE)
            throw new IllegalStateException("for using adapter, you must select autoComplete as input type.");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        autoCompleteTextView.setAdapter(adapter);
    }



    private void setImeOptions(){
        if(eImeOptions == EImeOptions.ACTION_NEXT)
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if(eImeOptions == EImeOptions.ACTION_DONE)
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if(eImeOptions == EImeOptions.NORMAL)
            editText.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    private void setListeners() {
        setOnClickListener(this);

        if (eIsPassword) {
            imageView.setOnClickListener(v -> {

                if (password_hide) {
                    password_hide = false;
                    editText.setInputType(
                            TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_eye));
                } else {
                    password_hide = true;
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_eye_off));
                }
                editText.setSelection(editText.getText().length());
            });
        }
    }

    public void setExtendedViewOnClickListener(ExtendedViewOnClickListener extendedViewOnClickListener) {
        this.extendedViewOnClickListener = extendedViewOnClickListener;
    }

    public void setError(CharSequence error) {
        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_red);
        //sajad said that remove all error, red background is enough
        //editText.setError(error);
        //Due to new comments, I cancel my suggestion. So I comment delete below lines, we just show
        //red border for errors.
        //errorView.setVisibility(View.VISIBLE);
        //errorView.setText(error);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteContent() {
        return editText.getText().toString();
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String content) {
        this.editText.setText(content);
    }

    public void seteContent(String eContent) {
        this.editText.setText(eContent);
    }

    public int geteInputType() {
        return eInputType;
    }

    public void seteInputType(int eInputType) {
        this.eInputType = eInputType;
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        errorView.setVisibility(View.GONE);
        if (focused)
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        else
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
    }

    public void setSelection(int i) {
        editText.setSelection(i);
    }

    @Override
    public boolean onEditorAction(TextView textView,  int actionId, KeyEvent keyEvent) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            editText.clearFocus();
        }
        return false;
    }

    public boolean isIsEnable() {
        return eIsEnable;
    }

    public void setIsEnable(boolean eIsEnable) {
        this.eIsEnable = eIsEnable;
    }

    public interface EInputType {

        int TEXT = 0;
        int INTEGER = 1;
        int EMAIL = 2;
        int PASSWORD = 3;
        int PHONE = 4;
        int SUBURB = 5;
        int CALENDAR = 6;
        int BUDGET = 7;
        int SPINNER = 8;
        int AUTOCOMPLETE = 9;
    }

    public interface EImeOptions {

        int ACTION_NEXT = 0;
        int ACTION_DONE = 1;
        int NORMAL = 2;
    }

    public interface EBoxSize{
        int NORMAL = 0;
        int SMALL = 1;
    }

    public interface ExtendedViewOnClickListener {
        public void onClick();
    }


}
