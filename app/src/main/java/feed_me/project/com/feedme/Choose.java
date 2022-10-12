package feed_me.project.com.feedme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Choose extends AppCompatActivity {

    FirebaseAuth auth;
    String userid;
    DatabaseReference ref;
    ProgressDialog Progressbar;
    Button btnworker,btnuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btnuser=findViewById(R.id.btn_User);
        btnworker=findViewById(R.id.btn_Worker);

        Progressbar = new ProgressDialog(this);
        Progressbar.setMessage("Please Wait..!!");
        Progressbar.show();
        Progressbar.setCanceledOnTouchOutside(false);



        userid= auth.getInstance().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Register");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("User").hasChild(userid)){
                    Intent intent = new Intent(Choose.this,User.class);
                    startActivity(intent);
                    finish();
                }
                else if(dataSnapshot.child("Worker").hasChild(userid)){
                    Intent intent = new Intent(Choose.this,Worker.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Progressbar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnworker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choose.this,SignUp.class);
                intent.putExtra("Type","Worker");
                intent.putExtra("FLAG","Worker");
                startActivity(intent);
                finish();
            }
        });
        btnuser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choose.this,SignUp.class);
                intent.putExtra("Type","User");
                intent.putExtra("FLAG","User");
                startActivity(intent);
                finish();
            }
        });

    }
}
