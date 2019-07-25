package com.example.homelessservices;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/**
 * Created by Sean on 9/15/2017.
 */

public class TransmitStatus {

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    public static void showProgress(final boolean show, final View view) {
//
//        final View mContentView = view.findViewById(R.id.mContentView);
//        final View mProgressView = view.findViewById(R.id.load_data_progress);
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
//        {
//            //int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//            int shortAnimTime = 2000;
//            mContentView.setVisibility(show ? view.GONE : view.VISIBLE);
//            mContentView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation)
//                {
//                    mContentView.setVisibility(show ? view.GONE : view.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? view.VISIBLE : view.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener (new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation)
//                {
//                    mProgressView.setVisibility(show ? view.VISIBLE : view.GONE);
//                }
//            });
//        }
//        else
//        {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? view.VISIBLE : view.GONE);
//            mContentView.setVisibility(show ? view.GONE : view.VISIBLE);
//        }
//    }
}
