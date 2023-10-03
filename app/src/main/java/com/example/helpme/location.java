package com.example.helpme;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.helpme.model.Notifi_Item;
import com.example.helpme.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;


public class location extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        requestLocationUpdates();

    }

//Create the persistent notification//

    private void buildNotification() {
        String stop = "stop";
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(stopReceiver, new IntentFilter(stop));
        registerReceiver(stopReceiver2,filter);
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

// Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.appicon);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                        Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                        if (notifi_item.user.getId().equals(FirebaseAuth.getInstance().getUid()) && notifi_item.isActive) {
                            notifi_item.isActive=false;
                          FirebaseDatabase.getInstance().getReference("Notification").child(notifi_item.id).setValue(notifi_item);

                        }
                    }

                    unregisterReceiver(stopReceiver);
                    Intent intent1=new Intent(context,MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stopSelf();
                    context.startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

    protected BroadcastReceiver stopReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean remove=true;
                    for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                        Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                        if (notifi_item.user.getId().equals(FirebaseAuth.getInstance().getUid()) ) {
                         remove=false;

                        }
                    }
                    if(remove){
                        unregisterReceiver(stopReceiver);
                        unregisterReceiver(stopReceiver2);
                        stopSelf();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

//Initiate the request to track the device's location//

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(10000);


//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//


        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//
                    String userId=FirebaseAuth.getInstance().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(userId);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {


//Save the location data to the database//

                        ref.setValue(location);


                    }
                }
            }, null);
        }else
        {
            Toast.makeText(location.this,"Open Your Location",Toast.LENGTH_SHORT).show();

        }
    }
}