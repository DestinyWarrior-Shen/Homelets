package com.example.homelessservices;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment implements View.OnClickListener,AdapterView.OnItemLongClickListener ,AdapterView.OnItemClickListener{

    private TextView current_user,num_reviews,listViewTitle;
    private ImageView head,img_camera,img_album,edit_name;
    private ListView listView;
    private ArrayList<String> userCommentList, onePlaceCommentList,tmpList;
    private ArrayList<FoodPlace> foodPlaces,favouritePlaces;
    private ReviewAdapter adapter;
    private DatabaseReference mDatabaseRootRef,userCommentRef,foodPlaceRef,foodPlaceCommentRef,currentUserNameRef;
    private FirebaseAuth mAuth;
    private ValueEventListener foodPlaceListListener;

    private Uri imageUri;
    private final int CAMERA_REQUEST_CODE = 0;
    private final int GALLERY_REQUEST_CODE = 2;
    private final int CROP_REQUEST_CODE = 1;

    private AlertDialog alertDialog;
    private View view;
    private ProgressDialog progressDialog;
    private int pressTimes = 0;
    private Bitmap bitmapDownload,bitmapFromOtherMediaNew,bitmapFromOtherMediaOld;
    private Menu mMenu;
    private String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");
        setHasOptionsMenu(true);

        initData();
        initView(view);

        reverseOrder();
        //reverseOrderList = setListOrder(userCommentList);
        adapter = new ReviewAdapter();
        listView.setAdapter(adapter);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return view;
    }

    private void initView(View view) {
        head = (ImageView) view.findViewById(R.id.head_image);
        edit_name = (ImageView) view.findViewById(R.id.edit_name);
        current_user = (TextView) view.findViewById(R.id.current_user);
        num_reviews = (TextView) view.findViewById(R.id.num_review);
        listViewTitle = (TextView) view.findViewById(R.id.tv_review);
        listView = (ListView) view.findViewById(R.id.review_listView);

        userName = ReadDataFromFireBase.readUserName();
        if (userName.length() == 0)
            current_user.setText(mAuth.getCurrentUser().getEmail());
        else
            current_user.setText(userName);


        if (userCommentList.size() == 0)
            num_reviews.setText("");
        else if (userCommentList.size() == 1)
            num_reviews.setText("1 review");
        else
            num_reviews.setText(userCommentList.size() + " reviews");


        if (userCommentList.size() == 0)
            listViewTitle.setText("No reviews");
        else
            listViewTitle.setText("Your all reviews (Long click can delete comment)");
    }

    private void initData() {

        progressDialog = new ProgressDialog(getActivity());
        userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        favouritePlaces = ReadDataFromFireBase.readOneUserFromFireBase();
        foodPlaces = ReadDataFromFireBase.readFoodData();
        onePlaceCommentList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRootRef = FirebaseDatabase.getInstance().getReference();

        downloadHeadIconFromFireBase();

    }

    private ArrayList<String> setListOrder(ArrayList<String> userCommentList){
        ArrayList<String> list = new ArrayList<>();
        tmpList = new ArrayList<>();
        for (int i = userCommentList.size() - 1; i>=0; i--)
        {
            list.add(userCommentList.get(i));
        }
        return list;
    }

    private void reverseOrder() {
        tmpList = new ArrayList<>(userCommentList);
        Collections.reverse(tmpList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            listView.setOnItemLongClickListener(this);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.head_image:
                showUpdatePhotoDialog();
                break;

            case R.id.edit_name:
                showChangeNameDialog();
                break;
        }
    }

    private void showUpdatePhotoDialog() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View textEntryView = inflater.inflate(R.layout.upload_photo_dialog, null);

        img_camera = (ImageView) textEntryView.findViewById(R.id.img_camera);
        img_album = (ImageView) textEntryView.findViewById(R.id.img_album);

        AlertDialog.Builder dl = new AlertDialog.Builder(view.getContext());
        dl.setView(textEntryView);
        dl.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        alertDialog = dl.show();

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                checkCameraPermission();
            }
        });

        img_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                checkStoragePermission(false);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] userCommentOneRecordArray = userCommentList.get(position).split("\\^");
        String[] findPlaceLatitudeArray = userCommentOneRecordArray[1].split("-");
        String longitude = findPlaceLatitudeArray[0];
        FoodPlace foodPlace = new FoodPlace();
        for (FoodPlace fp : foodPlaces)
        {
            if (fp.getLongitude().equals(longitude))
            {
                foodPlace = fp;
                break;
            }
        }
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.transmitPlaceObjectToPlaceDetailFragment(foodPlace,favouritePlaces,userCommentList);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content,placeDetails);
        ft.addToBackStack(null);
        ft.commit();
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
                if (userCommentList.size() == 0)
                    num_reviews.setText("No reviews");
                else if (userCommentList.size() == 1)
                    num_reviews.setText("1 review");
                else
                    num_reviews.setText(userCommentList.size()+" reviews");
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Comment has been deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
        return true;
    }

    private void deleteCommentFromFireBase(int position) {

        String[] userCommentOneRecordArray = tmpList.get(position).split("\\^");
        final String dateAndTime = userCommentOneRecordArray[0];
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
                            for (Iterator<String> iterator = onePlaceCommentList.iterator();iterator.hasNext();)
                            {
                                String value = iterator.next();
                                String[] array = value.split("\\^");
                                if (array[0].equals(dateAndTime))
                                {
                                    iterator.remove();
                                    break;
                                }
                            }
                            foodPlaceCommentRef = foodPlaceRef.child(placeKeyIndex).child("reviewList");
                            foodPlaceCommentRef.setValue(onePlaceCommentList);

                            foodPlaceRef.removeEventListener(foodPlaceListListener);
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

        tmpList.remove(position);
        userCommentList = new ArrayList<>(tmpList);
        Collections.reverse(userCommentList);

        userCommentRef = mDatabaseRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("commentList");
        userCommentRef.setValue(userCommentList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
        mMenu = menu;
        mMenu.findItem(R.id.cancel).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        pressTimes +=1;
        if (id == R.id.edit)
        {
            if (pressTimes % 2 != 0 )
            {
                item.setIcon(R.drawable.ok);
                mMenu.findItem(R.id.cancel).setVisible(true);
                edit_name.setVisibility(View.VISIBLE);
                edit_name.setOnClickListener(this);
                head.setClickable(true);
                head.setOnClickListener(this);
                head.setImageResource(R.drawable.updatephoto);

            }
            else
            {
                item.setIcon(R.drawable.edit);
                head.setClickable(false);
                edit_name.setVisibility(View.GONE);
                if (!current_user.getText().toString().equals(userName)) {
                    updateUserName(current_user.getText().toString());
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setUserName(current_user.getText().toString());
                }

                mMenu.findItem(R.id.cancel).setVisible(false);
                if (bitmapDownload != null)
                    head.setImageBitmap(bitmapDownload);
                else
                    head.setImageResource(R.drawable.profile);

                if (bitmapFromOtherMediaNew != null && bitmapFromOtherMediaNew != bitmapFromOtherMediaOld)
                {
                    head.setImageBitmap(bitmapFromOtherMediaNew);
                    uploadHeadIconToFireBase(bitmapFromOtherMediaNew);
                    bitmapFromOtherMediaOld = bitmapFromOtherMediaNew;
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setPhoto(bitmapFromOtherMediaNew);
                    bitmapDownload = bitmapFromOtherMediaNew;
                }
            }
            return true;
        }
        if (id == R.id.cancel)
        {
            mMenu.findItem(R.id.cancel).setVisible(false);
            mMenu.findItem(R.id.edit).setIcon(R.drawable.edit);
            current_user.setText(userName);
            head.setClickable(false);
            edit_name.setVisibility(View.GONE);
            if (bitmapDownload != null)
                head.setImageBitmap(bitmapDownload);
            else
                head.setImageResource(R.drawable.profile);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkCameraPermission () {
        int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED)
        {
            requestCameraPermission();
        }
        else
        {
            checkStoragePermission(true);
        }
    }

    private void checkStoragePermission (boolean flag) {
        int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED)
        {
            if (!flag)
                requestStoragePermission(false);
            else
                requestStoragePermission(true);
        }
        else
        {
            if (!flag)
                galleryOpen();
            else
                cameraOpen();
        }
    }

    private void requestCameraPermission() {
        if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
        {
            Toast.makeText(getActivity(),"CAMERA permission allows us to access CAMERA app",Toast.LENGTH_SHORT).show();
            checkStoragePermission(true);
        }
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA},12);
    }

    private void requestStoragePermission(boolean flag) {
        if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Toast.makeText(getActivity(),"GALLERY permission allows us to access GALLERY app",Toast.LENGTH_SHORT).show();
            if (!flag)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1234);
            else
                cameraOpen();
        }
        else
        {
            if (!flag)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
            else
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 12:
            {
                if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        //head.setOnClickListener(this);
                        checkStoragePermission(true);
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case 123:
            {
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        //head.setOnClickListener(this);
                        galleryOpen();
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case 1234:
            {
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        cameraOpen();
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            if (imageUri != null)
            {
                cropImage(imageUri);
            }
        }
        else if (requestCode == GALLERY_REQUEST_CODE)
        {
            if (data != null) {
                imageUri = data.getData();
                cropImage(imageUri);
            }
        }
        else if (requestCode == CROP_REQUEST_CODE)
        {
            if (imageUri != null)
            {
                bitmapFromOtherMediaNew = decodeUriBitmap(imageUri);
                head.setImageBitmap(bitmapFromOtherMediaNew);
            }
        }
    }

    private void cameraOpen() {
        imageUri = Uri.fromFile(getImageStoragePath(getContext()));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
    }

    private void galleryOpen() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image from Gallery"),GALLERY_REQUEST_CODE);
    }

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        //设置了true的话直接返回bitmap，可能会很占内存
        intent.putExtra("return-data", false);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //设置输出的地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private File getImageStoragePath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"temp.jpg");
            return file;
        }
        return null;
    }

    private Bitmap decodeUriBitmap(Uri uri) {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void downloadHeadIconFromFireBase() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://homelessservices-43603.appspot.com");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        StorageReference specificRef = mStorageRef.child("Photos/" + mAuth.getCurrentUser().getEmail());
        final long ONE_MEGABYTE = 512 * 512;
        specificRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapDownload = bitmap;
                head.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
            }
        });
    }

    private void uploadHeadIconToFireBase(Bitmap bitmap) {
        Uri uri1 = getImageUri(getContext(),bitmap);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StorageReference filePath = FirebaseStorage.getInstance().getReferenceFromUrl("gs://homelessservices-43603.appspot.com").child("Photos").child(mAuth.getCurrentUser().getEmail());
        filePath.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(),"Upload Done", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Some errors occurs", Toast.LENGTH_SHORT).show();
                // Handle any errors
            }
        });
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return tmpList.size();
        }

        @Override
        public String getItem(int position)
        {
            return tmpList.get(position);
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

            if (tmpList.size() != 0)
            {
                String[] array = tmpList.get(position).split("\\^");

                String year = array[0].substring(0,4);
                String month = array[0].substring(5,7);
                String date = array[0].substring(8,10);
                String time = array[0].substring(11).replace("-",":");
                holder.DateTime.setText(date + "/"+ month + "/" + year + " " + time);
                String[] array1 = array[1].split("-");
                holder.place_name.setText(array1[1]);
                holder.ratingBar.setRating(Float.parseFloat(array[2]));
                holder.mark.setText(array[2]);
                holder.comment.setText(array[3]);
            }

            return convertView;
        }
    }

    private void updateUserName(String newName) {
        currentUserNameRef = mDatabaseRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userName");
        currentUserNameRef.setValue(newName);
    }

    private void showChangeNameDialog() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View textEntryView = inflater.inflate(R.layout.change_name_dialog, null);

        final EditText changeName = (EditText) textEntryView.findViewById(R.id.change_name_editText);

        AlertDialog.Builder dl = new AlertDialog.Builder(view.getContext());
        dl.setView(textEntryView);
        dl.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                current_user.setText(changeName.getText().toString());
            }
        });
        dl.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        dl.show();
    }
}
