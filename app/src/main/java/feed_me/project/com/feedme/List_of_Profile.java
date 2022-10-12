package feed_me.project.com.feedme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by FaRoO on 04-Aug-19.
 */

public class List_of_Profile extends RecyclerView.Adapter<List_of_Profile.classViewHolder> {
    ArrayList<String> Posts=new ArrayList<>();
    ArrayList<String> PostTime=new ArrayList<>();
    ArrayList<String> Ptitle=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> PeopleUP=new ArrayList<>();
    ArrayList<String> PeopleDOWN=new ArrayList<>();

    Context context;
    String Name,userid,Puserid,Type;
    FirebaseStorage storage;
    FirebaseAuth auth;
    StorageReference storageRef;
    DatabaseReference refkarma,ref;

    public List_of_Profile(Context ctx,ArrayList<String> ptitle, ArrayList<String> posts, ArrayList<String> postKey,
                           String name,String userid,String type, ArrayList<String> postTime) {

        this.PostKey = postKey;
        this.Posts = posts;
        this.context=ctx;
        this.Name=name;
        this.Ptitle=ptitle;
        this.Puserid=userid;
        this.Type=type;
        this.PostTime=postTime;
    }
    @NonNull
    @Override
    public List_of_Profile.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_profile, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Profile.classViewHolder holder, final int position) {
        holder.TVname.setText(Name);
        holder.TVptitle.setText(Ptitle.get(position));
        holder.TVpost.setText(Posts.get(position)+"\n\n"+PostTime.get(position));

        userid=auth.getInstance().getUid();
        if(userid.equals(Puserid)){
            holder.BTNdown.setVisibility(View.INVISIBLE);
            holder.BTNup.setVisibility(View.INVISIBLE);
        }
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://feed-5098b.appspot.com/images/").child(userid+".jpg");
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

        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
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
                    if (PeopleUP.contains(userid)) {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_up);
                        holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    } else if (PeopleDOWN.contains(userid)) {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sort_down);
                        holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    }
                }
                holder.BTNup.setText(String.valueOf(PeopleUP.size()));
                holder.BTNdown.setText(String.valueOf(PeopleDOWN.size()));

                if (PeopleUP.size() > PeopleDOWN.size()) {
                    holder.BTNup.setTextColor(ContextCompat.getColor(context,R.color.Green));
                    holder.BTNdown.setTextColor(ContextCompat.getColor(context,R.color.cardview_dark_background));
                } else if (PeopleDOWN.size() > PeopleUP.size()) {
                    holder.BTNdown.setTextColor(ContextCompat.getColor(context,R.color.Red));
                    holder.BTNup.setTextColor(ContextCompat.getColor(context,R.color.cardview_dark_background));
                } else if (PeopleDOWN.size() > 0 || PeopleUP.size() > 0) {
                    {
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
                            Toast.makeText(context,"aya",Toast.LENGTH_LONG).show();
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
        if(userid.equals(Puserid)|| Type.equals("DModerator")||Type.equals("WModerator")){
            holder.BTNdelete.setVisibility(View.VISIBLE);

            holder.BTNdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog Deletedialogue =new AlertDialog.Builder(context)
                            //set message, title, and icon
                            .setTitle("Delete")
                            .setMessage("Are you Sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FirebaseDatabase.getInstance().getReference().child("Post").child(Puserid).child(PostKey.get(position)).removeValue();
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
    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVpost,TVname,TVptitle;
        CircleImageView PProfile;
        Button BTNup,BTNdown,BTNdelete;

        public classViewHolder(View itemView) {
            super(itemView);
            TVname =itemView.findViewById(R.id.tvppname);
            TVptitle =itemView.findViewById(R.id.tvptitle);
            TVpost = itemView.findViewById(R.id.tvpost);
            PProfile = itemView.findViewById(R.id.ppprofile);
            BTNdown = itemView.findViewById(R.id.btndown);
            BTNup = itemView.findViewById(R.id.btnup);
            BTNdelete = itemView.findViewById(R.id.btndelete);
        }
    }
}
