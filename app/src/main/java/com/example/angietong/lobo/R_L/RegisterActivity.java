package com.example.angietong.lobo.R_L;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.example.angietong.lobo.R;
import com.example.angietong.lobo.UI.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;

public class RegisterActivity extends AppCompatActivity {


    final String TAG = "RegisterActivity";


    ////connecting the xml items into this java class and turning them into their according java object
    @Bind(R.id.registerButton)
    Button mRegisterButton;

    //@Bind(R.id.usernameTextField)
    EditText mUsernameTextField;

    //@Bind(R.id.emailAddressTextField)
    EditText mEmailAddressTextField;

    //@Bind(R.id.passwordTextField)
    EditText mPasswordTextField;

    EditText mPassword2TextField;

    CheckBox cCheckBox;


    public void Register(View view) {
        Log.d(TAG, "Check0");
        String username = mUsernameTextField.getText().toString();
        String emailAddress = mEmailAddressTextField.getText().toString();
        String password = mPasswordTextField.getText().toString();
        String passwordConfirm = mPassword2TextField.getText().toString();


        if (username == null || username.equals("")) {
            showToast("Name cannot be empty");
            return;
        }


        if (password == null || password.equals("")) {
            showToast("Password cannot be empty");
            return;
        }

        if (passwordConfirm == null || password.equals("")) {
            showToast("Password Confirmation cannot be empty");
            return;
        }

        if (!passwordConfirm.equals(password)) {
            showToast("Password Confirmation must equal Password");
            return;
        }

        if(!cCheckBox.isChecked())
        {
            showToast("You must accept terms and conditions to register");
            return;
        }


        Log.d(TAG, "Check1");

        BackendlessUser user = new BackendlessUser(); //creating a new user object

        //setting the user parameters
        user.setPassword(password);
        user.setEmail(emailAddress);
        user.setProperty("username", username);
        user.setProperty("loginID", username);
        Log.d(TAG, "Check2");
//////ADD TESTERS

        //registering throug backendless


        Backendless.UserService.register( user, new DefaultCallback<BackendlessUser>( RegisterActivity.this )
        {
            @Override
            public void handleResponse( BackendlessUser response )
            {
                Log.d(TAG, "Check3");
                super.handleResponse( response );
                startActivity( new Intent( getBaseContext(), MainActivity.class ) );
                finish();
            }
        } );
        /*
        try {
            user = Backendless.UserService.register(user);
        } catch (BackendlessException exception) {
            Log.d(TAG, "It fked up");
 d
            // an error has occurred, the error code can be retrieved with fault.getCode()
        }
*/

    }

    @Override       //the on create is what is run automatically when activity is ran
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameTextField = (EditText) findViewById(R.id.registerActivityUsername);
        mEmailAddressTextField = (EditText) findViewById(R.id.registerActivityEmailAddress);
        mPasswordTextField = (EditText) findViewById(R.id.registerActivityPassword);
        mPassword2TextField = (EditText) findViewById(R.id.registerActivityPasswordConfirm);
        cCheckBox = (CheckBox) findViewById(R.id.registerActivityCheckBox);
        Log.d(TAG, "Check-0");
      /*
        mRegisterButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick( View view )
            {
               Register(null);

            }
        });*/


    /*
        setContentView(R.layout.activity_register);
        final String Tag = "RegisterActivity";
        BackendlessUser user = new BackendlessUser();
        user.setProperty(    "email", "mo@gmail.com" );
        user.setPassword( "iAmWatchingU" );
    */

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    public void toLoginActivity(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}


   // Backendless.( appId, secretKet, version ); // where to get the argument values for this call











