package com.brdalsnes.predictable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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

    private String username;
    private int money, netWorth;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Event> modEvents = new ArrayList<>();
    //private List<ParseObject> ticketList = new ArrayList<>();

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

        //Loading
        loadingSpinner.setVisibility(View.VISIBLE);

        //Fill event list with events
        Firebase ref = new Firebase("https://predictable-88ea7.firebaseio.com/Events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modEvents.clear();
                for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    Event event = new Event();
                    event.setName(eventSnapshot.child("name").getValue(String.class));
                    event.setImage(eventSnapshot.child("image").getValue(String.class));
                    event.setDescription(eventSnapshot.child("description").getValue(String.class));
                    event.setValue(eventSnapshot.child("value").getValue(double.class));
                    event.setCategory(eventSnapshot.child("category").getValue(String.class));
                    modEvents.add(event);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Create list
        list = (ListView)findViewById(R.id.listView);
        eventAdapter = new EventAdapter(MainActivity.this, modEvents, money);
        list.setAdapter(eventAdapter);

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
                Log.i("TEST", "run");
                Intent intent = new Intent(MainActivity.this, CreateUserActivity.class);
                startActivity(intent);
            }
        });

        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingSpinner.setVisibility(View.GONE);
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
                if(lhs.getCreatedAt() == rhs.getCreatedAt()){
                    return 0;
                }
                return myCompare(lhs.getCreatedAt().compareTo(rhs.getCreatedAt())) ? -1 : 1;
            }
        });
    }
    public void sortDeadline(){
        Collections.sort(modEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getDeadline() == rhs.getDeadline()){
                    return 0;
                }
                return myCompare(lhs.getDeadline().compareTo(rhs.getDeadline())) ? -1 : 1;
            }
        });
    }

    public boolean myCompare(int i){
        if(i < 0){
            return true;
        }
        return false;
    }

    public void onItemClick(int position){
        loadingSpinner.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.usernameTextView)
    public void openProfile(){
        loadingSpinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.netWorthTextView)
    public void openProfileA(){
        loadingSpinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.leaderboardTextView)
    public void openLeaderboard(){
        loadingSpinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, LeaderboardActivity.class);
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
