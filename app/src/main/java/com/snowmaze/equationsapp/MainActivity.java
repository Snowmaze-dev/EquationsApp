package com.snowmaze.equationsapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EquationsListAdapter.ItemClick {


    RecyclerView list;
    Button add;
    EditText equation;
    EquationsListAdapter adapter;

    @Override
    public void deleteClicked(final Equation eq) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getEquationDAO().delete(eq);
            }
        }).start();
    }

    @Override
    public void itemClicked(Equation eq) {
    }

    @Database(entities = {Equation.class}, version = 1)
    public static abstract class AppDatabase extends RoomDatabase {
        public abstract EquationDAO getEquationDAO();
    }
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(this, AppDatabase.class, "equations").build();
        equation = findViewById(R.id.equation);
        add = findViewById(R.id.add);
        list = findViewById(R.id.list);
        adapter = new EquationsListAdapter(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.setItemClickListener(this);
        add.setOnClickListener(this);
        new getEquations().execute();
    }

    @Override
    public void onClick(View v) {
        final Equation equat = new Equation(equation.getText().toString());
        try {
            equat.parse();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.getEquationDAO().insert(equat);
                }
            }).start();
            adapter.addEquation(equat);
        } catch (Exception e) {
            Toast.makeText(this, "Не уравнение " + e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    class getEquations extends AsyncTask<Void, Void, List<Equation>> {

        @Override
        protected List<Equation> doInBackground(Void... voids) {
            return db.getEquationDAO().getAllEquations();
        }

        @Override
        protected void onPostExecute(List<Equation> equations) {
            adapter.setEquations(equations);
        }
    }
}

