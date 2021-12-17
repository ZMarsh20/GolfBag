package com.example.mygolfbag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {
    static SQLiteDatabase sqLiteDatabase;
    static MyBagDatabaseHelper myBagDatabaseHelper;
    int pos;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        myBagDatabaseHelper = new MyBagDatabaseHelper(getApplicationContext());
        sqLiteDatabase = myBagDatabaseHelper.getReadableDatabase();

        int id = intent.getIntExtra("id", 0);
        pos = intent.getIntExtra("pos", -1);

        user = (User) intent.getSerializableExtra("user");

        Fragment fragment;
        if (id == -1) fragment = new ViewFragment();
        else if (id == R.id.addClub) fragment = new AddClubFragment();
        else fragment = new LoginFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutDetail, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}