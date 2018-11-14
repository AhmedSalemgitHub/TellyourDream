package com.tellyourdream.tellyourdream;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<myDreamItems> {

    public MyAdapter(@NonNull Context context, @NonNull List<myDreamItems> dreamItems) {
        super(context, 0, dreamItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View dreamItem = convertView;

        if (dreamItem == null) {
            dreamItem = LayoutInflater.from(getContext()).inflate(R.layout.dream_item_list
                    , parent, false);
        }

        myDreamItems currentDream = getItem(position);

        TextView ownerTextView =
                dreamItem.findViewById(R.id.item_dream_owner_textView);
        ownerTextView.setText(currentDream.getOwner());


        TextView dreamTextView =
                 dreamItem.findViewById(R.id.item_dream_date_textView);
        dreamTextView.setText(currentDream.getDreamTime().toString());

        TextView replyOrNot =
                dreamItem.findViewById(R.id.dream_reply_status_textView);
        replyOrNot.setText(currentDream.getReplystatus());



        return dreamItem;
    }
}
