package com.example.homelessservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

/**
 * Created by Eric on 17/8/17.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener
{
    private TextView userName;
    private ImageView photo;
    private MenuItem menuItem;
    private Fragment profile;
    private MenuItem profile_menu;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private String email,currentUserID;
    private ProgressDialog progressDialog;
    private DrawerLayout drawer;

    private ArrayList<Service> serviceList,locationServiceList,virtualServiceList;
    private DatabaseReference foodPlaceRef,locationServiceRef,virtualServiceRef;

    private ArrayList<FoodPlace> foodPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceList = new ArrayList<>();
        locationServiceList = new ArrayList<>();
        virtualServiceList = new ArrayList<>();
        foodPlaceRef = FirebaseDatabase.getInstance().getReference().child("FoodPlaces");
        locationServiceRef = FirebaseDatabase.getInstance().getReference().child("LocationServices");
        virtualServiceRef = FirebaseDatabase.getInstance().getReference().child("VirtualServices");

        if (getSelectedTheme() == 0)
        {
            storePreference("theme", 2131361961);
            storePreference("color", -13285544);
        }
        setTheme(getSelectedTheme());//Using preferred theme
        setSelectedFontTheme();//Using preferred text size

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getSelectedColor());

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View view = navigationView.getHeaderView(0);

        LinearLayout header = (LinearLayout)view.findViewById(R.id.head_id);
        header.setBackgroundColor(getSelectedColor());

        progressDialog = new ProgressDialog(this);

        photo = (ImageView) view.findViewById(R.id.imageView);
        photo.setOnClickListener(this);

        userName = (TextView) view.findViewById(R.id.tv_user_name);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Menu menu = navigationView.getMenu();
        menuItem = (MenuItem) menu.findItem(R.id.nav_account);
        profile_menu = (MenuItem) menu.findItem(R.id.nav_profile);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            currentUserID = mAuth.getCurrentUser().getUid();
            email = mAuth.getCurrentUser().getEmail();
            menuItem.setTitle("Logout");
            userName.setText(mAuth.getCurrentUser().getEmail());
            profile_menu.setVisible(true);
            ReadDataFromFireBase.downloadHeadIconFromFireBase(progressDialog,photo,userName);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        Home home = new Home();
        ft.replace(R.id.fragment_content,home);
        ft.addToBackStack(null);
        ft.commit();

//        getFoodPlaceFromAPI();
//        getServicePlaceFromAPI();
        ReadDataFromFireBase.readFoodData();
        ReadDataFromFireBase.readLocationServiceData();
        ReadDataFromFireBase.readVirtualServiceData();
        ReadDataFromFireBase.readToiletData();
        ReadDataFromFireBase.readOneUserFromFireBase();
        ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        ReadDataFromFireBase.readUserName();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void settingDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            drawer.openDrawer(GravityCompat.START);
    }

    public View getNavigationView() {
        return navigationView;
    }

    /**
     * invoked when items in navigation drawer is clicked
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_home)
        {
            Home home = new Home();
            ft.replace(R.id.fragment_content,home);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_twenty_rank)
        {
            Rank rank = new Rank();
            ft.replace(R.id.fragment_content,rank);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_find_places)
        {
            Statement statement = new Statement();
            ft.replace(R.id.fragment_content,statement);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_find_toilet)
        {
//            Filter_Toilet filterToilet = new Filter_Toilet();
//            ft.replace(R.id.fragment_content,filterToilet);
//            ft.addToBackStack(null);
//            ft.commit();
            ToiletCluster toiletCluster= new ToiletCluster();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.fragment_content,toiletCluster);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_favourite)
        {
            if (mAuth.getCurrentUser() != null)
            {
                Favourite favourite = new Favourite();
                ft.replace(R.id.fragment_content,favourite);
                ft.addToBackStack(null);
                ft.commit();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
                builder.setMessage("This function can only be used after you login!");
                builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Login login = new Login();
                        ft.replace(R.id.fragment_content,login);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
                builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }

        }
        else if (id == R.id.nav_schedule)
        {
            FoodRecommendation foodRecommendation = new FoodRecommendation();
            ft.replace(R.id.fragment_content,foodRecommendation);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_other_service)
        {
            ServiceCategory serviceCategory = new ServiceCategory();
            ft.replace(R.id.fragment_content,serviceCategory);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_setting)
        {
            Setting setting = new Setting();
            ft.replace(R.id.fragment_content,setting);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_about)
        {
            About about = new About();
            ft.replace(R.id.fragment_content,about);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (id == R.id.nav_account)
        {
            if (item.getTitle().toString().equals("Login"))
            {
                Login login = new Login();
                ft.replace(R.id.fragment_content,login);
                ft.addToBackStack(null);
                ft.commit();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
                builder.setTitle("Sign Out?");
                builder.setMessage("Are you sure you wish to logout?");
                builder.setPositiveButton("Logout",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                menuItem.setTitle("Login");
                                userName.setText("");
                                profile_menu.setVisible(false);
                                photo.setImageResource(R.drawable.avatar);
                                Toast.makeText(getBaseContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                                refreshActivity();
                            }
                        });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }

        }
        else if (id == R.id.nav_profile)
        {
            if (mAuth.getCurrentUser() != null)
            {
                if (profile == null)
                {
                    profile = new Profile();
                }
                ft.replace(R.id.fragment_content,profile);
                ft.addToBackStack(null);
                ft.commit();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
                builder.setMessage("This function can only be used after you login!");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }
//        else if (id == R.id.nav_add)
//        {
//            //writeServiceToFireBase();
//            //writeFoodPlaceToFireBase();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void refreshActivity() {
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
    }


    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the preferred theme from preference file
     * @return the selected theme
     */
    private int getSelectedTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int selectedTheme = sharedPreferences.getInt("theme", 0);
        return selectedTheme;
    }

    /**
     * Get the preferred color from preference file
     * @return the selected color
     */
    private int getSelectedColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int selectedColor = sharedPreferences.getInt("color", R.color.colorPrimaryDark);
        return selectedColor;
    }

    /**
     * Set font theme using preferred size
     */
    private void setSelectedFontTheme(){
        switch ("FontSize" + getSelectedFontSize()){
            case "FontSize10":
                setTheme(R.style.FontSize10);
                break;
            case "FontSize14":
                setTheme(R.style.FontSize14);
                break;
            case "FontSize18":
                setTheme(R.style.FontSize18);
                break;
            case "FontSize22":
                setTheme(R.style.FontSize22);
                break;
            case "FontSize26":
                setTheme(R.style.FontSize26);
                break;
        }
    }

    /**
     * Get the selected text size from the preference file
     */
    private int getSelectedFontSize(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int size = sharedPreferences.getInt("font_size", 20);
        return size;
    }

    private void storePreference(String key, int item){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,item);
        editor.apply();
    }


    /////////////////////////////////////////////////////////////////////////////



    public void setPhoto(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
    }

    public void setUserName(String name) {
        userName.setText(name);
    }




    public void writeServiceToFireBase() {
        for (Service service : locationServiceList)
        {
            String[] longitudeArray = service.getLongitude().split("\\.");
            DatabaseReference eachFoodPlacePoint = locationServiceRef.child(longitudeArray[0] + longitudeArray[1]);
            eachFoodPlacePoint.setValue(service);
        }
        for (Service service : virtualServiceList)
        {
            DatabaseReference eachFoodPlacePoint = virtualServiceRef.child(service.getName());
            eachFoodPlacePoint.setValue(service);
        }
    }

    public void writeFoodPlaceToFireBase() {
        for (FoodPlace foodPlace : foodPlaceList)
        {
            String[] longitudeArray = foodPlace.getLongitude().split("\\.");
            DatabaseReference eachFoodPlacePoint = foodPlaceRef.child(longitudeArray[0] + longitudeArray[1]);
            eachFoodPlacePoint.setValue(foodPlace);
        }
    }

    public void getServicePlaceFromAPI() {
        new AsyncTask<Void, Void, ArrayList<Service>>() {
            @Override
            protected ArrayList<Service> doInBackground(Void... params)
            {
                return RestClient_Service.collectService();
            }
            @Override
            protected void onPostExecute(ArrayList<Service> tmpFoodPlaceList)
            {
                serviceList = tmpFoodPlaceList;
                divideBaseOnLocation();
            }
        }.execute();
    }

    public void getFoodPlaceFromAPI() {
        new AsyncTask<Void, Void, ArrayList<FoodPlace>>() {
            @Override
            protected ArrayList<FoodPlace> doInBackground(Void... params)
            {
                return RestClient.collectFoodPlace();
            }
            @Override
            protected void onPostExecute(ArrayList<FoodPlace> tmpFoodPlaceList)
            {
                foodPlaceList = tmpFoodPlaceList;
            }
        }.execute();
    }

    private void divideBaseOnLocation() {
        for (Service service : serviceList)
        {
            if (service.getLongitude().equals(""))
            {
                if (service.getCategory_1().equals("Helpful phone number") ||
                        service.getCategory_1().equals("Helpful website"))
                {
                    virtualServiceList.add(service);
                }
            }
            else
            {
                locationServiceList.add(service);
            }
        }
    }








    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.imageView:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (mAuth.getCurrentUser() != null)
                {
                    Profile profile1 = new Profile();
                    ft.replace(R.id.fragment_content,profile1);
                }
                else
                {
                    Login login = new Login();
                    ft.replace(R.id.fragment_content,login);
                }
                ft.addToBackStack(null);
                ft.commit();
                settingDrawer();
                break;
        }
    }
}
