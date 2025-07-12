package com.example.appprototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.appprototype.databinding.ActivityOverviewBinding;

public class OverviewActivity extends AppCompatActivity
{

    ActivityOverviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home)
            {
                replaceFragment(new HomeFragment());
            }
            else if(item.getItemId() == R.id.map)
            {
                replaceFragment(new MapFragment());
            }
            else if(item.getItemId() == R.id.events)
            {
                replaceFragment(new EventsFragment());
            }
            else if(item.getItemId() == R.id.sights)
            {
                replaceFragment(new SightsFragment());
            }
            else if(item.getItemId() == R.id.settings)
            {
                replaceFragment((new SettingsFragment()));
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.frame_layout, fragment);
        fragTrans.commit();
    }
}