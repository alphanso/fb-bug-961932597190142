package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Collection;

public class ParseStarterProjectActivity extends Activity {
    private static final String LOG_TAG = "ParseStarterProject";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
            openMainActivity();
        }
	}

    public void onFbLogin(View v) {
        Log.i(LOG_TAG, "Fb login initiated");
        final ParseUser currentUser = ParseUser.getCurrentUser();
        final Collection<String> permissions = Arrays.asList("public_profile", "email", "user_friends");

        if(currentUser != null) {
            Log.i(LOG_TAG, "Linking Anonymous user with facebook");
            ParseFacebookUtils.linkWithReadPermissionsInBackground(currentUser, this, permissions, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(LOG_TAG, "Auth complete");
                    if (e == null) {
                        if (ParseFacebookUtils.isLinked(currentUser)) {
                            onUserSignedUpAndLoggedIn();
                        } else {
                            onUserCancelledLogin();
                        }
                    } else {
                        Log.w(LOG_TAG, "something went wrong during facebook login: " + e.toString()+ e.getCode() + e.getMessage());
                        Log.w(LOG_TAG, e);
                        //ToastHelper.showDefaultErrorMessageToast(LoginActivity.this);
                    }
                }
            });
        } else {
            Log.i(LOG_TAG, "Logging in new user with facebook");
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    Log.w(LOG_TAG, "Auth complete");
                    if (err == null) {
                        if (user == null) {
                            onUserCancelledLogin();
                        } else if (user.isNew()) {
                            onUserSignedUpAndLoggedIn();
                        } else {
                            onUserLoggedIn();
                        }
                    } else {
                        Log.w(LOG_TAG, "something went wrong during facebook login: " + err.getMessage());
                        //ToastHelper.showDefaultErrorMessageToast(LoginActivity.this);
                    }
                }
            });
        }
    }

    private void onUserLoggedIn() {
        Log.w(LOG_TAG, "New user session created");
        openMainActivity();
    }
;
    private void onUserCancelledLogin() {
        Log.w(LOG_TAG, "User Login cancelled");
    }

    private void onUserSignedUpAndLoggedIn() {
        Log.w(LOG_TAG, "New user created and logged-in");
        openMainActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onGuestLogin(View v) {
        Log.i(LOG_TAG, "Guest login");
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.e(LOG_TAG, "Anonymous login failed." + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Anonymous login failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(LOG_TAG, "Anonymous user logged in.");
                    }
                }
            });
        }
        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
