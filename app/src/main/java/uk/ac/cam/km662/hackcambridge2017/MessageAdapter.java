package uk.ac.cam.km662.hackcambridge2017;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kamile on 28/01/2017.
 */

public class MessageAdapter extends BaseAdapter {


    public static final int DIRECTION_BOT = 0;
    public static final int DIRECTION_USER = 1;

    private LayoutInflater layoutInflater;
    private ArrayList<Message> messages;
    private Activity context;

    public MessageAdapter(Activity context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
        layoutInflater = context.getLayoutInflater();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (messages != null) {
            return messages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (messages != null) {
            return messages.get(i).getMessage();
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        boolean directionIsUser = messages.get(i).getIsUser();
        if (directionIsUser){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    //Adapter to create message bubbles to add to listview
    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        RecyclerView.ViewHolder holder;

        int direction = getItemViewType(i);

        // show message on left or right, depending on if
        // it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_BOT) {
                res = R.layout.bot_message;
            } else if (direction == DIRECTION_USER) {
                res = R.layout.user_message;
            }
            convertView = layoutInflater.inflate(res, parent, false);
        }

        String message = messages.get(i).getMessage();
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        return convertView;
    }


}
