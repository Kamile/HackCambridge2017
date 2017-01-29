package uk.ac.cam.km662.hackcambridge2017;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_page);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* Create an Intent that will start the Login Activity. */
                Intent intent = new Intent(LoadPage.this, Login.class);
                LoadPage.this.startActivity(intent);
                LoadPage.this.finish();
            }
        }, 3000);
    }
}
