package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.helpme.model.Notifi_Item;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class EmergencyActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int PERMISSIONS_REQUEST = 99;
    public String callerId;

    private GoogleMap mMap;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Map");
        setSupportActionBar(toolbar);
        Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Map");


        Bundle intent = getIntent().getExtras();
        try {
            callerId = intent.getString("id");
        } catch (Exception e) {
            Log.e("error getting data", e.getMessage());
        }
        ;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

//Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


//Check whether GPS tracking is enabled//
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

//If the permission has been granted...//

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//...then start the GPS tracking service//

            startTrackerService();
        } else {

//If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

//Start the TrackerService//

    private void startTrackerService() {

        startService(new Intent(this, location.class));

//Notify the user that tracking has been enabled//

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

//Close EmergencyActivity//

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Marker> markers = new ArrayList<Marker>();
        ArrayList<Marker> helpermarkers = new ArrayList<Marker>();

        mMap.animateCamera(CameraUpdateFactory.zoomBy(14f));

        FirebaseDatabase.getInstance().getReference("location").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (callerId != null && !Objects.equals(FirebaseAuth.getInstance().getUid(), callerId)) {
                    double helperlat = (double) snapshot.child(FirebaseAuth.getInstance().getUid()).child("latitude").getValue();
                    double helperlng = (double) snapshot.child(FirebaseAuth.getInstance().getUid()).child("longitude").getValue();
                    LatLng helper = new LatLng(helperlat, helperlng);
                    for (Marker m : helpermarkers
                    ) {
                        m.setVisible(false);
                    }
                    helpermarkers.clear();
                    helpermarkers.add(mMap.addMarker(new MarkerOptions().position(helper).title("helper").icon(BitmapDescriptorFactory.defaultMarker(280))
                    ));
                }

                if(snapshot.child(callerId).getValue()!=null){
                    double lat = (double) snapshot.child(callerId).child("latitude").getValue();
                    double lng = (double) snapshot.child(callerId).child("longitude").getValue();
                    LatLng userLocation = new LatLng(lat, lng);
                    float zoomLevel = 20.0f;

                    // Add a marker
                    if(!markers.isEmpty()) {
                        Marker lastMarker= markers.get(markers.size()-1);
                        if (   Utils.distance(lat,lng,lastMarker.getPosition().latitude,lastMarker.getPosition().longitude)>5)
                        {
                            for (Marker m : markers
                            ) {
                                m.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_panorama_fish_eye_24));
                            }
                            if(markers.size()>5){
                                markers.remove(0);
                            }
                            markers.add(mMap.addMarker(new MarkerOptions().position(userLocation).title("Help Caller")));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,zoomLevel));
                        }
                    }else {
                        markers.add(mMap.addMarker(new MarkerOptions().position(userLocation).title("Help Caller")));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,zoomLevel));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.cancel:
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot i : snapshot.child("Notification").getChildren()) {
                            Notifi_Item notifi_item = i.getValue(Notifi_Item.class);
                            if (notifi_item.user.getId().equals(FirebaseAuth.getInstance().getUid()) && notifi_item.isActive) {
                                notifi_item.isActive=false;
                                FirebaseDatabase.getInstance().getReference("Notification").child(notifi_item.id).setValue(notifi_item);
                                getApplicationContext().startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return true;

        }
        return false;
    }
}



