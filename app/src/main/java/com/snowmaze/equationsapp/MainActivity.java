package com.snowmaze.equationsapp;

import android.app.AlertDialog;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements EquationsListAdapter.ItemClick {


    RecyclerView list;
    EquationsListAdapter adapter;
    Toolbar toolbar;

    @Override
    public void deleteClicked(final Equation eq) {
        new replace().execute(adapter.getEquations());
    }

    @Override
    public void itemClicked(Equation eq) {
    }

    @Override
    public void itemSwapped() {
        new replace().execute(adapter.getEquations());
    }

    @Database(entities = {Equation.class}, version = 1)
    public static abstract
    class AppDatabase extends RoomDatabase {
        public abstract EquationDAO getEquationDAO();
    }
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(this, AppDatabase.class, "equations").build();
        list = findViewById(R.id.list);
        toolbar = findViewById(R.id.toolbar_main);
        adapter = new EquationsListAdapter();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        ItemTouchHelper.Callback callback =
                new ItemMoveCallBack(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(list);
        adapter.setItemClickListener(this);
        setSupportActionBar(toolbar);
        new getEquations().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage("Введите уравнение");
        alert.setTitle("Доабвить новое уравнение");
        alert.setView(edittext);

        alert.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int id = 0;
                if(!(adapter.getEquations().size() == 0)) {
                    for (Equation equation : adapter.getEquations()) {
                        if (equation.getId() > id) {
                            id = equation.getId();
                        }
                    }
                        id += 1;
                }
                final Equation equat = new Equation(edittext.getText().toString(),id);
                try {
                    equat.parse();
                    insert(equat);
                    adapter.addEquation(equat);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
        return true;
    }

    public void insert(final Equation equation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getEquationDAO().insert(equation);
            }
        }).start();
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
    class replace extends  AsyncTask<List<Equation>, Void, Void> {

        @Override
        protected Void doInBackground(List<Equation>... lists) {
            db.getEquationDAO().clearTable();
            db.getEquationDAO().insertAll(lists[0]);
            return null;
        }
    }
}

