package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.controlers.ContactsAdapter;
import com.example.helpme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Page");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addContact();
        fromDataBase();
        editUserName();
        editUserPhoneNumber();
    }

    void addContact(){
        ((Button) findViewById(R.id.btn_add_contact)).setOnClickListener((View.OnClickListener) v -> {
            String phoneNumber = ((EditText) findViewById(R.id.add_contact)).getText().toString();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot user:snapshot.getChildren()) {
                        if(Objects.equals((String) user.child("phoneNumber").getValue(), phoneNumber)){
                            reference.child(firebaseUser.getUid()).child("contacts").push().setValue(user.child("id").getValue());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }

    void editUserName(){
        ((Button) findViewById(R.id.change_name)).setOnClickListener((View.OnClickListener) v -> {
            String username = ((MaterialEditText) findViewById(R.id.user_name)).getText().toString();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!username.isEmpty()){
                        reference.child(firebaseUser.getUid()).child("username").setValue(username);
                        Toast.makeText(getApplicationContext(),"User Name Changed",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"Enter Name to Change It",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }

    void editUserPhoneNumber(){
        ((Button) findViewById(R.id.change_num)).setOnClickListener((View.OnClickListener) v -> {
            String phoneNumber = ((MaterialEditText) findViewById(R.id.ph_num)).getText().toString();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (phoneNumber.length() == 11){
                        reference.child(firebaseUser.getUid()).child("phoneNumber").setValue(phoneNumber);
                        Toast.makeText(getApplicationContext(),"Phone Number Changed",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"phone number is not valid".toUpperCase(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }

    void fromDataBase(){
        TextView username,user_phone;
        username=findViewById(R.id.username);
        user_phone =findViewById(R.id.userphone);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child(firebaseUser.getUid()).getValue(User.class);
                username.setText(user.getUsername());
                user_phone.setText(user.getPhoneNumber());
                ListView contactsList = findViewById(R.id.contactsList);
                contactsList.setAdapter(new ContactsAdapter(user.getUsersFromContacts(snapshot),getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}