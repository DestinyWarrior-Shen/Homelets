package com.example.homelessservices;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
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


public class AccountSetting extends Fragment implements View.OnClickListener
{
    private LinearLayout changePassword,deleteAccount;
    private FirebaseAuth mAuth;
    private EditText email;
    private LinearLayout emailLayout;
    private Button OK;
    private TextView time;
    private int count = 60;
    private int COUNT_TIME = 0;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_account_setting, container, false);
        getActivity().setTitle("Setting");
        changePassword = (LinearLayout) view.findViewById(R.id.layout_change_password);
        deleteAccount = (LinearLayout) view.findViewById(R.id.layout_delete_account);
        emailLayout = (LinearLayout) view.findViewById(R.id.layout_email);
        email = (EditText) view.findViewById(R.id.email_change);
        OK = (Button) view.findViewById(R.id.btn_finish);
        time = (TextView) view.findViewById(R.id.tv_time);


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
                    return;
                }
                count--;
                time.setText(""+count+"s");
                sendEmptyMessageDelayed(COUNT_TIME,1000);
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
            deleteAccount.setOnClickListener(this);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layout_change_password:
                changePSW();
                break;
        }
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
                    changePassword.setClickable(false);
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
}
