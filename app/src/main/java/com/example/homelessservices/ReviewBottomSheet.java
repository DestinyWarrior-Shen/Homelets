package com.example.homelessservices;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReviewBottomSheet extends Fragment
{
    private ListView listView;
    private TextView averageMark,numReview;
    private RatingBar indicatorRB;
    private ImageView headIcon;

    private ArrayList<String> commentList = new ArrayList<>(),correctOrderCommentList;
    private String average_mark;
    private String numOfReviews;
    private ReviewAdapter reviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_bottom_sheet, container, false);
        registerUI(view);

        reviewAdapter = new ReviewAdapter();
        listView.setAdapter(reviewAdapter);

        averageMark.setText(average_mark);
        indicatorRB.setRating(Float.parseFloat(average_mark));
        numReview.setText(numOfReviews);
        return view;
    }

    public void registerUI(View view)
    {
        listView = (ListView) view.findViewById(R.id.review_list);
        averageMark = (TextView) view.findViewById(R.id.average_mark);
        numReview = (TextView) view.findViewById(R.id.num_reviews);
        indicatorRB = (RatingBar) view.findViewById(R.id.average_ratingBar);
    }


    public void sendDataToBottomSheetFragment(ArrayList<String> foodPlaces, String average_mark,String numOfReviews) {
        for (int i = foodPlaces.size()-1; i >= 0; i--)
            commentList.add(foodPlaces.get(i));

        this.average_mark = average_mark;
        this.numOfReviews = numOfReviews;
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return commentList.size();
        }

        @Override
        public String getItem(int position)
        {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        private class ViewHolder
        {
            ImageView head_image;
            TextView user_name;
            RatingBar mark;
            TextView time;
            TextView comment;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = View.inflate(getContext(),R.layout.review_list_item, null);
                holder.head_image = (ImageView) convertView.findViewById(R.id.head_portrait);
                holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                holder.mark = (RatingBar) convertView.findViewById(R.id.user_rate_mark);
                holder.time = (TextView) convertView.findViewById(R.id.review_time);
                holder.comment = (TextView) convertView.findViewById(R.id.review_content);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            String[] component = commentList.get(position).split("\\^");

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null)
            {
                String email = mAuth.getCurrentUser().getEmail();
                String displayName = ReadDataFromFireBase.readUserName();
                if (component[1].equals(email)) {
                    if (!displayName.isEmpty())
                        holder.user_name.setText(displayName);
                    else
                        holder.user_name.setText(component[1]);
                    ReadDataFromFireBase.downloadHeadIconFromFireBase(holder.head_image);
                }
                else {
                    //holder.head_image.setImageResource(R.drawable.avatar);
                    holder.user_name.setText(component[1]);
                }
            }
            else {
                //holder.head_image.setImageResource(R.drawable.avatar);
                holder.user_name.setText(component[1]);
            }
            holder.mark.setRating(Float.parseFloat(component[2]));
            holder.comment.setText(component[3]);
            try
            {
                setReviewForEach(component[0],holder.time);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            return convertView;
        }

        private void setReviewForEach(String dateString,TextView tv_time) throws ParseException {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            int numberOfDays = differentDaysByMillisecond(simpleDateFormat.parse(dateString),date);
            if (numberOfDays < 1)
            {
                tv_time.setText("less than a day");
            }
            else if (numberOfDays <=7)
            {
                tv_time.setText(numberOfDays + " days ago");
            }
            else if (numberOfDays > 7 && numberOfDays <= 30)
            {
                int weekNumber = numberOfDays/7;
                String weekNo = String.valueOf(weekNumber);
                tv_time.setText(weekNo.charAt(0)+" weeks ago");
            }
            else if (numberOfDays > 30)
            {
                int monthNumber = numberOfDays/30;
                String monthNo = String.valueOf(monthNumber);
                if (monthNumber < 10)
                {
                    tv_time.setText(monthNo.charAt(0)+ " months ago");
                }
                else
                {
                    tv_time.setText(monthNo.charAt(0) + monthNo.charAt(1) + " months ago");
                }
            }
        }

        private int differentDaysByMillisecond(Date date1,Date date2) {
            int days = (int)((date2.getTime() - date1.getTime()) / (1000*3600*24));
            return days;
        }
    }
}
