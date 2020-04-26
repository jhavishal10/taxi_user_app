package com.taxitime.rideo.Activities;




import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.aapbd.appbajarlib.notification.AlertMessage;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;
import com.taxitime.rideo.UserApplication;
import com.taxitime.rideo.Helper.CustomDialog;
import com.taxitime.rideo.Helper.SharedHelper;
import com.taxitime.rideo.Helper.URLHelper;
import com.taxitime.rideo.Models.CardInfo;
import com.taxitime.rideo.PaytmGateway.Api;
import com.taxitime.rideo.PaytmGateway.Checksum;
import com.taxitime.rideo.PaytmGateway.Constants;
import com.taxitime.rideo.PaytmGateway.Paytm;
import com.taxitime.rideo.R;
import com.taxitime.rideo.Utils.MyBoldTextView;
import com.taxitime.rideo.Utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityWallet extends AppCompatActivity implements View.OnClickListener, PaytmPaymentTransactionCallback, PaymentStatusListener {

    private final int ADD_CARD_CODE = 435;
    String pricea;
    private Button add_fund_button;
    private ProgressDialog loadingDialog;

    private Button add_money_button;
    private EditText money_et;
    private MyBoldTextView balance_tv;
    private String session_token;
    private Button one, two, three, add_money;
    private double update_amount = 0;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol, lblPaymentChange, lblCardNumber;
    private LinearLayout lnrAddmoney, lnrClose, lnrWallet;
    private int selectedPosition = 0;
    Utilities utils = new Utilities();
    private CardInfo cardInfo;
    private ImageView backArrow;
    private  String custid = "", orderId = "", mid = "";
    boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hover.initialize(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = (Button) findViewById(R.id.add_fund_button);
        balance_tv = (MyBoldTextView) findViewById(R.id.balance_tv);
        currencySymbol = (TextView) findViewById(R.id.currencySymbol);
        add_money = (Button) findViewById(R.id.add_money);
        context = this;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = (EditText) findViewById(R.id.money_et);
        lblPaymentChange = (TextView) findViewById(R.id.lblPaymentChange);
        lblCardNumber = (TextView) findViewById(R.id.lblCardNumber);
        lnrClose = (LinearLayout) findViewById(R.id.lnrClose);
        lnrAddmoney = (LinearLayout) findViewById(R.id.lnrAddmoney);
        lnrWallet = (LinearLayout) findViewById(R.id.lnrWallet);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        add_money = (Button) findViewById(R.id.add_money);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        one.setText(SharedHelper.getKey(context, "currency") + "10");
        two.setText(SharedHelper.getKey(context, "currency") + "20");
        three.setText(SharedHelper.getKey(context, "currency") + "50");

        // gone initially
        lnrAddmoney.setVisibility(View.GONE);

        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardInfo != null) {
                    addMoney(cardInfo);
                } else {
                    AlertMessage.showMessage(context, "Alert", "Kindly select a card");
                }
            }
        });

        lblPaymentChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardInfoArrayList.size() > 0) {
                    showChooser();
                } else {
                    gotoAddCard();
                }
            }
        });

        lnrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddmoney.setVisibility(View.GONE);
            }
        });

        lnrWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        add_fund_button.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(context.getResources().getString(R.string.please_wait));

        session_token = SharedHelper.getKey(this, "access_token");

        getBalance();
        getCards(false);
    }





    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                getBalance();
                            }
                            return;
                        }
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    currency = jsonObject.optString("currency");
                                    balance_tv.setText(jsonObject.optString("currency") + jsonObject.optString("wallet_balance"));
                                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_BALANCE");
                                }
                            }
                        } else {

                        }
                    }
                });
    }

    private void refreshAccessToken(final String tag) {

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("GET_BALANCE")) {
                    getBalance();
                } else if (tag.equalsIgnoreCase("GET_CARDS")) {
                    getCards(loading);
                } else {
                    addMoney(cardInfo);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                    utils.GoToBeginActivity(ActivityWallet.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
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


    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (lnrAddmoney.getVisibility() == View.VISIBLE) {
            lnrAddmoney.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void getCards(final boolean showLoading) {
        loading = showLoading;
        if (loading) {
            if (customDialog != null)
                customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response != null) {
                            if (showLoading) {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            }
                            if (e != null) {
                                if (e instanceof TimeoutException) {
                                    displayMessage(context.getResources().getString(R.string.please_try_again));
                                }
                                if (e instanceof NetworkErrorException) {
                                    getCards(showLoading);
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject cardObj = jsonArray.getJSONObject(i);
                                        CardInfo card_Info = new CardInfo();
                                        card_Info.setCardId(cardObj.optString("card_id"));
                                        card_Info.setCardType(cardObj.optString("brand"));
                                        card_Info.setLastFour(cardObj.optString("last_four"));
                                        cardInfoArrayList.add(card_Info);

                                        if (i == 0) {
                                            lblCardNumber.setText("XXXX-XXXX-XXXX-" + card_Info.getLastFour());
                                            cardInfo = card_Info;
                                        }
                                    }

                                    if (showLoading) {
                                        if (cardInfoArrayList.size() > 0) {
                                            showChooser();
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_CARDS");
                                }
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    //Stripe Gateway
                    update_amount = Double.parseDouble(money_et.getText().toString().trim());
                    lnrAddmoney.setVisibility(View.VISIBLE);


                    //UPI Payment GateWay India
//                    final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
//                            .with(this)
//                            .setPayeeVpa("yourUPIID")
//                            .setPayeeName(SharedHelper.getKey(context, "first_name"))
//                            .setTransactionId(generateString())
//                            .setTransactionRefId(generateString())
//                            .setDescription("Wallet Amount Add: "+ money_et.getText().toString().trim() )
//                            .setAmount(String.valueOf(Double.valueOf(money_et.getText().toString().trim())))
//                            .build();
//                    easyUpiPayment.startPayment();
//                    easyUpiPayment.setPaymentStatusListener(ActivityWallet.this);



                }
                break;

            case R.id.one:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("10");
                break;
            case R.id.two:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("20");
                break;
            case R.id.three:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                money_et.setText("50");
                break;
        }
    }


    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        }else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                addMoney(cardInfo);
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {

            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {

            }
        }else if (requestCode == 1225 && resultCode == Activity.RESULT_OK) {
            String[] sessionTextArr = data.getStringArrayExtra("session_messages");
            String uuid = data.getStringExtra("uuid");
            addMoney(cardInfo);
        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        }
    }

    private void showChooser() {

        final String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(context.getResources().getString(R.string.add_money_using));
        builderSingle.setSingleChoiceItems(cardsList, selectedPosition, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card = "";
            card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                Log.e("Items clicked===>", "" + selectedPosition);
                cardInfo = cardInfoArrayList.get(selectedPosition);
                lblCardNumber.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
//                addMoney(cardInfoArrayList.get(selectedPosition));
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.show();
    }

    private void addMoney(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();


        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());


        Ion.with(this)
                .load(URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result

                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                addMoney(cardInfo);
                            }
                            return;
                        }

                        Log.e("ION response", response.getResult() + " ");

                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());

                                JSONObject userObj = jsonObject.getJSONObject("user");
                                balance_tv.setText(currency + userObj.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                money_et.setText("");
                                lnrAddmoney.setVisibility(View.GONE);
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                AlertMessage.showMessage(context, "Wallet", jsonObject.optString("message"));

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            try {
                                if (response != null && response.getHeaders() != null) {
                                    if (response.getHeaders().code() == 401) {
                                        refreshAccessToken("ADD_MONEY");
                                    }
                                }
                            } catch (Exception exception) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }

//
//    public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {
//        String custid = "", orderId = "", mid = "";
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
//            //initOrderId();
//
//
//        }
//
//
//    }
    public void makeflutterwavePayment(){
        String email = SharedHelper.getKey(context, "email");
        String fName = "Kwenda";
        String lName = "Taxi";
        String txRef = email +" "+  UUID.randomUUID().toString() ;
        String publicKey = "";
        String encryptionKey = "";



            /*
            Create instance of RavePayManager
             */
        Double  amount = Double.valueOf(money_et.getText().toString());
        new RavePayManager(this).setAmount(amount)
                .setCountry("NG")
                .setCurrency("ZMW")
                .setEmail(email)
                .setfName(fName)
                .setlName(lName)
                .setPaymentPlan("mobilemoneyzambia")
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(txRef)
                .acceptZmMobileMoneyPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptBankTransferPayments(true)
                .acceptMpesaPayments(true)
                .acceptUssdPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .onStagingEnv(false).
                allowSaveCardFeature(true)
                .initialize();
    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        addMoney(cardInfo);
    }

    @Override
    public void networkNotAvailable() {
    }

    @Override
    public void clientAuthenticationFailed(String s) {
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  " + s);
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true " + s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  ");
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel ");
    }


    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {

    }

    @Override
    public void onTransactionSuccess() {
        addMoney(cardInfo);
    }

    @Override
    public void onTransactionSubmitted() {

    }

    @Override
    public void onTransactionFailed() {

    }

    @Override
    public void onTransactionCancelled() {

    }
}
