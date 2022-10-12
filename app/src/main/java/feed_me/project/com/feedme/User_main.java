package feed_me.project.com.feedme;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import feed_me.project.com.feedme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class User_main extends Fragment {
    String name,email,From,Type,userid;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference ref;
    Spinner PO,City;
    CircleImageView IMGprofile,IMGrate;
    EditText ETpost,ETptitle,House,Street;
    Button BTNeditprofile,BTNpostdone,BTNpost;
    RecyclerView ProfileRecycler;
    TextView Dtvrating,TVname,TVdiscription;
    ArrayList<String> Posts=new ArrayList<>();
    ArrayList<String> PostTime=new ArrayList<>();
    ArrayList<String> Ptitle=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> TotalUp=new ArrayList<>();
    ArrayList<String> TotalDown=new ArrayList<>();



    public User_main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_main, container, false);

        userid = auth.getInstance().getUid();

        IMGprofile = rootView.findViewById(R.id.imageprofile);
        IMGrate = rootView.findViewById(R.id.uimagerate);
        TVdiscription = rootView.findViewById(R.id.tvDisc);
        BTNpost = rootView.findViewById(R.id.ubtnpost);
        TVname = rootView.findViewById(R.id.tvUsername);
        Dtvrating = rootView.findViewById(R.id.dtvrating);
        BTNeditprofile = rootView.findViewById(R.id.btneditinfo);
        ProfileRecycler = rootView.findViewById(R.id.UProfilerecycler);

        Bundle bundle=getArguments();
        From = bundle.getString("From");
        if(From.equals("Home")){
            BTNpost.setVisibility(View.INVISIBLE);
            BTNeditprofile.setVisibility(View.INVISIBLE);
            userid=bundle.getString("ID");
        }
        ref = FirebaseDatabase.getInstance().getReference().child("Register").child("User").child(userid);

        ProfileRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://feed-5098b.appspot.com/images/").child(userid + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                IMGprofile.setImageBitmap(bitmap);
            }
        });
        ref.addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference().child("Type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(userid).exists()){
                        Type=dataSnapshot.child(userid).getValue().toString();
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

                            FirebaseDatabase.getInstance().getReference().child("Karma").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    TotalUp.clear();
                                    TotalDown.clear();
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
                                    Dtvrating.setText(String.valueOf(Total));
                                    if (Total > 0) {
                                        Dtvrating.setTextColor(Color.GREEN);
                                    } else {
                                        Dtvrating.setTextColor(Color.RED);
                                    }
                                    if (Total > 50) {
                                        IMGrate.setImageResource(R.drawable.m);
                                        FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("DModerator");
                                    } else {
                                        IMGrate.setImageResource(R.drawable.d);
                                        FirebaseDatabase.getInstance().getReference().child("Type").child(userid).setValue("Donor");
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        BTNeditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SignUp.class);
                intent.putExtra("Type","User");
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


        return rootView;
    }

}
