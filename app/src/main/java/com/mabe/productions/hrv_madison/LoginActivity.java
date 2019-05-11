package com.mabe.productions.hrv_madison;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
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

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.database.DatabaseError;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebase.FireGlobalUser;
import com.mabe.productions.hrv_madison.firebase.FireUser;
import com.mabe.productions.hrv_madison.firebase.FirebaseUtils;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2;
    private TextView txt_slogan;
    private EditText editText_username;
    private EditText editText_password;
    private Button btn_login;
    private AppCompatButton registerButton;

    private ImageButton imgBtn_facebook;
    private ImageButton imgBtn_google;
    private ImageView circle;
    private ImageView img_login_appicon;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CustomLoadingDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Utils.changeNotifBarColor(Color.parseColor("#3e5266"), getWindow());

        //This is required for Facebook SDK to work
        AppEventsLogger.activateApp(getApplication());

        initializeViews();
        setFonts();

        //Registering facebook login callback
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //Saving user data to firebase after successful facebook auth.
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );

        //GMAIL LOGIN
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initializeViews() {


        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation right_to_left = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation bottom_to_top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation top_to_bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fade_in_delay = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);
        Animation top_to_bottom_delay = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_delay);


        txt_slogan = (TextView) findViewById(R.id.slogan);
        editText_username = (EditText) findViewById(R.id.usernameEditText);
        editText_password = (EditText) findViewById(R.id.passwordEditText);
        imgBtn_facebook = (ImageButton) findViewById(R.id.login_facebook);
        imgBtn_google = (ImageButton) findViewById(R.id.login_googlePlus);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        registerButton = (AppCompatButton) findViewById(R.id.noAccount);
        circle = (ImageView) findViewById(R.id.circleDot);
        img_login_appicon = (ImageView) findViewById(R.id.imageView);


        imgBtn_facebook.startAnimation(left_to_right);
        imgBtn_google.startAnimation(right_to_left);
        btn_login.startAnimation(bottom_to_top);
        registerButton.startAnimation(bottom_to_top);
        circle.startAnimation(fade_in);
        editText_username.startAnimation(fade_in_delay);
        editText_password.startAnimation(fade_in_delay);
        txt_slogan.startAnimation(top_to_bottom_delay);


        Utils.hideKeyboardAfterClickaway(editText_username, this);
        Utils.hideKeyboardAfterClickaway(editText_password, this);

    }

    private void setFonts() {
        Typeface face_slogan = Typeface.createFromAsset(getAssets(),
                "fonts/CORBEL.TTF");
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/Verdana.ttf");

        txt_slogan.setTypeface(face_slogan);
        btn_login.setTypeface(futura);
        editText_username.setTypeface(futura);
        editText_password.setTypeface(futura);
    }


    public void register(View view){
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    public void login(View view) {

        Crashlytics.log("Beggining to authenticate with e-mail");


        String email = editText_username.getText().toString();
        String password = editText_password.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        progressDialog = new CustomLoadingDialog(this, "Logging you in");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (!user.isEmailVerified()) {
                                Crashlytics.log("E-mail is unverified.");
                                Toast.makeText(LoginActivity.this, "Your email is unverified!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Crashlytics.log("E-mail is verified");


                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                            if (isNew) {
                                //In this case, we know for sure that user hasn't provided us with initial information.
                                //FirebaseUtils.addUser();
                                Crashlytics.log("User is new as determined by getAdditionalUserInfo().isNewUser().");

                                startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                            } else {

                                //If user already exists, we still need to check whether he has completed initial info questionnaire
                                Crashlytics.log("User is not new as determined by getAdditionalUserInfo().isNewUser().");

                                //Showing a custom progress dialog
                                final CustomLoadingDialog loadingDialog = new CustomLoadingDialog(LoginActivity.this, "Checking your account status");
                                loadingDialog.show();
                                Crashlytics.log("Checking if user has done initial activity evaluation.");
                                FirebaseUtils.isInitialDone(new FirebaseUtils.OnInitialDoneFetchListener() {
                                    @Override
                                    public void onSuccess(boolean isInitialDone) {
                                        loadingDialog.dismiss();

                                        if (isInitialDone) {
                                            Crashlytics.log("User has done initial activity evaluation.");
                                            //User has done the initial questionnaire. Downloading it's data, and launching MainScreenActivity afterwards.
                                            getGlobalUserInformation(true, true);
                                        } else {
                                            //User has not filled out the initial questionnaire. Opening IntroInitialPage for the user to do so.
                                            Crashlytics.log("User has not done initial activity evaluation");
                                            LoginActivity.this.finish();
                                            startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                                        }
                                    }

                                    @Override
                                    public void onFailure(DatabaseError error) {
                                        loadingDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }


                    }
                });


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
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Authenticates FirebaseUser with gmail {@link GoogleSignInAccount}.
     * After successful authentication the following is done:
     * If user has filled out initial data, it is fetched, and {@link MainScreenActivity} is launched.
     * If user has not filled out initial data, {@link IntroInitialPage} is launched.
     *
     * @param acct The {@link GoogleSignInAccount}, that is used for authentication
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        progressDialog = new CustomLoadingDialog(this, "Logging you in");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(facebookGmailListener);
    }


    /**
     * Authenticates FirebaseUser with facebook AccessToken.
     * After successful authentication the following is done:
     * If user has filled out initial data, it is fetched, and {@link MainScreenActivity} is launched.
     * If user has not filled out initial data, {@link IntroInitialPage} is launched.
     *
     * @param token The facebook {@link AccessToken}, that is used for authentication
     */
    private void firebaseAuthWithFacebook(AccessToken token) {

        progressDialog = new CustomLoadingDialog(this, "Logging you in");
        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, facebookGmailListener);
    }

    /**
     * Fetches and saves the global user information (email, displayname) locally
     *
     * @param showProgressDialog            If true, a progress dialog is shown
     * @param startFetchingInitialInfoAfter If true, getInitialUserInformation(boolean) method is called after
     */
    private void getGlobalUserInformation(boolean showProgressDialog, final boolean startFetchingInitialInfoAfter) {
        Crashlytics.log("Downloading global user data.");

        final CustomLoadingDialog dialog = new CustomLoadingDialog(LoginActivity.this, "Loading your data");

        if (showProgressDialog) {
            dialog.show();
        }

        FirebaseUtils.getGlobalUserInstance(new FirebaseUtils.OnGlobalUserDoneFetchListener() {
            @Override
            public void onSuccess(FireGlobalUser globalUser) {
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_USERNAME, globalUser.getDisplayname(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_EMAIL, globalUser.getEmail(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                progressDialog.dismiss();

                if (startFetchingInitialInfoAfter) {
                    getInitialUserInformation(true);
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });


    }

    /**
     * This method gets initial user information (height, weight, etc.) from the
     * database, and saves it locally (SharedPreferences).
     * <p>
     * It runs on assumption that initial user information exists in remote database.
     * <p>
     * After data is saved locally, MainScreenActivity is started.
     *
     * @param showProgressDialog If true, progress dialog is shown while data is being fetched.
     */
    private void getInitialUserInformation(boolean showProgressDialog) {

        Crashlytics.log("Downloading initial user info.");

        final CustomLoadingDialog dialog = new CustomLoadingDialog(LoginActivity.this, "Loading your data");

        if (showProgressDialog) {
            dialog.show();
        }

        FirebaseUtils.getUserFromFirebase(new FirebaseUtils.OnUserDoneFetchListener() {
            @Override
            public void onSuccess(FireUser fireUser) {
                Crashlytics.log("Saving initial user info to shared prefs.");

                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_ACTIVITY_STREAK, fireUser.getActivity_streak(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_INITIAL_DURATION, fireUser.getBase_duration(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_KMI, fireUser.getKmi(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_BIRTHDAY, fireUser.getBirthday(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_GENDER, fireUser.getGender(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_HEIGHT, fireUser.getHeight(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX, fireUser.getActivity_index(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_WEIGHT, fireUser.getWeight(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_BASE_DURATION, fireUser.getMaxDuration(), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_WEEK_DAYS, FeedReaderDbHelper.getWeeksFromString(fireUser.getWorkout_days()), FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_WEEKLY_PROGRAM_GENERATED_DATE, fireUser.getFirst_weekly_Date(), FeedReaderDbHelper.SHARED_PREFS_SPORT);

                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_WORKOUT_INTERVALS, fireUser.getWorkout_intervals(), FeedReaderDbHelper.SHARED_PREFS_SPORT);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_RUNNING_PULSE_ZONES, fireUser.getRunning_pulse_zones(), FeedReaderDbHelper.SHARED_PREFS_SPORT);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_WALKING_PULSE_ZONES, fireUser.getWalking_pulse_zones(), FeedReaderDbHelper.SHARED_PREFS_SPORT);
                Utils.saveToSharedPrefs(LoginActivity.this, FeedReaderDbHelper.FIELD_DURATION, fireUser.getWorkout_duration(), FeedReaderDbHelper.SHARED_PREFS_SPORT);

                dialog.dismiss();

                startActivity(new Intent(LoginActivity.this, MainScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }


    private OnCompleteListener facebookGmailListener = new OnCompleteListener<AuthResult>() {

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            progressDialog.dismiss();
            if (task.isSuccessful()) {

                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                if (isNew) {
                    //In this case, we know for sure that user hasn't provided us with initial information.
                    FirebaseUtils.addUser(null);
                    startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
                } else {
                    //If user already exists, we still need to check whether he has completed initial info questionnaire

                    //Showing a custom progress dialog
                    final CustomLoadingDialog loadingDialog = new CustomLoadingDialog(LoginActivity.this, "Checking your account status");
                    loadingDialog.show();

                    FirebaseUtils.isInitialDone(new FirebaseUtils.OnInitialDoneFetchListener() {
                        @Override
                        public void onSuccess(boolean isInitialDone) {
                            loadingDialog.dismiss();

                            if (isInitialDone) {
                                //User has done the initial questionnaire. Downloading it's data, and launching MainScreenActivity afterwards.
                                getGlobalUserInformation(true, true);
                            } else {
                                //User has not filled out the initial questionnaire. Opening IntroInitialPage for the user to do so.
                                startActivity(new Intent(LoginActivity.this, IntroInitialPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }

                        @Override
                        public void onFailure(DatabaseError error) {
                            loadingDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

            }



        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        Runtime.getRuntime().gc();
    }
}
