package com.example.homelessservices;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by Fancy on 2017/09/10.
 */

public class FontSetting extends Fragment {
    private SeekBar seekBar;
    private TextView changingText;
    private Button confirm_font_size;

    public static FontSetting newInstance() {
        
        Bundle args = new Bundle();
        
        FontSetting fragment = new FontSetting();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font, container, false);
        seekBar = (SeekBar)view.findViewById(R.id.text_size_controller);
        changingText = (TextView)view.findViewById(R.id.changingText);
        confirm_font_size = (Button)view.findViewById(R.id.confirm_font_size);
        int index = (getSelectedFontSize()-10)/4;
        changingText.setTextSize(COMPLEX_UNIT_SP, getSelectedFontSize());
        seekBar.setProgress(index);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            // A listener for a seek bar
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progress = progress * 4 + 10;
                    changingText.setTextSize(COMPLEX_UNIT_SP, progress);
                    //System.out.println(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            // A listener for a button to make sure change all text size in the app
            confirm_font_size.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int size = seekBar.getProgress() * 4 + 10;
                    storePreference("font_size", size);
                    System.out.println(size);
                    getActivity().recreate();
                    //setFontSize();
                    Toast.makeText(getContext(), "Text size has been changed", Toast.LENGTH_SHORT).show();
                }
            });
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
     * Get the selected text size from the preference file
     */
    private int getSelectedFontSize(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int size = sharedPreferences.getInt("font_size", 14);
        return size;
    }


    private List<View> getAllChildViews() {
        View view = getActivity().getWindow().getDecorView();
        return getAllChildViews(view);
    }

    private List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                allChildren.add(viewChild);
                allChildren.addAll(getAllChildViews(viewChild));
            }
        }
        return allChildren;
    }

    public void setFontSize(){
        List<View> views = getAllChildViews();
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) instanceof TextView) {
                ((TextView) views.get(i)).setTextSize(COMPLEX_UNIT_PX, getSelectedFontSize());
                break;
            }
            if (views.get(i) instanceof Button) {
                ((Button) views.get(i)).setTextSize(COMPLEX_UNIT_PX,getSelectedFontSize());
                break;
            }
            if (views.get(i) instanceof EditText) {
                ((EditText) views.get(i)).setTextSize(COMPLEX_UNIT_PX,getSelectedFontSize());
                break;
            }
        }
    }
}
