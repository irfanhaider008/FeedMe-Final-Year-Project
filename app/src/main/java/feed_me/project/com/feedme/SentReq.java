package feed_me.project.com.feedme;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import feed_me.project.com.feedme.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentReq extends Fragment {

    String userid;
    FirebaseAuth auth;
    DatabaseReference refsentreq,Cuserdb;
    ArrayList<String> UID=new ArrayList<>();
    ArrayList<String> Srequest=new ArrayList<>();
    ArrayList<String> Sresponse=new ArrayList<>();
    ArrayList<String> Stime=new ArrayList<>();
    RecyclerView SentReqRecycler;

    public SentReq() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sent_req, container, false);

        userid=auth.getInstance().getUid();
        refsentreq=FirebaseDatabase.getInstance().getReference().child("SentReq").child(userid);

        SentReqRecycler=rootView.findViewById(R.id.sentreqrecycler);
        SentReqRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Cuserdb=FirebaseDatabase.getInstance().getReference().child("Type");
        Cuserdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Type=dataSnapshot.child(userid).getValue().toString();
                if(Type.equals("Donor")){
                    SentReqRecycler.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        refsentreq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stime.clear();
                Sresponse.clear();
                Srequest.clear();
                UID.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    Stime.add(child.getKey());
                    Srequest.add(child.child("Title").getValue().toString());
                    Sresponse.add(child.child("Response").getValue().toString());
                    UID.add(child.child("UID").getValue().toString());
                }
                SentReqRecycler.setAdapter(new List_of_SentReq(getContext(),Stime,Srequest,Sresponse,UID));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;
    }

}
class List_of_SentReq extends RecyclerView.Adapter<List_of_SentReq.classViewHolder> {
    ArrayList<String> Srequest=new ArrayList<>();
    ArrayList<String> Sresponse=new ArrayList<>();
    ArrayList<String> Stime=new ArrayList<>();
    ArrayList<String> UID=new ArrayList<>();

    DatabaseReference repref,reqref;
    FirebaseAuth auth;
    String user_id;
    Context context;

    public List_of_SentReq(Context ctx,ArrayList<String> stime,ArrayList<String> srequest, ArrayList<String> sresponse,ArrayList<String> uid){
        this.Stime = stime;
        this.Srequest = srequest;
        this.context=ctx;
        this.Sresponse=sresponse;
        this.UID=uid;
    }
    @NonNull
    @Override
    public List_of_SentReq.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_sentreq, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_SentReq.classViewHolder holder, final int position) {
        holder.TVrequest.setText("Request for: "+Srequest.get(position)+
                                     "\n\n"+Stime.get(position));
        holder.TVresponse.setText(Sresponse.get(position));

        if(Sresponse.get(position).equals("In Progress")){
            holder.TVresponse.setTextColor(Color.YELLOW);
            holder.BTNcomplete.setVisibility(View.VISIBLE);
        }else if(Sresponse.get(position).equals("Rejected")){
            holder.TVresponse.setTextColor(Color.RED);
        }else if(Sresponse.get(position).equals("Completed")){
            holder.TVresponse.setTextColor(Color.GREEN);
        }
        user_id=auth.getInstance().getUid();
        repref=FirebaseDatabase.getInstance().getReference().child("SentReq").child(user_id);
        reqref=FirebaseDatabase.getInstance().getReference().child("Requests");

        holder.BTNcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repref.child(Stime.get(position)).child("Response").setValue("Completed");
                reqref.child(UID.get(position)).child(Stime.get(position)).child("Response").setValue("Completed");
                holder.BTNcomplete.setVisibility(View.INVISIBLE);
                holder.TVresponse.setText("Completed");
                holder.TVresponse.setTextColor(Color.GREEN);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Stime.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVrequest,TVresponse;
        Button BTNcomplete;

        public classViewHolder(View itemView) {
            super(itemView);

            TVrequest =itemView.findViewById(R.id.tvsentreq);
            TVresponse =itemView.findViewById(R.id.tvsentresponse);
            BTNcomplete =itemView.findViewById(R.id.btncomplete);



        }
    }
}
