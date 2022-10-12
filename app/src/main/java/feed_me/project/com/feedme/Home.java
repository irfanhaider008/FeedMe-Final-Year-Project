package feed_me.project.com.feedme;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import feed_me.project.com.feedme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    String userid;
    FirebaseAuth auth;
    RecyclerView PostRecycler;
    ProgressDialog Progressbar;
    DatabaseReference ref,rateref;
    ArrayList<String> PostUserID=new ArrayList<>();
    ArrayList<String> Posts=new ArrayList<>();
    ArrayList<String> Ptitle=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> PostTime=new ArrayList<>();
    ArrayList<String> Bad=new ArrayList<>();
    ArrayList<String> Good=new ArrayList<>();
    ArrayList<String> Average=new ArrayList<>();
    ArrayList<String> TotalDown=new ArrayList<>();
    ArrayList<String> TotalUp=new ArrayList<>();

    public Home() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        userid=auth.getInstance().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Post");
        rateref= FirebaseDatabase.getInstance().getReference().child("WorkerRate");

        PostRecycler=rootView.findViewById(R.id.homerecycler);
        PostRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostKey.clear();
                Posts.clear();
                PostUserID.clear();
                Ptitle.clear();
                PostTime.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    if(!child.getKey().equals(userid)) {
                        for (DataSnapshot child1 : child.getChildren()) {
                            PostUserID.add(child.getKey());
                            Ptitle.add(child1.child("Title").getValue().toString());
                            Posts.add(child1.child("Post").getValue().toString());
                            PostTime.add(child1.child("Time").getValue().toString());
                            PostKey.add((child1.getKey()));
                        }
                    }
                }
                Collections.reverse(PostKey);
                Collections.reverse(Posts);
                Collections.reverse(PostUserID);
                Collections.reverse(Ptitle);
                Collections.reverse(PostTime);
                PostRecycler.setAdapter(new List_of_Post(getContext(),Ptitle,Posts,PostKey,PostUserID,PostTime));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//////////////////////////////////////SUSPENSION CODE///////////////////////////////////////////////////////////////////////////////////
        Progressbar = new ProgressDialog(getContext());
        final ArrayList<String> Postkey=new ArrayList<>();
/////////////////////////////////////////////Donor Points//////////////////////////////////////////////////////////////////////////////
        FirebaseDatabase.getInstance().getReference().child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    Postkey.clear();
                    for (DataSnapshot child : dataSnapshot.child(userid).getChildren()) {
                        Postkey.add((child.getKey()));
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("Karma").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (Postkey.contains(child.getKey())) {
                                    TotalDown.clear();
                                    TotalUp.clear();
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
                            final int TotalDonorPoints = G + B;
///////////////////////////////////////Worker Points///////////////////////////////////////////////////////////////////////////////////
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
                                        final int[] Rate = {G + B};
                                        Rate[0]=Rate[0]+TotalDonorPoints;
                                        if (Rate[0] <= -25) {
                                            FirebaseDatabase.getInstance().getReference().child("Suspended").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child(userid).exists()) {
                                                        final Date currentLocalTime = Calendar.getInstance().getTime();
                                                        final DateFormat date = new SimpleDateFormat("dd MM yyyy");
                                                        String T = date.format(currentLocalTime);
                                                        Date date1 = null;
                                                        try {
                                                            date1 = date.parse(T);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        final long Time = date1.getTime();
                                                        String Days = dataSnapshot.child(userid).child("Days").getValue().toString();
                                                        Date date2 = null;
                                                        try {
                                                            date2 = date.parse(Days);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        long suspendtime = date2.getTime();
                                                        long different = Time - suspendtime;
                                                        long secondsInMilli = 1000;
                                                        long minutesInMilli = secondsInMilli * 60;
                                                        long hoursInMilli = minutesInMilli * 60;
                                                        long daysInMilli = hoursInMilli * 24;
                                                        long elapsedDays = different / daysInMilli;
                                                        String DaysDiff = String.valueOf(elapsedDays);
                                                        if (Integer.parseInt(DaysDiff) > 7) {
                                                            FirebaseDatabase.getInstance().getReference().child("WorkerRate").child(userid).removeValue();
                                                            FirebaseDatabase.getInstance().getReference().child("Suspended").child(userid).removeValue();
                                                            for(int i=0;i<Postkey.size();i++){
                                                                FirebaseDatabase.getInstance().getReference().child("Karma").child(Postkey.get(i)).removeValue();
                                                            }
                                                            Toast.makeText(getContext(), "Suspension Ended", Toast.LENGTH_LONG).show();
                                                            Rate[0]=0;
                                                            Progressbar.dismiss();
                                                        } else {
                                                            Progressbar.setMessage("Your Points are low.\nYou are Suspended for 7 Days");
                                                            Progressbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                                            Progressbar.setMax(7);
                                                            Progressbar.setProgress(Integer.parseInt(DaysDiff));
                                                            Progressbar.setCanceledOnTouchOutside(false);
                                                            Progressbar.setCancelable(false);
                                                            Progressbar.show();
                                                        }
                                                    } else if(Rate[0]<=-25){
                                                        final Date currentLocalTime = Calendar.getInstance().getTime();
                                                        DateFormat date = new SimpleDateFormat("dd MM yyyy");
                                                        String Time = date.format(currentLocalTime);
                                                        FirebaseDatabase.getInstance().getReference().child("Suspended").child(userid).child("Days").setValue(Time);
                                                        Progressbar = new ProgressDialog(getContext());
                                                        Progressbar.setMessage("Your Points are low. You are Suspended for 7 Days");
                                                        Progressbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                                        Progressbar.setMax(7);
                                                        Progressbar.setCanceledOnTouchOutside(false);
                                                        Progressbar.setCancelable(false);
                                                        Progressbar.show();
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return  rootView;
    }

}
