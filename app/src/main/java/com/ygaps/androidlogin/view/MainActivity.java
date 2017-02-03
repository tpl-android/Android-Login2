package com.ygaps.androidlogin.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ygaps.androidlogin.R;
import com.ygaps.androidlogin.network.MyAPIClient;
import com.ygaps.androidlogin.network.UserService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        userService = MyAPIClient.getInstance().getAdapter().create(UserService.class);

        Button logout = (Button)findViewById(R.id.b_Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                userService.logout(new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        // Clear token
                        //client.setAccessToken(null);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(MainActivity.this.getString(R.string.saved_access_token));
                        editor.remove(MainActivity.this.getString(R.string.saved_access_token_time));
                        editor.commit();
                        // Open LoginActivity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        mProgressDialog.hide();
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mProgressDialog.hide();
                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
