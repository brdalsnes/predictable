package com.brdalsnes.predictable;

import android.app.Activity;


public class ProfileActivity extends Activity {
    /*
    private ParseUser currentUser;
    private int money;
    private int netWorth;
    private List<ParseObject> holdings;

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

        currentUser = ParseUser.getCurrentUser();
        netWorth = currentUser.getInt("assets");
        money = currentUser.getInt("money");
        holdings = currentUser.getList("ticketList");

        netWorthTextView.setText(netWorth + "");
        moneyTextView.setText(money + "");

        //Removes empty tickets
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ticket");
        for(int i = 0; i < holdings.size(); i++){
            ParseObject ticket = holdings.get(i);
            try {
                final ParseObject listTicket = query.get(ticket.getObjectId()); //Gets ticket object

                if(listTicket.getInt("number") == 0){
                    holdings.remove(i);
                    listTicket.deleteInBackground();
                }
            }
            catch (ParseException e){
                e.printStackTrace();
            }
        }
        currentUser.put("ticketList", holdings);
        currentUser.saveInBackground();


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
