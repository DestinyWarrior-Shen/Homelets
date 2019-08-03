package com.example.homelessservices;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Fancy on 2017/09/19.
 */

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private HashMap<String,ArrayList<String>> categoryMap;
    private Spinner categorySpinner,subCategorySpinner;
    private CheckBox mon,tue,wed,thu,fri,sat,sun,ph;
    private Button apply;
    private BottomSheetBehavior behavior;
    private ArrayList<CheckBox> allDaysCheckBoxList;
    private ArrayList<String> categories,selectCheckBoxNameList;
    private ArrayList<FoodPlace> foodPlaces;
    private String categorySelected, subCategorySelected;
    private View subCategoryView;
    private int today = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(),R.layout.fragment_filter_bottom_sheet, null);
        registerUI(view);

        foodPlaces = ReadDataFromFireBase.readFoodData();
        categories = ReadDataFromFireBase.findAllCategories(foodPlaces);
        initialSpinner(categorySpinner,categories);

        //subCategoryView.setVisibility(View.GONE);
        categoryMap = ReadDataFromFireBase.findAllCateAndSubCate(foodPlaces);

        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        allDaysCheckBoxList.get(today).setText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()) + "*");

        if (categorySelected != null && subCategorySelected != null && selectCheckBoxNameList != null)
        {
            for (int i=0;i<allDaysCheckBoxList.size();i++)
            {
                if (selectCheckBoxNameList.contains(allDaysCheckBoxList.get(i).getText().toString()))
                    allDaysCheckBoxList.get(i).setChecked(true);
                else if (selectCheckBoxNameList.contains(allDaysCheckBoxList.get(i).getText().toString() + "*"))
                {
                    allDaysCheckBoxList.get(i).setChecked(true);
                    String name = allDaysCheckBoxList.get(i).getText().toString();
                    allDaysCheckBoxList.get(i).setText(name +"*");
                }
            }
            categorySpinner.setSelection(categories.indexOf(categorySelected),true);
            if (categorySelected.equals("All"))
                subCategoryView.setVisibility(View.GONE);
            initialSpinner(subCategorySpinner,categoryMap.get(categorySelected));
            subCategorySpinner.setSelection(categoryMap.get(categorySelected).indexOf(subCategorySelected),true);
        }
        else
        {
            initialSpinner(subCategorySpinner,categoryMap.get("All"));
            allDaysCheckBoxList.get(today).setChecked(true);
        }

        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(2500);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return dialog;
    }

    public void registerUI(View view) {
        categorySpinner = (Spinner) view.findViewById(R.id.spinner_category);
        subCategorySpinner = (Spinner) view.findViewById(R.id.spinner_sub);
        apply = (Button) view.findViewById(R.id.btn_ok);
        subCategoryView = view.findViewById(R.id.select_sub_category);
        allDaysCheckBoxList = new ArrayList<>();

        sun = (CheckBox) view.findViewById(R.id.cb_sun);
        allDaysCheckBoxList.add(sun);
        mon = (CheckBox) view.findViewById(R.id.cb_mon);
        allDaysCheckBoxList.add(mon);
        tue = (CheckBox) view.findViewById(R.id.cb_tue);
        allDaysCheckBoxList.add(tue);
        wed = (CheckBox) view.findViewById(R.id.cb_wed);
        allDaysCheckBoxList.add(wed);
        thu = (CheckBox) view.findViewById(R.id.cb_thu);
        allDaysCheckBoxList.add(thu);
        fri = (CheckBox) view.findViewById(R.id.cb_fri);
        allDaysCheckBoxList.add(fri);
        sat = (CheckBox) view.findViewById(R.id.cb_sat);
        allDaysCheckBoxList.add(sat);
        ph = (CheckBox) view.findViewById(R.id.cb_ph);
        allDaysCheckBoxList.add(ph);
    }



    public void sendMessageToDialog(ArrayList<String> selectCheckBoxNameList,String categorySelected,String subCategorySelected) {
        this.selectCheckBoxNameList = selectCheckBoxNameList;
        this.categorySelected = categorySelected;
        this.subCategorySelected = subCategorySelected;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * category spinner lister
         */
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                categorySelected = parent.getItemAtPosition(position).toString();

                if (!(categorySelected.equals("All")))
                {
                    subCategoryView.setVisibility(View.VISIBLE);
                }
                else
                {
                    subCategoryView.setVisibility(View.GONE);
                }
                ArrayAdapter<String> arrayAdapterSub = new ArrayAdapter<String>(getActivity(),R.layout.spinner_checked_text,categoryMap.get(categorySelected))
                {
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
                    {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.spinner_item_layout,null);
                        settingSpinnerView(position,view,categoryMap.get(categorySelected),subCategorySpinner);
                        return view;
                    }
                };
                arrayAdapterSub.setDropDownViewResource(R.layout.spinner_item_layout);
                subCategorySpinner.setAdapter(arrayAdapterSub);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        /**
         * Sub-Category Spinner Listener
         */
        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                subCategorySelected = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        /**
         * Apply Button Listener
         */
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<FoodPlace> resultList = getFoodPlacesBasedOnDays(getFoodPlacesBasedOnCate());

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ListPlace listPlace = new ListPlace();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                listPlace.setListToListPlaceFragment(resultList,false);
                listPlace.sendFilterCondition(selectCheckBoxNameList,categorySelected,subCategorySelected);
                ft.replace(R.id.fragment_content,listPlace);
                ft.addToBackStack(null);
                ft.commit();
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void settingSpinnerView(int position, View view, ArrayList<String> list, Spinner spinner) {
        TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
        ImageView check = (ImageView) view.findViewById(R.id.spinner_item_checked_image);
        label.setText(list.get(position));
        if (spinner.getSelectedItemPosition() == position)
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorSelect));
            check.setImageResource(R.drawable.checked);
            label.setTextColor(Color.WHITE);
        }
        else
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorSpinner));
            check.setImageResource(R.drawable.unchecked);
        }
    }

    private void initialSpinner(final Spinner spinner, final ArrayList<String> strings) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, strings);
//        spinner.setAdapter(adapter);

        ArrayAdapter<String> arrayAdapterCate = new ArrayAdapter<String>(getActivity(),R.layout.spinner_checked_text,strings)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.spinner_item_layout,null);
                settingSpinnerView(position,view,strings,spinner);
                return view;
                //super.getDropDownView(position, convertView, parent);
            }
        };
        arrayAdapterCate.setDropDownViewResource(R.layout.spinner_item_layout);
        spinner.setAdapter(arrayAdapterCate);
    }

    /**
     * Get all open food places of a specific category on specific days
     * @param resultListByCate the list of a specific food category
     * @return the list of a specific food category on specific days
     */
    private ArrayList<FoodPlace> getFoodPlacesBasedOnDays(ArrayList<FoodPlace> resultListByCate) {

        ArrayList<FoodPlace> resultList = new ArrayList<>();
        selectCheckBoxNameList = new ArrayList<>();
        for(CheckBox checkBox : allDaysCheckBoxList)
        {
            if (checkBox.isChecked())
            {
                selectCheckBoxNameList.add(checkBox.getText().toString());
                for(FoodPlace foodPlace: resultListByCate)
                {
                    if ((!getThatDayStatus(checkBox.getText().toString(),foodPlace).equals("Closed") &&
                            !getThatDayStatus(checkBox.getText().toString(),foodPlace).equals("N/A")) &&
                            !resultList.contains(foodPlace))
                        resultList.add(foodPlace);
                }
            }
        }
        if (selectCheckBoxNameList.size() == 0)
            resultList = new ArrayList<>(resultListByCate);
        return resultList;
    }

    /**
     * Get a list of a specific food category
     * @return the list of a specific food category
     */
    private ArrayList<FoodPlace> getFoodPlacesBasedOnCate() {
        ArrayList<FoodPlace> resultList = new ArrayList<>();
        if (categorySelected.equals("All"))
            resultList = foodPlaces;
        else
            for (FoodPlace foodPlace: foodPlaces)
            {
                if (foodPlace.getCategory().equals(categorySelected) && foodPlace.getSub_category().equals(subCategorySelected))
                {
                    resultList.add(foodPlace);
                }
            }
        return resultList;
    }

    private String getThatDayStatus(String text, FoodPlace foodPlace) {

        String methodName = "";

        if (text.contains(" (Today)"))
        {
            methodName = "get" + text.substring(0,text.lastIndexOf(" "));
        }
        else
        {
            if (text.equals("Public Holiday"))
                methodName = "getPublic_holidays";
            else
                methodName = "get" + text;
        }
        String thatDayStatus = "";
        try
        {
            Method method = foodPlace.getClass().getMethod(methodName);
            try
            {
                thatDayStatus = (String)method.invoke(foodPlace);
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
        return thatDayStatus;
    }
}
