package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";

    EditText editTextNama;
    Button buttonAdd;
    Spinner spinnerGenres;

    DatabaseReference databaseArtist;


    ListView listViewArtist;
    List <Artist> artists;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            databaseArtist = FirebaseDatabase.getInstance().getReference("artist");

            editTextNama = (EditText) findViewById(R.id.editTextNama);
            buttonAdd = (Button) findViewById(R.id.buttonAdd);
            spinnerGenres = (Spinner) findViewById(R.id.spinerGenres);

            listViewArtist = (ListView) findViewById(R.id.listViewArtist);

            artists = new ArrayList<>();

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addArtist();
                }
            });
            listViewArtist.setOnItemClickListener((new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Artist artist = artists.get(i);
                    Intent intent = new Intent(getApplicationContext(),AddTrackActivity.class);

                    intent.putExtra(ARTIST_ID, artist.getArtistId());
                    intent.putExtra(ARTIST_NAME, artist.getArtistName());


                    startActivity(intent);

                }
            }));
        }

    @Override
    protected void onStart() {
        super.onStart();

        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                artists.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                        Artist artist = artistSnapshot.getValue(Artist.class);

                        artists.add(artist);

                }

                ArtistList adapter = new ArtistList(MainActivity.this , artists);
                listViewArtist.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addArtist(){
            String name = editTextNama.getText().toString().trim();
            String genre = spinnerGenres.getSelectedItem().toString() ;

            if (!TextUtils.isEmpty(name)){

                    String id = databaseArtist.push().getKey();

                    Artist artist = new Artist(id, name ,genre);

                    databaseArtist.child(id).setValue(artist);
                    Toast.makeText(this, "Artis Add", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this, "Tulis Nama", Toast.LENGTH_LONG).show();
            }
        }
}