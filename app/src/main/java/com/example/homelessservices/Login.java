package com.example.homelessservices;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends Fragment
{
    private static final String TAG = "MainActivity";
    private Button forgetButton, goToSignUpButton, mEmailSignInButton;
    private FirebaseAuth mAuth;
    private FirebaseAuthListener mAuthListener;
    private EditText mEmailView,mPasswordView;
    private View mProgressView,mLoginFormView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuthListener();

        mEmailView = (EditText) view.findViewById(R.id.email);
        mPasswordView = (EditText) view.findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
//            {
//                if (id == R.id.Login || id == EditorInfo.IME_ACTION_DONE)
//                {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        forgetButton = (Button) view.findViewById(R.id.email_forget_button);
        forgetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                String emailAddress = mEmailView.getText().toString();
                if (emailAddress.length() != 0)
                {
                    auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful())
                            {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(getActivity(), "Email sent successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    builder.setMessage("Please input your email address");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });

        goToSignUpButton = (Button) view.findViewById(R.id.go_to_sign_up_button);
        goToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Register register = new Register();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragment_content, register);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        return view;
    }

    /**
     * Execute after onCreate method, adding FireBase authentication state listener to monitor the
     * authentication state.
     */
    @Override
    public void onStart() {
        super.onStart();

        if(mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Execute when activity is terminated, remove FireBase authentication state listener.
     */
    @Override
    public void onStop() {
        super.onStop();

        if(mAuth != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    /**
     * Attempts to sign in, if there are form errors (invalid email, invalid passwords), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for email address if is empty.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner
            showProgress(true);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), R.string.Login_success, Toast.LENGTH_SHORT).show();
                    }
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified to jump to Home activity.
                    if (!task.isSuccessful())
                    {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        Toast.makeText(getActivity(), R.string.Login_failed, Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
            });
        }
    }

    /**
     * Judging the availability of the email
     */
    public boolean isEmailValid(String email) {
        boolean status = false;
        if (email.contains("@")) {
            status = true;
        }
        return status;
    }

    /**
     * Judging the availability of the passwords
     */
    public boolean isPasswordValid(String password) {
        boolean judge = false;
        if (password.length() > 6) {
            judge = true;
        }
        return judge;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            //int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            int shortAnimTime = 200;
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener (new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Define a class to implement the AuthStateListener interface. Define the state change behaviour
     * if user has logged in, jumping to Home activity.
     */
    private class FirebaseAuthListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
        {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if(user != null)
            {
                Intent newIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(newIntent);
            }
            else
            {
                // User just signed out
                //Toast.makeText(LoginActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


