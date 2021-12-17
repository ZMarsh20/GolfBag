package com.example.mygolfbag;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

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
import android.widget.EditText;
import android.widget.ProgressBar;

public class SignUpFragment extends Fragment {
    EditText editTextUsername, editTextPassword, editTextConfirm, editTextName;
    boolean tablet, fault;
    ProgressBar progressBar;
    String username, password;
    MainActivity mainActivity;
    DetailActivity detailActivity;
    Handler handler;
    SQLiteDatabase sqLiteDatabase;
    MyBagDatabaseHelper myBagDatabaseHelper;

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
            if (isGood()) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                check();
            } else {
                if (fault) {
                    editTextUsername.setBackgroundColor(RED);
                } else {
                    editTextUsername.setBackgroundColor(WHITE);
                    editTextPassword.setBackgroundColor(RED);
                    editTextConfirm.setBackgroundColor(RED);
                }
            }

        } else if (item.getItemId() == R.id.delete) {
            if (tablet) clearHistory();
            else {
                Intent intent = new Intent();
                detailActivity.setResult(Activity.RESULT_CANCELED,intent);
                detailActivity.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        try {
            mainActivity = (MainActivity) getActivity();
            tablet = true;
        } catch(Exception e) {
            detailActivity = (DetailActivity) getActivity();
        }

        myBagDatabaseHelper = new MyBagDatabaseHelper(getContext());
        sqLiteDatabase = myBagDatabaseHelper.getReadableDatabase();

        editTextConfirm = view.findViewById(R.id.editTextConfirmPassword);
        editTextName = view.findViewById(R.id.editTextPersonName);
        editTextPassword = view.findViewById(R.id.editTextPasswordSignUp);
        editTextUsername = view.findViewById(R.id.editTextUsernameSignup);
        progressBar = view.findViewById(R.id.progressBarSignUp);

        return view;
    }

    public boolean isGood() {
        if (editTextUsername.getText().toString().equals("")) {
            fault = true;
            return false;
        } else if (editTextPassword.getText().toString().equals("") || editTextConfirm.getText().toString().equals("")
                || (editTextPassword.getText().toString().equals(editTextConfirm.getText().toString())) == false) {
            fault = false;
            return false;
        } else return true;
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Uh oh");
                        builder.setMessage("Looks like that username is taken :(");
                        builder.show();
                        break;
                    case 2: //checked and good
                        progressBar.setVisibility(View.GONE);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(USER, username);
                        contentValues.put(PASS, password);
                        String name = editTextName.getText().toString();
                        contentValues.put(NAME, name);
                        sqLiteDatabase.insert(USERTABLE, null, contentValues);
                        User user = new User(username, password, name, myBagDatabaseHelper.getCountUsers());
                        if (tablet) {
                            mainActivity.user = user;
                            mainActivity.myBagAdapter.user = user;
                        }
                        else detailActivity.user = user;
                        if (tablet) clearHistory();
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
            while (cursor.moveToNext()) {
                if (username.equals(cursor.getString(cursor.getColumnIndex(USER)))) {
                    handler.sendEmptyMessage(1);
                    break;
                }
            }
            if (!cursor.moveToNext()) handler.sendEmptyMessage(2);
        }
    }

    void clearHistory() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }
}