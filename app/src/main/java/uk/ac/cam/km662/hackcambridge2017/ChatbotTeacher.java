package uk.ac.cam.km662.hackcambridge2017;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private Button sendButton;
    private ImageButton smileyButton;

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
        sendButton = (Button) findViewById(R.id.sendButton);
        smileyButton = (ImageButton) findViewById(R.id.smileyButton);

        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        smileyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatbotTeacher.this, Analytics.class);
                ChatbotTeacher.this.startActivity(intent);
            }
        });
    }

    // Message sent my user
    private void sendMessage(){
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }
        Message userMessage = new Message(messageBody, MessageAdapter.DIRECTION_USER);
        messageAdapter.addMessage(userMessage);
        messageBodyField.setText("");
        messageBodyField.setHint("Type a message . . . ");
    }
}
