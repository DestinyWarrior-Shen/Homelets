package com.example.homelessservices;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Rank extends Fragment
{
    private ListView listview;
    private RankAdapter rankAdapter;
    private ArrayList<FoodPlace> favouritePlaces,foodPlaceArrayList,newFoodPlaceArrayList;
    private ArrayList<String> userCommentList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        getActivity().setTitle("Popular Food Places");
        listview = (ListView) view.findViewById(R.id.list_view);
        newFoodPlaceArrayList = new ArrayList<>();

        favouritePlaces = ReadDataFromFireBase.readOneUserFromFireBase();
        foodPlaceArrayList = ReadDataFromFireBase.readFoodData();
        userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();

        Collections.sort(foodPlaceArrayList, new Comparator<FoodPlace>() {
            @Override
            public int compare(FoodPlace o1, FoodPlace o2) {
                //return 0;
                return o2.getAddTimes().compareTo(o1.getAddTimes());
            }
        });

        for (int i=0; i<20;i++)
        {
            newFoodPlaceArrayList.add(foodPlaceArrayList.get(i));
        }

        rankAdapter = new RankAdapter(getContext(),newFoodPlaceArrayList);
        listview.setAdapter(rankAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                {
                    FoodPlace foodPlace = rankAdapter.getItem(position);

                    PlaceDetails placeDetails = new PlaceDetails();
                    placeDetails.transmitPlaceObjectToPlaceDetailFragment(foodPlace,favouritePlaces,userCommentList);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,placeDetails);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }
}
