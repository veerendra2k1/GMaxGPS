package com.gmaxgps.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gmaxgps.MainApplication;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.WebService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class LoginOptions extends AppCompatActivity {

    LoginButton loginButton = null;
    SignInButton signInButton = null;
    private static final int RC_SIGN_IN = 87;
    CallbackManager callbackManager = null;
    Uri image_uri;
    RelativeLayout email_signup = null , mobile_signup = null;
    String email = "", phone = "", first_name = "", last_name = "", full_name = "", account_id = "";
    String password = "gmaxgps";
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    ProfileTracker profileTracker;
    GoogleSignInClient mGoogleSignInClient;
    String TAG = "LoginOptions";

    Context ct = this;
    private static final String EMAIL = "email";
    //Firebse Aut
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);


        mobile_signup = (RelativeLayout) findViewById(R.id.mobile_signup);

        mobile_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ct, MobileAuth.class));
                finish();
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.google_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");



        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

                if (loginResult != null)
                {

                }

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {

                if (oldProfile != null && currentProfile != null) {

                }
            }
        };


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try {
                            if (loginResult != null) {
                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                Log.v("SignUpPassword", response.toString());

                                                try {
                                                    // Application code
                                                    if (object.has("email"))
                                                        email = object.getString("email");
                                                    if (object.has("phone"))
                                                        phone = object.getString("phone"); // 01/31/1980 format

                                                    if (object.has("first_name"))
                                                        first_name = object.getString("first_name");
                                                    if (object.has("name"))
                                                        full_name = object.getString("name");

                                                    if (object.has("last_name"))
                                                        last_name = object.getString("last_name");

                                                    if (full_name != null && full_name.contains(" ")) {
                                                        String[] full_name_ar = full_name.split(" ");

                                                        first_name = full_name_ar[0];
                                                        last_name = full_name_ar[1];
                                                        AppSession.save(ct, Consts.FIRST_NAME, full_name_ar[0]);
                                                        AppSession.save(ct, Consts.LAST_NAME, full_name_ar[1]);
                                                    }

                                                    full_name = first_name + " " + last_name;

                                                    if (object.has("id"))
                                                        account_id = object.getString("id");

                                                     Profile profile = Profile.getCurrentProfile();
                                                    String id = profile.getId();
                                                    String link = profile.getLinkUri().toString();
                                                    Log.i("Link", link);
                                                    if (Profile.getCurrentProfile() != null) {
                                                        Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                                        image_uri = Profile.getCurrentProfile().getProfilePictureUri(200, 200);
                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if(email!= null && !email.equalsIgnoreCase(""))
                                                {
                                                    AppSession.save(ct, Consts.USER_NAME, full_name);
                                                    AppSession.save(ct, Consts.EMAIL, email);
                                                    AppSession.save(ct, Consts.FACEBOOK_ID, account_id);
                                                    if(image_uri!= null)
                                                        AppSession.save(ct, Consts.IMAGE_URI, image_uri.toString());

                                                    LoginManager.getInstance().logOut();

                                                    signInWithFirebase();

                                                }
                                                else
                                                    Toast.makeText(ct,"Something is wrong",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender,birthday");
                                request.setParameters(parameters);
                                request.executeAsync();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        if (exception != null) {

                        }
                    }
                });


    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            profileTracker.stopTracking();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LoginOption", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        try {
            if (account != null) {
                full_name = account.getDisplayName();

                email = account.getEmail();
                image_uri = account.getPhotoUrl();
                account_id = account.getId();
                if (full_name != null && full_name.contains(" ")) {
                    String[] full_name_ar = full_name.split(" ");

                    first_name = full_name_ar[0];
                    last_name = full_name_ar[1];
                    AppSession.save(ct, Consts.FIRST_NAME, full_name_ar[0]);
                    AppSession.save(ct, Consts.LAST_NAME, full_name_ar[1]);
                }


                AppSession.save(ct, Consts.USER_NAME, full_name);
                AppSession.save(ct, Consts.EMAIL, email);
                AppSession.save(ct, Consts.GOOGLE_ID, account_id);
                AppSession.save(ct, Consts.IMAGE_URI, image_uri.toString());

                mGoogleSignInClient.signOut();

                signInWithFirebase();



            }
        } catch (Exception e) {
            e.printStackTrace();


        }
    }


    private void createAccount() {


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String presenterId = user.getUid();
                    AppSession.save(ct, Consts.FIRBASE_USER_ID, presenterId);

                    login();
                   /* startActivity(new Intent(ct, ConfirmInfoActivity.class));
                    finish();*/

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

                }

            }
        });
    }

    private void signInWithFirebase() {
        Log.d(TAG, "signIn:" + email);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String presenterId = user.getUid();
                    AppSession.save(ct, Consts.FIRBASE_USER_ID, presenterId);
                    login();


                } else {
                    createAccount();
                }


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
                .putString(MainApplication.PREFERENCE_PHONE, phone)
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


}
