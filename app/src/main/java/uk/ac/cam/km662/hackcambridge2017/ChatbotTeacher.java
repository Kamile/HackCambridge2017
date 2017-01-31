package uk.ac.cam.km662.hackcambridge2017;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatbotTeacher extends AppCompatActivity {

    private static final String PREFS_NAME = "Cache";
    private static final boolean DIRECTION_BOT = false;
    private static final boolean DIRECTION_USER = true;

    private String username = "";

    private int currentTopic;
    private int currentDifficulty;

    private String localToken = "";
    private String conversationId = "";
    private String primaryToken = "";
    private String botName = "";

    //keep the last Response MsgId, to check if the last response is already printed or not
    private String lastResponseMsgId = "";
    
    private GoogleApiClient client;

    private EditText messageBodyField;
    private String messageBody;
    private ListView messagesList;
    private MessageAdapter messageAdapter;
    private Button sendButton;
    private ImageButton smileyButton;
    private TextView responseValueText;


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            pollBotResponses();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_teacher);

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        messagesList = (ListView) findViewById(R.id.listMessages);
        sendButton = (Button) findViewById(R.id.sendButton);
        smileyButton = (ImageButton) findViewById(R.id.smileyButton);
        responseValueText = (TextView) findViewById(R.id.response_value);
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        messagesList.setAdapter(messageAdapter);

        welcomeMessage(); // start message asking user for name

        primaryToken = getMetaData(getBaseContext(),"botPrimaryToken");
        botName = getMetaData(getBaseContext(),"botName").toLowerCase();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        runnable.run();

        final String conversationTokenInfo = startConversation();

        //listen for a click on the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageBody = messageBodyField.getText().toString();
                if (TextUtils.isEmpty(messageBody)) {
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                Message userMessage = new Message(messageBody, DIRECTION_USER);
                userMessage.setId(207); //dummy
                userMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));

                messageAdapter.addMessage(userMessage);
                messageBodyField.setText("");


                //String conversationTokenInfo = responseValueText.getText().toString();
                JSONObject jsonObject = null;

                 if(conversationTokenInfo != "") {
                     try {
                         jsonObject = new JSONObject(conversationTokenInfo);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }

                 //send message to bot and get the response using the api conversations/{conversationid}/activities
                 if(jsonObject != null) {
                     try {
                         conversationId = jsonObject.get("conversationId").toString();
                         localToken = jsonObject.get("token").toString();
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }

                 if(conversationId != "") {
                     sendMessageToBot(userMessage.getMessage());
                 }
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
    
    private void setUsername() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // If can't find name, just call them buddy
        username = settings.getString("username", "buddy");

    }
  
    //returns the conversationID
    private String startConversation()
    {
        //NEED TO CHANGE: network work should be done over an async task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations";

        //new ConnectionToServer(responseValueText).execute(UrlText, primaryToken);

        URL url  = null;
        String responseValue = "";

        try {
            url = new URL(UrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        try {
            String basicAuth = "Bearer " + primaryToken;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            responseValue = readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return responseValue;
    }


    private void welcomeMessage() {
      String helloMsg = "Hello! Welcome to your Octocat - your one stop app for the pursuit of knowledge. How may I address you?";
      Message welcome = new Message(helloMsg,DIRECTION_BOT);
      displayMessage(welcome);
    }

    //sends the message by making it an activity to the bot
    private void sendMessageToBot(String messageText) {

        //Only for demo sake, otherwise the network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations/"+ conversationId + "/activities";
        URL url = null;
        //new SendToBotAsync().execute(UrlText, localToken, messageText);

        try {
            url = new URL(UrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            String basicAuth = "Bearer " + localToken;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type","message");
                jsonObject.put("text",messageText);
                jsonObject.put("from",(new JSONObject().put("id","user1")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String postData = jsonObject.toString();

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
            OutputStream out = urlConnection.getOutputStream();
            out.write(postData.getBytes());

            int responseCode = urlConnection.getResponseCode();

            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            }
            else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String responseValue = readStream(in);
                Log.w("responseSendMsg ",responseValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

    }
  
  //Get the bot response by polling a GET to directline API
    private String getBotResponse () {

        //Only for demo sake, otherwise the network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String UrlText = "https://directline.botframework.com/v3/directline/conversations/"+ conversationId + "/activities";

        URL url = null;
        String responseValue = "";

        try {
            url = new URL(UrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            String basicAuth = "Bearer " + localToken;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            int responseCode = urlConnection.getResponseCode();
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            }
            else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                responseValue = readStream(in);
                Log.w("responseSendMsg ",responseValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return responseValue;
    }
  
    //read the chat bot response
    private String readStream(InputStream in) {
        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = 0;
            try {
                n = r.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }

        Log.w("streamValue",s.toString());
        return s.toString();
    }

    public void pollBotResponses() {
        String botResponse = "";
        if (conversationId != "" && localToken != "") {
            botResponse = getBotResponse();
            if (botResponse != "") {
                try {
                    JSONObject jsonObject = new JSONObject(botResponse);
                    String responseMsg = "";
                    Integer arrayLength = jsonObject.getJSONArray("activities").length();
                    String msgFrom = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).getJSONObject("from").get("id").toString();
                    String curMsgId = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("id").toString();

                    if (msgFrom.trim().toLowerCase().equals(botName)) {
                        if (lastResponseMsgId == "") {
                            responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                            AddResponseToChat(responseMsg);
                            lastResponseMsgId = curMsgId;
                        } else if (!lastResponseMsgId.equals(curMsgId)) {
                            responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                            AddResponseToChat(responseMsg);
                            lastResponseMsgId = curMsgId;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        handler.postDelayed(runnable, 1000*2);
    }

    
    /*
    Add the bot response to chat window & check for special messages
     */
    private void AddResponseToChat(String botResponse) {
        //check for special messages - correct and wrong
        if (botResponse.indexOf("Correct")!=-1) {
          //currentTopic score++;
          //Score.incrementQuestionScore();
        } else if (botResponse.indexOf("Wrong")!=-1) {
          //score--;
          //Score.decrementQuestionScore();
          //Score.incrementQuestionScore();
          //Score.incrementTopic(currentTopic,currentDifficulty);
        } else if (botResponse.indexOf("Wrong")!=-1) {
          //score--;
          //Score.decrementQuestionScore();
          //Score.decrementTopic(currentTopic,currentDifficulty);
        }
        Message message = new Message(botResponse, DIRECTION_BOT);
        message.setId(307); //dummy
        message.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        message.setMessage(botResponse);
        displayMessage(message);
    }
    
    public void displayMessage(Message message) {
        messageAdapter.addMessage(message);
        messageAdapter.notifyDataSetChanged();
        messagesList.setSelection(messagesList.getCount() - 1); //scroll

    }
    
    /*
    Get metadata from manifest file against a given key
     */
    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("Metadata", "Unable to load meta-data: " + e.getMessage());
        }
        return null;
    }

    public Action getIndexApiAction() {
          Thing object = new Thing.Builder()
                  .setName("Chat Page")
                  .setUrl(Uri.parse("https://directline.botframework.com/v3/directline/conversations/"+ conversationId + "/activities"))
                  .build();
          return new Action.Builder(Action.TYPE_VIEW)
                  .setObject(object)
                  .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                  .build();
      }  
  
    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
  
  
    
}
