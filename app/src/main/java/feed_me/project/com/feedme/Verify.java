package feed_me.project.com.feedme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import feed_me.project.com.feedme.R;

import java.util.concurrent.TimeUnit;

public class Verify extends AppCompatActivity {

    private String verficationId;
    FirebaseAuth mAuth;
    private ProgressBar progressBar;
    EditText editText;
    String phonenumber;
    Button SignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth=FirebaseAuth.getInstance ();
        progressBar =findViewById (R.id.progressbar);
        editText=findViewById (R.id.editTextCode);
        SignIn=findViewById(R.id.buttonSignIn);

        Intent intent = getIntent();
        phonenumber = intent.getStringExtra("phonenumber");


        sendVerficationcode (phonenumber);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code =editText.getText ().toString ().trim ();
                if (code.isEmpty ()||code.length ()<6){
                    editText.setError ("Enter your Verfication code");
                    editText.requestFocus ();
                }
                else{
                    verifycode (code);
                }
            }
        });
    }
    private void verifycode(String code)
    {
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential (verficationId ,code);
        SignInwithCredential(credential);
        mAuth.signInWithCredential (credential).addOnCompleteListener (new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful ()) {
                    Intent intent = new Intent(Verify.this, Choose.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Toast.makeText (Verify.this,task.getException ().getMessage (),Toast.LENGTH_SHORT).show ();

                }


            }
        });

    }

    private void SignInwithCredential(PhoneAuthCredential credential) {

    }


    private void sendVerficationcode(String number){
        progressBar.setVisibility (View.VISIBLE);
        PhoneAuthProvider.getInstance ().verifyPhoneNumber (
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mcallBack
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mcallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent (s, forceResendingToken);
            verficationId=s;
            progressBar.setVisibility (View.INVISIBLE);

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode ();
            if (code !=null){
                editText.setText (code);
                verifycode (code);

            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText (Verify.this,e.getMessage (),Toast.LENGTH_SHORT).show ();
        }
    };
}

