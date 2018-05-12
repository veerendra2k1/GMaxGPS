package com.gmaxgps.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmaxgps.MainApplication;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.CommonFunction;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.WebService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;


import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static com.gmaxgps.Utils.CommonFunction.hideKeyBoard;


public class MobileAuth extends AppCompatActivity implements View.OnClickListener {


    RelativeLayout phone_num_ver_layout = null ;

    TextView send_otp = null;
    ImageView phone_back = null;

    String phone_number = "";

    String email = "", phone = "", first_name = "", last_name = "", full_name = "", account_id = "";
    String password = "gmaxgps";

    EditText mPhoneNumberField, mVerificationField;
    TextView mStartButton, mVerifyButton, mResendButton;
    Context ct = this;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    String countryCode = "";
    CountryCodePicker countryCodePicker = null;
    private static final String TAG = "PhoneAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        phone_num_ver_layout = (RelativeLayout) findViewById(R.id.phone_num_ver_layout);


        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);


        phone_back = (ImageView) findViewById(R.id.phone_back);

        phone_back.setOnClickListener(this);
        phone_num_ver_layout.setOnClickListener(this);
        //  send_otp.setOnClickListener(this);


        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (TextView) findViewById(R.id.button_start_verification);
        mVerifyButton = (TextView) findViewById(R.id.button_verify_phone);
        mResendButton = (TextView) findViewById(R.id.button_resend);

        mResendButton.setVisibility(View.GONE);
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(ct, "Please add your country code with phone number.", Toast.LENGTH_SHORT).show();
                CommonFunction.cancelProgress();
               /* if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                }*/
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                CommonFunction.cancelProgress();

                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_verify_phone:


                try {
                    String code = mVerificationField.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        mVerificationField.setError("Cannot be empty.");
                        return;
                    }
                    hideKeyBoard(this);


                    String cur_name = mPhoneNumberField.getText().toString();
                    if (cur_name != null && !cur_name.equalsIgnoreCase("") && cur_name.equalsIgnoreCase(phone_number))
                    {
                        CommonFunction.showProgressDialog(ct, "Verifying...");


                        //  if(CommonFunction.isNetworkAvailable(this))
                        if (mVerificationId != null && !mVerificationId.equalsIgnoreCase(""))
                            verifyPhoneNumberWithCode(mVerificationId, code);
                        else
                            Toast.makeText(this, "Please send OTP again.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mPhoneNumberField.setError("Incorrect number.");
                      //  Toast.makeText(this, "Incorrect number.", Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    CommonFunction.cancelProgress();
                    e.printStackTrace();
                }


                break;

            case R.id.button_start_verification:
                try {
                    if (!validatePhoneNumber()) {
                        return;
                    }

                    hideKeyBoard(this);
                   countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

                    CommonFunction.showProgressDialog(ct, "Sending...");
                    if (CommonFunction.isNetworkAvailable(this)) {
                        String phoneNumber = countryCode+mPhoneNumberField.getText().toString();
                        startPhoneNumberVerification(phoneNumber);
                    }
                    else
                        Toast.makeText(this, Consts.NETWORK_FAILER, Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    CommonFunction.cancelProgress();
                    e.printStackTrace();
                }


                break;
            case R.id.phone_back:
                phone_num_ver_layout.setVisibility(View.GONE);
                phone_back.setVisibility(View.GONE);
                break;

            case R.id.phone_num_ver_layout:
                break;
            default:
                break;
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //  phone_number = mPhoneNumberField.getText().toString().trim();
                            String presenterId = user.getUid();
                            AppSession.save(ct, Consts.FIRBASE_USER_ID, presenterId);
                            email = ""+phone_number+"@gmaxgps.com";
                            AppSession.save(ct, Consts.EMAIL, email);
                            AppSession.save(ct, Consts.PHONE_NUMBER, phone_number);
                            hideKeyBoard(ct);
                            login();
                        } else {

                            Toast.makeText(ct, "Please enter valid otp.", Toast.LENGTH_SHORT).show();
                        }
                        CommonFunction.cancelProgress();
                    }
                });
    }



    private void login() {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ct);

        preferences
                .edit()
                .putBoolean(MainApplication.PREFERENCE_AUTHENTICATED, true)
                .putString(MainApplication.PREFERENCE_EMAIL, email)
                .putString(MainApplication.PREFERENCE_PASSWORD, password)
                .putString(MainApplication.PREFERENCE_PHONE, phone_number)
                .putString(MainApplication.FIRST_NAME, first_name)
                .putString(MainApplication.LAST_NAME, last_name)
                .apply();


        final ProgressDialog progress = new ProgressDialog(ct);
        progress.setMessage(getString(R.string.app_loading));
        progress.setCancelable(false);
        progress.show();
        final MainApplication application = (MainApplication) getApplication();
        application.getServiceAsync(new MainApplication.GetServiceCallback() {
            @Override
            public void onServiceReady(OkHttpClient client, Retrofit retrofit, WebService service) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }

                startActivity(new Intent(ct, MainActivity.class));
                finish();
            }

            @Override
            public boolean onFailure() {
                if (progress.isShowing()) {
                    progress.dismiss();


                }

                return false;

                //  getActivity().finish();
                // startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }



    private void startPhoneNumberVerification(String phoneNumber) {

        phone_number = phoneNumber;
        //  mResendButton.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
        {
            Toast.makeText(ct,"Please enter phone number",Toast.LENGTH_SHORT).show();
          //  mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


}
