package com.example.mygolfbag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.view.View.INVISIBLE;

public class MyBagAdapter extends RecyclerView.Adapter {
    Context context;
    static Cursor cursor;
    static SQLiteDatabase sqLiteDatabase;
    static MyBagDatabaseHelper myBagDatabaseHelper;
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

    private Listener listener;

    public interface Listener{
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public MyViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

    public MyBagAdapter(Context context, User mainUser) {
        myBagDatabaseHelper = new MyBagDatabaseHelper(context);
        sqLiteDatabase = myBagDatabaseHelper.getReadableDatabase();
        user = mainUser;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bag_card_view, parent, false);
        CardView cardView = view.findViewById(R.id.cardView);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        CardView cardView = myViewHolder.getCardView();

        cursor = sqLiteDatabase.query(TABLE, new String[]{TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER},
                OWNER + " = " + user.id, null, null, null, YARDS + " DESC", null);
        cursor.moveToPosition(position);

        ImageView imageView = cardView.findViewById(R.id.imageView);
        TextView textViewType = cardView.findViewById(R.id.textViewCardType);
        TextView textViewYards = cardView.findViewById(R.id.textViewCardYards);
        TextView textViewBrand = cardView.findViewById(R.id.textViewCardBrand);
        TextView textViewLoft = cardView.findViewById(R.id.textViewCardLoft);
        TextView textViewShaft = cardView.findViewById(R.id.textViewCardShaft);
        TextView textViewFlex = cardView.findViewById(R.id.textViewCardFlex);

        int yards = cursor.getInt(cursor.getColumnIndex(YARDS));
        float loft = cursor.getFloat(cursor.getColumnIndex(LOFT));
        String type = cursor.getString(cursor.getColumnIndex(TYPE));
        String flex = cursor.getString(cursor.getColumnIndex(FLEX));
        String shaft = cursor.getString(cursor.getColumnIndex(SHAFT));
        String brand = cursor.getString(cursor.getColumnIndex(BRAND));
        String fileName = cursor.getString(cursor.getColumnIndex(IMAGE));

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        imageView.setImageBitmap(bitmap);

        textViewBrand.setText(brand);
        textViewFlex.setText(flex);
        textViewLoft.setText(Float.toString(loft) + '°');
        textViewShaft.setText(shaft);
        textViewType.setText(type);
        textViewYards.setText(yards + " Yds");


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBagDatabaseHelper.getCount(user.id);
    }
}
