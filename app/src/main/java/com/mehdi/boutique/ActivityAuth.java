package com.mehdi.boutique;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ActivityAuth extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;


    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String uid = preferences.getString("uid", null);
        String wilaya = preferences.getString("wilaya", null);

        if ((user != null) && (uid != null) && (wilaya != null)){
            startActivity(new Intent(ActivityAuth.this, HomeActivity.class));
        }else {
            signIn();
        }


    }

    public void signIn(){
        Log.d("SignUpActivity", "user null");
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                100);
    }


    public void setupUser(final FirebaseUser user){

            final String uid = user.getUid();
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(ActivityAuth.this).edit();
            preferences.putString("uid", uid);
            preferences.apply();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
         String wilaya = preference.getString("wilaya", null);
         Log.e("ActivityAuth", "Wilaya : " + wilaya);

        Log.e("ActivityAuth", "Wilaya : " + uid);


         if (wilaya != null){
             if (wilaya.equals("Choisissez etat")){
                 CompleteCompte.user = user;
                 startActivity(new Intent(ActivityAuth.this, CompleteCompte.class));
                 return;
             }
             reference.child("BOUTIQUIS").child(wilaya).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     if (dataSnapshot.hasChild(uid)){
                         startActivity(new Intent(ActivityAuth.this, HomeActivity.class));
                     }else {
                         CompleteCompte.user = user;
                         startActivity(new Intent(ActivityAuth.this, CompleteCompte.class));
                     }
                 }

                 @Override
                 public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) { }
             });

         }else {
             CompleteCompte.user = user;
             startActivity(new Intent(ActivityAuth.this, CompleteCompte.class));
         }



    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    startActivity(new Intent(ActivityAuth.this, ActivityAuth.class));
                    return;
                }
                setupUser(user);
            } else {
                Toast.makeText(this, "Sign Filed", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
