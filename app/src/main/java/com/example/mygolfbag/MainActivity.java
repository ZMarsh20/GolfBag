package com.example.mygolfbag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements MyBagAdapter.Listener{
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    TextView textView;
    MyBagAdapter myBagAdapter;
    boolean setOn = true;
    int pos = -1;
    public User user;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Settings");
            builder.setMessage("By clicking yes you will turn off the notification for having more than 14 clubs in your bag which can lead to unwanted strokes.");

            String yes, no;
            if (setOn) {
                yes = "Off please";
                no = "It can stay";
            } else {
                yes = "Keep off";
                no = "Back on";
            }

            builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setOn = false;
                    textView.setVisibility(View.GONE);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.golfball);
                    mp.start();
                }
            });
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setOn = true;
                    if (myBagAdapter.myBagDatabaseHelper.getCount(user.id) > 14) textView.setVisibility(View.VISIBLE);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.swing);
                    mp.start();
                }
            });
            builder.show();
        }
        else {
            if (frameLayout == null) {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("id", item.getItemId());
                intent.putExtra("pos", -1);
                intent.putExtra("user", user);
                startActivityForResult(intent,1);
            }
            else {
                Fragment fragment = null;
                if (item.getItemId() == R.id.addClub) fragment = new AddClubFragment();
                else if (item.getItemId() == R.id.login) fragment = new LoginFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = this.getSupportFragmentManager();
                    for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                        fragmentManager.popBackStack();
                    }
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                user = (User) intent.getSerializableExtra("user");
                myBagAdapter.user = user;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        if (setOn && myBagAdapter.myBagDatabaseHelper.getCount(user.id) > 14) textView.setVisibility(View.VISIBLE);
        else textView.setVisibility(View.GONE);
        myBagAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User(null,null,null,-1);

        frameLayout = findViewById(R.id.frameLayout);

        textView = findViewById(R.id.textViewLimit);
        recyclerView = findViewById(R.id.recyclerView);

        myBagAdapter = new MyBagAdapter(getApplicationContext(), user);
        recyclerView.setAdapter(myBagAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myBagAdapter.setListener(this);
        myBagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int position) {
        pos = position;
        if (frameLayout == null) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("pos", position);
            intent.putExtra("id", -1);
            intent.putExtra("user", user);
            startActivityForResult(intent,1);
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new ViewFragment());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}