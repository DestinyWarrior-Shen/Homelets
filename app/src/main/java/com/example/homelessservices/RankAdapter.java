package com.example.homelessservices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eric on 3/9/17.
 */

public class RankAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<FoodPlace> foodPlaceArrayList;

    public RankAdapter(Context context, ArrayList<FoodPlace> foodPlaceArrayList)
    {
        this.context = context;
        this.foodPlaceArrayList = foodPlaceArrayList;
    }

    @Override
    public int getCount()
    {
        return foodPlaceArrayList.size();
    }

    @Override
    public FoodPlace getItem(int position)
    {
        return foodPlaceArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolder
    {
        TextView place_name;
        TextView place_suburb;
        TextView place_person;
        TextView place_cost;
        TextView addTimes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = new ViewHolder();
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rank_list_item, null);
            //convertView = View.inflate(getContext(),R.layout.list_item,null);
            holder.place_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.place_suburb = (TextView) convertView.findViewById(R.id.tv_suburb);
            holder.place_person = (TextView) convertView.findViewById(R.id.tv_who);
            holder.place_cost = (TextView) convertView.findViewById(R.id.tv_fee);
            holder.addTimes = (TextView) convertView.findViewById(R.id.add_number);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.place_name.setText(foodPlaceArrayList.get(position).getName());
        holder.place_suburb.setText(foodPlaceArrayList.get(position).getSuburb());
        if (foodPlaceArrayList.get(position).getWho().length() >= 40)
        {
            holder.place_person.setText(foodPlaceArrayList.get(position).getWho().substring(0,35)+"....");
        }
        else
        {
            holder.place_person.setText(foodPlaceArrayList.get(position).getWho());
        }

        if (foodPlaceArrayList.get(position).getCost().length() >= 40)
        {
            holder.place_cost.setText(foodPlaceArrayList.get(position).getCost().substring(0,35)+"....");
        }
        else
        {
            holder.place_cost.setText(foodPlaceArrayList.get(position).getCost());
        }

        holder.addTimes.setText(""+foodPlaceArrayList.get(position).getAddTimes());

        return convertView;
    }

}
