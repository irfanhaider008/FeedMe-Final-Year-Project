package feed_me.project.com.feedme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PhoneAuth extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;
    FirebaseAuth mAuth;
    Button BTNhelp;
    TextView TVhelp;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        Window window=getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        mAuth = FirebaseAuth.getInstance();

        spinner = findViewById (R.id.spinnerCountries);
        BTNhelp = findViewById (R.id.btnhelp);
        spinner.setAdapter (new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Country.countryNames));
        editText = findViewById (R.id.editTextPhone);

        BTNhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(PhoneAuth.this, android.R.style.Theme_DeviceDefault);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.helpdialogue);

                TVhelp = dialog.findViewById (R.id.tvhelp);
                TVhelp.setText("INTRODUCTION:\n\n" +
                        "In this world, enough food is produced to feed the entire population but because of food waste an estimated one third of all food produced is wasted every year. In our country (Pakistan) 40 percent of food is wasted and approximately 36 million tons of food wasted in every year. Food wastage and poverty is a global problem, poverty occurs due to food loss and food wastage. On the global hunger index out of 118 developing countries, Pakistan is ranked at 107. \n\n" +
                        "This application is a platform where user registers with their mobile number. If any User wants to donate surplus food, he can donate food by staying at home; he will simply post food detail in application. Any worker of that specific area will collect the food. All the members in this application work voluntarily.\n\n" +
                        "INSTALL AND MANAGE THE APP:\n\n" +
                        "\tYou can install the app from the play Store. Once you've installed it, you can keep track of what you've installed and remove the app if you want to.\n\n" +
                        "SIGN UP METHOD:\n\n" +
                        "•\tInstall the application first.\n" +
                        "•\tRequired a phone number for registration.\n" +
                        "•\tAfter putting phone number, required a verification code which would be sent on that phone number.\n" +
                        "•\tAfter putting verification code, have to choose the type (Worker or Donor).\n" +
                        "•\tAnd then, required some details to put like (username, email, address etc).\n\n" +
                        "LOGIN METHOD:\n\n" +
                        "•\tInstall the application first.\n" +
                        "•\tRequired a phone number for registration.\n" +
                        "•\tAfter putting phone number, required a verification code which would be sent on that phone number.\n" +
                        " \n\n" +
                        "STEP TO USE THE APPLICATION:\n\n" +
                        "DONOR CAN:\n\n" +
                        "•\tEdit profile.\n" +
                        "•\tView new feeds.\n" +
                        "•\tRate other posts.\n" +
                        "•\tDonate food.\n" +
                        "•\tAccept or reject worker’s request on his/her post.\n" +
                        "•\tRate workers.\n" +
                        "•\tDelete own post.\n\n" +
                        "WORKER CAN:\n\n" +
                        "•\tWorks as donor.\n" +
                        "•\tSend request on donor’s post.\n" +
                        "•\tCollect food from donor and give the feedback to the donor.\n" +
                        "On the basis of Karma system donor and worker can become moderator if their points accede the limit.\n\n" +
                        "MODERATOR CAN:\n\n" +
                        "•\tWorks as donor or worker.\n" +
                        "•\tDelete irrelevant posts.\n\n" +
                        "KARMA SYSTEM:\n\n" +
                        "•\tUp vote the post / worker’s performance (15 points).\n" +
                        "•\tDown vote the post / worker’s performance (-5 points).\n" +
                        "•\tDonor / worker will become moderator on (50 points).\n" +
                        "•\tDonor‘s / worker’s account will be suspended for 7 days on (-25 points).\n" +
                        "\n" +
                        "\n" +
                        "Our vision is to\n\n “SAVE FOOD & MAKE THE PAKISTAN HUNGER FREE”\n");
                dialog.show();
            }
        });
        findViewById (R.id.buttonContinue).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String code = Country.countryAreaCodes[spinner.getSelectedItemPosition ()];
                String number=editText.getText ().toString ().trim ();
                if (number.isEmpty ()|| number.length ()< 10){
                    editText.setError (" Valid Number is Required ");
                    editText.requestFocus ();
                    return;
                }
                String phoneNumber ="+" + code+number;

                Intent intent = new Intent(PhoneAuth.this,Verify.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart ();
        if (FirebaseAuth.getInstance ().getCurrentUser () != null){
            Intent intent = new Intent(this, Choose.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
