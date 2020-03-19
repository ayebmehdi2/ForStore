package com.mehdi.boutique;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mehdi.boutique.databinding.HomeBinding;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements AdapterGifts.click {

    HomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String uid;
    private String wilaya;
    private AdapterGifts giftsAdapter;
    ArrayList<GIFT> gifts = new ArrayList<>();
    BoutiqueUser boutiqueUser;


    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.home);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", null);
        wilaya = preferences.getString("wilaya", null);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        Log.e("HomeActivity", "Wilaya : " + wilaya);
        Log.e("HomeActivity", "Wilaya : " + uid);

        if (wilaya == null) {
            startActivity(new Intent(this, CompleteCompte.class));
        }

        reference.child("BOUTIQUES").child(wilaya).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                BoutiqueUser user = dataSnapshot.getValue(BoutiqueUser.class);
                if (user == null) return;
                boutiqueUser = user;
                if (user.getPhoto() != null){
                    try {
                        Glide.with(HomeActivity.this).load(user.getPhoto()).into(binding.photo);
                    }catch (Exception e){ }
                }else{
                    binding.photo.setImageResource(R.drawable.ic_account);
                }

                binding.name.setText(user.getName());

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) { }
        });


        giftsAdapter = new AdapterGifts(this);
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        binding.rec.setHasFixedSize(true);
        binding.rec.setAdapter(giftsAdapter);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                giftsAdapter.swapAdapter(null);

                if (!(dataSnapshot.getChildrenCount() > 0)) {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.rec.setVisibility(View.GONE);
                    return;
                }

                binding.rec.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.GONE);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GIFT g = snapshot.getValue(GIFT.class);
                    gifts.add(g);
                }
                giftsAdapter.swapAdapter(gifts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child("BOUTIQUES").child(wilaya).child(uid).child("gifts").addValueEventListener(valueEventListener);

        binding.bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, profile.class));
            }
        });

    }

    private String currentGift = "";

    @Override
    public void clic(GIFT g) {
        currentGift = g.getQr_code();
        goScanCode();
    }

    public void goScanCode(){
        IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        try {
            final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                String code = result.getContents();
                if (code == null) return;
                binding.l.setVisibility(View.VISIBLE);
                if (currentGift.equals(code)){

                    if (boutiqueUser != null){
                        reference.child("BOUTIQUES").child(boutiqueUser.getWilaya()).child(boutiqueUser.getUid()).child("gifts").child(currentGift).removeValue();
                    }
                    reference.child("USERS").child(uid).child("myGifts").child(currentGift).child("status").setValue("Recu");

                    binding.imgDes.setImageResource(R.drawable.ic_correct);
                    binding.des.setText(" Right person");

                }else {
                    binding.imgDes.setImageResource(R.drawable.ic_remove);
                    binding.des.setText(" Wrong person");
                }

                binding.ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.l.setVisibility(View.GONE);
                    }
                });


            } else {
                super.onActivityResult(requestCode, resultCode, data);
            } } catch (Exception e) { super.onActivityResult(requestCode, resultCode, data);e.printStackTrace(); }

    }

}
