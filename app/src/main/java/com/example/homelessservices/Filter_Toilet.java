package com.example.homelessservices;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class Filter_Toilet extends Fragment
{
    private RadioGroup rg;
    private View typeView;
    private ArrayList<CheckBox> allTypesCheckBoxList,selectedTypesCheckBoxList;
    private CheckBox male,female,wheelchair,baby;
    private ArrayList<ToiletPlace> toiletPlaceArrayList,toiletResultList;
    private Button ok,nearest;
    private String status;
    private RadioButton radioAll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_filter__toilet, container, false);
        getActivity().setTitle("Find Near Toilet");
        typeView = view.findViewById((R.id.select_type));
        rg = (RadioGroup) view.findViewById(R.id.radio_group);
        allTypesCheckBoxList = new ArrayList<>();
        selectedTypesCheckBoxList = new ArrayList<>();
        toiletResultList = new ArrayList<>();
        status = "All Toilets";

        toiletPlaceArrayList = ReadDataFromFireBase.readToiletData();

        radioAll = (RadioButton) view.findViewById(R.id.radio_all);
        radioAll.setChecked(true);
        ok = (Button) view.findViewById(R.id.btn_okok);
        nearest = (Button) view.findViewById(R.id.btn_nearest);
        male = (CheckBox) view.findViewById(R.id.cb_m);
        female = (CheckBox) view.findViewById(R.id.cb_fm);
        wheelchair = (CheckBox) view.findViewById(R.id.cb_wh);
        baby = (CheckBox) view.findViewById(R.id.cb_ba);

        allTypesCheckBoxList.add(male);
        allTypesCheckBoxList.add(female);
        allTypesCheckBoxList.add(wheelchair);
        allTypesCheckBoxList.add(baby);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final View view = getView();
        if (view != null)
        {
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
                {
                    int radioButtonId = group.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) view.findViewById(radioButtonId);
                    if (radioButton.getText().equals("All Toilets"))
                    {
                        typeView.setVisibility(View.GONE);
                        status = "All Toilets";
                    }
                    else
                    {
                        radioAll.setChecked(false);
                        typeView.setVisibility(View.VISIBLE);
                        status = "Select Toilets";
                    }
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    selectedTypesCheckBoxList = new ArrayList<>();
                    toiletResultList = new ArrayList<>();

                    if (status.equals("All Toilets"))
                    {
                        toiletResultList = new ArrayList<>(toiletPlaceArrayList);
                    }
                    else
                    {
                        for(CheckBox checkBox : allTypesCheckBoxList)
                        {
                            if (checkBox.isChecked())
                            {
                                selectedTypesCheckBoxList.add(checkBox);
                            }
                        }

                        toiletResultList = new ArrayList<>(toiletPlaceArrayList);
                        for (CheckBox cb : selectedTypesCheckBoxList)
                        {
                            for(int index = 0; index < toiletResultList.size();)
                            {
                                if(getThatTypeStatus(cb.getText().toString(),toiletResultList.get(index)).equals("no"))
                                    toiletResultList.remove(index);
                                else
                                    index++;
                            }
                        }
                    }

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ToiletCluster cluster = new ToiletCluster();
                    cluster.transmitToToiletClusterFragment(toiletResultList);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,cluster);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });

        }
    }

    public String getThatTypeStatus(String text, ToiletPlace toiletPlace) {
        String methodName = "";
        if (text.equals("Baby Facility"))
        {
            methodName = "getBaby_facil";
        }
        else
        {
            methodName = "get" + text;
        }
        String thatTypeStatus = "";
        try
        {
            Method method = toiletPlace.getClass().getMethod(methodName);
            try
            {
                thatTypeStatus = (String)method.invoke(toiletPlace);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return thatTypeStatus;
    }
}
