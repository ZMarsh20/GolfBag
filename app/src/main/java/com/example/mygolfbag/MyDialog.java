package com.example.mygolfbag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.View;

import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {
    int position;
    boolean tablet;
    User user;

    public static final String TABLE_ID = MyBagDatabaseHelper.TABLE_ID;
    public static final String TABLE = MyBagDatabaseHelper.TABLE;
    public static final String TYPE = MyBagDatabaseHelper.TYPE;
    public static final String LOFT = MyBagDatabaseHelper.LOFT;
    public static final String BRAND = MyBagDatabaseHelper.BRAND;
    public static final String SHAFT = MyBagDatabaseHelper.SHAFT;
    public static final String FLEX = MyBagDatabaseHelper.FLEX;
    public static final String YARDS = MyBagDatabaseHelper.YARDS;
    public static final String DESC = MyBagDatabaseHelper.DESC;
    public static final String IMAGE = MyBagDatabaseHelper.IMAGE;
    public static final String OWNER = MyBagDatabaseHelper.OWNER;

    public MyDialog(int i, boolean b){
        position = i;
        tablet = b;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("ARE YOU SURE?")
                .setMessage("By clicking yes you will delete this club from your bag. This can NOT be undone")
                .setNegativeButton("No, don't remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Yes, remove club", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tablet) {
                            user = ((MainActivity) getActivity()).user;
                        } else {
                            user = ((DetailActivity) getActivity()).user;
                        }
                        MyBagDatabaseHelper myBagDatabaseHelper = new MyBagDatabaseHelper(getContext());
                        SQLiteDatabase sqLiteDatabase = myBagDatabaseHelper.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.query(TABLE, new String[]{TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER},
                                OWNER + " = " + user.id, null, null, null, YARDS + " DESC", null);
                        cursor.moveToPosition(position);
                        sqLiteDatabase.delete(TABLE, TABLE_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(TABLE_ID)))});
                        if (tablet) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.myBagAdapter.notifyDataSetChanged();
                            if (mainActivity.setOn && mainActivity.myBagAdapter.myBagDatabaseHelper.getCount(mainActivity.user.id) > 14) mainActivity.textView.setVisibility(View.VISIBLE);
                            else mainActivity.textView.setVisibility(View.GONE);
                            mainActivity.onBackPressed();
                        } else {
                            Intent intent = new Intent();
                            getActivity().setResult(Activity.RESULT_CANCELED, intent);
                            getActivity().finish();
                        }
                        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.golfball);
                        mp.start();
                    }
                })
                .create();
    }
}
