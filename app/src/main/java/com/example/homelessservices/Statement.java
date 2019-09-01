package com.example.homelessservices;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Statement extends Fragment
{
    private Button btn_ok;
    private ArrayList<FoodPlace> todayFoodPlaces;
    private DatabaseReference foodReference;
    private ValueEventListener foodReferenceListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_statements, container, false);
        getActivity().setTitle("Find Food Services");
        foodReference = FirebaseDatabase.getInstance().getReference().child("FoodPlaces");
        todayFoodPlaces = new ArrayList<>();
        readFoodData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            btn_ok = (Button) view.findViewById(R.id.btn_statement_ok);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ListPlace listPlace = new ListPlace();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    listPlace.setListToListPlaceFragment(todayFoodPlaces,true);
                    ft.replace(R.id.fragment_content,listPlace);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }

    public void readFoodData(){
        if (foodReference != null)
        {
            foodReferenceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    todayFoodPlaces.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        FoodPlace fp = snapshot.getValue(FoodPlace.class);
                        todayFoodPlaces.add(fp);
                    }
                    getFoodPlacesToday();
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            };
            foodReference.addListenerForSingleValueEvent(foodReferenceListener);
        }
    }

    /**
     * Get all open food places opened on today
     * @return the list of food places opened on today
     */
    private void getFoodPlacesToday(){
        //foodPlaces = ReadDataFromFireBase.readFoodData();
        Calendar calendar = Calendar.getInstance();
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

        for(int index = 0; index < todayFoodPlaces.size();)
        {
            if(getThatDayStatus(today,todayFoodPlaces.get(index)).equals("Closed") || getThatDayStatus(today,todayFoodPlaces.get(index)).equals("N/A"))
                todayFoodPlaces.remove(index);
            else
                index++;
        }
    }

    private String getThatDayStatus(String text, FoodPlace foodPlace) {
        String methodName = "get" + text;
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

    @Override
    public void onStop() {
        super.onStop();
        if (foodReference != null)
        {
            foodReference.removeEventListener(foodReferenceListener);
        }
    }
}
