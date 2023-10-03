package com.example.helpme.controlers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.helpme.EmergencyActivity;
import com.example.helpme.MainActivity;
import com.example.helpme.R;
import com.example.helpme.model.Notifi_Item;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter extends BaseAdapter {
    ArrayList <Notifi_Item> notifiItems;
    Context context;


    public Adapter(ArrayList<Notifi_Item> notifiItems, Context context) {
        this.notifiItems = notifiItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notifiItems.size();
    }

    @Override
    public Object getItem(int position) {

        return notifiItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notifi_Item notifi_item = notifiItems.get(position);

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE \ndd/MM/yyyy", Locale.UK);
        SimpleDateFormat timeFormat = new SimpleDateFormat(" hh:mm a", Locale.UK);

        ImageView user_image = listItemView.findViewById(R.id.userImage);
        TextView user_name = listItemView.findViewById(R.id.userName);
        TextView date = listItemView.findViewById(R.id.date);
        TextView time = listItemView.findViewById(R.id.time);

        user_name.setText(notifi_item.user.getUsername());
        date.setText(dateFormat.format(notifi_item.currentDate));
        time.setText(timeFormat.format(notifi_item.currentDate));

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                    Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                    if (notifi_item.isActive ) {

                        user_image.setImageResource(R.drawable.ic_baseline_person_pin_circle_24);


                    }else { user_image.setImageResource(R.drawable.ic_baseline_person_pin_circle_notctive);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmergencyActivity.class);
                intent.putExtra("id", notifi_item.user.getId());
                context.startActivity(intent);

            }
        });
        return listItemView;


    }
}