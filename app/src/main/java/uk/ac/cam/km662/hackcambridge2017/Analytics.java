package uk.ac.cam.km662.hackcambridge2017;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class Analytics extends AppCompatActivity {

    private static final String PREFS_NAME = "Cache";
    private ImageView userProfilePic;
    private TextView analyticsGreeting;
    private ViewPager viewPager;
    private ImageButton leftNavigate;
    private ImageButton rightNavigate;

    private FragmentPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        userProfilePic = (ImageView) findViewById(R.id.user_profile_pic);
        analyticsGreeting = (TextView) findViewById(R.id.analytics_title);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        leftNavigate = (ImageButton) findViewById(R.id.left_nav);
        rightNavigate = (ImageButton) findViewById(R.id.right_nav);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // If can't find name, just call them buddy
        String username = settings.getString("username", "buddy");
        String profilePictureUrl = settings.getString("profilePicture",
                "https://www.google.co.uk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjW8P3InObRAhWB7hoKHUcmAUkQjRwIBw&url=http%3A%2F%2Fwww.visualsays.com%2Fcat-pictures%2F&psig=AFQjCNFYlAWBYnAQEm2Ka3T0kkIzxHpN3g&ust=1485740424989722");


        analyticsGreeting.setText("Your Progress, " + username);
        Picasso.with(getApplicationContext()).load(profilePictureUrl)
                .transform(new CircleTransform()).into(userProfilePic);


        leftNavigate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }

            }
        });

        rightNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                tab++;
                viewPager.setCurrentItem(tab);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //When graph scrolled
            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(Analytics.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_PAGES = 2;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_PAGES; // total number of pages
        }

        //Return fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return LineChartFragment.newInstance(0, "Total correct answers over time");
                case 1:
                    return PieChartFragment.newInstance(1, "Correct vs incorrect answers");
                default:
                    return null;
            }
        }

        //Return page title
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }



}
