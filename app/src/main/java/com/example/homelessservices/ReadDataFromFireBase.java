package com.example.homelessservices;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sean on 9/15/2017.
 */

public class ReadDataFromFireBase {

    private static DatabaseReference mDatabaseRootRef = FirebaseDatabase.getInstance().getReference();
    private static ValueEventListener foodReferenceListener, toiletReferenceListener, userReferenceListener,
                                        locationServiceListener,virtualServiceListener;
    private static ArrayList<ToiletPlace> toiletList = new ArrayList<>();
    private static ArrayList<FoodPlace> foodPlacesList = new ArrayList<>();
    private static ArrayList<FoodPlace> favouriteList = new ArrayList<>();
    private static ArrayList<String> commentsList = new ArrayList<>();
    private static ArrayList<Service> locationServiceList = new ArrayList<>();
    private static ArrayList<Service> virtualServiceList = new ArrayList<>();
    private static String user_name = "";

    public static HashMap<String,ArrayList<String>> findAllCateAndSubCate(ArrayList<FoodPlace> totalFoodPlaceList) {
        ArrayList<String> allSubCategory = new ArrayList<>();
        HashMap<String,ArrayList<String>> categoryMap = new HashMap<>();
        categoryMap.put("All",allSubCategory);

        for (FoodPlace foodPlace : totalFoodPlaceList)
        {
            boolean category_already_exist = false;
            boolean subCategory_already_exist = false;
            String category = foodPlace.getCategory();
            String subCategory = foodPlace.getSub_category();

            ArrayList<String> allCategory = new ArrayList<>(categoryMap.keySet());

            for (String existCategory : allCategory)
            {
                if (category.equals(existCategory))
                {
                    category_already_exist = true;
                    ArrayList<String> subcategoryList = categoryMap.get(category);
                    for (String existSubCategory : subcategoryList)
                    {
                        if (subCategory.equals(existSubCategory))
                        {
                            subCategory_already_exist = true;
                            break;
                        }
                    }
                    if (!subCategory_already_exist)
                    {
                        categoryMap.get(category).add(subCategory);
                        categoryMap.get("All").add(subCategory);
                    }
                    break;
                }
            }
            if (!category_already_exist)
            {
                ArrayList<String> subCategoryListOneCategory = new ArrayList<>();
                subCategoryListOneCategory.add(subCategory);
                allSubCategory.add(subCategory);
                categoryMap.put(category,subCategoryListOneCategory);
            }
        }
        return categoryMap;
    }

    public static ArrayList<String> findAllCategories(ArrayList<FoodPlace> totalFoodPlaceList) {
        ArrayList<String> allCategories = new ArrayList<>();
        String category = "";
        allCategories.add("All");

        for (FoodPlace foodPlace : totalFoodPlaceList)
        {
            category = foodPlace.getCategory();
            if(!allCategories.contains(category))
                allCategories.add(category);
        }
        return allCategories;
    }

    public static ArrayList<FoodPlace> readFoodData(){
        DatabaseReference foodReference = mDatabaseRootRef.child("FoodPlaces");
        if (foodReference != null)
        {
            foodReferenceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    foodPlacesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        FoodPlace fp = snapshot.getValue(FoodPlace.class);
                        foodPlacesList.add(fp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }

            };
            foodReference.addValueEventListener(foodReferenceListener);
        }
        return foodPlacesList;
    }

    public static ArrayList<Service> readLocationServiceData() {
        DatabaseReference locationServicesReference = mDatabaseRootRef.child("LocationServices");
        if (locationServicesReference != null)
        {
            locationServiceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    locationServiceList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Service service = snapshot.getValue(Service.class);
                        locationServiceList.add(service);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }

            };
            locationServicesReference.addValueEventListener(locationServiceListener);
        }
        return locationServiceList;
    }

    public static ArrayList<Service> readVirtualServiceData() {
        DatabaseReference virtualServicesReference = mDatabaseRootRef.child("VirtualServices");
        if (virtualServicesReference != null)
        {
            virtualServiceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    virtualServiceList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Service service = snapshot.getValue(Service.class);
                        virtualServiceList.add(service);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }

            };
            virtualServicesReference.addValueEventListener(virtualServiceListener);
        }
        return virtualServiceList;
    }

    public static ArrayList<ToiletPlace> readToiletData(){

        DatabaseReference toiletReference = mDatabaseRootRef.child("ToiletPlaces");

        if (toiletReference != null)
        {
            toiletReferenceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    ToiletPlace tp = new ToiletPlace();
                    toiletList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        tp = snapshot.getValue(ToiletPlace.class);
                        toiletList.add(tp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            };
            toiletReference.addValueEventListener(toiletReferenceListener);
        }
        return toiletList;
    }

    public static ArrayList<FoodPlace> readOneUserFromFireBase() {

        final DatabaseReference userReference = mDatabaseRootRef.child("Users");

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID, email;

        if (mAuth.getCurrentUser() != null){
            currentUserID= mAuth.getCurrentUser().getUid();
            email = mAuth.getCurrentUser().getEmail();
            if (userReference != null)
            {
                userReferenceListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            favouriteList = user.getFavouriteList();
                        }
                        else
                        {
                            User user = new User(email);
                            userReference.child(currentUserID).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                };
                userReference.child(currentUserID).addValueEventListener(userReferenceListener);
            }
        }
        return favouriteList;
    }

    public static void downloadHeadIconFromFireBase(final ProgressDialog progressDialog, final ImageView head, final TextView userName) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://homelessservices-43603.appspot.com");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        StorageReference specificRef = mStorageRef.child("Photos/"+mAuth.getCurrentUser().getEmail());
        final long ONE_MEGABYTE = 512 * 512;
        specificRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                head.setImageBitmap(bitmap);
                readUserName(userName);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                progressDialog.dismiss();
                // Handle any errors
            }
        });
    }

    public static Bitmap[] downloadHeadIconFromFireBase(final ImageView image) {
        final Bitmap[] bitmapWhole = {null};
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://homelessservices-43603.appspot.com");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        StorageReference specificRef = mStorageRef.child("Photos/"+mAuth.getCurrentUser().getEmail());
        final long ONE_MEGABYTE = 512 * 512;
        specificRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
                bitmapWhole[0] = bitmap;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle any errors
                image.setImageResource(R.drawable.avatar);
                Bitmap bmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                bitmapWhole[0] = bmap;
            }
        });
        return bitmapWhole;
    }

    public static ArrayList<String> readOneUserCommentsFromFireBase() {

        final DatabaseReference userReference = mDatabaseRootRef.child("Users");

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID, email;

        if (mAuth.getCurrentUser() != null){
            currentUserID= mAuth.getCurrentUser().getUid();
            email = mAuth.getCurrentUser().getEmail();
            if (userReference != null)
            {
                userReferenceListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            commentsList = user.getCommentList();
                        }
                        else
                        {
                            User user = new User(email);
                            userReference.child(currentUserID).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                };
                userReference.child(currentUserID).addValueEventListener(userReferenceListener);
            }
        }
        return commentsList;
    }

    public static String readUserName() {
        final DatabaseReference userReference = mDatabaseRootRef.child("Users");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID, email;

        if (mAuth.getCurrentUser() != null)
        {
            currentUserID = mAuth.getCurrentUser().getUid();
            email = mAuth.getCurrentUser().getEmail();
            userReference.child(currentUserID).addValueEventListener(userReferenceListener);
            if (userReference != null)
            {
                userReferenceListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            user_name = user.getUserName();
                        }
                        else
                        {
                            User user = new User(email);
                            userReference.child(currentUserID).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                };

            }
        }
        return user_name;
    }

    public static void readUserName(final TextView userName) {
        final DatabaseReference userReference = mDatabaseRootRef.child("Users");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID, email;

        if (mAuth.getCurrentUser() != null)
        {
            currentUserID = mAuth.getCurrentUser().getUid();
            email = mAuth.getCurrentUser().getEmail();
            if (userReference != null)
            {
                userReferenceListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            userName.setText(user.getUserName());
                        }
                        else
                        {
                            User user = new User(email);
                            userReference.child(currentUserID).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                };
                userReference.child(currentUserID).addValueEventListener(userReferenceListener);
            }
        }
    }

}
