package com.taxitime.rideo.Activities;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.taxitime.rideo.API.RetrofitClient;
import com.taxitime.rideo.Models.CityResponse;
import com.taxitime.rideo.UserApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.UIManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxitime.rideo.Helper.ConnectionHelper;
import com.taxitime.rideo.Helper.CustomDialog;
import com.taxitime.rideo.Helper.SharedHelper;
import com.taxitime.rideo.Helper.URLHelper;
import com.taxitime.rideo.R;
import com.taxitime.rideo.Utils.MyCheckbox;
import com.taxitime.rideo.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

 public class RegisterActivity extends AppCompatActivity {

         String verificationCodeBySystem;
         FirebaseAuth mAuth;
         final String[] phone = new String[1];
         CountryCodePicker mcpp;
         TextView mPhone, sendotpagain;
         PinView mPin;
         Dialog myDialog; MyCheckbox checkbox;
         Button alsign;
         TextView terms;
        PinView otpcode;
        Button verifiy_btn, get_otp;
        LinearLayout view_beforeverify, view_afterverify;
        public Context context = RegisterActivity.this;
        public Activity activity = RegisterActivity.this;
        String TAG = "RegisterActivity";
        String strViewPager = "";
        String device_token, device_UDID;
        ImageView backArrow;
        Button nextICON;
        EditText email, first_name, last_name, mobile_no, password;
        CustomDialog customDialog;
        ConnectionHelper helper;
        Boolean isInternet;
        Utilities utils = new Utilities();
        Boolean fromActivity = false;


        public static int APP_REQUEST_CODE = 99;

        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
        UIManager uiManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            myDialog = new Dialog(this);
            mAuth = FirebaseAuth.getInstance();
            try {
                Intent intent = getIntent();
                if (intent != null) {
                    if (getIntent().getExtras().containsKey("viewpager")) {
                        strViewPager = getIntent().getExtras().getString("viewpager");
                    }
                    if (getIntent().getExtras().getBoolean("isFromMailActivity")) {
                        fromActivity = true;
                    } else if (!getIntent().getExtras().getBoolean("isFromMailActivity")) {
                        fromActivity = false;
                    } else {
                        fromActivity = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                fromActivity = false;
            }

            findViewById();
            GetToken();

            if (Build.VERSION.SDK_INT > 15) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            nextICON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (!Utilities.isValidEmail(email.getText().toString())) {
                        displayMessage(getString(R.string.not_valid_email));
                    }else if (mPin.getText().toString().equals("") ) {
                        displayMessage(getString(R.string.password_validation));
                    } else if (mPhone.getText().toString().equals("") ) {
                        displayMessage("Please Enter Phone Number");
                    }  else if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                        displayMessage(getString(R.string.first_name_empty));
                    } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                        displayMessage(getString(R.string.last_name));
                    }else if (!checkbox.isChecked()) {
                        displayMessage("Please confirm terms & conditions");
                    }else {
                        if (isInternet) {
                            checkMailAlreadyExit(view);
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }
                }
            });
            alsign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent(RegisterActivity.this,  otpscreen.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                    RegisterActivity.this.finish();
                }
            });

            terms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.base + "privacy"));
                    startActivity(browserIntent);
                }
            });
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            verifiy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(otpcode.length() < 6){
                        Toast.makeText(context, "Wrong OTP Code", Toast.LENGTH_SHORT).show();
                    }else{
                        Utilities.hideKeyboard(RegisterActivity.this);
                        customDialog.show();
                        verifyCode(String.valueOf(otpcode.getText()));
                    }
                }
            });
            sendotpagain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendotpagain.setEnabled(false);
                    customDialog.show();
                    sendVerificationCodeToUser(mPhone.getText().toString());
                }
            });
            get_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPhone.getText().toString().equals("")){
                        displayMessage("Please Enter Your Phone Number");
                    }else{
                        new CountDownTimer(60000, 1000) {
                            {
                                //some code...
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                sendotpagain.setText(getResources().getString(R.string.send_otp_again)+" ("+millisUntilFinished/1000+")" );
                            }

                            public void onFinish() {
                                sendotpagain.setEnabled(true);
                                v.invalidate();
                            }
                        }.start();
                        Utilities.hideKeyboard(RegisterActivity.this);
                        customDialog.show();
                        sendVerificationCodeToUser(mPhone.getText().toString());

                    }
                }
            });

            mPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String x = s.toString();
                    if(x.startsWith("0"))
                    {
                        mPhone.setText("");
                    }
                }
            });

            checkverification();


        }

     private void checkverification() {
         customDialog.show();
         Call<CityResponse> call = RetrofitClient.getInstance().getApi().getcity();
         call.enqueue(new Callback<CityResponse>() {
             @Override
             public void onResponse(Call<CityResponse> call, retrofit2.Response<CityResponse> response) {
                 if(response.isSuccessful()){
                     if(!response.body().isVerification()){
                         get_otp.setVisibility(View.GONE);
                         view_afterverify.setVisibility(View.VISIBLE);
                     }


                 }
                 if(customDialog.isShowing())
                     customDialog.dismiss();
             }

             @Override
             public void onFailure(Call<CityResponse> call, Throwable t) {
                 if(customDialog.isShowing())
                     customDialog.dismiss();
             }
         });
     }


     public void hideKeyboard(View view) {
             InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
             inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
         }

     @Override
        protected void onResume() {
            super.onResume();
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
        }

        public void findViewById() {
            otpcode = (PinView) findViewById(R.id.otppin);
            verifiy_btn = (Button) findViewById(R.id.verify_btn);
            get_otp = (Button) findViewById(R.id.get_otp);
            view_beforeverify = (LinearLayout) findViewById(R.id.view_beforeverification);
            view_afterverify = (LinearLayout) findViewById(R.id.view_afterverification);
            mPin = (PinView) findViewById(R.id.passpin);
            mPhone = (TextView) findViewById(R.id.idphone);
            mcpp = (CountryCodePicker) findViewById(R.id.mcpp);
            email = (EditText) findViewById(R.id.email);
            first_name = (EditText) findViewById(R.id.first_name);
            last_name = (EditText) findViewById(R.id.last_name);
            password = (EditText) findViewById(R.id.password);
            alsign = (Button) findViewById(R.id.alsign);
            checkbox = (MyCheckbox) findViewById(R.id.checkedterms);
            terms = (TextView) findViewById(R.id.termms);
            nextICON = (Button) findViewById(R.id.nextBtn);
            backArrow = (ImageView) findViewById(R.id.backArrow);
            sendotpagain = (TextView) findViewById(R.id.sendotpagain);
            helper = new ConnectionHelper(context);
            isInternet = helper.isConnectingToInternet();
            if (!fromActivity) {
                email.setText(SharedHelper.getKey(context, "email"));
            }

        }

        public void checkMailAlreadyExit(View view) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("email", email.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHECK_MAIL_ALREADY_REGISTERED,
                    object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    //phoneLogin();
                    registerAPI();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        utils.print("MyTest", "" + error);
                        utils.print("MyTestError", "" + error.networkResponse);
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            if (response.statusCode == 422) {

                                json = UserApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    if (json.startsWith(getString(R.string.email_exist))) {
                                        displayMessage(getString(R.string.email_exist));
                                    } else {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            checkMailAlreadyExit(view);
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }


         private void sendVerificationCodeToUser(String phoneNo) {
             PhoneAuthProvider.getInstance().verifyPhoneNumber(
                     mcpp.getSelectedCountryCodeWithPlus() + phoneNo,
                     60,
                     TimeUnit.SECONDS,
                     TaskExecutors.MAIN_THREAD,
                     mCallbacks);
         }


         private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


             @Override
             public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                 super.onCodeSent(s, forceResendingToken);
                 verificationCodeBySystem = s;
                 if(customDialog.isShowing())
                     customDialog.dismiss();
                 mPhone.setEnabled(false);
                 get_otp.setVisibility(View.GONE);
                 view_beforeverify.setVisibility(View.VISIBLE);
             }


             @Override
             public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

             }


             @Override
             public void onVerificationFailed(FirebaseException e) {
                 Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         };


         private void verifyCode(String codeByUser){


             PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
             signInTheUserByCredentials(credential);


         }


         private void signInTheUserByCredentials(PhoneAuthCredential credential) {


             FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


             firebaseAuth.signInWithCredential(credential)
                     .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful()){
                                 mPhone.setEnabled(false);
                                 view_beforeverify.setVisibility(View.GONE);
                                 view_afterverify.setVisibility(View.VISIBLE);
                                 if(customDialog.isShowing())
                                     customDialog.dismiss();

                             }
                             else{
                                 if(customDialog.isShowing())
                                     customDialog.dismiss();
                                 Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                             }


                         }
                     });


         }

        @Override
        protected void onActivityResult(
                final int requestCode,
                final int resultCode,
                final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data != null) {
                if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
                    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            Log.e(TAG, "onSuccess: Account Kit" + account.getId());
                            Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                            if (AccountKit.getCurrentAccessToken().getToken() != null) {
                                SharedHelper.putKey(RegisterActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
                                //SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));
                                // Get phone number
                                PhoneNumber phoneNumber = account.getPhoneNumber();
                                String phoneNumberString = phoneNumber.toString();
                                SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumberString);
                                registerAPI();
                            } else {
                                SharedHelper.putKey(RegisterActivity.this, "account_kit_token", "");
                                SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.False));
                                SharedHelper.putKey(context, "email", "");
                                SharedHelper.putKey(context, "login_by", "");
                                SharedHelper.putKey(RegisterActivity.this, "account_kit_token", "");
                                Intent goToLogin = new Intent(RegisterActivity.this, WelcomeScreenActivity.class);
                                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(goToLogin);
                                finish();
                            }
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e(TAG, "onError: Account Kit" + accountKitError);
                            displayMessage(getResources().getString(R.string.social_cancel));
                        }
                    });
                    if (loginResult != null) {
                        SharedHelper.putKey(this, "account_kit", getString(R.string.True));
                    } else {
                        SharedHelper.putKey(this, "account_kit", getString(R.string.False));
                    }
                    if (loginResult.getError() != null) {
                    } else if (loginResult.wasCancelled()) {
                    } else {
                        if (loginResult.getAccessToken() != null) {
                            Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken().toString());
                            SharedHelper.putKey(this, "account_kit", loginResult.getAccessToken().toString());
                        } else {
                            SharedHelper.putKey(this, "account_kit", "");
                        }
                    }
                }
            }
        }


        private void registerAPI() {
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", "" + device_token);
                object.put("login_by", "manual");
                object.put("first_name", first_name.getText().toString());
                object.put("last_name", last_name.getText().toString());
                object.put("email", email.getText().toString());
                object.put("password", mPin.getText().toString());
                object.put("mobile", mcpp.getSelectedCountryCodeWithPlus()+ mPhone.getText().toString().trim());
                object.put("picture", "");
                object.put("social_unique_id", "");

                utils.print("InputToRegisterAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.register, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        //customDialog.dismiss();
                        utils.print("SignInResponse", response.toString());
                    SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                    SharedHelper.putKey(RegisterActivity.this, "password", mPin.getText().toString());
                    signIn();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        utils.print("MyTest", "" + error);
                        utils.print("MyTestError", "" + error.networkResponse);
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //   Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = UserApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    if (json.startsWith("The email has already been taken")) {
                                        displayMessage(getString(R.string.email_exist));
                                    } else {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                    //displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }


                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            registerAPI();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        }

        private void GoToBeginActivity() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            Intent mainIntent = new Intent(RegisterActivity.this, ActivityEmail.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }

        public void signIn() {
            if (isInternet) {
                customDialog = new CustomDialog(RegisterActivity.this);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                JSONObject object = new JSONObject();
                try {
                    object.put("grant_type", "password");
                    object.put("client_id", URLHelper.client_id);
                    object.put("client_secret", URLHelper.client_secret);
                    object.put("username", SharedHelper.getKey(RegisterActivity.this, "email"));
                    object.put("password", mPin.getText().toString());
                    object.put("scope", "");
                    object.put("device_type", "android");
                    object.put("device_id", device_UDID);
                    object.put("device_token", device_token);
                    utils.print("InputToLoginAPI", "" + object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        utils.print("SignUpResponse", response.toString());
                        SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                        SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                        SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                        getProfile();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    try {
                                        if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                            //Call Refresh token
                                        } else {
                                            displayMessage(errorObj.optString("message"));
                                        }
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 422) {

                                    json = UserApplication.trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        displayMessage(json);
                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }


                        } else {
                            if (error instanceof NoConnectionError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof NetworkError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof TimeoutError) {
                                signIn();
                            }
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

                UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.something_went_wrong_net));
            }

        }

        public void getProfile() {
            if (isInternet) {
                customDialog = new CustomDialog(RegisterActivity.this);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                JSONObject object = new JSONObject();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            //customDialog.dismiss();
                            utils.print("GetProfile", response.toString());
                        SharedHelper.putKey(RegisterActivity.this, "id", response.optString("id"));
                        SharedHelper.putKey(RegisterActivity.this, "first_name", response.optString("first_name"));
                        SharedHelper.putKey(RegisterActivity.this, "last_name", response.optString("last_name"));
                        SharedHelper.putKey(RegisterActivity.this, "email", response.optString("email"));
                        SharedHelper.putKey(RegisterActivity.this, "picture", URLHelper.base + "storage/" + response.optString("picture"));
                        SharedHelper.putKey(RegisterActivity.this, "gender", response.optString("gender"));
                        SharedHelper.putKey(RegisterActivity.this, "mobile", response.optString("mobile"));
                        SharedHelper.putKey(RegisterActivity.this, "wallet_balance", response.optString("wallet_balance"));
                        SharedHelper.putKey(RegisterActivity.this, "payment_mode", response.optString("payment_mode"));
                        if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                            SharedHelper.putKey(context, "currency", response.optString("currency"));
                        else
                            SharedHelper.putKey(context, "currency", "$");
                        SharedHelper.putKey(context, "sos", response.optString("sos"));
                        SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));

                        //phoneLogin();
                        GoToMainActivity();
                   /* if (!SharedHelper.getKey(activity,"account_kit_token").equalsIgnoreCase("")) {

                    }else {
                        GoToMainActivity();
                    }*/

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    try {
                                        if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                            refreshAccessToken();
                                        } else {
                                            displayMessage(errorObj.optString("message"));
                                        }
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 422) {

                                    json = UserApplication.trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        displayMessage(json);
                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }


                        } else {
                            if (error instanceof NoConnectionError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof NetworkError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof TimeoutError) {
                                getProfile();
                            }
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(RegisterActivity.this, "token_type") + " " + SharedHelper.getKey(RegisterActivity.this, "access_token"));
                        return headers;
                    }
                };

                UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.something_went_wrong_net));
            }
        }

        private void refreshAccessToken() {


            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.v("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                        GoToBeginActivity();
                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            refreshAccessToken();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        }

        public void GetToken() {
            try {
                if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                    device_token = SharedHelper.getKey(context, "device_token");
                    utils.print(TAG, "GCM Registration Token: " + device_token);
                } else {
                    device_token = "" + FirebaseInstanceId.getInstance().getToken();
                    SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                    utils.print(TAG, "Failed to complete token refresh: " + device_token);
                }
            } catch (Exception e) {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh");
            }

            try {
                device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                utils.print(TAG, "Device UDID:" + device_UDID);
            } catch (Exception e) {
                device_UDID = "COULD NOT GET UDID";
                e.printStackTrace();
                utils.print(TAG, "Failed to complete device UDID");
            }
        }


        public void GoToMainActivity() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }

        public void displayMessage(String toastString) {
            utils.print("displayMessage", "" + toastString);
            try {
                Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } catch (Exception e) {
                try {
                    Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
                } catch (Exception ee) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        protected void onRestart() {
            super.onRestart();
        }


    }
