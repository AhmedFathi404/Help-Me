package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.helpme.model.Notifi_Item;
import com.example.helpme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class Frag1_Main extends Fragment {
    Button action;
    FirebaseDatabase db;

    FirebaseUser firebaseUser;
    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag1__main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db=FirebaseDatabase.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        // emergency button
        action=view.findViewById(R.id.btn_action);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isActive = false;
                        for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                            Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                            if (notifi_item.user.getId().equals(FirebaseAuth.getInstance().getUid()) && notifi_item.isActive) {
                                Intent intent =new Intent(getActivity(), EmergencyActivity.class);
                                intent.putExtra("id",FirebaseAuth.getInstance().getUid());
                                getActivity().startActivity(intent);
                                isActive = true;

                            }
                        }
                        if (!isActive) {
                            User user = snapshot.child("Users").child(firebaseUser.getUid()).getValue(User.class);

                            DatabaseReference id = db.getReference("Notification").push();

                            Notifi_Item notifi_item = new Notifi_Item(true, id.getKey(), user, Calendar.getInstance().getTime(), R.drawable.ic_baseline_person_pin_circle_notctive);
                            id.setValue(notifi_item);

                            Intent intent =new Intent(getActivity(), EmergencyActivity.class);
                            intent.putExtra("id",notifi_item.user.getId());
                            getActivity().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        });
    }
}