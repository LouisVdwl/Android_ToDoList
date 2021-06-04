package com.example.to_dolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> todoitems;
    private ArrayAdapter<String> aa;
    ListView mavariableListView;
    private NotesDbAdapter mDbHelper;
    private int mNoteNumber = 1;
    private SQLiteDatabase mDb;
    //TextView tv2 = (TextView) findViewById(R.id.tv2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        mavariableListView = (ListView) findViewById(R.id.listView1);
        fillData();
        // todoitems = new ArrayList<String>();
        // aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoitems);
       // mavariableListView.setAdapter(aa);

        mavariableListView.setOnItemClickListener((parent, view, position, id) -> {
            mDbHelper.deleteNote(id);
            fillData();
            // todoitems.remove(position);
            // aa.notifyDataSetChanged();
        });

        registerForContextMenu(mavariableListView);

    }

    public void createNote(String task){
        String noteName = "Note " + mNoteNumber++;
        mDbHelper.createNote(noteName, task);
        fillData();
    }

    public void addItems(View v){

        EditText mavariableEditText = (EditText) findViewById(R.id.input1);
        ListView mavariableListView = (ListView) findViewById(R.id.listView1);

        // todoitems.add(0, mavariableEditText.getText().toString());
        // aa.notifyDataSetChanged();
        createNote(mavariableEditText.getText().toString());
        mavariableEditText.setText("");

        fillData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    public void clearList(View v){
        mDbHelper.deleteAllNotes();
        fillData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.google_search:
                try {
                    Cursor c = (Cursor) mavariableListView.getItemAtPosition(info.position);
                    String escapedQuery = URLEncoder.encode(c.getString(2), "UTF-8");
                    Uri uri = Uri.parse("http://www.google.com/search?q=" + escapedQuery);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.maps_search:
                try {
                    Cursor c = (Cursor) mavariableListView.getItemAtPosition(info.position);
                    String escapedQuery = URLEncoder.encode(c.getString(2), "UTF-8");
                    Uri uri = Uri.parse("https://www.google.com/maps/place/" + escapedQuery);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optionDelete:
                displayPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message).setTitle(R.string.title_message);
        builder.setPositiveButton(R.string.opt1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDbHelper.deleteAllNotes();
                fillData();
            }
        });
        builder.setNegativeButton(R.string.opt2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_BODY };
        int[] to = new int[] { R.id.tv1, R.id.tv2 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        mavariableListView.setAdapter(notes);
    }
}