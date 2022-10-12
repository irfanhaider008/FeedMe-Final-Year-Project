package feed_me.project.com.feedme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

/**
 * Created by FaRoO on 04-Aug-19.
 */

public class List_of_Post extends RecyclerView.Adapter<List_of_Post.classViewHolder> {
    ArrayList<String> Posts=new ArrayList<>();
    ArrayList<String> PostTime=new ArrayList<>();
    ArrayList<String> Ptitle=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> PeopleUP=new ArrayList<>();
    ArrayList<String> PeopleDOWN=new ArrayList<>();
    ArrayList<String> PostUserID=new ArrayList<>();


    Context context;
    String userid,Time,Type;
    FirebaseStorage storage;
    FirebaseAuth auth;
    StorageReference storageRef;
    DatabaseReference refkarma,ref,refrequest,refsentreq;
    EditText ETwname,ETwhouse,ETwstreet,ETwaproxtime,ETcontact;
    Button BTNreqdone;
    Spinner SPpo,SPcity;

    public List_of_Post(Context ctx,ArrayList<String> ptitle, ArrayList<String> posts, ArrayList<String> postKey,
                        ArrayList<String> postuserid,ArrayList<String> postTime){
        this.PostKey = postKey;
        this.Posts = posts;
        this.context=ctx;
        this.PostUserID=postuserid;
        this.Ptitle=ptitle;
        this.PostTime=postTime;
    }
    @NonNull
    @Override
    public List_of_Post.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_post, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Post.classViewHolder holder, final int position) {
        userid=auth.getInstance().getUid();
        if(userid.equals(PostUserID.get(position))){
            holder.BTNdown.setVisibility(View.INVISIBLE);
            holder.BTNup.setVisibility(View.INVISIBLE);
            holder.BTNrequest.setVisibility(View.INVISIBLE);
        }
        FirebaseDatabase.getInstance().getReference().child("Register").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Worker").child(PostUserID.get(position)).exists()){
                    holder.TVname.setText(dataSnapshot.child("Worker").child(PostUserID.get(position)).child("Name").getValue().toString());
                    holder.TVname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Worker_main fragment= new Worker_main();
                            Bundle From=new Bundle();
                            From.putString("From","Home");
                            From.putString("ID",PostUserID.get(position));
                            fragment.setArguments(From);
                            android.support.v4.app.FragmentManager manager=((AppCompatActivity)context).getSupportFragmentManager();
                            if(dataSnapshot.child("Worker").child(userid).exists()){
                                manager.beginTransaction().replace(R.id.workercontainer,fragment).addToBackStack(null).commit();
                            }else{
                                manager.beginTransaction().replace(R.id.usercontainer,fragment).addToBackStack(null).commit();
                            }
                        }
                    });
                }else{
                    holder.TVname.setText(dataSnapshot.child("User").child(PostUserID.get(position)).child("Name").getValue().toString());
                    holder.TVname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            User_main fragment= new User_main();
                            Bundle From=new Bundle();
                            From.putString("From","Home");
                            From.putString("ID",PostUserID.get(position));
                            fragment.setArguments(From);
                            android.support.v4.app.FragmentManager manager=((AppCompatActivity)context).getSupportFragmentManager();
                            if(dataSnapshot.child("User").child(userid).exists()){
                                manager.beginTransaction().replace(R.id.usercontainer,fragment).addToBackStack(null).commit();
                            }else{
                                manager.beginTransaction().replace(R.id.workercontainer,fragment).addToBackStack(null).commit();
                            }                        }
                    });
                }

                if(dataSnapshot.child("User").child(userid).exists()){
                    holder.BTNrequest.setVisibility(View.INVISIBLE);
                }else if(dataSnapshot.child("Worker").child(userid).exists()){
                    FirebaseDatabase.getInstance().getReference().child("Type").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(userid).exists()){
                                Type=dataSnapshot.child(userid).getValue().toString();
                                if (Type.equals("Donor")) {
                                    holder.BTNrequest.setVisibility(View.INVISIBLE);
                                }
                                if(Type.equals("DModerator")||Type.equals("WModerator")){
                                    holder.BTNdelete.setVisibility(View.VISIBLE);
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
        FirebaseDatabase.getInstance().getReference().child("Post").child(PostUserID.get(position)).child(PostKey.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Status").exists()){
                    String status=dataSnapshot.child("Status").getValue().toString();
                    if(status.equals("Picked")){
                        holder.BTNrequest.setText("Picked");
                        holder.BTNrequest.setEnabled(false);
                    }else{
                        holder.BTNrequest.setText("Request");
                        holder.BTNrequest.setEnabled(true);
                    }
                }else{
                    holder.BTNrequest.setText("Request");
                    holder.BTNrequest.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.TVptitle.setText(Ptitle.get(position));
        holder.TVpost.setText(Posts.get(position)+"\n\n"+PostTime.get(position));

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://feed-5098b.appspot.com/images/").child(PostUserID.get(position)+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.PProfile.setImageBitmap(bitmap);
            }
        });

        ref= FirebaseDatabase.getInstance().getReference().child("Karma").child(PostKey.get(position));
        refkarma= FirebaseDatabase.getInstance().getReference().child("Karma");
        refrequest= FirebaseDatabase.getInstance().getReference().child("Requests");
        refsentreq= FirebaseDatabase.getInstance().getReference().child("SentReq");


        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PeopleDOWN.clear();
                PeopleUP.clear();
                if (dataSnapshot.hasChildren()) {
                    for(DataSnapshot child:dataSnapshot.getChildren()){
                        if(child.getValue().toString().equals("up")){
                            PeopleUP.add(child.getKey());
                        }else{
                            PeopleDOWN.add(child.getKey());
                        }
                    }
                    if(PeopleUP.contains(userid)){
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_up);
                        holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    }
                    else if(PeopleDOWN.contains(userid)){
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_down);
                        holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    }
                }
                holder.BTNup.setText(String.valueOf(PeopleUP.size()));
                holder.BTNdown.setText(String.valueOf(PeopleDOWN.size()));

                if(PeopleUP.size()>PeopleDOWN.size()){
                    holder.BTNup.setTextColor(ContextCompat.getColor(context,R.color.Green));
                    holder.BTNdown.setTextColor(ContextCompat.getColor(context,R.color.cardview_dark_background));
                }
                else if(PeopleDOWN.size()>PeopleUP.size()) {
                    holder.BTNdown.setTextColor(ContextCompat.getColor(context,R.color.Red));
                    holder.BTNup.setTextColor(ContextCompat.getColor(context,R.color.cardview_dark_background));
                }else if(PeopleDOWN.size()>0||PeopleUP.size()>0){{
                    holder.BTNup.setTextColor(ContextCompat.getColor(context,R.color.Yellow));
                    holder.BTNdown.setTextColor(ContextCompat.getColor(context,R.color.Yellow));
                }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.BTNup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref= FirebaseDatabase.getInstance().getReference().child("Karma").child(PostKey.get(position));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PeopleDOWN.clear();
                        PeopleUP.clear();
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.getValue().toString().equals("up")) {
                                    PeopleUP.add(child.getKey());
                                } else {
                                    PeopleDOWN.add(child.getKey());
                                }
                            }
                        }
                        if (PeopleUP.contains(userid)) {
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).removeValue();
                        }
                        else if(PeopleDOWN.contains(userid)){
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_up);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            Drawable wrappedDrawable1 = DrawableCompat.wrap(unwrappedDrawable1);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable1, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).setValue("up");
                        }
                        else{
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_up);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).setValue("up");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        holder.BTNdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref= FirebaseDatabase.getInstance().getReference().child("Karma").child(PostKey.get(position));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PeopleDOWN.clear();
                        PeopleUP.clear();
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.getValue().toString().equals("up")) {
                                    PeopleUP.add(child.getKey());
                                } else {
                                    PeopleDOWN.add(child.getKey());
                                }
                            }
                        }
                        if (PeopleDOWN.contains(userid)) {
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).removeValue();
                        }
                        else if(PeopleUP.contains(userid)){
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_down);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            Drawable wrappedDrawable1 = DrawableCompat.wrap(unwrappedDrawable1);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable1, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).setValue("down");
                        }
                        else{
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_down);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);

                            refkarma.child(PostKey.get(position)).child(userid).setValue("down");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.BTNrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.requestdialogue);

                ETwhouse=dialog.findViewById(R.id.etwhouse);
                ETcontact=dialog.findViewById(R.id.etcontact);
                ETwstreet=dialog.findViewById(R.id.etwstreet);
                SPpo=dialog.findViewById(R.id.spwpo);
                ETwname=dialog.findViewById(R.id.etwname);
                SPcity=dialog.findViewById(R.id.spwcity);
                BTNreqdone=dialog.findViewById(R.id.btnreqdone);
                ETwaproxtime=dialog.findViewById(R.id.etwaproxtime);

                BTNreqdone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ETwname.getText().toString().isEmpty()){
                            ETwname.setError("Please enter your name");
                        }else if(ETwhouse.getText().toString().isEmpty()){
                            ETwhouse.setError("Please enter your House#");
                        }else if(ETwstreet.getText().toString().isEmpty()){
                            ETwstreet.setError("Please enter your Street#");
                        }else if(SPpo.getSelectedItem().toString().equals("Select Colony")){
                            TextView errorText = (TextView)SPpo.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            errorText.setText("Please Select Colony/PO");
                        }else if(SPcity.getSelectedItem().toString().equals("Select City")){
                            TextView errorText = (TextView)SPcity.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            errorText.setText("Please Select City");
                        }else if(ETwaproxtime.getText().toString().isEmpty()){
                            ETwaproxtime.setError("Please enter approx time to reach");
                        }else if(ETcontact.getText().toString().isEmpty()){
                            ETcontact.setError("Please Provide Contact Number");
                        } else{
                            final Date currentLocalTime = Calendar.getInstance().getTime();
                            DateFormat date=new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                            Time=date.format(currentLocalTime);

                            String Location=ETwhouse.getText().toString()+" "+ETwstreet.getText().toString()+
                                            " "+SPpo.getSelectedItem().toString()+" "+SPcity.getSelectedItem().toString();
                            Map request = new HashMap();
                            request.put("Name",ETwname.getText().toString());
                            request.put("WID",userid);
                            request.put("Location",Location);
                            request.put("AprxTime",ETwaproxtime.getText().toString());
                            request.put("Title",Ptitle.get(position));
                            request.put("Response","Sent");
                            request.put("Contact",ETcontact.getText().toString());
                            request.put("Pkey",PostKey.get(position));

                            Map sentreq = new HashMap();
                            sentreq.put("Title",Ptitle.get(position));
                            sentreq.put("Response","Sent");
                            sentreq.put("UID",PostUserID.get(position));

                            refrequest.child(PostUserID.get(position)).child(Time).setValue(request);
                            refsentreq.child(userid).child(Time).setValue(sentreq);

                            Toast.makeText(context,"Request Sent Successfully",Toast.LENGTH_LONG).show();
                            ETwhouse.setText("");
                            ETwstreet.setText("");
                            ETwname.setText("");
                            ETwaproxtime.setText("");
                            ETcontact.setText("");
                        }
                    }
                });
                dialog.show();

            }
        });
        holder.BTNdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog Deletedialogue =new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Are you Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseDatabase.getInstance().getReference().child("Post").child(PostUserID.get(position)).child(PostKey.get(position)).removeValue();
                                Toast.makeText(context,"Successfully Deleted",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                Deletedialogue.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVpost,TVname,TVptitle;
        CircleImageView PProfile;
        Button BTNup,BTNdown,BTNrequest,BTNdelete;

        public classViewHolder(View itemView) {
            super(itemView);

            TVname =itemView.findViewById(R.id.htvppname);
            TVptitle =itemView.findViewById(R.id.htvptitle);
            TVpost = itemView.findViewById(R.id.htvpost);
            PProfile = itemView.findViewById(R.id.hppprofile);
            BTNdown = itemView.findViewById(R.id.hbtndown);
            BTNup = itemView.findViewById(R.id.hbtnup);
            BTNrequest = itemView.findViewById(R.id.hbtnrequest);
            BTNdelete = itemView.findViewById(R.id.btnhdelete);
        }
    }
}
