package com.example.homelessservices;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.thebluealliance.spectrum.SpectrumDialog;


public class Setting extends Fragment implements View.OnClickListener
{
    private LinearLayout theme_setting,size_setting,changePassword,emailLayout;
    private FirebaseAuth mAuth;
    private EditText email;
    private Button OK;
    private TextView time;
    private int count = 60;
    private int COUNT_TIME = 0;
    private Handler handler;
    //private Button theme_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        getActivity().setTitle("Settings");
        theme_setting = (LinearLayout) view.findViewById(R.id.layout_theme_setting);
        size_setting = (LinearLayout) view.findViewById(R.id.layout_size_setting);
        changePassword = (LinearLayout) view.findViewById(R.id.layout_change_password);
        emailLayout = (LinearLayout) view.findViewById(R.id.layout_email);
        email = (EditText) view.findViewById(R.id.email_change);
        OK = (Button) view.findViewById(R.id.btn_finish);
        time = (TextView) view.findViewById(R.id.tv_time);
//        theme_button = (Button) theme_setting.findViewById(R.id.color_click);
//        theme_button.setBackgroundColor(getSelectedColor());
        mAuth = FirebaseAuth.getInstance();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg)
            {
                if(count <= 0)
                {
                    count = 60;
                    time.setText("");
                    changePassword.setClickable(true);
                    OK.setClickable(true);
                    return;
                }
                count--;
                time.setText(""+count+"s");
                sendEmptyMessageDelayed(COUNT_TIME,1000);
                changePassword.setClickable(false);
            }
        };
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            changePassword.setOnClickListener(this);
            theme_setting.setOnClickListener(this);
            size_setting.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layout_change_password:
                changePSW();
                break;
            case R.id.layout_theme_setting:
                showDialog();
                break;
            case R.id.layout_size_setting:
                transactionToSettingFragment(new FontSetting());
                break;
        }
    }

    public void transactionToSettingFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void sendEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Email sent successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Some errors occur", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changePSW() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("Are you sure you want to change password ?");
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (mAuth.getCurrentUser() == null)
                {
                    emailLayout.setVisibility(View.VISIBLE);
                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            sendEmail(email.getText().toString());
                            handler.sendEmptyMessage(COUNT_TIME);
                            OK.setClickable(false);
                        }
                    });

                }
                else
                {
                    sendEmail(mAuth.getCurrentUser().getEmail());
                    handler.sendEmptyMessage(COUNT_TIME);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();

    }

    /**
     * Get the preferred color from preference file
     * @return the selected color
     */
    private int getSelectedColor(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int selectedColor = sharedPreferences.getInt("color", R.color.colorPrimaryDark);
        return selectedColor;
    }

    /**
     * Display a color palette for users to choose a theme color
     */
    private void showDialog(){
        DialogFragment dialogFragment = new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColor(getSelectedColor())
                .setDismissOnColorSelected(true)
                .setTitle("Select Your Favorite Theme Color")
                .setOutlineWidth(0)
                .setFixedColumnCount(4)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult)
                        {
                            int selectedtheme = setColorTheme(color);
                            storePreference("theme", selectedtheme);
                            storePreference("color", color);
                            getActivity().recreate();
                            Toast.makeText(getContext(), "Theme color has been changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build();
        Activity activity = getActivity();
        if(activity instanceof FragmentActivity) {
            dialogFragment.show(((FragmentActivity)activity).getSupportFragmentManager(),"theme color");
        }
    }

    /**
     * Save the selected color/theme into a preference file
     *
     * @param key
     *          Key of selected color is "color"
     *          Key of selected theme is "theme"
     *          Key of selected theme is "text size"
     * @param item Color/Theme/Text Size
     */
    private void storePreference(String key, int item){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,item);
        editor.apply();
    }

    /**
     * Based on the selected color, the related theme will be found
     * @param color the color is chose by the user
     * @return the theme is created by the selected color
     */
    private int setColorTheme(int color){
        switch (color){
            case 0Xffef5350:
                return R.style.AppThemeRed;

            case 0Xffec407a:
                return R.style.AppThemePink;

            case 0Xffff3d00:
                return R.style.AppThemeDeepOrangeAccent;

            case 0Xffffab40:
                return R.style.AppThemeOrangeAccent;

            case 0Xffff7043:
                return R.style.AppThemeOrange;

            case 0Xffffee58:
                return R.style.AppThemeYellow;

            case 0Xff7e57c2:
                return R.style.AppThemeDeepPurple;

            case 0Xffba68c8:
                return R.style.AppThemePurple;

            case 0Xff03a9f4:
                return R.style.AppThemeLightBlue;

            case 0Xff00e676:
                return R.style.AppThemeGreenAccent;

            case 0Xff1de9b6:
                return R.style.AppThemeTealAccent;

            case 0Xffaed581:
                return R.style.AppThemeLightGreen;

            case 0Xff000000:
                return R.style.AppThemeBlack;

            case 0Xffbcaaa4:
                return R.style.AppThemeBrown;

            case 0Xffbdbdbd:
                return R.style.AppThemeGray;

            default:
                return R.style.AppTheme;
        }
    }
}
