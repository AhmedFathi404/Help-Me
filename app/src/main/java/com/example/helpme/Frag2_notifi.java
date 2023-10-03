package com.example.helpme;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.helpme.controlers.Adapter;
import com.example.helpme.model.Notifi_Item;
import com.example.helpme.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Frag2_notifi extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewStart(view);
    }

    public void onViewStart(View view) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Notifi_Item> notifi_items=new ArrayList<>();
                for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                    Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                    DataSnapshot userLocation = snapshot.child("location").child(FirebaseAuth.getInstance().getUid());
                    DataSnapshot notificationLocation = snapshot.child("location").child(notifi_item.user.getId());
                    User user = snapshot.child("Users").child(notifi_item.user.getId()).getValue(User.class);

                    if(userLocation.getValue() != null){
                        double lat = (double) userLocation.child("latitude").getValue();
                        double lng = (double) userLocation.child("longitude").getValue();
                        double lat2 = (double) notificationLocation.child("latitude").getValue();
                        double lng2 = (double) notificationLocation.child("longitude").getValue();

                        if(!Objects.equals(user.getId(), FirebaseAuth.getInstance().getUid())
                                && (Utils.distance(lat,lng,lat2,lng2)<1000
//                                || user.getContacts().containsValue(FirebaseAuth.getInstance().getUid())
                            )
                        ) {
                            notifi_items.add(notifi_item);
                        }
                    }
                }

                Adapter adapter=new Adapter(notifi_items,getActivity());
                ListView listView =view.findViewById(R.id.notification_list);
                Collections.reverse(notifi_items);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag2_notifi, container, false);
    }
}