package com.example.helpme.controlers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.helpme.R;
import com.example.helpme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {
    public ArrayList<User> contacts;
    public Context context;

    public ContactsAdapter(ArrayList<User> users, Context context) {
        this.contacts = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User contact = contacts.get(position);
        Log.v("contact.toString() : ", contact.toString());
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.contact_name)).setText(contact.getUsername());
        ((TextView) view.findViewById(R.id.contact_num)).setText(contact.getPhoneNumber());

        ((Button) view.findViewById(R.id.delete_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference contactsChild =  FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("contacts");
                contactsChild.child(contact.getId()).removeValue();
            }
        });
        return view;
    }
}
