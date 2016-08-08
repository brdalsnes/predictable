package com.brdalsnes.predictable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LeaderboardActivity extends Activity {

    private DatabaseReference database;

    @InjectView(R.id.leaderboard) TableLayout leaderboard;
    @InjectView(R.id.usernameTextView) TextView userNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.inject(this);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").orderByChild("netWorth").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TableRow row = new TableRow(LeaderboardActivity.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    row.setBackgroundResource(R.drawable.cell_bg);

                    TextView name = new TextView(LeaderboardActivity.this);
                    name.setTextColor(Color.parseColor("#000000"));
                    name.setTextSize(20);
                    String nameText = (i + 1) + ". " + snapshot.child("username").getValue(String.class);
                    name.setText(nameText);

                    TextView netWorth = new TextView(LeaderboardActivity.this);
                    netWorth.setTextColor(Color.parseColor("#4DB870"));
                    netWorth.setTextSize(20);
                    String netWorthText = "    " + round(snapshot.child("netWorth").getValue(double.class), 2);
                    netWorth.setText(netWorthText);

                    row.addView(name);
                    row.addView(netWorth);
                    leaderboard.addView(row);

                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.usernameTextView)
    public void openProfile () {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
