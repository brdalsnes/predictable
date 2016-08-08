package com.brdalsnes.predictable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Bjornar on 05/08/2015.
 */
public class EventAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList<Event> eventList = new ArrayList<>();
    private String id;
    private double money;
    private static LayoutInflater inflater = null;
    private Dialog customDialog;
    public Event tempEvent = null;


    public EventAdapter(Activity activity, ArrayList<Event> eventList, String id, double money){
        this.activity = activity;
        this.eventList = eventList;
        this.id = id;
        this.money = money;

        eventList.clear();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder{  //Holder class for xml-elements
        ImageView eventImageView;
        TextView eventName;
        TextView priceTextView;

    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.event_list_item, null);

            holder = new ViewHolder();
            holder.eventName = (TextView)convertView.findViewById(R.id.eventName);
            holder.eventImageView = (ImageView)convertView.findViewById(R.id.eventImageView);
            holder.priceTextView = (TextView)convertView.findViewById(R.id.priceTextView);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        if(eventList.size() == 0){
            holder.eventName.setText("No Data");
        }
        else{
            tempEvent = null;
            tempEvent = eventList.get(position);

            //Set names/values for display
            holder.eventName.setText(tempEvent.getName());

            String priceText = (int)tempEvent.getValue() + "%";
            holder.priceTextView.setText(priceText);
            holder.priceTextView.setTextColor(getColor(tempEvent.getValue()));

            //Image things

            int imageId = activity.getResources().getIdentifier(tempEvent.getImage(), "drawable", "com.brdalsnes.predictable");

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitImage = BitmapFactory.decodeResource(activity.getResources(), imageId, options);

            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            holder.eventImageView.getLayoutParams().height = 720;
            holder.eventImageView.getLayoutParams().width = 630;

            holder.eventImageView.setImageBitmap(bitImage);

            //Set clicks
            holder.eventImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
            holder.eventName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
            holder.priceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Log.i("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener implements View.OnClickListener{
        private int position;

        public OnItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.i("CustomAdapter", "=====Row button clicked=====");

            MainActivity sct = (MainActivity)activity;
            sct.onItemClick(position);
        }
    }

    public void popDialog(int position) {
        final MainActivity main = new MainActivity();

        customDialog = new Dialog(activity);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        customDialog.setContentView(dialogView);

        tempEvent = eventList.get(position);
        final double price = tempEvent.getValue();
        final String ticker = tempEvent.getTicker();

        TextView tickerTextView = (TextView) dialogView.findViewById(R.id.tickerTextView);
        TextView moneyTextView = (TextView) dialogView.findViewById(R.id.moneyTextView);
        TextView tradePriceView = (TextView) dialogView.findViewById(R.id.tradePriceView);
        final TextView priceTextView = (TextView) dialogView.findViewById(R.id.priceTextView);

        tickerTextView.setText(tempEvent.getTicker());
        moneyTextView.setText(money + "");
        tradePriceView.setText("Trading at: " + price);
    }

    public void openEvent(int position){
        tempEvent = eventList.get(position);

        Intent intent = new Intent(activity, EventDisplayActivity.class);
        intent.putExtra("event", tempEvent);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

    public int getColor(double power) {
        float HSV[] = new float[3];
        HSV[0] = (float)power;
        HSV[1] = (float)0.9;
        HSV[2] = (float)0.9;

        return Color.HSVToColor(HSV);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}