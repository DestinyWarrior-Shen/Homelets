package com.example.homelessservices;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class Description extends Fragment
{
    private TextView title,description;
    private String content;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        title = (TextView) view.findViewById(R.id.title1);
        description = (TextView) view.findViewById(R.id.text_description);

        description.setText(content);
        return view;
    }

    public void sendContentToDescriptionFragment(String content)
    {
        this.content = content;
    }
}
