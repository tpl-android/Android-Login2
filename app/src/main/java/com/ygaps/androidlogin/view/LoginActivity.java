package com.ygaps.androidlogin.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.androidlogin.R;
import com.ygaps.androidlogin.model.LoginRequest;
import com.ygaps.androidlogin.model.LoginResponse;
import com.ygaps.androidlogin.network.MyAPIClient;
import com.ygaps.androidlogin.network.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    public static String TAG  = "LoginActivity";
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressDialog mProgressDialog;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        userService = MyAPIClient.getInstance().getAdapter().create(UserService.class);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        mProgressDialog.show();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(cancel == true){
            mProgressDialog.hide();
        }else{
            LoginRequest request = new LoginRequest();
            request.setUsername(email);
            request.setPassword(password);
            userService.login(request, new Callback<LoginResponse>() {
                @Override
                public void success(LoginResponse loginResponse, Response response) {
// Save login info
                    MyAPIClient.getInstance().setAccessToken(loginResponse.getData().getToken());
                    // Persist access token
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_access_token), loginResponse.getData().getToken());
                    long time = (new Date()).getTime()/1000;
                    editor.putString(getString(R.string.saved_access_token), loginResponse.getData().getToken());
                    editor.putLong(getString(R.string.saved_access_token_time), time);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    LoginActivity.this.finish();

                    mProgressDialog.hide();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, error.getMessage()+ " "+ error.getUrl());
                    switch (error.getKind()) {
                        case NETWORK:
                        case UNEXPECTED:
                            Toast.makeText(LoginActivity.this, R.string.error_check_network_connection, Toast.LENGTH_LONG).show();
                            break;
                        case HTTP:
                            if (error.getResponse().getStatus() == 401 || 400 == error.getResponse().getStatus()) {
                                mEmailView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                                break;
                            }
                        default:
                            Toast.makeText(LoginActivity.this, R.string.error_backend_error, Toast.LENGTH_LONG).show();
                    }
                    mProgressDialog.hide();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

