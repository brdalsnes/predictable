package com.brdalsnes.predictable;

import android.app.Activity;
import android.widget.TableLayout;
import android.widget.TextView;

import butterknife.InjectView;


public class LeaderboardActivity extends Activity {

    @InjectView(R.id.leaderboard) TableLayout leaderboard;
    @InjectView(R.id.usernameTextView) TextView userNameTextView;

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.inject(this);

        final ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.orderByDescending("assets");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {

                if (e == null) {
                    int max = users.size(); //Keeps list 20 or less
                    if(max > 20){
                        max = 20;
                    }

                    for (int i = 0; i < max; i++){

                        TableRow row = new TableRow(LeaderboardActivity.this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);
                        row.setBackgroundResource(R.drawable.cell_bg);

                        TextView name = new TextView(LeaderboardActivity.this);
                        name.setTextColor(Color.parseColor("#000000"));
                        name.setTextSize(20);
                        name.setText((i+1) + ". " + users.get(i).getString("username"));

                        TextView netWorth = new TextView(LeaderboardActivity.this);
                        netWorth.setTextColor(Color.parseColor("#4DB870"));
                        netWorth.setTextSize(20);
                        netWorth.setText(users.get(i).getInt("assets")+ "");

                        row.addView(name);
                        row.addView(netWorth);
                        leaderboard.addView(row);
                    }
                }
                else{
                    displayError(e);
                }
            }
        });
    }


    @OnClick(R.id.usernameTextView)
    public void openProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }*/
}
