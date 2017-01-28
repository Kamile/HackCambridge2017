package uk.ac.cam.km662.hackcambridge2017;

import android.app.ListActivity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.id.list;
import static android.R.id.message;
import static java.security.AccessController.getContext;

public class ChatbotTeacher extends AppCompatActivity {

    private EditText messageBodyField;
    private String messageBody;
    private ListView messagesList;
    private MessageAdapter messageAdapter;

    private View userMessage;
    TextView txtMessage;

    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_teacher);

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        messagesList = (ListView) findViewById(R.id.listMessages);

        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);



        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        //messagesList.setAdapter(adapter);


        //listen for a click on the send button
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }



        messageAdapter.addMessage(messageBody, MessageAdapter.DIRECTION_USER);
//


        //need to notify datasetchanged
        messageBodyField.setText("");
        messageBodyField.setHint("Type a message . . . ");
    }
}
