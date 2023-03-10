package com.jobtick.android.payment;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.ADD_BILLING;
import static com.jobtick.android.utils.Constant.BASE_URL;

public abstract class AddBillingAddressImpl implements AddBillingAddress{

    private final Context context;
    private final SessionManager sessionManager;

    public AddBillingAddressImpl(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
    }

    @Override
    public void add(String addressLine1, String addressLine2, String city, String state, String postcode, String country) {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                onSuccess();

                            } else {
                                onError(new Exception("Something went wrong."));
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        FirebaseCrashlytics.getInstance().recordException(e);
                        onError(new Exception("Something went wrong."));
                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        FirebaseCrashlytics.getInstance().recordException(error);

                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            onValidationError(ErrorType.UnAuthenticatedUser, "user is not authenticated.");
                            return;
                        }
                        Log.d("errorjson", jsonError);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                onError(new Exception(jsonObject_error.getString("message")));
                            }
                            else if (jsonObject_error.has("errors")) {
                                onError(new Exception(jsonObject_error.getJSONObject("errors").getString("errors")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError(new Exception("Something went wrong."));
                        }
                    } else {
                        onError(new Exception("something went wrong."));
                    }
                    Timber.e(error.toString());
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("line1", addressLine1);
                map1.put("line2", addressLine2);
                map1.put("city", city);
                map1.put("post_code", postcode);
                map1.put("state", state);
                map1.put("country", country);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public abstract void onSuccess();
    public abstract void onError(Exception e);
    public abstract void onValidationError(ErrorType errorType, String message);

    public enum ErrorType{
        UnAuthenticatedUser
    }
}
