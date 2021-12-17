package com.example.mygolfbag;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddClubFragment extends Fragment {
    Button buttonCamera, buttonPhoto;
    EditText editTextBrand, editTextLoft, editTextAddInfo, editTextYards;
    ArrayAdapter<CharSequence> adapterType, adapterFlex;
    TextView textView, textView2, textView3, textViewImg;
    Spinner spinnerType, spinnerFlex;
    ToggleButton toggleButton;
    ImageView imageView;
    boolean skip = false;
    SQLiteDatabase sqLiteDatabase;
    DetailActivity detailActivity;
    MainActivity mainActivity;
    boolean tablet = false;
    boolean update = false;
    Cursor cursor;
    Uri selectedImage;
    String fileName;
    User user;
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
            if (check()) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.golfball);
                mp.start();
            }
        } else if (item.getItemId() == R.id.delete) {
            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.golfball);
            mp.start();
            if (tablet) {
                mainActivity.onBackPressed();
            } else {
                Intent intent = new Intent();
                detailActivity.setResult(Activity.RESULT_CANCELED, intent);
                detailActivity.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_club, container, false);
        try {
            mainActivity = (MainActivity) getActivity();
            sqLiteDatabase = mainActivity.myBagAdapter.sqLiteDatabase;
            tablet = true;
            pos = mainActivity.pos;
            user = mainActivity.myBagAdapter.user;
        } catch(Exception e) {
            detailActivity = (DetailActivity) getActivity();
            sqLiteDatabase = detailActivity.sqLiteDatabase;
            pos = detailActivity.pos;
            user = detailActivity.user;
        }
        if (pos < 0) update = false;
        else update = true;

        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swing);
        mp.start();

        buttonCamera = view.findViewById(R.id.buttonCamera);
        buttonPhoto = view.findViewById(R.id.buttonPhoto);
        imageView = view.findViewById(R.id.imageViewPreview);
        textViewImg = view.findViewById(R.id.textViewImg);
        editTextBrand = view.findViewById(R.id.editTextBrand);
        editTextLoft = view.findViewById(R.id.editTextLoft);
        textView = view.findViewById(R.id.textView3);
        editTextAddInfo = view.findViewById(R.id.editTextAddInfo);
        editTextYards = view.findViewById(R.id.editTextYards);
        textView2 = view.findViewById(R.id.textView6);
        spinnerType = view.findViewById(R.id.spinnerType);
        spinnerFlex = view.findViewById(R.id.spinnerFlex);
        textView3 = view.findViewById(R.id.textView5);
        toggleButton = view.findViewById(R.id.toggleButtonShaft);

        adapterFlex = ArrayAdapter.createFromResource(getContext(),
                R.array.flexs, android.R.layout.simple_spinner_item);
        adapterFlex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFlex.setAdapter(adapterFlex);

        adapterType = ArrayAdapter.createFromResource(getContext(),
                R.array.types, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTextYards.setVisibility(View.VISIBLE);
                editTextLoft.setVisibility(View.VISIBLE);
                spinnerFlex.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);
                skip = false;
                switch (position) {
                    case 1: //Driver
                        editTextLoft.setText("10.5");
                        break;
                    case 3: //3-wood
                        editTextLoft.setText("15");
                        break;
                    case 4: //5-wood
                        editTextLoft.setText("19");
                        break;
                    case 5: //Hybrid
                    case 6: //3-hybrid
                        editTextLoft.setText("21");
                        break;
                    case 7: //5-hybrid
                        editTextLoft.setText("27");
                        break;
                    case 8: //Iron
                    case 9: //2-iron
                        editTextLoft.setText("18");
                        break;
                    case 10: //3-iron
                        editTextLoft.setText("21");
                        break;
                    case 11: //4-iron
                        editTextLoft.setText("24");
                        break;
                    case 12: //5-iron
                        editTextLoft.setText("27");
                        break;
                    case 13: //6-iron
                        editTextLoft.setText("31");
                        break;
                    case 14: //7-iron
                        editTextLoft.setText("35");
                        break;
                    case 15: //8-iron
                        editTextLoft.setText("38");
                        break;
                    case 16: //9-iron
                        editTextLoft.setText("42");
                        break;
                    case 17: //Wedge
                    case 18: //P-wedge
                        editTextLoft.setText("46");
                        break;
                    case 19: //A-wedge
                        editTextLoft.setText("52");
                        break;
                    case 20: //S-wedge
                        editTextLoft.setText("56");
                        break;
                    case 21: //L-wedge
                        editTextLoft.setText("60");
                        break;
                    case 22: //Putter
                        editTextYards.setVisibility(View.GONE);
                        editTextLoft.setVisibility(View.GONE);
                        spinnerFlex.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);
                        textView3.setVisibility(View.GONE);
                        skip = true;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    action = Intent.ACTION_OPEN_DOCUMENT;
                } else {
                    action = Intent.ACTION_PICK;
                }
                Intent pickPhoto = new Intent(action,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        if (update) setUp();
        return view;
    }

    private void setUp() {
        try {
            cursor = sqLiteDatabase.query(TABLE, new String[]{TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER},
                    OWNER + " = " + user.id, null, null, null, YARDS + " DESC", null);
            cursor.moveToPosition(pos);
            editTextBrand.setText(cursor.getString(cursor.getColumnIndex(BRAND)));
            editTextLoft.setText(cursor.getString(cursor.getColumnIndex(LOFT)));
            editTextAddInfo.setText(cursor.getString(cursor.getColumnIndex(DESC)));
            editTextYards.setText(cursor.getString(cursor.getColumnIndex(YARDS)));
            int i;
            for (i = 0; i < adapterType.getCount(); i++)
                if (cursor.getString(cursor.getColumnIndex(TYPE)).equals(adapterType.getItem(i)))
                    break;
            spinnerType.setSelection(i);
            boolean shaft = cursor.getString(cursor.getColumnIndex(SHAFT)).equals("Steel");
            toggleButton.setSelected(shaft);
            for (i = 0; i < adapterFlex.getCount(); i++)
                if (cursor.getString(cursor.getColumnIndex(FLEX)).equals(adapterFlex.getItem(i)))
                    break;
            if (i == adapterFlex.getCount()) i = 0;
            spinnerFlex.setSelection(i);
            try {
                fileName = cursor.getString(cursor.getColumnIndex(IMAGE));
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
                imageView.setImageBitmap(bitmap);
                textViewImg.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } finally {}
        } catch (CursorIndexOutOfBoundsException e) {
            update = false;
        }
    }

    private class MyAsyncTask extends AsyncTask<Integer, Double, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.golfball);
            mp.start();
            if (tablet) {
                mainActivity.myBagAdapter.notifyDataSetChanged();
                if (mainActivity.setOn && mainActivity.myBagAdapter.myBagDatabaseHelper.getCount(user.id) > 14) mainActivity.textView.setVisibility(View.VISIBLE);
                mainActivity.onBackPressed();
            } else {
                Intent intent = new Intent();
                detailActivity.setResult(Activity.RESULT_CANCELED,intent);
                detailActivity.finish();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TYPE, String.valueOf(spinnerType.getSelectedItem()));
            contentValues.put(BRAND, String.valueOf(editTextBrand.getText()));
            contentValues.put(SHAFT, String.valueOf(toggleButton.getText()));
            contentValues.put(DESC, String.valueOf(editTextAddInfo.getText()));
            if (skip) {
                contentValues.put(LOFT, 0);
                contentValues.put(YARDS, 0);
                contentValues.put(FLEX, "Putter");
            } else {
                contentValues.put(FLEX, String.valueOf(spinnerFlex.getSelectedItem()));
                contentValues.put(YARDS, Integer.valueOf(String.valueOf(editTextYards.getText())));
                contentValues.put(LOFT, Float.valueOf(String.valueOf(editTextLoft.getText())));
            }
            contentValues.put(OWNER, user.id);
            contentValues.put(IMAGE, fileName);
            if (update) {
                cursor = sqLiteDatabase.query(TABLE, new String[]{TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER},
                        OWNER + " = " + user.id, null, null, null, YARDS + " DESC", null);
                cursor.moveToPosition(pos);
                sqLiteDatabase.update(TABLE, contentValues, TABLE_ID + "= ?",
                        new String[] {cursor.getString(cursor.getColumnIndex(TABLE_ID))});
            } else {
                sqLiteDatabase.insert(TABLE, null, contentValues);
            }
            return null;
        }
    }

    private boolean check() {
        if (editTextBrand.getText().toString().equals("")) editTextBrand.setText("Not Specified");
        if (editTextYards.getText().toString().trim().length() == 0) editTextYards.setText("0");
        if (editTextLoft.getText().toString().trim().length() == 0) editTextLoft.setText("0");
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_club.png";
            Bitmap bitmap = null;
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        selectedImage = imageReturnedIntent.getData();
                        bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        selectedImage = imageReturnedIntent.getData();
                        try {
                            InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                            if (is != null) {
                                bitmap = BitmapFactory.decodeStream(is);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            try {
                FileOutputStream fileOutputStream = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textViewImg.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            fileName = getContext().getFileStreamPath(fileName).getAbsolutePath();
            Bitmap bm = BitmapFactory.decodeFile(fileName);
            imageView.setImageBitmap(bm);
        } catch (Exception e) {}
    }
}