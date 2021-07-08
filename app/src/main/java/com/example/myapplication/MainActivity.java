package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Following are the objects on the screen
    private FloatingActionButton addBtn;
    private RecyclerView eventRecycler;
    private RecyclerView placesRecycler;

    //Following is the main progress dialog, we show on starting of the app
    ProgressDialog mainprogressDialog;

    //Following is the mainlayout;
    private RelativeLayout mainlayout;

    //Declaring the imageviews on botttom sheet globally tomake the priview af images avilable to the user
    ImageView imageViewEvent;
    ImageView imageViewPlace;

    //Following are the objects for image picking function at event bottom sheet
    private Uri imageUriEvent=null;
    int REQUESTCODE_EVENT=1;

    //Following are the objects for image picking function at place bottom sheet
    private Uri imageUriPlace=null;
    int REQUESTCODE_PLACE=2;

    //Following are the required objects for the venets recycler
    private List<Events> eventsList;
    private EventAdapter eventAdapter;

    //Following are the required objects for the places recycler
    private List<Place> placeList;
    private PlaceAdapter placeAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Folllowing is the code to set the status bar as color
        Window window= MainActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.design_default_color_primary));


        //Restricting the lanscape orientation (Locking Orientation for activity)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Following is the method, to initialize, all the objects with their respective ids
        initialize();

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Now, first of all, we will vanish thw whole layout and, start a progress dialog, and do the loading process in the background
        mainlayout.setVisibility(View.GONE);
        //Starting the progress dialog
        mainprogressDialog = new ProgressDialog(MainActivity.this);
        mainprogressDialog.setCanceledOnTouchOutside(false);
        mainprogressDialog.setTitle("Connecting");
        mainprogressDialog.setMessage("Please wait a while we connect to our servers and fetch the valuable data for you. It may take sonme time depending upon your network connectivity.");
        mainprogressDialog.show(); //Starting the progress dialog
        mainprogressDialog.setCancelable(false);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //Following is the code, to set up and fetch the data for the recycler view of evgents
        eventRecycler.setHasFixedSize(true);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        eventsList=new ArrayList<>();

        fetchAllTheEventsandDisplay(); //Following is teh method, that fetches all the events and display


        //Following is teh code, to set up the recycl;er for the places
        placesRecycler.setHasFixedSize(true);
        placesRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2,LinearLayoutManager.HORIZONTAL,false));
        placeList=new ArrayList<>();


        fetchAllThePlacesandDisplay(); //Following is teh method, that fetches all the places and display





        //Now, we need to show a dialog to ask what to add....(Either event or place)
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creating alert Dialog with three Buttons

                AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialog3.setTitle("Add Data to Databse");
                alertDialog3.setMessage("What do you want to add?");
                alertDialog3.setIcon(R.drawable.add_ic);
                alertDialog3.setPositiveButton("Place",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //User has clicked PLACE, Now, we have to implement the logic here
                                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
                                View dialog2= LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_place,null);

                                //Following are the objects on teh bottom sheet dialog
                                imageViewPlace=dialog2.findViewById(R.id.image_addPlace);
                                EditText cityName=dialog2.findViewById(R.id.cityname_addPlace);
                                Button addBtn=dialog2.findViewById(R.id.addBtn_addPlace);

                                imageViewPlace.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pickImage(REQUESTCODE_PLACE);
                                    }
                                });

                                addBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (imageUriPlace==null){
                                            Toast.makeText(MainActivity.this, "Image is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (TextUtils.isEmpty(cityName.getText().toString()))
                                        {
                                            Toast.makeText(MainActivity.this, "City/Place name is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            //Now, we have verified the credentials, and can now store them in databse
                                            //Now, we will start a progress dialog first
                                            ProgressDialog progressDialog;
                                            progressDialog = new ProgressDialog(MainActivity.this);
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.setTitle("Adding the Place");
                                            progressDialog.setMessage("Please wait a while we add the place to databse. It may take upto a minute.");
                                            progressDialog.show(); //Starting the progress dialog
                                            progressDialog.setCancelable(false);

                                            //Now, we have to upload the image
                                            final String[] downloadImageUrl = {""}; //We will store the store the url in this variable

                                            //The reference to events in Databse
                                            DatabaseReference plaeRef=FirebaseDatabase.getInstance().getReference().child("Place");
                                            //Generating a sepearet UID (Unique key) for the event
                                            String uid=plaeRef.push().getKey();

                                            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Place").child(uid+ ".jpg");


                                            //Now, that the old image is removed,
                                            final UploadTask uploadTask = filePath.putFile(imageUriPlace);

                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, uploadTask.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                throw task.getException();

                                                            }

                                                            downloadImageUrl[0] = filePath.getDownloadUrl().toString();

                                                            return filePath.getDownloadUrl();
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {


                                                            downloadImageUrl[0] =task.getResult().toString();

                                                            //Now, we have the imageurr and can set the data to datyabse
                                                            final Map<String,Object> map=new HashMap();
                                                            map.put("image",downloadImageUrl[0]);
                                                            map.put("uid",uid);
                                                            map.put("city",cityName.getText().toString());



                                                            plaeRef.child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        //Adding Sucessful
                                                                        Toast.makeText(MainActivity.this, "Place Added Sucessfully", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.dismiss();
                                                                        bottomSheetDialog.cancel();
                                                                        imageUriPlace=null;

                                                                    }
                                                                    else
                                                                    {
                                                                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                                        //Adding failed for some reason
                                                                        progressDialog.dismiss();
                                                                        bottomSheetDialog.cancel();
                                                                    }
                                                                }
                                                            });




                                                        }
                                                    });
                                                }


                                            });




                                        }
                                    }
                                });






                                bottomSheetDialog.setContentView(dialog2);
                                bottomSheetDialog.show();

                            }
                        });

                alertDialog3.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // User pressed Cancel button. Write Logic Here

                            }
                        });

                alertDialog3.setNegativeButton("Event",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // User pressed Event button. Write Logic Here
                                //Now, we will show a bottom sheet from the where the user should be able to add events
                                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
                                View dialog2= LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_event,null);

                                //Following are the objects on the bottom sheet to add event
                                imageViewEvent=dialog2.findViewById(R.id.image_addEvent);
                                EditText title=dialog2.findViewById(R.id.title_addEvent);
                                EditText dates=dialog2.findViewById(R.id.dates_addEvent);
                                EditText place=dialog2.findViewById(R.id.city_addEvent);
                                EditText fee=dialog2.findViewById(R.id.fee_addEvent);
                                Button btn=dialog2.findViewById(R.id.addBtn_addEvent);

                                imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pickImage(REQUESTCODE_EVENT);
                                    }
                                });

                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Checking if all fields are provided
                                        if (imageUriEvent==null)
                                        {
                                            Toast.makeText(MainActivity.this, "Image is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (TextUtils.isEmpty(title.getText().toString()))
                                        {
                                            Toast.makeText(MainActivity.this, "Title is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (TextUtils.isEmpty(dates.getText().toString()))
                                        {
                                            Toast.makeText(MainActivity.this, "Date is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (TextUtils.isEmpty(place.getText().toString()))
                                        {
                                            Toast.makeText(MainActivity.this, "City is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (TextUtils.isEmpty(fee.getText().toString()))
                                        {
                                            Toast.makeText(MainActivity.this, "Fee is mandatory", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            //Now, we will start a progress dialog first
                                            ProgressDialog progressDialog;
                                            progressDialog = new ProgressDialog(MainActivity.this);
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.setTitle("Adding the Event");
                                            progressDialog.setMessage("Please wait a while we add the event to databse. It may take upto a minute.");
                                            progressDialog.show(); //Starting the progress dialog
                                            progressDialog.setCancelable(false);

                                            //The reference to events in Databse
                                            DatabaseReference eventRef=FirebaseDatabase.getInstance().getReference().child("Event");
                                            //Generating a sepearet UID (Unique key) for the event
                                            String uid=eventRef.push().getKey();




                                            //Now, we have to upload the image
                                            final String[] downloadImageUrl = {""}; //We will store the store the url in this variable

                                            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Event").child(uid+ ".jpg");




                                            final UploadTask uploadTask = filePath.putFile(imageUriEvent);

                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, uploadTask.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                throw task.getException();

                                                            }

                                                            downloadImageUrl[0] = filePath.getDownloadUrl().toString();

                                                            return filePath.getDownloadUrl();
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {


                                                            downloadImageUrl[0] =task.getResult().toString();

                                                            //Now, we have the imageurr and can set the data to datyabse
                                                            final Map<String,Object> map=new HashMap();
                                                            map.put("image",downloadImageUrl[0]);
                                                            map.put("uid",uid);
                                                            map.put("title",title.getText().toString());
                                                            map.put("date",dates.getText().toString());
                                                            map.put("city",place.getText().toString());
                                                            map.put("fee",fee.getText().toString());


                                                            eventRef.child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        //Adding Sucessful
                                                                        Toast.makeText(MainActivity.this, "Event Added Sucessfully", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.dismiss();
                                                                        bottomSheetDialog.cancel();
                                                                        imageUriEvent=null;

                                                                    }
                                                                    else
                                                                    {
                                                                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                                        //Adding failed for some reason
                                                                        progressDialog.dismiss();
                                                                        bottomSheetDialog.cancel();
                                                                    }
                                                                }
                                                            });




                                                        }
                                                    });
                                                }


                                            });



                                        }


                                    }
                                });








                                bottomSheetDialog.setContentView(dialog2);
                                bottomSheetDialog.show();
                            }
                        });

                alertDialog3.show();

            }
        });




    }

    private void fetchAllThePlacesandDisplay() {
        FirebaseDatabase.getInstance().getReference().child("Place").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (placeList!=null)
                {
                    placeList.clear();
                }
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Place place=dataSnapshot.getValue(Place.class);
                        placeList.add(place);
                    }

                    //Now, sending data to the adapter, to display
                    Collections.reverse(placeList);
                    placeAdapter=new PlaceAdapter(placeList,getApplicationContext(),MainActivity.this);
                    placesRecycler.setAdapter(placeAdapter);

                    placeAdapter.notifyDataSetChanged();

                    //Now. here, it mens that data is loaded, as it is the last function, to fetch the data, so, we will maketh emain layout visible
                    //and dismiss the progress dialog
                    mainlayout.setVisibility(View.VISIBLE);
                    mainprogressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Hello User! Welcome", Toast.LENGTH_SHORT).show();



                }
                else
                {
                    mainlayout.setVisibility(View.VISIBLE);
                    mainprogressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Hello User! Welcome", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void fetchAllTheEventsandDisplay() {

        FirebaseDatabase.getInstance().getReference().child("Event").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (eventsList!=null)
                {
                    eventsList.clear();
                }
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Events events=dataSnapshot.getValue(Events.class);
                        eventsList.add(events);
                    }

                    //Setting up the data to recycler view
                    Collections.reverse(eventsList);
                    eventAdapter=new EventAdapter(eventsList,getApplicationContext(),MainActivity.this);
                    eventRecycler.setAdapter(eventAdapter);

                    eventAdapter.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        addBtn=findViewById(R.id.addBtn_home);
        eventRecycler=findViewById(R.id.recycler_events);
        placesRecycler=findViewById(R.id.recycler_places);
        mainlayout=findViewById(R.id.mainlayout);
    }

    //Following are the methods, to take image URi from the image picked from gallery
    private void pickImage(int code) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, code);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUESTCODE_EVENT && resultCode == Activity.RESULT_OK && data.getData() != null) {

            imageUriEvent=data.getData();
            Picasso.get().load(imageUriEvent).into(imageViewEvent);
        }
        else if (requestCode == REQUESTCODE_PLACE && resultCode == Activity.RESULT_OK && data.getData() != null) {

            imageUriPlace=data.getData();
            Picasso.get().load(imageUriPlace).into(imageViewPlace);
        }

        else {

            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}