package com.brdalsnes.predictable;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.InjectView;

public class EventDisplayActivity extends Activity {

    private Event currentEvent;
    private int number;

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


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        currentEvent = (Event) extras.getSerializable("event");

        eventName.setText(currentEvent.getName());
        eventDescription.setText(currentEvent.getDescription());
        eventDeadline.setText(currentEvent.getDeadline().toString());
        int imageId = getResources().getIdentifier(currentEvent.getImage(), "drawable", getPackageName());
        eventImageView.setImageResource(imageId);
        final double price = currentEvent.getValue();
        final String ticker = currentEvent.getTicker();

        usernameTextView.setText(username);
        moneyTextView.setText(money + "");


        //Seekbar
        //numBar.setMax((int)(currentUser.getInt("assets")/Math.min(price, 100 - price)));

        numBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                number = progress;
                priceTextView.setText("Tickets: " + progress + "    Price: ");
                yesPriceView.setText(price*number + "");
                slashView.setText(" / ");
                noPriceView.setText(((100 - price)*number) + "");
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
            public void onClick(View v) {
                if(number != 0) { //if NOT empty
                    final int numType = number;
                    int newMoney = money;
                    boolean inList = false;

                    if ((price*numType) < money) {
                        newMoney -= price * numType;
                        priceChange(numType, 0);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ticket");
                        for (int i = 0; i < ticketList.size(); i++) {
                            final ParseObject ticket = ticketList.get(i);
                            try {
                                ParseObject listTicket = query.get(ticket.getObjectId());
                                if (listTicket.getString("ticker").equals(ticker) && listTicket.getBoolean("yes")) {
                                    int oldNum = ticket.getInt("number");
                                    ticket.put("number", oldNum + numType);
                                    ticket.saveInBackground();
                                    inList = true;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                displayError(e);
                            }
                        }
                        if (!inList) {
                            ParseObject newTicket = new ParseObject("Ticket");
                            newTicket.put("ticker", ticker);
                            newTicket.put("number", numType);
                            newTicket.put("yes", true);
                            ticketList.add(newTicket);
                        }

                        orderReceived(newMoney);
                        currentUser.put("money", newMoney);
                        currentUser.put("ticketList", ticketList);
                        currentUser.saveInBackground();

                    }
                    else{
                        invalidTrade();
                    }
                }
            }
        });

        sellYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number != 0){
                    final int numType = number;
                    int newMoney = money;
                    boolean inList = false;
                    newMoney += price * numType;


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Ticket");
                    for(int i = 0; i < ticketList.size(); i++){
                        final ParseObject ticket = ticketList.get(i);
                        try {
                            ParseObject listTicket = query.get(ticket.getObjectId()); //Gets ticket object
                            if(listTicket.getString("ticker").equals(ticker) && listTicket.getBoolean("yes") && numType <= listTicket.getInt("number")){
                                int oldNum = ticket.getInt("number");
                                ticket.put("number", oldNum - numType);
                                ticket.saveInBackground();

                                inList = true;

                                priceChange(-numType, 0);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            displayError(e);
                        }
                    }
                    if(!inList){
                        invalidTrade();
                    }
                    else {
                        orderReceived(newMoney);
                        currentUser.put("money", newMoney);
                        currentUser.put("ticketList", ticketList);
                        currentUser.saveInBackground();
                    }
                }
            }
        });

        buyNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number != 0) { //if NOT empty
                    final int numType = number;
                    int newMoney = money;
                    boolean inList = false;
                    double noPrice = 100 - price;

                    if ((noPrice * numType) < money) {
                        newMoney -= noPrice * numType;
                        priceChange(0, numType);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ticket");
                        for (int i = 0; i < ticketList.size(); i++) {
                            final ParseObject ticket = ticketList.get(i);
                            try {
                                ParseObject listTicket = query.get(ticket.getObjectId());
                                if (listTicket.getString("ticker").equals(ticker) && !listTicket.getBoolean("yes")) {
                                    int oldNum = ticket.getInt("number");
                                    ticket.put("number", oldNum + numType);
                                    ticket.saveInBackground();
                                    inList = true;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                displayError(e);
                            }
                        }
                        if (!inList) {
                            ParseObject newTicket = new ParseObject("Ticket");
                            newTicket.put("ticker", ticker);
                            newTicket.put("number", numType);
                            newTicket.put("yes", false);
                            ticketList.add(newTicket);
                        }
                        orderReceived(newMoney);
                        currentUser.put("money", newMoney);
                        currentUser.put("ticketList", ticketList);
                        currentUser.saveInBackground();

                    }
                    else{
                        invalidTrade();
                    }
                }
            }
        });

        sellNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number != 0){
                    final int numType = number;
                    int newMoney = money;
                    boolean inList = false;
                    double noPrice = 100 - price;
                    newMoney += noPrice * numType;

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Ticket");
                    for(int i = 0; i < ticketList.size(); i++){
                        final ParseObject ticket = ticketList.get(i);
                        try {
                            ParseObject listTicket = query.get(ticket.getObjectId()); //Gets ticket object
                            if(listTicket.getString("ticker").equals(ticker) && !listTicket.getBoolean("yes") && numType <= listTicket.getInt("number")){
                                int oldNum = ticket.getInt("number");
                                ticket.put("number", oldNum - numType);
                                ticket.saveInBackground();

                                inList = true;

                                priceChange(0, -numType);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            displayError(e);
                        }
                    }
                    if(!inList){
                       invalidTrade();
                    }
                    else {
                        orderReceived(newMoney);
                        currentUser.put("money", newMoney);
                        currentUser.put("ticketList", ticketList);
                        currentUser.saveInBackground();
                    }
                }
            }
        });
    }

    public void priceChange(final int yesChange, final int noChange){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.getInBackground(currentEvent.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseEvent, ParseException e) {
                if(e == null){
                    final double b = 64.0;

                    double yes = parseEvent.getInt("yes") + yesChange;
                    double no = parseEvent.getInt("no") + noChange;

                    double calcPrice = (Math.exp(yes/b)/(Math.exp(yes/b) + Math.exp(no/b))) * 100;

                    currentEvent.setValue(calcPrice);

                    parseEvent.put("yes", yes);
                    parseEvent.put("no", no);
                    parseEvent.put("marketValue", calcPrice);
                    parseEvent.saveInBackground();
                }
                else{
                    e.printStackTrace();
                }
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
    }*/
}
