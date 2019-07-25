package com.example.homelessservices;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.wooplr.spotlight.SpotlightConfig;
//import com.wooplr.spotlight.SpotlightView;

import java.util.ArrayList;
import java.util.List;

/**
 * Display the greeting message when first turn on the application
 */
public class Home extends Fragment implements View.OnClickListener
{
    private ViewPager mViewPager;
    private List<ImageView> images;
    //private List<Fragment> fragmentView;
    private List<View> dots;
    private int[] imageIds = new int[]{R.drawable.wel1, R.drawable.wel2, R.drawable.wel3, R.drawable.wel4};

    //private TextView title;
    private ViewPagerAdapter adapter;
    //private FragmentPagerAdapter adapter;
    //private ScheduledExecutorService scheduledExecutorService;
    //private CardView startUsing,login;
    private TextView findFood,findToilet, recommend, findService;
    //private SpotlightConfig config;
    //private int mPosition;


    /**
     * Create the view of the home fragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, null);

        getActivity().setTitle("Home Page");

        findFood = (TextView) view.findViewById(R.id.food_bottom);
        setIcon(findFood, R.drawable.food);

        findToilet = (TextView) view.findViewById(R.id.toilet_bottom);
        setIcon(findToilet, R.drawable.toilet);

        recommend = (TextView) view.findViewById(R.id.rf_bottom);
        setIcon(recommend, R.drawable.schedule);

        findService = (TextView) view.findViewById(R.id.service_bottom);
        setIcon(findService, R.drawable.trust);

        mViewPager = (ViewPager) view.findViewById(R.id.vp);

        images = new ArrayList<>();
        for(int i = 0; i < imageIds.length; i++)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }
//        fragmentView = new ArrayList<>();
//        LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view1 = inflater1.inflate(R.layout.fragment_home_welcome, container, false);
//        View view2 = inflater1.inflate(R.layout.fragment_home_food, container, false);
//        View view3 = inflater1.inflate(R.layout.fragment_home_toilet, container, false);

//        fragmentView.add(view1);
//        fragmentView.add(view2);
//        fragmentView.add(view3);


//        Home_welcome home_welcome = new Home_welcome();
//        Home_food home_food = new Home_food();
//        Home_toilet home_toilet = new Home_toilet();
//        fragmentView = new ArrayList<>();
//        fragmentView.add(home_welcome);
//        fragmentView.add(home_food);
//        fragmentView.add(home_toilet);
        dots = new ArrayList<>();
        dots.add(view.findViewById(R.id.dot_0));
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));
        dots.add(view.findViewById(R.id.dot_3));

        dots.get(0).setBackgroundResource(R.drawable.dot_focused);
        dots.get(1).setBackgroundResource(R.drawable.dot_normal);
        dots.get(2).setBackgroundResource(R.drawable.dot_normal);
        dots.get(3).setBackgroundResource(R.drawable.dot_normal);


        adapter = new ViewPagerAdapter();
//        adapter = new FragmentPagerAdapter(getFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return fragmentView.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return fragmentView.size();
//            }
//
//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//                return super.instantiateItem(container, position);
//            }
//        };
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position)
            {
                for (int i = 0; i < dots.size(); i++){
                    dots.get(i).setBackgroundResource(R.drawable.dot_normal);
                }
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findFood.setOnClickListener(this);
        findToilet.setOnClickListener(this);
        findService.setOnClickListener(this);
        recommend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (v.getId())
        {
            case R.id.food_bottom:
                Statement statement = new Statement();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.fragment_content,statement);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.toilet_bottom:
                ToiletCluster toiletCluster= new ToiletCluster();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragment_content,toiletCluster);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.service_bottom:
                ServiceCategory serviceCategory = new ServiceCategory();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragment_content,serviceCategory);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.rf_bottom:
                FoodRecommendation foodRecommendation = new FoodRecommendation();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragment_content,foodRecommendation);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
//			super.destroyItem(container, position, object);
//			view.removeView(view.getChildAt(position));
//			view.removeViewAt(position);
            view.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)  {
            View view = images.get(position);
            container.addView(images.get(position));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (position == 0)
                    {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.settingDrawer();
//                        new SpotlightView.Builder(getActivity())
//                                .introAnimationDuration(400)
//                                .enableRevealAnimation(true)
//                                .performClick(true)
//                                .fadeinTextDuration(400)
//                                .headingTvColor(Color.parseColor("#eb273f"))
//                                .headingTvSize(32)
//                                .headingTvText("Core functions")
//                                .subHeadingTvColor(Color.parseColor("#ffffff"))
//                                .subHeadingTvSize(16)
//                                .subHeadingTvText("Helpful information\n Very considerate.")
//                                .maskColor(Color.parseColor("#dc000000"))
//                                .target(activity.getNavigationView())
//                                .lineAnimDuration(400)
//                                .lineAndArcColor(Color.parseColor("#eb273f"))
//                                .dismissOnTouch(true)
//                                .dismissOnBackPress(true)
//                                .enableDismissAfterShown(true)
//                                .usageId("a") //UNIQUE ID
//                                .show();
                    }
                    else if (position == 1)
                    {
                        Statement statement = new Statement();
                        ft.replace(R.id.fragment_content,statement);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    else if (position == 2)
                    {
                        ToiletCluster toiletCluster= new ToiletCluster();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.fragment_content,toiletCluster);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    else if (position == 3)
                    {
                        ServiceCategory serviceCategory = new ServiceCategory();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.fragment_content,serviceCategory);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });
            return images.get(position);
        }
    }

    private void setIcon(TextView textView, int url){
        Drawable drawable = getResources().getDrawable(url);
        drawable.setBounds(0,30,110,110);
        textView.setCompoundDrawables(null,drawable,null,null);
        textView.setGravity(Gravity.CENTER);
    }
//    /**
//     * Using thread pool to implement view rotate with specific time period
//     */
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleWithFixedDelay(new ViewPageTask(), 10, 10, TimeUnit.SECONDS);
//    }
//
//    private class ViewPageTask implements Runnable
//    {
//        @Override
//        public void run()
//        {
//            currentItem = (currentItem + 1) % imageIds.length;
//            mHandler.sendEmptyMessage(0);
//        }
//    }
//
//    /**
//     * get data from sub-thread
//     */
//    private Handler mHandler = new Handler()
//    {
//        public void handleMessage(android.os.Message msg)
//        {
//            mViewPager.setCurrentItem(currentItem);
//        };
//    };
//
//    @Override
//    public void onStop() {
//        super.onStop();
//    }

}
