package com.example.homelessservices;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Profile_review extends Fragment implements AdapterView.OnItemLongClickListener {

    private ArrayList<String> userCommentList, onePlaceCommentList;
    private ListView listView;
    private ReviewAdapter adapter;
    private DatabaseReference mDatabaseRootRef,userCommentRef,foodPlaceRef,foodPlaceCommentRef;
    private FirebaseAuth mAuth;
    private ValueEventListener foodPlaceListListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_review, container, false);
        userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        onePlaceCommentList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRootRef = FirebaseDatabase.getInstance().getReference();
        listView = (ListView) view.findViewById(R.id.review_listView);
        adapter = new ReviewAdapter();
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            listView.setOnItemLongClickListener(this);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete comment?");
        builder.setMessage("Are you sure you want to delete this comment?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                deleteCommentFromFireBase(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Person has been deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
        return false;
    }

    private void deleteCommentFromFireBase(int position) {

        String[] userCommentOneRecordArray = userCommentList.get(position).split("\\^");
        String dateAndTime = userCommentOneRecordArray[0];
        String[] findPlaceLatitudeArray = userCommentOneRecordArray[1].split("-");
        String[] findPlaceKeyIndexArray = findPlaceLatitudeArray[0].split("\\.");
        final String placeKeyIndex = findPlaceKeyIndexArray[0] + findPlaceKeyIndexArray[1];
        foodPlaceRef = mDatabaseRootRef.child("FoodPlaces");
        if (foodPlaceRef != null)
        {
            foodPlaceListListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        if (snapshot.getKey().equals(placeKeyIndex))
                        {
                            onePlaceCommentList.clear();
                            FoodPlace fp = snapshot.getValue(FoodPlace.class);
                            onePlaceCommentList = fp.getReviewList();
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        foodPlaceRef.addListenerForSingleValueEvent(foodPlaceListListener);

        for (String onePlaceComment : onePlaceCommentList)
        {
            String[] array = onePlaceComment.split("\\^");
            if (array[0].equals(dateAndTime))
            {
                onePlaceCommentList.remove(onePlaceComment);
            }
        }
        foodPlaceCommentRef = foodPlaceRef.child(placeKeyIndex).child("reviewList");
        foodPlaceCommentRef.setValue(onePlaceCommentList);

        foodPlaceRef.removeEventListener(foodPlaceListListener);

        userCommentList.remove(position);

        userCommentRef = mDatabaseRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("commentList");
        userCommentRef.setValue(userCommentList);
    }

    private class ReviewAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return userCommentList.size();
        }

        @Override
        public String getItem(int position)
        {
            return userCommentList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        private class ViewHolder
        {
            TextView DateTime;
            TextView place_name;
            RatingBar ratingBar;
            TextView mark;
            TextView comment;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                convertView = View.inflate(getContext(),R.layout.personal_reviews_list_item, null);
                holder.DateTime = (TextView) convertView.findViewById(R.id.date_time);
                holder.place_name = (TextView) convertView.findViewById(R.id.place_name);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                holder.mark = (TextView) convertView.findViewById(R.id.mark);
                holder.comment = (TextView) convertView.findViewById(R.id.comment);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            String[] array = userCommentList.get(position).split("\\^");

            String date = array[0].substring(0,10);
            String time = array[0].substring(11).replace("-",":");
            holder.DateTime.setText(date + " "+ time);
            String[] array1 = array[1].split("-");
            holder.place_name.setText(array1[1]);
            holder.ratingBar.setRating(Float.parseFloat(array[2]));
            holder.mark.setText(array[2]);
            holder.comment.setText(array[3]);

            return convertView;
        }
    }
}
