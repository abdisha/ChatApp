package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.net.Credentials;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.toshi.aerke.Utilitis.UserState;

import java.util.concurrent.TimeUnit;

public class CreateAcount extends AppCompatActivity {
       private View verifyView,progressbar,sendCodeView;
       private TextView sentTime,reSend;
       private EditText phone,Code;
       private  CountryCodePicker countryCodePicker;
       private PhoneAuthProvider.OnVerificationStateChangedCallbacks changedCallbacks;
       private  PhoneAuthProvider.ForceResendingToken reSendToken;
       Button send,verify,useEmail;
       private String phoneNumber;
       private FirebaseAuth firebaseAuth;
       private String userId;
    private String varificationCode;
    UserState userState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acount);
        InitializeController();
        firebaseAuth = FirebaseAuth.getInstance();
        countryCodePicker.detectSIMCountry(true);
        useEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAcount.this,Login.class));
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(validetInput()){
                   showProgressBar(true,false);

                   PhoneAuthProvider.getInstance().verifyPhoneNumber(
                       phoneNumber,
                       60,
                           TimeUnit.SECONDS,
                           CreateAcount.this,
                           changedCallbacks,
                           reSendToken
                   );
               }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Code.getText().toString().isEmpty()){
                    VerifyOnclick(varificationCode,Code.getText().toString());
                }
            }
        });
    }

    private boolean validetInput() {
         phoneNumber = phone.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            phone.setError("Provide your phone!");
            return false;
        }if(phoneNumber.length()<9){
            phone.setError("Invalid phone! ");
            return false;
        }
        phoneNumber = countryCodePicker.getFullNumberWithPlus();
         return true;
    }

    private void InitializeController() {
        verifyView = (View)findViewById(R.id.codeVerifyView);
        sendCodeView =(View)findViewById(R.id.phoneAuthView);
        progressbar =(View)findViewById(R.id.progress);
        sentTime =(TextView)findViewById(R.id.sentTime);
        reSend =(TextView)findViewById(R.id.resend);
        phone =(EditText) findViewById(R.id.phone);
        Code = (EditText)findViewById(R.id.codeVerify);
        send =(Button)findViewById(R.id.sendCode);
        verify=(Button)findViewById(R.id.btnVerify);
        useEmail =(Button)findViewById(R.id.txtuserEmail);
        countryCodePicker =(CountryCodePicker)findViewById(R.id.countryCodePicker);
        sendCodeView.setVisibility(View.VISIBLE);
        verifyView.setVisibility(View.GONE);
        countryCodePicker.getSelectedCountryCodeWithPlus();
        countryCodePicker.registerCarrierNumberEditText(phone);
          changedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
              @Override
              public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                 showProgressBar(true,false);

                 Code.setText(phoneAuthCredential.getSmsCode());
                  WaitingTime(false);
                         if(!Code.getText().toString().isEmpty())
                          signInWithCredential(phoneAuthCredential);

              }

              @Override
              public void onVerificationFailed(FirebaseException e) {
                  Snackbar.make(phone.getRootView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                  WaitingTime(false);
                  showProgressBar(false,false);


              }

              @Override
              public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                  super.onCodeSent(s, forceResendingToken);
                  reSendToken = forceResendingToken;
                   varificationCode = s;
                  showProgressBar(false,true);
                  WaitingTime(true);
              }
          };
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        WaitingTime(false);
        showProgressBar(true,false);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(CreateAcount.this,Profile.class));
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                WaitingTime(false);
                showProgressBar(false,false);

                Snackbar.make(Code.getRootView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void showProgressBar(boolean show,boolean codeSent){
        if(!(codeSent) && show){
            useEmail.setVisibility(View.INVISIBLE);
            verifyView.setVisibility(View.INVISIBLE);

            sendCodeView.setVisibility(View.INVISIBLE);
            progressbar.setVisibility(View.VISIBLE);
        }else if(codeSent && !(show)){
            useEmail.setVisibility(View.INVISIBLE);
            verifyView.setVisibility(View.VISIBLE);

            sendCodeView.setVisibility(View.INVISIBLE);
            progressbar.setVisibility(View.GONE);
        }else if(!(codeSent) && !(show)){
            useEmail.setVisibility(View.VISIBLE);
            verifyView.setVisibility(View.INVISIBLE);

            sendCodeView.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()!=null){
            userId = firebaseAuth.getCurrentUser().getUid();
            userState = UserState.getInstance(userId);
            userState.setUserState(true);
            startActivity(new Intent(this,Home.class));

        }
    }
    private void VerifyOnclick(String VerificationCode,String Code){
        
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(VerificationCode,Code);
        if(!phoneAuthCredential.equals(null))
        signInWithCredential(phoneAuthCredential);
        
    }
    private  void WaitingTime(boolean startCount){

        final CountDownTimer count = new CountDownTimer(90000,1) {
            @Override
            public void onTick(final long millisUntilFinished) {
               new Handler().post(new Runnable() {
                   @Override
                   public void run() {
                         sentTime.setText("Resend Code in "+TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                   }
               });
            }

            @Override
            public void onFinish() {
               ResendCode(reSendToken);
            }
        };
        if(startCount){
          count.start();
        }else {
           count.cancel();
        }
    }

    private void ResendCode(PhoneAuthProvider.ForceResendingToken reSendToken) {
        if(reSendToken!=null){
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth.getCurrentUser()!=null){
            userState.setUserState(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(firebaseAuth.getCurrentUser()!=null){
            userState.setUserState(false);
        }
    }
}
