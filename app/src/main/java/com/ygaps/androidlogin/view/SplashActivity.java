package com.ygaps.androidlogin.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.ygaps.androidlogin.R;
import com.ygaps.androidlogin.manager.Constants;
import com.ygaps.androidlogin.network.MyAPIClient;

import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        long time = sharedPref.getLong(getString(R.string.saved_access_token_time), (long)0);
        // Get user info with access token
        long expire = (new Date()).getTime()/1000 - time;
        MyAPIClient.getInstance().setAccessToken(accessToken);

        if (TextUtils.isEmpty(accessToken) || expire > Constants.expire_token) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }
}
