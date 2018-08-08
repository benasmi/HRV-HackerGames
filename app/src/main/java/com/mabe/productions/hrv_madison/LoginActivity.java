package com.mabe.productions.hrv_madison;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebaseDatase.FireUser;
import com.mabe.productions.hrv_madison.firebaseDatase.FirebaseUtils;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2 ;
    private TextView txt_slogan;
    private TextView txt_noAccount;
    private TextView txt_noRegistration;
    private EditText editText_username;
    private EditText editText_password;
    private Button btn_login;
    private ImageButton imgBtn_facebook;
    private ImageButton imgBtn_google;
    private ImageView circle;
    private ImageView img_login_appicon;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());




        AppEventsLogger.activateApp(this);



        //FacebookSdk.sdkInitialize(getApplicationContext());


        initializeViews();
        setFonts();

        //FACEBOOK LOGIN
        // Some code
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback < LoginResult > () {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d("auth", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );
        ///////////////////////////////////////////////////////////////////////////////////


        //GMAIL LOGIN
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        ///////////////////////////////////////////////////////////////////////////////////

    }

    private void initializeViews(){


        Animation left_to_right = AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        Animation right_to_left = AnimationUtils.loadAnimation(this,R.anim.right_to_left);
        Animation bottom_to_top = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        Animation top_to_bottom  = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        Animation fade_in  = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        Animation fade_in_delay  = AnimationUtils.loadAnimation(this,R.anim.fade_in_delay);
        Animation top_to_bottom_delay  = AnimationUtils.loadAnimation(this,R.anim.top_to_bottom_delay);


        txt_slogan = (TextView) findViewById(R.id.slogan);
        txt_noAccount = (TextView) findViewById(R.id.noAccount);
        txt_noRegistration = (TextView) findViewById(R.id.noRegistration);
        editText_username = (EditText) findViewById(R.id.usernameEditText);
        editText_password = (EditText) findViewById(R.id.passwordEditText);
        imgBtn_facebook = (ImageButton) findViewById(R.id.login_facebook);
        imgBtn_google = (ImageButton) findViewById(R.id.login_googlePlus);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        circle = (ImageView) findViewById(R.id.circleDot);
        img_login_appicon = (ImageView) findViewById(R.id.imageView);

        imgBtn_facebook.startAnimation(left_to_right);
        imgBtn_google.startAnimation(right_to_left);
        btn_login.startAnimation(bottom_to_top);
        txt_noAccount.startAnimation(top_to_bottom);
        circle.startAnimation(fade_in);
        editText_username.startAnimation(fade_in_delay);
        editText_password.startAnimation(fade_in_delay);
        txt_noRegistration.startAnimation(fade_in_delay);
        txt_slogan.startAnimation(top_to_bottom_delay);

        txt_noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

    }

    private void setFonts(){
        Typeface face_slogan = Typeface.createFromAsset(getAssets(),
                "fonts/CORBEL.TTF");
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/Verdana.ttf");

        txt_slogan.setTypeface(face_slogan);
        txt_noRegistration.setTypeface(verdana);
        btn_login.setTypeface(futura);
        editText_username.setTypeface(futura);
        editText_password.setTypeface(futura);
        txt_noAccount.setTypeface(verdana);
    }

    public void login(View view) {

            if(Utils.readFromSharedPrefs_bool(this,FeedReaderDbHelper.FIELD_DONE_INITIAL,FeedReaderDbHelper.SHARED_PREFS_USER_DATA)){
                startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
            }else{
                startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
            }

    }

    public void facebookLogin(View view) {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("public_profile")
        );
    }

    public void googleLogin(View view) {
        googleSignIn();
    }

    // this part was missing thanks to wesely
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("auth", "Google sign in failed", e);
                // ...
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("auth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("auth", "signInWithCredential:success");
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.i("auth", "User doesn't exists in auth database: " + String.valueOf(isNew));

                            if(isNew){

                                Log.i("auth", "First time user providing info...");
                                FirebaseUtils.addUser();
                                startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                            }else{

                               FirebaseUtils.isInitialDone(new FirebaseUtils.OnInitialDoneFetchListener() {
                                   @Override
                                   public void onSuccess(boolean isInitialDone) {
                                       Log.i("auth", "LoginGoogle: " + String.valueOf(isInitialDone));
                                       if(isInitialDone){
                                           startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
                                       }else{
                                           startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                                       }
                                   }

                                   @Override
                                   public void onFailure(DatabaseError error) {

                                   }
                               });



                                    }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("auth", "signInWithCredential:failure", task.getException());

                        }

                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("auth", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {   if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("auth", "signInWithCredential:success");
                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                        Log.i("auth", "User doesn't exists in auth database: " + String.valueOf(isNew));

                        if(isNew){
                            Log.i("auth", "First time user providing info...");
                            FirebaseUtils.addUser();
                            startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                        }else{

                            FirebaseUtils.isInitialDone(new FirebaseUtils.OnInitialDoneFetchListener() {
                                @Override
                                public void onSuccess(boolean isInitialDone) {
                                    Log.i("auth", "LoginFacebook: " + String.valueOf(isInitialDone));
                                    if(isInitialDone){
                                        startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                                    }
                                }

                                @Override
                                public void onFailure(DatabaseError error) {

                                }
                            });

                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("auth", "signInWithCredential:failure", task.getException());

                    }


                    }
                });
    }

}
