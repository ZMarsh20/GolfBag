package com.example.mygolfbag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import static android.graphics.Color.RED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginFragment extends Fragment {
    EditText editTextUsername, editTextPassword;
    ProgressBar progressBar;
    Button button;
    String username, password;
    boolean tablet;
    MainActivity mainActivity;
    DetailActivity detailActivity;
    Handler handler;
    SQLiteDatabase sqLiteDatabase;
    MyBagDatabaseHelper myBagDatabaseHelper;
    User user;

    public static final String USERTABLE = MyBagDatabaseHelper.USERTABLE;
    public static final String TABLE_ID = MyBagDatabaseHelper.TABLE_ID;
    public static final String USER = MyBagDatabaseHelper.USER;
    public static final String PASS = MyBagDatabaseHelper.PASS;
    public static final String NAME = MyBagDatabaseHelper.NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (tablet) fragmentTransaction.replace(R.id.frameLayout, new SignUpFragment());
            else fragmentTransaction.replace(R.id.frameLayoutDetail, new SignUpFragment());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.delete){
            if (tablet) {
                mainActivity.onBackPressed();
            } else {
                Intent intent = new Intent();
                intent.putExtra("user", user);
                detailActivity.setResult(Activity.RESULT_OK,intent);
                detailActivity.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        try {
            mainActivity = (MainActivity) getActivity();
            tablet = true;
            user = mainActivity.user;
            if (user.id != -1) userSignedIn();
        } catch(Exception e) {
            detailActivity = (DetailActivity) getActivity();
            user = detailActivity.user;
            if (user.id != -1) userSignedIn();
        }

        myBagDatabaseHelper = new MyBagDatabaseHelper(getContext());
        sqLiteDatabase = myBagDatabaseHelper.getReadableDatabase();

        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        progressBar = view.findViewById(R.id.progressBar);
        button = view.findViewById(R.id.buttonSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                check();
            }
        });

        return view;
    }

    public void check() {
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case 1: //checked but wrong
                        progressBar.setVisibility(View.GONE);
                        editTextPassword.setBackgroundColor(RED);
                        editTextUsername.setBackgroundColor(RED);
                        break;
                    case 2: //checked and good
                        if (tablet) {
                            mainActivity.user = user;
                            mainActivity.myBagAdapter.user = user;
                            mainActivity.myBagAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                        if (tablet) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                                fragmentManager.popBackStack();
                            }
                        }
                        else {
                            Intent intent = new Intent();
                            intent.putExtra("user",user);
                            detailActivity.setResult(Activity.RESULT_OK,intent);
                            detailActivity.finish();
                        }
                        break;
                }
            }
        };
        MyThread thread = new MyThread(handler, username, password);
        thread.start();
    }

    public class MyThread extends Thread {
        Handler threadHandler;
        String username, password;

        MyThread(Handler myHandler, String Username, String Password){
            threadHandler = myHandler;
            username = Username;
            password = Password;
        }

        @Override
        public void run() {
            super.run();
            Cursor cursor = sqLiteDatabase.query(USERTABLE, new String[]{TABLE_ID, PASS, USER, NAME},
                    null, null, null, null, null, null);

            handler.sendEmptyMessage(0);
            cursor.moveToFirst();

            try {
                do {
                    if (username.equals(cursor.getString(cursor.getColumnIndex(USER)))) {
                        if (password.equals(cursor.getString(cursor.getColumnIndex(PASS)))) {
                            handler.sendEmptyMessage(2);
                            user.username = cursor.getString(cursor.getColumnIndex(USER));
                            user.password = cursor.getString((cursor.getColumnIndex(PASS)));
                            user.name = cursor.getString(cursor.getColumnIndex(NAME));
                            user.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                        } else handler.sendEmptyMessage(1);
                        break;
                    }
                } while (cursor.moveToNext());
                if (cursor.moveToNext() == false && user.id == -1) handler.sendEmptyMessage(1);
            } catch (CursorIndexOutOfBoundsException e) {
                handler.sendEmptyMessage(1);
            }
        }
    }

    public void userSignedIn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Log Out?");
        builder.setMessage(user.name + " is currently logged in. By clicking yes you will log out. Continue?");

        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                User noCurrent = new User(null,null,null,-1);
                if (tablet) {
                    mainActivity.user = noCurrent;
                    mainActivity.myBagAdapter.user = mainActivity.user;
                    mainActivity.myBagAdapter.notifyDataSetChanged();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    for(int j = 0; j < fragmentManager.getBackStackEntryCount(); j++) {
                        fragmentManager.popBackStack();
                    }
                } else {
                    user = noCurrent;
                    Intent intent = new Intent();
                    intent.putExtra("user",user);
                    detailActivity.setResult(Activity.RESULT_OK,intent);
                    detailActivity.finish();
                }
            }
        });
        builder.setNegativeButton("Stay Logged in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (tablet) {
                    mainActivity.onBackPressed();
                } else {
                    Intent intent = new Intent();
                    detailActivity.setResult(Activity.RESULT_CANCELED, intent);
                    detailActivity.finish();
                }
            }
        });
        builder.show();
    }
}