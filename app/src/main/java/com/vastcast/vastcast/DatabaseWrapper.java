package com.vastcast.vastcast;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class DatabaseWrapper {
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String userID;

    protected void onCreate() {
        // for data persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myRef=FirebaseDatabase.getInstance().getReference("User");
        userID=myRef.push().getKey();
    }
    public void addCollection(Collection col){
        myRef.child("users").child(userID).child("library").push().setValue(col);
    }
    public void addToQueue(Collection col){
        myRef.child("users").child(userID).child("queue").push().setValue(col);
    }
    public void updateCollection(Collection col){
        //this isn't entirely right atm
        myRef.child("users").child(userID).child("library").setValue(col);
    }
    public void updateQueue(Collection col){
        //this isn't entirely right atm
        myRef.child("users").child(userID).child("queue").setValue(col);
    }
    public void removeFromCollection(Collection col){
       // myRef.child("users").child(userID).child("library").removeValue();
    }
    public void removeFromQueue(Collection col){
        // myRef.child("users").child(userID).child("queue").removeValue();
    }

}