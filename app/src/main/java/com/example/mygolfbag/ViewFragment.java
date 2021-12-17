package com.example.mygolfbag;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.view.View.INVISIBLE;

public class ViewFragment extends Fragment {
    TextView textViewViewBrand, textViewViewLoft, textViewViewYards,
            textViewViewDesc, textViewViewFlex, textViewViewType, textViewViewShaft;
    ImageView imageView;
    MainActivity mainActivity;
    DetailActivity detailActivity;
    SQLiteDatabase sqLiteDatabase;
    boolean tablet = false;
    Cursor cursor;
    int pos;

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
            if (tablet) fragmentTransaction.replace(R.id.frameLayout, new AddClubFragment());
            else fragmentTransaction.replace(R.id.frameLayoutDetail, new AddClubFragment());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.delete) {
            MyDialog myDialog = new MyDialog(pos, tablet);
            myDialog.show(getFragmentManager(), "does this matter?");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swing);
        mp.start();

        textViewViewBrand = view.findViewById(R.id.textViewViewBrand);
        textViewViewLoft = view.findViewById(R.id.textViewViewLoft);
        textViewViewYards = view.findViewById(R.id.textViewViewYards);
        textViewViewDesc = view.findViewById(R.id.textViewViewDesc);
        textViewViewFlex = view.findViewById(R.id.textViewViewFlex);
        textViewViewType = view.findViewById(R.id.textViewViewType);
        textViewViewShaft = view.findViewById(R.id.textViewViewShaft);
        imageView = view.findViewById(R.id.imageViewView);

        try {
            mainActivity = (MainActivity) getActivity();
            sqLiteDatabase = mainActivity.myBagAdapter.sqLiteDatabase;
            tablet = true;
        } catch(Exception e) {
            detailActivity = (DetailActivity) getActivity();
            sqLiteDatabase = detailActivity.sqLiteDatabase;
        }
        User user;
        if (tablet) {
            user = mainActivity.user;
        } else {
            user = detailActivity.user;
        }
        cursor = sqLiteDatabase.query(TABLE, new String[]{TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER},
                OWNER + " = " + user.id, null, null, null, YARDS + " DESC", null);
        if (tablet) {
            pos = mainActivity.pos;
        } else {
            pos = detailActivity.pos;
        }
        cursor.moveToPosition(pos);

        textViewViewBrand.setText(cursor.getString(cursor.getColumnIndex(BRAND)));
        textViewViewLoft.setText(cursor.getString(cursor.getColumnIndex(LOFT)));
        textViewViewYards.setText(cursor.getString(cursor.getColumnIndex(YARDS)));
        textViewViewDesc.setText(cursor.getString(cursor.getColumnIndex(DESC)));
        textViewViewFlex.setText(cursor.getString(cursor.getColumnIndex(FLEX)));
        textViewViewType.setText(cursor.getString(cursor.getColumnIndex(TYPE)));
        textViewViewShaft.setText(cursor.getString(cursor.getColumnIndex(SHAFT)));

        String fileName = cursor.getString(cursor.getColumnIndex(IMAGE));
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        imageView.setImageBitmap(bitmap);

        return view;
    }
}