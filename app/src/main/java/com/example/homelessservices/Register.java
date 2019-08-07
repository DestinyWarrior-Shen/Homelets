package com.example.homelessservices;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Register extends Fragment implements FirebaseAuth.AuthStateListener{
    private Button signUpButton;
    private FirebaseAuth mAuth;
    //private FirebaseAuthListener mAuthListener;
    private EditText mEmailView,mPasswordView;
    private View progressView,signUpFormView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();
        //mAuthListener = new FirebaseAuthListener();

        mEmailView = (EditText) view.findViewById(R.id.signup_email);
        mPasswordView = (EditText) view.findViewById(R.id.signup_password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
//            {
//                if (id == R.id.signUp || id == EditorInfo.IME_NULL)
//                {
//                    attemptSignUp();
//                    return true;
//                }
//                return false;
//            }
//        });

        signUpButton = (Button) view.findViewById(R.id.email_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        signUpFormView = view.findViewById(R.id.signUp_form);
        progressView = view.findViewById(R.id.signUp_progress);

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
            mAuth.addAuthStateListener(this);
    }

    /**
     * Execute when activity is terminated, remove FireBase authentication state listener.
     */
    @Override
    public void onStop() {
        super.onStop();

        if(mAuth != null)
            mAuth.removeAuthStateListener(this);
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


    /**
     * Attempts register the account specified by the login form.
     * If there are form errors (invalid email, invalid passwords), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean interrupt = false;
        View focusView = null;

        // Check for a email address if is empty.
        if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            interrupt = true;
        }
        else if (!isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_not_valid_email));
            focusView = mEmailView;
            interrupt = true;
        }

        // Check for password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            interrupt = true;
        }
        else if (!isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            interrupt = true;
        }

        if (interrupt)
        {
            // There was an error; not attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), R.string.SignUp_success,Toast.LENGTH_SHORT).show();
                    }
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), R.string.SignUp_failed,Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
            });
        }
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
            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener (new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

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

//    /**
//     * Define a class to implement the AuthStateListener interface. Define the state change behaviour
//     * if user has logged in, jumping to Home activity.
//     */
//    private class FirebaseAuthListener implements FirebaseAuth.AuthStateListener {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
//        {
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//
//            if(user != null)
//            {
//                Intent newIntent = new Intent(getActivity(), MainActivity.class);
//                startActivity(newIntent);
//            }
//            else
//            {
//                // User just signed out
//                //Toast.makeText(LoginActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
