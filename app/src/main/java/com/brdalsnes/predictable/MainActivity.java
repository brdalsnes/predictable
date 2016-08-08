package com.brdalsnes.predictable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends Activity {

    ListView list;
    EventAdapter eventAdapter;

    private FirebaseUser currentUser;
    private String username;
    private double money, netWorth;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Event> modEvents = new ArrayList<>();
    private String id;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @InjectView(R.id.netWorthTextView) TextView netWorthTextView;
    @InjectView(R.id.usernameTextView) TextView usernameTextView;
    @InjectView(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @InjectView(R.id.sortSpinner) Spinner sortSpinner;
    @InjectView(R.id.loadingSpinner) ProgressBar loadingSpinner;
    @InjectView(R.id.leaderboardTextView) TextView leaderboardTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        database = FirebaseDatabase.getInstance().getReference();

        //Respond to changes in sign-up state
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    // User is signed in

                    //Ugly, but had to be done
                    id = null;
                    while (id == null){
                        id = currentUser.getUid();
                    }

                    database.child("users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usernameTextView.setText(dataSnapshot.child("username").getValue(String.class));
                            String netWorthText = Math.round(dataSnapshot.child("netWorth").getValue(double.class)) + "";
                            netWorthTextView.setText(netWorthText);

                            //Update net worth and money
                            money = dataSnapshot.child("money").getValue(double.class);
                            updateNetWorth();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        };

        //Loading
        loadingSpinner.setVisibility(View.VISIBLE);

        //Dropdown customization
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_arrays, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        categoriesSpinner.setAdapter(adapter);

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modEvents.clear();
                switch (position){
                    case 0:
                        //All
                        setAllEvents();
                        break;
                    case 1:
                        categorize("Politics");
                        break;
                    case 2:
                        categorize("Sports");
                        break;
                    case 3:
                        categorize("Entertainment");
                        break;
                    case 4:
                        categorize("Science");
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        adapter = ArrayAdapter.createFromResource(this, R.array.sort_arrays, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        //Top traded
                    case 1:
                        sortHighPrice();
                        break;
                    case 2:
                        sortLowPrice();
                        break;
                    case 3:
                        sortNewest();
                        break;
                    case 4:
                        sortDeadline();
                        break;
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        leaderboardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Test
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingSpinner.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance().getReference();

        //Fill event list with events
        database.child("Events").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                modEvents.clear();
                for(com.google.firebase.database.DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    Event event = new Event();
                    event.setTicker(dataSnapshot.child("ticker").getValue(String.class));
                    event.setName(eventSnapshot.child("name").getValue(String.class));
                    event.setImage(eventSnapshot.child("image").getValue(String.class));
                    event.setDescription(eventSnapshot.child("description").getValue(String.class));
                    event.setValue(eventSnapshot.child("value").getValue(double.class));
                    event.setYes(eventSnapshot.child("yes").getValue(Long.class).intValue());
                    event.setNo(eventSnapshot.child("no").getValue(Long.class).intValue());
                    event.setCategory(eventSnapshot.child("category").getValue(String.class));
                    event.setDateCreated(eventSnapshot.child("created").getValue(Long.class).intValue());
                    event.setDateDeadline(eventSnapshot.child("deadline").getValue(Long.class).intValue());
                    modEvents.add(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Create list
        list = (ListView)findViewById(R.id.listView);
        eventAdapter = new EventAdapter(MainActivity.this, modEvents, id, money);
        list.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void categorize(String category){
        for (int i = 0; i < events.size(); i++){
            if(events.get(i).getCategory().equals(category)){
                modEvents.add(events.get(i));
            }
        }
    }

    public void setAllEvents(){
        for(int i = 0; i < events.size(); i++){
            modEvents.add(events.get(i));
        }
    }

    //Sorting functions
    public void sortHighPrice(){
        Collections.sort(modEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getValue() == rhs.getValue()){
                    return 0;
                }
                return lhs.getValue() > rhs.getValue() ? -1 : 1;
            }
        });
    }
    public void sortLowPrice(){
        Collections.sort(modEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getValue() == rhs.getValue()){
                    return 0;
                }
                return lhs.getValue() < rhs.getValue() ? -1 : 1;
            }
        });
    }
    public void sortNewest(){
        Collections.sort(modEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getDateCreated() == rhs.getDateCreated()){
                    return 0;
                }
                return lhs.getDateCreated() < rhs.getDateCreated() ? -1 : 1;
            }
        });
    }
    public void sortDeadline(){
        Collections.sort(modEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getDateDeadline() == rhs.getDateDeadline()){
                    return 0;
                }
                return lhs.getDateDeadline() < rhs.getDateDeadline() ? -1 : 1;
            }
        });
    }

    public void onItemClick(int position){
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    public void updateNetWorth(){
        database.child("users").child(id).child("tickets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean enterFor = false;
                for(final com.google.firebase.database.DataSnapshot ticketSnapshot: dataSnapshot.getChildren()){
                    enterFor = true;

                    final String dataString = ticketSnapshot.getKey();
                    String[] parts = dataString.split("_");
                    final String eventName = parts[0];
                    final String eventType = parts[1];
                    final int numTickets = ticketSnapshot.getValue(Long.class).intValue();

                    //Check each ticket
                    database.child("Events").child(eventName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot eventSnapshot) {
                            //Check if expired
                            if(eventSnapshot.child("status").getValue(String.class).equals(eventType)){
                                //Correct predict
                                money += 100 * numTickets;
                                database.child("users").child("tickets").child(dataString).removeValue();
                            }
                            else if(eventType.equals("yes") || eventType.equals("no")){
                                //Wrong predict
                                database.child("users").child("tickets").child(dataString).removeValue();
                            }

                            //Event still active
                            if(eventType.equals("yes")){
                                //Yes event
                                netWorth = money + (eventSnapshot.child("value").getValue(double.class) * numTickets);
                            }
                            else if(eventType.equals("no")){
                                //No event
                                netWorth = money + ((100 - eventSnapshot.child("value").getValue(double.class)) * numTickets);
                            }

                            database.child("users").child(id).child("netWorth").setValue(netWorth);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                //No tickets
                if(!enterFor){
                    netWorth = money;
                    database.child("users").child(id).child("netWorth").setValue(netWorth);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @OnClick(R.id.usernameTextView)
    public void openProfile(){
        loadingSpinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @OnClick(R.id.netWorthTextView)
    public void openProfileA(){
        loadingSpinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void printEventList(String name, ArrayList<Event> eventList){
        String printString = "[";
        for(int i = 0; i < eventList.size(); i++){
            printString += eventList.get(i).getTicker() + ", ";
        }
        printString += "]";
        Log.i(name, printString);
    }
}
