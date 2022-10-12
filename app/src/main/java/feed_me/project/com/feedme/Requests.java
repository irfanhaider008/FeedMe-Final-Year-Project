package feed_me.project.com.feedme;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
public class Requests extends Fragment {

    RecyclerView ReqRecycler;
    FirebaseAuth auth;
    DatabaseReference Reqref,Cuserdb;
    String user_id,FLAG;
    ArrayList<String> Rtitle=new ArrayList<>();
    ArrayList<String> Wname=new ArrayList<>();
    ArrayList<String> WID=new ArrayList<>();
    ArrayList<String> Wlocation=new ArrayList<>();
    ArrayList<String> Wtime=new ArrayList<>();
    ArrayList<String> Response=new ArrayList<>();
    ArrayList<String> Reqkey=new ArrayList<>();
    ArrayList<String> Pkey=new ArrayList<>();
    ArrayList<String> Wcontact=new ArrayList<>();




    public Requests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

        Bundle bundle=getArguments();
        FLAG = bundle.getString("FLAG");

        user_id=auth.getInstance().getUid();
        Reqref= FirebaseDatabase.getInstance().getReference().child("Requests").child(user_id);

        ReqRecycler=rootView.findViewById(R.id.reqrecycler);
        ReqRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if(!FLAG.equals("USER")) {
            Cuserdb = FirebaseDatabase.getInstance().getReference().child("Type");
            Cuserdb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String Type = dataSnapshot.child(user_id).getValue().toString();
                    if (Type.equals("Worker")) {
                        ReqRecycler.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        Reqref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Wname.clear();
                Wlocation.clear();
                Wtime.clear();
                Response.clear();
                Reqkey.clear();
                Rtitle.clear();
                WID.clear();
                Wcontact.clear();
                Pkey.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    Reqkey.add(child.getKey());
                    WID.add(child.child("WID").getValue().toString());
                    Rtitle.add(child.child("Title").getValue().toString());
                    Wname.add(child.child("Name").getValue().toString());
                    Wlocation.add(child.child("Location").getValue().toString());
                    Wtime.add(child.child("AprxTime").getValue().toString());
                    Response.add(child.child("Response").getValue().toString());
                    Wcontact.add(child.child("Contact").getValue().toString());
                    Pkey.add(child.child("Pkey").getValue().toString());
                }
                ReqRecycler.setAdapter(new List_of_Requests(getContext(),Reqkey,Wname,Wlocation,Wtime,Rtitle,WID,Wcontact,Pkey));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;
    }

}
class List_of_Requests extends RecyclerView.Adapter<List_of_Requests.classViewHolder> {
    ArrayList<String> Wname=new ArrayList<>();
    ArrayList<String> Wlocation=new ArrayList<>();
    ArrayList<String> Wtime=new ArrayList<>();
    ArrayList<String> Rtitle=new ArrayList<>();
    ArrayList<String> WID=new ArrayList<>();
    ArrayList<String> Pkey=new ArrayList<>();
    ArrayList<String> Reqkey=new ArrayList<>();
    ArrayList<String> Wcontact=new ArrayList<>();



    Context context;
    String response="Dumy";
    DatabaseReference reqref,repref,rateref;
    FirebaseAuth auth;
    String user_id;

    public List_of_Requests(Context ctx,ArrayList<String> reqkey,ArrayList<String> wname, ArrayList<String> wlocation,
                            ArrayList<String> wtime,ArrayList<String> rtitle,ArrayList<String> wid,ArrayList<String> wcontact,ArrayList<String> pkey){

        this.Wname = wname;
        this.Wlocation = wlocation;
        this.context=ctx;
        this.Wtime=wtime;
        this.Rtitle=rtitle;
        this.WID=wid;
        this.Reqkey=reqkey;
        this.Wcontact=wcontact;
        this.Pkey=pkey;
    }
    @NonNull
    @Override
    public List_of_Requests.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_requests, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Requests.classViewHolder holder, final int position) {
        holder.TVrequest.setText("Request for: "+Rtitle.get(position)+"\nFrom: "+Wname.get(position)+
                                    "\nLocation: "+Wlocation.get(position)+
                                    "\nContact: "+Wcontact.get(position));
        holder.TVtime.setText("Approx Time to Reach: "+Wtime.get(position)+"Min");

        user_id=auth.getInstance().getUid();
        rateref=FirebaseDatabase.getInstance().getReference().child("WorkerRate");
        repref=FirebaseDatabase.getInstance().getReference().child("SentReq");
        reqref=FirebaseDatabase.getInstance().getReference().child("Requests").child(user_id);

        reqref.child(Reqkey.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Response").exists()) {
                    response = dataSnapshot.child("Response").getValue().toString();
                }
                if(response.equals("In Progress")){
                    holder.BTNaccept.setVisibility(View.INVISIBLE);
                    holder.BTNreject.setVisibility(View.INVISIBLE);
                    holder.TVinprogress.setVisibility(View.VISIBLE);
                }else if(response.equals("Completed")){
                    holder.BTNaccept.setVisibility(View.INVISIBLE);
                    holder.BTNreject.setVisibility(View.INVISIBLE);
                    holder.TVinprogress.setVisibility(View.VISIBLE);
                    holder.TVinprogress.setText("Completed");
                    holder.TVinprogress.setTextColor(Color.GREEN);
                    holder.LLcolor.setVisibility(View.VISIBLE);
                    holder.TVratenoti.setText("Task Completed Please Rate Worker Performance");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        rateref.child(WID.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Rtitle.get(position)).exists()){
                    String Rate=dataSnapshot.child(Rtitle.get(position)).getValue().toString();
                    if(Rate.equals("Bad")){
                        holder.BTNgreen.setVisibility(View.INVISIBLE);
                    }else{
                        holder.BTNred.setVisibility(View.INVISIBLE);
                    }
                    holder.TVratenoti.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.BTNaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqref.child(Reqkey.get(position)).child("Response").setValue("In Progress");
                repref.child(WID.get(position)).child(Reqkey.get(position)).child("Response").setValue("In Progress");
                FirebaseDatabase.getInstance().getReference().child("Post").child(user_id).
                        child(Pkey.get(position)).child("Status").setValue("Picked");

                holder.BTNaccept.setVisibility(View.INVISIBLE);
                holder.BTNreject.setVisibility(View.INVISIBLE);
                holder.TVinprogress.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Request Accepted",Toast.LENGTH_LONG).show();
            }
        });
        holder.BTNreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqref.child(Reqkey.get(position)).removeValue();//////apni request delete kar dy ga
                repref.child(WID.get(position)).child(Reqkey.get(position)).child("Response").setValue("Rejected");/////worker ka status change kary ga
                Toast.makeText(context,"Request Rejected",Toast.LENGTH_LONG).show();
            }
        });
        holder.BTNred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.BTNgreen.setVisibility(View.INVISIBLE);
                holder.TVratenoti.setVisibility(View.INVISIBLE);
                rateref.child(WID.get(position)).child(Rtitle.get(position)).setValue("Bad");
            }
        });holder.BTNgreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.BTNred.setVisibility(View.INVISIBLE);
                holder.TVratenoti.setVisibility(View.INVISIBLE);
                rateref.child(WID.get(position)).child(Rtitle.get(position)).setValue("Good");

            }
        });
    }

    @Override
    public int getItemCount() {
        return Wname.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVrequest,TVtime,TVinprogress,TVratenoti;
        Button BTNaccept,BTNreject,BTNred,BTNgreen;
        LinearLayout LLcolor;;

        public classViewHolder(View itemView) {
            super(itemView);

            TVrequest =itemView.findViewById(R.id.tvrequest);
            TVratenoti =itemView.findViewById(R.id.tvratenoti);
            TVinprogress =itemView.findViewById(R.id.tvinprogress);
            TVtime =itemView.findViewById(R.id.tvtime);
            BTNaccept = itemView.findViewById(R.id.btnaccept);
            BTNreject = itemView.findViewById(R.id.btnreject);
            BTNred =itemView.findViewById(R.id.btnbad);
            BTNgreen =itemView.findViewById(R.id.btngood);
            LLcolor =itemView.findViewById(R.id.llcolor);
        }
    }
}

