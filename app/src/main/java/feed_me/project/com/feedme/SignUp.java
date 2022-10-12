package feed_me.project.com.feedme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    EditText name,email,House,Street,CNIC;
    FirebaseAuth auth;
    TextView Heading;
    FirebaseStorage storage;
    StorageReference storageRef;
    Button BTNsignup,BTNselectprofile;
    CircleImageView IMGprofile;
    DatabaseReference ref,refw;
    String type,userid,FLAG;
    Spinner PO,City;
    private static final int RESULT_LOAD_IMG = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        if(intent!=null) {
            type = intent.getStringExtra("Type");
            FLAG=intent.getStringExtra("FLAG");
        }
        userid=auth.getInstance().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Register");


        name = findViewById(R.id.signup_name);
        Heading = findViewById(R.id.tv_signup_text);
        email = findViewById(R.id.signup_email);
        House = findViewById(R.id.signup_house);
        Street = findViewById(R.id.signup_street);
        PO = findViewById(R.id.signup_po);
        City = findViewById(R.id.signup_city);

        CNIC = findViewById(R.id.signup_cnic);
        BTNsignup = findViewById(R.id.btn_signup_register);
        BTNselectprofile = findViewById(R.id.btnselectprofile);
        IMGprofile = findViewById(R.id.imageprofile);

        if(FLAG.equals("EDIT")){
            Heading.setVisibility(View.INVISIBLE);
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
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(type.equals("Worker")){
                        name.setText(dataSnapshot.child("Worker").child(userid).child("Name").getValue().toString());
                        email.setText(dataSnapshot.child("Worker").child(userid).child("Email").getValue().toString());
                        CNIC.setText(dataSnapshot.child("Worker").child(userid).child("CNIC").getValue().toString());
//                        address.setText(dataSnapshot.child("Worker").child(userid).child("Address").getValue().toString());
                    }else{
                        name.setText(dataSnapshot.child("User").child(userid).child("Name").getValue().toString());
                        email.setText(dataSnapshot.child("User").child(userid).child("Email").getValue().toString());
                        CNIC.setText(dataSnapshot.child("User").child(userid).child("CNIC").getValue().toString());
//                        address.setText(dataSnapshot.child("User").child(userid).child("Address").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        BTNselectprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMG);

            }
        });

        BTNsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),IMGprofile.getBackground().toString(),Toast.LENGTH_LONG).show();

                boolean NIC = Pattern.matches("\\d{5}-\\d{7}-\\d{1}$", CNIC.getText().toString());

                if(name.getText().toString().isEmpty()){
                    name.setError("Please Enter Name");
                } else if(!email.getText().toString().contains("@") || !email.getText().toString().contains(".com")){
                    email.setError("Please Enter Valid Email");
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
                }else if(!NIC){
                    CNIC.setError("Please Enter Valid CNIC");
                }
                else if(IMGprofile.getDrawable().equals(null)){
                    Toast.makeText(getApplicationContext(),"PLease Provide Image",Toast.LENGTH_LONG).show();
                }
                else{
                    String address="House #: "+House.getText().toString()+" Street #: "+Street.getText().toString()+" "+
                                        PO.getSelectedItem().toString()+" City: "+City.getSelectedItem().toString();
                    if(type.equals("Worker")){
                        Map details = new HashMap();
                        details.put("Name", name.getText().toString());
                        details.put("Email", email.getText().toString());
                        details.put("CNIC", CNIC.getText().toString());
                        details.put("Address", address);
                        details.put("Type", "Worker");
                        ref.child("Worker").child(userid).setValue(details);

                        Intent intent=new Intent(SignUp.this,Worker.class);
                        startActivity(intent);
                    }
                    else if(type.equals("User")){
                        Map details = new HashMap();
                        details.put("Name", name.getText().toString());
                        details.put("Email", email.getText().toString());
                        details.put("CNIC", CNIC.getText().toString());
                        details.put("Address", address);
                        ref.child("User").child(userid).setValue(details);

                        Intent intent=new Intent(SignUp.this,User.class);
                        startActivity(intent);

                    }
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference mountainsRef = storageRef.child("images/" + userid + ".jpg");
                    IMGprofile.setDrawingCacheEnabled(true);
                    IMGprofile.buildDrawingCache();
                    Bitmap bitmap = IMGprofile.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Something went wrong with upload", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Succesfuly updated", Toast.LENGTH_LONG).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });


                }
            }
        });
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                IMGprofile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
