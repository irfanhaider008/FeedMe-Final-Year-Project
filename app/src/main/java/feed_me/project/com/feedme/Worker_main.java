package feed_me.project.com.feedme;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.http.conn.scheme.HostNameResolver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Worker_main extends Fragment {
    String name,email,From,Suspend="OFF",userid,Type;
    FirebaseAuth auth;
    Spinner PO,City;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference ref,rateref;
    CircleImageView IMGprofile,IMGrate;
    EditText ETpost,ETptitle,House,Street;
    Button BTNeditprofile,BTNpost,BTNdonor,BTNworker,BTNpostdone;
    RecyclerView ProfileRecycler;
    TextView Postheading,TVrating,TVname,TVdiscription;
    ArrayList<String> Posts=new ArrayList<>();
    ArrayList<String> PostTime=new ArrayList<>();
    ArrayList<String> Ptitle=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> Bad=new ArrayList<>();
    ArrayList<String> Good=new ArrayList<>();
    ArrayList<String> Average=new ArrayList<>();
    ArrayList<String> TotalUp=new ArrayList<>();
    ArrayList<String> TotalDown=new ArrayList<>();



    public Worker_main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_worker_main, container, false);

        userid=auth.getInstance().getUid();
        rateref= FirebaseDatabase.getInstance().getReference().child("WorkerRate");

        IMGprofile=rootView.findViewById(R.id.imageprofile);
        IMGrate=rootView.findViewById(R.id.imagerate);
        TVdiscription=rootView.findViewById(R.id.tvDisc);
        BTNpost=rootView.findViewById(R.id.btnpost);
        TVname=rootView.findViewById(R.id.tvUsername);
        BTNdonor=rootView.findViewById(R.id.btndonor);
        Postheading=rootView.findViewById(R.id.postheading);
        TVrating=rootView.findViewById(R.id.tvrating);
        BTNworker=rootView.findViewById(R.id.btnworker);
        BTNeditprofile=rootView.findViewById(R.id.btneditinfo);
        ProfileRecycler=rootView.findViewById(R.id.WProfilerecycler);

        Bundle bundle=getArguments();
        From = bundle.getString("From");
        if(From.equals("Home")){
            BTNpost.setVisibility(View.INVISIBLE);
            BTNeditprofile.setVisibility(View.INVISIBLE);
            BTNdonor.setVisibility(View.INVISIBLE);
            BTNworker.setVisibility(View.INVISIBLE);
            userid=bundle.getString("ID");
        }
        ref= FirebaseDatabase.getInstance().getReference().child("Register").child("Worker").child(userid);

        ProfileRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://feed-5098b.appspot.com/images/").child(userid+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                IMGprofile.setImageBitmap(bitmap);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    Type = dataSnapshot.child(userid).getValue().toString();
                    if (Type.equals("Donor") || Type.equals("DModerator")) {
                        if(!From.equals("Home")) {
                            BTNdonor.setVisibility(View.INVISIBLE);
                            BTNworker.setVisibility(View.VISIBLE);
                            BTNpost.setVisibility(View.VISIBLE);
                            Postheading.setVisibility(View.VISIBLE);
                        }
                        ProfileRecycler.setVisibility(View.VISIBLE);
                        if(Type.equals("Donor")){
                            IMGrate.setImageResource(R.drawable.d);
                        }else{
                            IMGrate.setImageResource(R.drawable.dm);
                        }                    } else {
                        if(!From.equals("Home")) {
                            BTNdonor.setVisibility(View.VISIBLE);
                            BTNworker.setVisibility(View.INVISIBLE);
                            BTNpost.setVisibility(View.INVISIBLE);
                            Postheading.setVisibility(View.INVISIBLE);
                        }
                        ProfileRecycler.setVisibility(View.INVISIBLE);
                        if(Type.equals("Worker")){
                            IMGrate.setImageResource(R.drawable.w);
                        }else{
                            IMGrate.setImageResource(R.drawable.wm);
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("Post").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userid).exists()) {
                                PostKey.clear();
                                Posts.clear();
                                Ptitle.clear();
                                PostTime.clear();
                                for (DataSnapshot child : dataSnapshot.child(userid).getChildren()) {
                                    Ptitle.add(child.child("Title").getValue().toString());
                                    Posts.add(child.child("Post").getValue().toString());
                                    PostTime.add(child.child("Time").getValue().toString());
                                    PostKey.add((child.getKey()));
                                }
                                ProfileRecycler.setAdapter(new List_of_Profile(getContext(), Ptitle, Posts, PostKey,
                                        TVname.getText().toString(), userid, Type, PostTime));

                                if (Type.equals("Worker") || Type.equals("WModerator")) {/////Calculate Points For Worker Mode/////
                                    rateref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child(userid).exists()) {
                                                Bad.clear();
                                                Average.clear();
                                                Good.clear();
                                                for (DataSnapshot child : dataSnapshot.child(userid).getChildren()) {
                                                    if (child.getValue().toString().equals("Good")) {
                                                        Good.add(child.getKey());
                                                    } else {
                                                        Bad.add(child.getKey());
                                                    }
                                                }
                                            }
                                            int G = Good.size() * 15;
                                            int B = Bad.size() * -5;
                                            int Rate = G + B;
                                            if (Rate > 50) {
                                                IMGrate.setImageResource(R.drawable.wm);
                                                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("WModerator");
                                            } else {
                                                IMGrate.setImageResource(R.drawable.w);

                                                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("Worker");

                                            }
                                            TVrating.setText(String.valueOf(Rate));
                                            if (Rate > 0) {
                                                TVrating.setTextColor(Color.GREEN);
                                            } else {
                                                TVrating.setTextColor(Color.RED);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else if (Type.equals("Donor") || Type.equals("DModerator")) {///////////////Calculate Points For Donor Mode/////
                                    FirebaseDatabase.getInstance().getReference().child("Karma").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            TotalDown.clear();
                                            TotalUp.clear();
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                if (PostKey.contains(child.getKey())) {
                                                    for (DataSnapshot child2 : child.getChildren()) {
                                                        if (child2.getValue().toString().equals("up")) {
                                                            TotalUp.add(child2.getKey());
                                                        } else {
                                                            TotalDown.add(child2.getKey());
                                                        }
                                                    }
                                                }
                                            }
                                            int G = TotalUp.size() * 15;
                                            int B = TotalDown.size() * -5;
                                            int Total = G + B;
                                            if (Total > 50) {
                                                IMGrate.setImageResource(R.drawable.dm);
                                                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("DModerator");
                                            } else {
                                                IMGrate.setImageResource(R.drawable.d);
                                                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("Donor");
                                            }
                                            TVrating.setText(String.valueOf(Total));
                                            if (Total > 0) {
                                                TVrating.setTextColor(Color.GREEN);
                                            } else {
                                                TVrating.setTextColor(Color.RED);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
////////////////////GET BASIC DATA OF PROFILE////////////////////////////////////////////////
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Address = dataSnapshot.child("Address").getValue().toString();
                String CNIC = dataSnapshot.child("CNIC").getValue().toString();
                String Email = dataSnapshot.child("Email").getValue().toString();
                TVdiscription.setText(CNIC + "\n\n" + Email + "\n\n" + Address);
                TVname.setText(dataSnapshot.child("Name").getValue().toString());
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        BTNeditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SignUp.class);
                intent.putExtra("Type","Worker");
                intent.putExtra("FLAG","EDIT");
                startActivity(intent);
            }
        });
        BTNpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.postdialogue);

                ETpost=dialog.findViewById(R.id.etpost);
                ETptitle=dialog.findViewById(R.id.etposttitle);
                House=dialog.findViewById(R.id.posthouse);
                Street=dialog.findViewById(R.id.poststreet);
                PO=dialog.findViewById(R.id.postpo);
                City=dialog.findViewById(R.id.postcity);
                BTNpostdone=dialog.findViewById(R.id.btnpostdone);

                BTNpostdone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ETptitle.getText().toString().equals("")||ETptitle.getText().toString().equals(" ")){
                            ETptitle.setError("PLease Gave a Title...");
                        }else if(ETpost.getText().toString().equals("")||ETpost.getText().toString().equals(" ")){
                            ETpost.setError("PLease Write Something...");
                        }else if(House.getText().toString().isEmpty()){
                            House.setError("Please Enter House #");
                        }else if(Street.getText().toString().isEmpty()){
                            Street.setError("Please Enter Street #");
                        }else if(PO.getSelectedItem().equals("Select Colony")){
                            TextView errorText = (TextView)PO.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            errorText.setText("Please Select Colony");
                        }else if(City.getSelectedItem().equals("Select City")){
                            TextView errorText = (TextView)City.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            errorText.setText("Please Select City");
                        }else{
                            Date currentLocalTime = Calendar.getInstance().getTime();
                            DateFormat date=new SimpleDateFormat("dd MMM yyyy hh:mm a");
                            String Time=date.format(currentLocalTime);

                            String POST=ETpost.getText().toString()+"\n\nLocation: House#"+House.getText().toString()+" Steeet#"+Street.getText().toString()
                                    +" "+PO.getSelectedItem().toString()+" Colony City "+City.getSelectedItem().toString();
                            Map post = new HashMap();
                            post.put("Title",ETptitle.getText().toString());
                            post.put("Post",POST);
                            post.put("Time",Time);
                            FirebaseDatabase.getInstance().getReference().child("Post").child(userid).push().updateChildren(post);
                            Toast.makeText(getContext(),"Successfully Done",Toast.LENGTH_LONG).show();
                            ETpost.setText("");
                            ETptitle.setText("");
                            House.setText("");
                            Street.setText("");
                            PO.setSelection(0);
                            City.setSelection(0);
                        }
                    }
                });
                dialog.show();
            }
        });
        BTNdonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             BTNdonor.setVisibility(View.INVISIBLE);
                BTNworker.setVisibility(View.VISIBLE);
                BTNpost.setVisibility(View.VISIBLE);
                ProfileRecycler.setVisibility(View.VISIBLE);
                Postheading.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("Donor");
            }
        });
        BTNworker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTNdonor.setVisibility(View.VISIBLE);
                BTNworker.setVisibility(View.INVISIBLE);
                BTNpost.setVisibility(View.INVISIBLE);
                ProfileRecycler.setVisibility(View.INVISIBLE);
                Postheading.setVisibility(View.INVISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("Worker");
            }
        });


        return rootView;
    }

}
