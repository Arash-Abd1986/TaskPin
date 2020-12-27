package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jobtick.R;

import java.util.Locale;


public class ExtendedCommentText extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {

    private String eTitle;
    private final String eContent;
    private final String eHint;
    private final boolean isMandatory;
    private int eMinSize;
    private int eMaxSize;
    private final boolean eStartFocus;
    private int eImeOptions;

    private final TextView textView;
    private final TextView counter;
    private final EditText editText;

    private TextWatcher textWatcher;


    public ExtendedCommentText(Context context) {
        this(context, null);
    }

    public ExtendedCommentText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedCommentText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedCommentText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedCommentText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedCommentText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedCommentText_eHint);
            eStartFocus = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eStartFocusComment, false);
            eMinSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMinCharSize, 10);
            eMaxSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMaxCharSize, 100);
            isMandatory = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eIsMandatory, false);

            String imeOptions = sharedAttribute.getString(R.styleable.ExtendedCommentText_eImeOptionsComment);
            if (imeOptions != null && !imeOptions.isEmpty())
                eImeOptions = Integer.parseInt(imeOptions);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_extended_comment_text, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        editText = (EditText) findViewById(R.id.content);
        textView = (TextView) findViewById(R.id.title);
        counter = (TextView) findViewById(R.id.counter);

        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eHint);
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
        setOnClickListener(this);

        setImeOptions();
        init();
    }

    private void setImeOptions(){
        if(eImeOptions == ExtendedEntryText.EImeOptions.ACTION_NEXT)
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if(eImeOptions == ExtendedEntryText.EImeOptions.ACTION_DONE)
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if(eImeOptions == ExtendedEntryText.EImeOptions.NORMAL)
            editText.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    private void init(){
        if(isMandatory){
            counter.setText(String.format(Locale.ENGLISH, "0/%d+", eMinSize));
            counter.setTextColor(getResources().getColor(R.color.strokeRed));
        }
        else {
            counter.setText(String.format(Locale.ENGLISH, "0/%d", eMaxSize));
            counter.setTextColor(getResources().getColor(R.color.N050));
        }

        if(eStartFocus){
            editText.requestFocus();
            showKeyboard(editText);
        }
    }

    public void setSelection(int i) {
        editText.setSelection(i);
    }

    @Override
    public void onClick(View v) {

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

    @Override
    public void onFocusChange(View view, boolean focused) {
        if (focused)
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        else
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if(textWatcher != null)
            textWatcher.onTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(textWatcher != null)
            textWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (isMandatory) {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.length();
                if (length < eMinSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d+", s.length(), eMinSize));
                    counter.setTextColor(getResources().getColor(R.color.strokeRed));
                } else if (length <= eMaxSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d", s.length(), eMaxSize));
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText(String.format(Locale.ENGLISH, "%d/%d+", s.length(), eMinSize));
                counter.setTextColor(getResources().getColor(R.color.strokeRed));
            }
        } else {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.length();
                if (length <= eMaxSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d", s.length(), eMaxSize));
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText(String.format(Locale.ENGLISH, "0/%d", eMaxSize));
                counter.setTextColor(getResources().getColor(R.color.N050));
            }
        }

        if(textWatcher != null)
            textWatcher.afterTextChanged(s);
    }

    public void setError(CharSequence error) {
        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_red);
        //editText.setError(error);
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

    public int geteMinSize() {
        return eMinSize;
    }

    public void seteMinSize(int eMinSize) {
        this.eMinSize = eMinSize;
    }

    public int geteMaxSize() {
        return eMaxSize;
    }

    public void seteMaxSize(int eMaxSize) {
        this.eMaxSize = eMaxSize;
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }
}
