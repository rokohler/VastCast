package com.vastcast.vastcast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

public class AddFeedActivity extends AppCompatActivity {
    private EditText txtUrl;
    private static DatabaseReference myData;
    private static DatabaseReference myRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        Toolbar toolbar = findViewById(R.id.toolbarAddFeed);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            /*TODO: Have Up Direct to Discover Page*/
            ab.setDisplayHomeAsUpEnabled(true);
        }
        Button btnEnterUrl = findViewById(R.id.btnEnterUrl);
        txtUrl = findViewById(R.id.txtUrl);
        btnEnterUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myRef= FirebaseDatabase.getInstance().getReference();
                myData=myRef.child("Database");
                myData.orderByChild("source").equalTo(txtUrl.getText().toString()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            Collection d= new Collection ();
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable <DataSnapshot> datasnap = dataSnapshot.getChildren();
                                for(DataSnapshot data : datasnap) {
                                    d = data.getValue(Collection.class);
                                }
                                if (d == null) {
                                    new RetrieveFeedTask().execute(txtUrl.getText().toString());
                                }
                                else{
                                    Intent i = new Intent(AddFeedActivity.this, DetailActivity.class);
                                    i.putExtra("podcast", d);
                                    AddFeedActivity.this.startActivity(i);
                                }
                            }
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("DatabaseWrapper", "getCollection:onCancelled", databaseError.toException());
                            }
                        });
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSettings:
                //launch settings with an intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Collection> {
        private Exception e = null;

        protected Collection doInBackground(String... urls) {
            try {
                URL source = new URL(urls[0]);
                return RSSFetcher.fetch(source);
            } catch(Exception e) {
                this.e = e;
                return null;
            }
        }

        protected void onPostExecute(Collection c) {
            if(e != null) {
                if(e instanceof MalformedURLException || e instanceof FileNotFoundException) {
                    txtUrl.setError("The entered URL is not a valid RSS feed");
                }
                Log.e("RSS", Log.getStackTraceString(e));
                //others are IOException and XmlPullParserException
            }
            else {
                Intent i = new Intent(AddFeedActivity.this, DetailActivity.class);
                i.putExtra("podcast", c);
                AddFeedActivity.this.startActivity(i);
            }
        }
    }
}
