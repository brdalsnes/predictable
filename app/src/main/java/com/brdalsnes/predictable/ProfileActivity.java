package com.brdalsnes.predictable;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
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


public class ProfileActivity extends Activity {

    private FirebaseUser currentUser;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String id;
    private double money;
    private double netWorth;

    @InjectView(R.id.logoutBtn) Button logoutBtn;
    @InjectView(R.id.netWorthTextView) TextView netWorthTextView;
    @InjectView(R.id.moneyTextView) TextView moneyTextView;
    @InjectView(R.id.holdingsTable) TableLayout holdingsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);

        final Activity activity = this;

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        database = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                //Net worth and money
                database.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        netWorth = dataSnapshot.child("netWorth").getValue(double.class);
                        money = dataSnapshot.child("money").getValue(double.class);
                        Log.i("MONEY", money + "");
                        netWorthTextView.setText(String.valueOf(netWorth));
                        moneyTextView.setText(String.valueOf(money));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        /*
        currentUser = ParseUser.getCurrentUser();
        netWorth = currentUser.getInt("assets");
        money = currentUser.getInt("money");
        holdings = currentUser.getList("ticketList");

        netWorthTextView.setText(netWorth + "");
        moneyTextView.setText(money + "");

        for(int i = 0; i < holdings.size(); i++){

            final TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setBackgroundResource(R.drawable.cell_bg);

            final TextView ticker = new TextView(this);
            //Color set later
            ticker.setTextSize(20);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            //params.gravity = 3; //Left
            ticker.setLayoutParams(params);

            final TextView value = new TextView(this);
            //Color set later
            value.setTextSize(20);
            params.weight = 1.0f;
            value.setLayoutParams(params);

            final TextView number = new TextView(this);
            number.setTextColor(Color.parseColor("#335C85"));
            number.setTextSize(20);
            number.setLayoutParams(params);

            final TextView total = new TextView(this);
            total.setTextColor(Color.parseColor("#335C85"));
            total.setTextSize(20);
            //params.gravity = 5; //Right
            total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));

            ParseObject ticket = holdings.get(i);
            try {
                final ParseObject listTicket = query.get(ticket.getObjectId()); //Gets ticket object

                ParseQuery<ParseObject> queryEvent = ParseQuery.getQuery("Events");
                queryEvent.whereEqualTo("ticker", listTicket.getString("ticker"));
                queryEvent.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if(e == null){
                            if(parseObjects.size() > 0) {
                                final ParseObject tempEvent = parseObjects.get(0);
                                final double marketValue = tempEvent.getInt("marketValue");
                                final double noValue = 100 - marketValue;
                                if(listTicket.getBoolean("yes")){
                                    value.setText(marketValue + "");
                                    ticker.setTextColor(getResources().getColor(R.color.my_green));
                                    total.setText((marketValue * listTicket.getInt("number")) + "");
                                }
                                else{
                                    value.setText(noValue + "");
                                    ticker.setTextColor(getResources().getColor(R.color.my_red));
                                    total.setText((noValue * listTicket.getInt("number")) + "");
                                }

                                value.setTextColor(getColor(marketValue));
                                ticker.setText(listTicket.getString("ticker"));
                                number.setText(listTicket.getInt("number") + "");

                                row.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, EventDisplayActivity.class);
                                        intent.putExtra("ticker", listTicket.getString("ticker"));
                                        intent.putExtra("name", tempEvent.getString("name"));
                                        intent.putExtra("description", tempEvent.getString("description"));
                                        intent.putExtra("deadline", tempEvent.getDate("deadline").toString());
                                        intent.putExtra("image", tempEvent.getString("image"));
                                        intent.putExtra("price", marketValue);
                                        intent.putExtra("money", money);
                                        activity.startActivity(intent);
                                    }
                                });
                            }
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }
            row.addView(ticker);
            row.addView(value);
            row.addView(number);
            row.addView(total);
            holdingsTable.addView(row);
        }


    }

    public int getColor(double power)
    {
        float HSV[] = new float[3];
        HSV[0] = (float)power;
        HSV[1] = (float)0.9;
        HSV[2] = (float)0.9;

        return Color.HSVToColor(HSV);
    }

    @OnClick(R.id.logoutBtn)
    public void logout(){
        ParseUser.logOut();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.leaderboardTextView)
    public void openLeaderboard(){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }*/

    }

}
