package com.example.umyhfilian.notepad;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import butterknife.Bind;
import butterknife.ButterKnife;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    GridLayoutManager gridLayoutManager;
    public boolean newNote = false;
    MainActivity mainActivity;
    ArrayList<NoteInfo> listOfNotes = new ArrayList<>();
    MenuItem actionsettings;
    MenuItem actionsettings2;
    MenuItem actionsettings3;
    int gridSize = 1;
    LinearLayoutManager llm;
    boolean isGrid;
    boolean hasAdded;
    Fragment frag;
    android.support.v4.app.FragmentManager fm;
    FragmentTransaction trans;
    NoteInfo noteInfo;
    SaveClass saveClass= new SaveClass();
    SharedPreferences sharedPreferences;
    Gson gson;
    public static final String FILE_NAME="filename";
    public static final String KEY_NAME = "key_name";
    private static final String TAG = "TAG";
    RVAdapter adapter;
    @Bind(R.id.recyclerView) RecyclerView rv;
    MenuItem checkDone3,pen2,trashCan1,addNote0,palette4,dots5,share,itemGroup,addNoteNew;
    Menu menu;
    int whatPage;
    boolean isAddingNote = false;
    int initialColor = 0xffff66;
    int color;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Init stuff for the first time
        initNeededStuff(savedInstanceState);
        //Load list if there is one
        loadList();
    }


    /**
     * Saves grid-state and weather if it's grid or linear-layout manager when user changes orientation
     * @param savedInstanceState bundle witch contains information you want to save
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("IS_GRID", isGrid);
        savedInstanceState.putInt("GRID_SIZE", gridSize);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Checks shared preferences for a list, and if there is, loads one
     */
    private void loadList() {
        String tmpString = sharedPreferences.getString(KEY_NAME, null);
        if (tmpString == null) {
            // There was nothing in shared.preferences, load default list
            Log.i(TAG, "nothing have been loaded");
            listOfNotes.add(new NoteInfo("1",1,date ));
            listOfNotes.add(new NoteInfo("2",1,date));
            listOfNotes.add(new NoteInfo("3",1,date));
            listOfNotes.add(new NoteInfo("4",1,date));
            saveClass.list = listOfNotes;
            adapter = new RVAdapter(listOfNotes, this);
            rv.setAdapter(adapter);
        } else {
            // There was something in shared.preferences, load it!
            Log.i(TAG, "list have been loaded");
            saveClass = gson.fromJson(tmpString, SaveClass.class);
            listOfNotes = saveClass.list;
            adapter = new RVAdapter(listOfNotes, this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Function for fetching a instance of fragment manager when inside a fragment
     * @return an instance of a fragment manager
     */
    public FragmentManager getFM(){
        return getSupportFragmentManager();
    }

    /**
     * Function for shutting down fragment inside of the NoteFragment class
     * @param frag the fragment you wish to shut down
     */
    public void shutDownFragment(Fragment frag){
        if( frag != null && frag.isVisible()){
            fm = getSupportFragmentManager();
            fm.popBackStack();
            rv.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            saveList();
        }
    }

    /**
     * Method for hiding software keyboard
     * @param input the view that opened the keyboard , most commonly a edit-text
     */
    protected void hideSoftKeyboard(EditText input) {
        input.setInputType(0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    /**
     * Method to force showing software keyboard
     */
    protected void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    //makes the buttons that should be visible in this activity visible
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        fm = getSupportFragmentManager();
        trans = fm.beginTransaction();
        frag = fm.findFragmentById(R.id.relativeLay);
        if(frag == null) {
            //If fragment is null then this should be MainActivity
            setTitleOfApp("NotePad");
            //Recycler view should then be visible
            rv.setVisibility(View.VISIBLE);
            //Also buttons that are needed
            dots5.setVisible(true);
            addNote0.setVisible(true);
            actionsettings.setVisible(true);
            //The palette button should not be visible
            palette4.setVisible(false);
        }
        return true;
    }
    //TODO: Finds all menu buttons, this should probably be rendered useless if I bind them with Butterknife at the top!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        actionsettings = menu.findItem(R.id.action_settings);
        actionsettings2 = menu.findItem(R.id.action_settings2);
        actionsettings3 = menu.findItem(R.id.action_settings3);
        itemGroup = menu.findItem(R.id.group_aSettings);
        addNoteNew = menu.findItem(R.id.add_button);
        share = menu.findItem(R.id.share_button).setVisible(false);
        addNote0 = menu.findItem(R.id.add_button).setVisible(true);
        trashCan1 = menu.findItem(R.id.remove_button).setVisible(false);
        pen2 = menu.findItem(R.id.edit_button).setVisible(false);
        checkDone3 = menu.findItem(R.id.done_button).setVisible(false);
        palette4 = menu.findItem(R.id.color_button).setVisible(false);
        dots5 = menu.getItem(7);

        //Div stuff
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onPause() {
        Log.i(TAG, "On PAUSE");
        saveList();
        super.onPause();
    }
    // logic for on back pressed when adding/editing a note
    @Override
    public void onBackPressed() {
        rv.setVisibility(View.VISIBLE);
        NoteFragment noteFragment = (NoteFragment) fm.findFragmentByTag("NEW_FRAGMENT");
        if(noteFragment != null) {
            if (noteFragment.editText.getText().toString().equals("")) {
                Log.d(TAG, "Edit text was empty, not saving that");
            }
            if (!noteFragment.editText.getText().toString().equals("")) {
                //addNoteFragment.saveNote();
                Log.d(TAG, "Edit text wasn't empty, i'm saving that");
            }
            if (noteFragment.newNote && hasAdded){
                noteFragment.addNewNote();
                hasAdded = false;
            }
        }
        super.onBackPressed();
    }
    @Override
    public void onDestroy() {
        saveList();
        super.onDestroy();
    }

    /**
     * Method for saving note list
     */
    public void saveList(){
        String jsonString = gson.toJson(saveClass);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, jsonString);
        editor.putBoolean("GRID_STATE", isGrid);
        editor.putInt("GRID_SIZE", gridSize);
        editor.apply();
    }


    void openColorDialog(final EditText editText, final RelativeLayout relativeLayout) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                editText.setBackgroundColor(color);
                relativeLayout.setBackgroundColor(color);
                mainActivity.color = color;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("INT_KEY2", color);
                editor.apply();
                Log.i(TAG, "the color is now saved");
            }
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                mainActivity.color = initialColor;
            }});
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Reverse the list
        if (id == R.id.action_settings) {
            Collections.reverse(listOfNotes);
            adapter.notifyDataSetChanged();
            saveList();
            return true;
        }
        //Sort alphabetically
        if (id == R.id.action_settings2){
            Collections.sort(listOfNotes, new Comparator<NoteInfo>() {
                public int compare(NoteInfo v1, NoteInfo v2) {
                    return v1.getNote2().compareTo(v2.getNote2());
                }
            });
            adapter.notifyDataSetChanged();
            saveList();
            return true;
        }
        //Sort by Date
        if (id == R.id.action_settings3){
            Collections.sort(listOfNotes, new Comparator<NoteInfo>() {
                public int compare(NoteInfo v1, NoteInfo v2) {
                    return v1.getDate().compareTo(v2.getDate());
                }
            });
            adapter.notifyDataSetChanged();
            Collections.reverse(listOfNotes);
            saveList();
            return true;
        }
        //Randomize the list
        if (id == R.id.action_settings4){
            long seed = System.nanoTime();
            Collections.shuffle(listOfNotes, new Random(seed));
            adapter.notifyDataSetChanged();
            saveList();
            return true;
        }
        //Sort by color
        if (id == R.id.sort_by_color){
            Comparator<NoteInfo> StuRollno = new Comparator<NoteInfo>() {

                public int compare(NoteInfo s1, NoteInfo s2) {

                    int rollno1 = s1.getColor2();
                    int rollno2 = s2.getColor2();

	        /*For ascending order*/
                    return rollno1-rollno2;
                }};

            Collections.sort(listOfNotes, StuRollno);
            adapter.notifyDataSetChanged();
            saveList();
            return true;
        }
        //User pressed the remove all notes button
        if(id == R.id.remove_all){
            //SHOW FRAGMENT
            Log.i(TAG, "PRESSED REMOVE ALL");
            FragmentManager fm = getSupportFragmentManager();
            RemoveAllNotesDialogFragment removeAllNotesDialogFragment = new RemoveAllNotesDialogFragment();
            removeAllNotesDialogFragment.show(fm, "fragment_remove_all_notes");
        }
        // Code for the addNote button
        if(id == R.id.add_button){
            if(!isAddingNote) {
                newNote = true;
                Bundle bundle = new Bundle();
                bundle.putBoolean("NEW_NOTE", newNote);
                addNoteNew.setVisible(false);
                fm = getSupportFragmentManager();
                trans = fm.beginTransaction();
                frag = new NoteFragment();
                frag.setArguments(bundle);
                trans.replace(R.id.relativeLay, frag, "NEW_FRAGMENT");
                trans.addToBackStack(null);
                trans.commit();
                rv.setVisibility(View.GONE);
                return true;
            }
        }
        // Code for show notes as list button
        if(id == R.id.list_button){
            isGrid = false;
            gridSize = 1;
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            Log.i(TAG, "PRESSED LIST");
            return true;
        }
        // Code for show notes as grid button
        if(id == R.id.grid_button){
            Log.i(TAG,"PRESSED GRID");
            isGrid = true;
            if(gridSize == 0){
                gridLayoutManager = new GridLayoutManager(this,1);
                rv.setLayoutManager(gridLayoutManager);
                gridSize++;
                return true;
            }
            if(gridSize == 1){
                gridLayoutManager = new GridLayoutManager(this,2);
                rv.setLayoutManager(gridLayoutManager);
                gridSize++;
                return true;
            }
            if(gridSize == 2){
                gridLayoutManager = new GridLayoutManager(this,3);
                rv.setLayoutManager(gridLayoutManager);
                gridSize++;
                return true;
            }
            if(gridSize == 3){
                gridLayoutManager = new GridLayoutManager(this,3);
                rv.setLayoutManager(gridLayoutManager);
                gridSize = 0;
                return true;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setTitleOfApp(String name){
        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle(name);
        }
    }
    private void initNeededStuff(Bundle savedInstanceState){
        setTitleOfApp("NotePad");
        date = new Date();
        date.getTime();
        noteInfo = new NoteInfo("",0,date);
        whatPage = 0;
        mainActivity = this;
        gson = new Gson();
        sharedPreferences = getSharedPreferences(FILE_NAME,0);
        if (sharedPreferences != null) {
            isGrid = sharedPreferences.getBoolean("GRID_STATE", false);
            gridSize = sharedPreferences.getInt("GRID_SIZE",1);
        }
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            isGrid = savedInstanceState.getBoolean("IS_GRID");
            gridSize = savedInstanceState.getInt("GRID_SIZE");
        }
        // checks if layout is a grid and how big it is
        if(isGrid){
            if(gridSize == 0){
                gridLayoutManager = new GridLayoutManager(this,1);
                rv.setLayoutManager(gridLayoutManager);
            }
            else {
                gridLayoutManager = new GridLayoutManager(this, gridSize);
                rv.setLayoutManager(gridLayoutManager);
            }
        }
        else{
            //if not grid set as linear layout manager (list)
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
        }
    }
}