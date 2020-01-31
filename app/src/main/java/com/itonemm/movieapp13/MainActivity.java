package com.itonemm.movieapp13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId()==R.id.movie_menu)
                {

                    FragmentManager fm=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fm.beginTransaction();
                    fragmentTransaction.replace(R.id.mainframe,new MovieFragment());
                    fragmentTransaction.commit();
                }
                if(menuItem.getItemId()==R.id.series_menu)
                {

                    FragmentManager fm=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fm.beginTransaction();
                    fragmentTransaction.replace(R.id.mainframe,new SeriesFragment());
                    fragmentTransaction.commit();
                }
                if(menuItem.getItemId()==R.id.category_menu)
                {

                    FragmentManager fm=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fm.beginTransaction();
                    fragmentTransaction.replace(R.id.mainframe,new CategoryFragment());
                    fragmentTransaction.commit();
                }
                return true;
            }
        });


        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.replace(R.id.mainframe,new MovieFragment());
        fragmentTransaction.commit();
    }
}
