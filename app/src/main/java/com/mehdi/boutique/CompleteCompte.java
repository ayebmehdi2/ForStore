package com.mehdi.boutique;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehdi.boutique.databinding.CompleteCompteActivityBinding;


public class CompleteCompte extends AppCompatActivity {

    CompleteCompteActivityBinding binding;
    public static FirebaseUser user = null;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.complete_compte_activity);

        if (user == null) {
         startActivity(new Intent(this, ActivityAuth.class));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = preferences.getString("uid", null);


        if (uid == null) return;


        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wilaya = binding.spinner.getSelectedItem().toString();
                if (wilaya.equals("Choisissez etat")){
                    Toast.makeText(CompleteCompte.this, "You must choose adress", Toast.LENGTH_SHORT).show();
                    return;
                }

                String des = binding.edt.getText().toString();
                if (!(des.length() > 0)){
                    Toast.makeText(CompleteCompte.this, "You must describe location", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor p = PreferenceManager.getDefaultSharedPreferences(CompleteCompte.this).edit();
                p.putString("wilaya", wilaya);
                p.apply();

                if (user.getPhotoUrl() == null){
                    reference.child("BOUTIQUES").child(wilaya).child(uid).setValue(new BoutiqueUser(uid, user.getDisplayName(), "", wilaya, des));
                }else {
                    reference.child("BOUTIQUES").child(wilaya).child(uid).setValue(new BoutiqueUser(uid, user.getDisplayName(),  user.getPhotoUrl().toString(), wilaya, des));
                }


                Intent intent = new Intent(CompleteCompte.this,  HomeActivity.class);
                startActivity(intent);
            }
        });



    }


}
