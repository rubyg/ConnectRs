package com.oolink.exo.connectrs;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import util.ConnectFacebook;
import util.ConnectGoogle;
import util.ConnectTwitter;
import util.MyAsyncTask;


public class Home extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private TextView myName, myEmail;
    private ImageView myProfile;
    private LinearLayout myProfileLayout;
    private final FragmentActivity fragmentActivity = this;
    private final Context contextActivity = this;

    private int logRs = 0;

    //Google
    private SignInButton ggin;
    private Button ggout;
    private ConnectGoogle connectGoogle;
    private final GoogleApiClient.OnConnectionFailedListener listener = this;


    //Facebook
    private LoginButton fb;
    private ConnectFacebook connectFacebook;


    //Twitter
    private TwitterLoginButton ttin;
    private Button ttout;
    private ConnectTwitter connectTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectFacebook = new ConnectFacebook(this);
        connectTwitter = new ConnectTwitter(this);
        setContentView(R.layout.activity_home);


        myName = (TextView) findViewById(R.id.myName);
        myEmail = (TextView) findViewById(R.id.myEmail);
        myProfile = (ImageView) findViewById(R.id.myProfil);
        myProfileLayout = (LinearLayout) findViewById(R.id.myProfileLayout);


        //For login Google
        connectGoogle = new ConnectGoogle(contextActivity, myName, myEmail, myProfile);
        connectGoogle.signInServices(fragmentActivity, listener);

        //Connexion Google
        ggin = (SignInButton) findViewById(R.id.ggin);
        ggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(connectGoogle.signInGoogle(), connectGoogle.getRcSignIn());
                logRs = 1;
            }
        });
        //Deconnexion Google
        ggout = (Button) findViewById(R.id.ggout);
        ggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectGoogle.signOutGoogle();
                updateUI(connectGoogle.isVisibility());
                logRs = 0;
            }
        });

        //For login Facebook
        fb = (LoginButton) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFacebook.handlerSignInResultFacebook(fb, myName, myEmail, myProfile);
                logRs = 2;

            }
        });


        //For login Twitter
        ttin = (TwitterLoginButton)

                findViewById(R.id.ttin);

        ttin.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        logRs = 3;
                                    }
                                }

        );
        ttin.setCallback(connectTwitter.CallTwitter(myName, myEmail, myProfile));

        //For logout Twitter
        ttout = (Button)

                findViewById(R.id.ttout);

        ttout.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         connectTwitter.sigOutTwitter();
                                         updateUI(false);
                                         logRs = 0;
                                         ttout.setVisibility(View.GONE);
                                     }
                                 }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Home.class.getSimpleName(), "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (logRs == 1) {
            connectGoogle.callGoogle();
        }

        if (logRs == 3) {
            updateUI(true);
            ttout.setVisibility(View.VISIBLE);
        } else {
            updateUI(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Home.class.getSimpleName(), "---Type de RS: " + logRs + " ---");
        //si Connexion via google +
        if (requestCode == connectGoogle.getRcSignIn() && logRs == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            connectGoogle.handleSignInResultGoogle(result);
            updateUI(connectGoogle.isVisibility());
            Log.d(Home.class.getSimpleName(), " Connexion Google");

        }
        //si Connexion via Facebook
        if (logRs == 2) {
            Log.d(Home.class.getSimpleName(), " Connexion Facebook");
            connectFacebook.getCallbackManager().onActivityResult(requestCode, resultCode, data);
            updateUI(true);

        }
        //Si connexion via Twitter
        if (logRs == 3) {
            Log.d(Home.class.getSimpleName(), " Connexion Twitter");
            ttin.onActivityResult(requestCode, resultCode, data);
            updateUI(true);

        }

    }

    /**
     * Visibilite des widgets et des layouts
     *
     * @param isSignedIn détermine le visibilité des widget en fonction de la connexion
     */

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            if (logRs == 1) {
                ggin.setVisibility(View.GONE);
                ggout.setVisibility(View.VISIBLE);
            }
            myProfileLayout.setVisibility(View.VISIBLE);
        } else {
            if (logRs == 1 || logRs == 0) {
                ggin.setVisibility(View.VISIBLE);
                ggout.setVisibility(View.GONE);
            }
            myProfileLayout.setVisibility(View.GONE);
        }
    }


}



