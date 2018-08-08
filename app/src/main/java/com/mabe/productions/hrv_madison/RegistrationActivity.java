package com.mabe.productions.hrv_madison;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.mabe.productions.hrv_madison.firebaseDatase.FirebaseUtils;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;

public class RegistrationActivity extends AppCompatActivity {

    private EditText register_name;
    private EditText register_username;
    private EditText register_password;
    private EditText register_repeat_password;
    private ImageView img_back_arrow;
    private TextView toolbar_title_registration;
    private TextInputLayout register_name_input;
    private TextInputLayout register_username_input;
    private TextInputLayout register_password_input;
    private TextInputLayout register_repeat_password_input;

    private AppCompatCheckBox terms_checkbox;
    private TextView txt_terms;
    private LinearLayout terms_layout;
    private FirebaseAuth mAuth;

    private AppCompatButton register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();
        setFonts();
    }


    public void backImage(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    private void setFonts(){
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        txt_terms.setTypeface(futura);
    }

    private void initializeViews(){
        Animation anim_txt_top_down = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation anim_left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_right_to_left = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation anim_button = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_delay);
        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);

        register_name = (EditText) findViewById(R.id.register_name);
        register_username = (EditText) findViewById(R.id.register_username);
        register_password = (EditText) findViewById(R.id.register_password);
        register_repeat_password = (EditText) findViewById(R.id.register_repeat_password);
        register_button = (AppCompatButton) findViewById(R.id.buttonRegister);

        terms_checkbox = (AppCompatCheckBox) findViewById(R.id.terms_checkbox);
        txt_terms = (TextView) findViewById(R.id.textView2);
        terms_layout = (LinearLayout) findViewById(R.id.layout_terms);

        register_name_input = (TextInputLayout) findViewById(R.id.register_name_input);
        register_username_input = (TextInputLayout) findViewById(R.id.register_username_input);
        register_password_input = (TextInputLayout) findViewById(R.id.register_password_input);
        register_repeat_password_input = (TextInputLayout) findViewById(R.id.register_repeat_password_input);
        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        toolbar_title_registration = (TextView) findViewById(R.id.toolbar_title_registration);

        register_name_input.startAnimation(anim_right_to_left);
        register_username_input.startAnimation(anim_left_to_right);
        register_password_input.startAnimation(anim_right_to_left);
        register_repeat_password_input.startAnimation(anim_left_to_right);
        terms_layout.startAnimation(anim_txt_top_down);
        register_button.startAnimation(anim_button);
        img_back_arrow.startAnimation(left_to_right);
        toolbar_title_registration.startAnimation(left_to_right_d);

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void register(View view) {
        String name = register_name.getText().toString();
        final String username = register_username.getText().toString();
        String password = register_password.getText().toString();
        String repeat_password = register_repeat_password.getText().toString();



        String estring = "Please enter a valid email address";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.BLACK);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
        ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);

        if(name.equals("")){
            register_name_input.setError("First name is required"); // show error
            return;
        }else{
            register_name_input.setError(null);
        }

        if(!isEmailValid(username)){
            register_username_input.setError("Enter valid email");
            return;
        }else{
            register_username_input.setError(null);
        }

        if(password.length()<8){
            register_password_input.setError("Password must contain atleast 8 characters");
            return;
        }else{
            register_password_input.setError(null);
        }

        if(!password.equals(repeat_password)){
            register_repeat_password_input.setError("Passwords do not match");
            return;
        }else{
            register_repeat_password_input.setError(null);
        }

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i("auth", user.getEmail());
                            Log.i("auth", user.getUid());
                            FirebaseUtils.addUser();
                            user.sendEmailVerification();
                            Utils.buildAlertDialogPrompt(RegistrationActivity.this, "Success!", "Successful registration. To login, please verify your email adress", "Cancel", "", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                }
                            },null);


                        } else {

                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                return;

                        }


                    }
                });




    }
}
