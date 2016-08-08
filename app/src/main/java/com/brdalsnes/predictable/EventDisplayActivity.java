package com.brdalsnes.predictable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EventDisplayActivity extends Activity {

    private int number;
    private String id;
    private int money;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @InjectView(R.id.usernameTextView) TextView usernameTextView;
    @InjectView(R.id.moneyTextView) TextView moneyTextView;
    @InjectView(R.id.eventName) TextView eventName;
    @InjectView(R.id.eventImageView) ImageView eventImageView;
    @InjectView(R.id.eventDeadline) TextView eventDeadline;
    @InjectView(R.id.eventDescription) TextView eventDescription;
    @InjectView(R.id.priceTextView) TextView priceTextView;
    @InjectView(R.id.yesPriceView) TextView yesPriceView;
    @InjectView(R.id.slashView) TextView slashView;
    @InjectView(R.id.noPriceView) TextView noPriceView;
    @InjectView(R.id.numBar) SeekBar numBar;
    @InjectView(R.id.buyYesBtn) Button buyYesBtn;
    @InjectView(R.id.sellYesBtn) Button sellYesBtn;
    @InjectView(R.id.buyNoBtn) Button buyNoBtn;
    @InjectView(R.id.sellNoBtn) Button sellNoBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        ButterKnife.inject(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        final Event currentEvent = (Event) extras.getSerializable("event");
        id = extras.getString("id");

        eventName.setText(currentEvent.getName());
        eventDescription.setText(currentEvent.getDescription());
        eventDeadline.setText(getDateString(currentEvent.getDateDeadline()));

        //Image
        int imageId = getResources().getIdentifier(currentEvent.getImage(), "drawable", getPackageName());
        eventImageView.getLayoutParams().height = 720;
        eventImageView.getLayoutParams().width = 630;
        eventImageView.setImageResource(imageId);

        final double price = currentEvent.getValue();
        //final String ticker = currentEvent.getTicker();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                database.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usernameTextView.setText(dataSnapshot.child("username").getValue(String.class));
                        money = dataSnapshot.child("money").getValue(Long.class).intValue();
                        String moneyText = money + "";
                        moneyTextView.setText(moneyText);
                        numBar.setMax((dataSnapshot.child("netWorth").getValue(Long.class).intValue() / (int) Math.min(price, 100 - price)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        numBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                number = progress;
                String priceText = "Tickets: " + progress + "    Price: ";
                priceTextView.setText(priceText);
                String yesPrice = round(price * number, 1) + "";
                yesPriceView.setText(yesPrice);
                slashView.setText(" / ");
                String noPrice = round((100 - price) * number, 1) + "";
                noPriceView.setText(noPrice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buyYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != 0) { //if NOT empty
                    final int numType = number;
                    int newMoney = money;

                    if ((price * numType) <= money) {
                        newMoney -= price * numType;
                        priceChange(numType, 0);

                        database.child("users").child(id).child("money").setValue(newMoney);

                        //Update user's tickets
                        database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() == null){
                                    database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                                            .setValue(numType);
                                }
                                else{
                                    database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                                            .setValue(numType + dataSnapshot.getValue(Long.class).intValue());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        orderReceived(newMoney);
                    }
                }
            }
        });

        buyNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != 0) { //if NOT empty
                    final int numType = number;
                    int newMoney = money;

                    if ((price * numType) <= money) {
                        newMoney -= price * numType;
                        priceChange(0, numType);

                        //Update money
                        database.child("users").child(id).child("money").setValue(newMoney);

                        //Update user's tickets
                        database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue() == null){
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                                                    .setValue(numType);
                                        }
                                        else{
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                                                    .setValue(numType + dataSnapshot.getValue(Long.class).intValue());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        orderReceived(newMoney);
                    }
                }
            }
        });

        sellYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != 0) { //if NOT empty
                    final int numType = number;

                    //Update user's tickets
                    database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null || dataSnapshot.getValue(Long.class).intValue() < numType){
                                        //Has NO tickets
                                        invalidTrade();
                                    }
                                    else{
                                        //Has tickets
                                        if(dataSnapshot.getValue(Long.class).intValue() == numType){
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                                                    .removeValue();
                                        }
                                        else {
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_yes")
                                                    .setValue(dataSnapshot.getValue(Long.class).intValue() - numType);
                                        }

                                        //Update money
                                        int newMoney = money;
                                        newMoney += price * numType;
                                        database.child("users").child(id).child("money").setValue(newMoney);

                                        priceChange(-numType, 0);

                                        orderReceived(newMoney);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        sellNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != 0) { //if NOT empty
                    final int numType = number;

                    //Update user's tickets
                    database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null || dataSnapshot.getValue(Long.class).intValue() < numType){
                                        //Has NO tickets
                                        invalidTrade();
                                    }
                                    else{
                                        //Has tickets
                                        if(dataSnapshot.getValue(Long.class).intValue() == numType){
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                                                    .removeValue();
                                        }
                                        else {
                                            database.child("users").child(id).child("tickets").child(currentEvent.getImage() + "_no")
                                                    .setValue(dataSnapshot.getValue(Long.class).intValue() - numType);
                                        }

                                        //Update money
                                        int newMoney = money;
                                        newMoney += price * numType;
                                        database.child("users").child(id).child("money").setValue(newMoney);

                                        priceChange(0, -numType);

                                        orderReceived(newMoney);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

    }
    //hello
    public void priceChange(final int yesChange, final int noChange){
        final double a = 0.2; //Formula constant

        Bundle extras = getIntent().getExtras();
        final Event currentEvent = (Event) extras.getSerializable("event");

        database.child("Events").child(currentEvent.getImage()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double yes = dataSnapshot.child("yes").getValue(Long.class).intValue() + yesChange;
                double no = dataSnapshot.child("no").getValue(Long.class).intValue() + noChange;

                double b = a*(yes + no) + 64;
                double calcPrice = (Math.exp(yes/b)/(Math.exp(yes/b) + Math.exp(no/b))) * 100; //Magic formula, LMSR

                currentEvent.setValue(calcPrice);

                database.child("Events").child(currentEvent.getImage()).child("yes").setValue(yes);
                database.child("Events").child(currentEvent.getImage()).child("no").setValue(no);
                database.child("Events").child(currentEvent.getImage()).child("value").setValue(calcPrice);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void orderReceived(int newMoney){
        moneyTextView.setText(newMoney + "");
        priceTextView.setText("Order received");
        yesPriceView.setText("");
        slashView.setText("");
        noPriceView.setText("");
    }

    public void invalidTrade(){
        priceTextView.setText("Invalid trade");
        yesPriceView.setText("");
        slashView.setText("");
        noPriceView.setText("");
    }


    @OnClick(R.id.usernameTextView)
    public void openProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.moneyTextView)
    public void openProfileA(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.leaderboardTextView)
    public void openLeaderboard(){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
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

    public String getDateString(int date){
        String dateString = Integer.toString(date);

        if(dateString.length() != 8){
            return "Could not display date";
        }

        return "Deadline: " + dateString.substring(0, 4) + "-" + dateString.substring(4, 6) + "-" + dateString.substring(6, 8);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
