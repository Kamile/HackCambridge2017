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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatbotTeacher extends AppCompatActivity {
  
    private Button sendButton;
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
        sendButton = (Button) findViewById(R.id.SendButton);

        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);

        primaryToken = getMetaData(getBaseContext(),"botPrimaryToken");
        botName = getMetaData(getBaseContext(),"botName").toLowerCase();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        //messagesList.setAdapter(adapter);

        //listen for a click on the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                pollBotResponses();

                String conversationTokenInfo = startConversation();
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
                    sendMessageToBot(messageText);
                }

            }
        });
    }
  
  //returns the conversationID
    private String startConversation()
    {
        //NEED TO CHANGE: network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations";
        URL url = null;
        String responseValue = "";

        try {
            url = new URL(UrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            String basicAuth = "Bearer "  + primaryToken;
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

        return  responseValue;
    }

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
      
      sendMessageToBot(messageBody);
    }
  
  //sends the message by making it an activity to the bot
    private void sendMessageToBot(String messageText) {
        //Only for demo sake, otherwise the network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations/" + conversationId + "/activities";
        URL url = null;

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

            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            }
            else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String responseValue = readStream(in);
                //Log.w("responseSendMsg ",responseValue);
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
    
    public void pollBotResponses()
    {
        String botResponse = "";
        if(conversationId != "" && localToken != "") {
            botResponse = getBotResponse();
            if (botResponse != "") {
                try {
                    JSONObject jsonObject = new JSONObject(botResponse);
                    String responseMsg = "";
                    Integer arrayLength = jsonObject.getJSONArray("activities").length();
                    String msgFrom = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).getJSONObject("from").get("id").toString();
                    String curMsgId = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).get("id").toString();

                    if(msgFrom.trim().toLowerCase().equals(botName)) {
                        if(lastResponseMsgId == "") {
                            responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                            AddResponseToChat(responseMsg);
                            lastResponseMsgId = curMsgId;
                        }
                        else if(!lastResponseMsgId.equals(curMsgId))
                        {
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
    }
  
  //Get the bot response by polling a GET to directline API
    private String getBotResponse() {
        //Only for demo sake, otherwise the network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations/" + conversationId + "/activities";
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

            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
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
        }
        catch (Exception e) {
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

        //Log.w("streamValue",s.toString());
        return s.toString();
    }
    
    /*
    Add the bot response to chat window
     */
    private void AddResponseToChat(String botResponse)
    {
        Message message = new Message();
        //message.setId(2);
        message.direction(0);
        message.setMessage(botResponse);
        displayMessage(message);
    }
    
    public void displayMessage(ChatMessage message) {
        messageAdapter.addMessage(message);
        messageAdapter.notifyDataSetChanged();
        messagesList.setSelection(messagesList.getCount() - 1);
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
                  .setName("Chat Page") // TODO: Define a title for the content shown.
                  // TODO: Make sure this auto-generated URL is correct.
                  .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                  .build();
          return new Action.Builder(Action.TYPE_VIEW)
                  .setObject(object)
                  .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                  .build();
      }  
  
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
  
  
    
}
