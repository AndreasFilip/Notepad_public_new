package com.example.umyhfilian.notepad;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class NoteFragment extends Fragment {
    MainActivity mainActivity;
    RelativeLayout relativeLayout;
    SharedPreferences prefs;
    EditText editText;
    private static final String TAG = "TAG";
    public static final String FILE_NAME="filename";
    MenuItem clrButton, doneButton, share;
    ImageView mimageView;
    ShareActionProvider mShareActionProvider;
    ColorDrawable cd;
    int colorCode;
    int i;
    int initialNoteColor;
    int tmpint;
    boolean isDeleted = false;
    boolean hasAddedNote;
    boolean booleanIsDeleted;
    boolean newNote = false;
    boolean editNote = false;
    private static final String KEY_TEXT_VALUE = "textValue";
    public static final String INT_KEY = "int_key";

    // Required empty public constructor
    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note_fragment, container, false);
        Log.i(TAG, "IN ON CREATE ADD FRAG");
        setHasOptionsMenu(true);
        findViews(view);
        Bundle args = getArguments();
        mainActivity.hasAdded = true;
        newNote = args.getBoolean("NEW_NOTE");
        // Checks whether or not it's a new note
        if(newNote){
            Log.i(TAG,"IS NEW NOTE");
            isNewNote(savedInstanceState);
        }
        else {
            Log.i(TAG,"ADD NOTE = FALSE");
        }
        //Checks if user is editing an existing note
        editNote = args.getBoolean("EDIT_NOTE");
        if(editNote){
           Log.i(TAG,"EDIT NOTE = TRUE");
        isEditNote();
        }

        return view;
    }

    /**
     * Finds's all the required Views
     * @param view Parent of all the views
     */
    private void findViews(View view){
        hasAddedNote = false;
        editText = (EditText)view.findViewById(R.id.editFragEditTxt);
        mainActivity = (MainActivity)getActivity();
        relativeLayout = (RelativeLayout)view.findViewById(R.id.relLayAddNote);
        mimageView = (ImageView)view.findViewById(R.id.imgViewFrag);
        mainActivity.showSoftKeyboard();
        editText.requestFocus();
    }

    /**
     * Function that contains the logic required for the app to run when user is editing a note
     */
    private void isEditNote(){
        colorCode = getCurrentEditTextColor(editText);
        editText.setBackgroundColor(colorCode);
        booleanIsDeleted = false;
        Bundle args = getArguments();
        String note = args.getString("KEY");
        i = args.getInt("INT_KEY");
        editText.setText(note);
        editText.setSelection(editText.length());
        initialNoteColor = mainActivity.noteInfo.getNoteColor(i, mainActivity.listOfNotes);
        tmpint = initialNoteColor;
        mainActivity.color = mainActivity.noteInfo.getNoteColor(i, mainActivity.listOfNotes);
        editText.setBackgroundColor(mainActivity.noteInfo.getNoteColor(i, mainActivity.listOfNotes));
        colorCode = getCurrentEditTextColor(editText);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.i(TAG, "FOUCS");
                    editText.setSelection(editText.getText().length());
                }
                if (!hasFocus) {
                    Log.i(TAG, "NOT FOUCS");
                }
            }
        });

    }

    /**
     * Save logic when user changes screen orientation
     * @param savedInstanceState Bundle that contains what you want to save
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        //Save text and color when user changes orientation
        if(newNote) {
            colorCode = getCurrentEditTextColor(editText);
            savedInstanceState.putString(KEY_TEXT_VALUE, editText.getText().toString());
            savedInstanceState.putInt(INT_KEY, colorCode);
        }
        // Sets the text in the EditText to be the share provider so that when user presses share the text is saved and shared
        if(editNote){
            colorCode = getCurrentEditTextColor(editText);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mainActivity.share);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Function that contains the logic required for the app to run when user is creating a new note
     */
    private void isNewNote(Bundle savedInstanceState){
        //Om användaren skapar en ny note
        prefs = this.getActivity().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String text = savedInstanceState.getString(KEY_TEXT_VALUE);
            int color = savedInstanceState.getInt(INT_KEY);
            editText.setText(text);
            editText.setBackgroundColor(color);
        }
    }

    /**
     * Function for getting a shareIntent required for sharing something with the share button
     * @return Intent used for sharing
     */
    private Intent getDefaultShareIntent(){
        //Share intent för share knappen
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
        intent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());
        return intent;
    }

    /**
     * Method that runs when a fragment is detached
     */
    @Override
    public void onDetach() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
        super.onDetach();
        Log.d(TAG, "ON DETATCH");
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AddNoteFrag has been destroyed");
        if(newNote) {
            colorCode = getCurrentEditTextColor(editText);
            editText.setBackgroundColor(colorCode);
            //shuts down fragment
            mainActivity.shutDownFragment(this);
        }
        // if edit text isn't empty save the text in it!
        if(editNote && !isDeleted && !editText.getText().toString().equals("")){
            saveNote();
        }
        else if (editNote && !isDeleted){
            mainActivity.listOfNotes.remove(i);
            mainActivity.adapter.notifyDataSetChanged();
            mainActivity.rv.setVisibility(View.VISIBLE);
            mainActivity.saveList();
        }
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
        newNote = false;
        editNote = false;
    }

    /**
     * Gets current color of an View
     */
    private int getCurrentEditTextColor(EditText editText){
        cd = (ColorDrawable) editText.getBackground();
        colorCode = cd.getColor();
        return colorCode;
    }

    /**
     * Function for adding a new note
     */
    protected void addNewNote(){
        hasAddedNote = true;
        String text = editText.getText().toString();
        mainActivity.hideSoftKeyboard(editText);
        // if edit text is empty, don't do anything and close the fragment
        if (text.equals("")){
            editText.setVisibility(View.INVISIBLE);
            mainActivity.shutDownFragment(this);
        } else {
            //Else add the color and text to the note and save the list
            colorCode = getCurrentEditTextColor(editText);
            mainActivity.listOfNotes.add(0, new NoteInfo(editText.getText().toString(), colorCode, HelpFunctionClass.getDate()));
            mainActivity.adapter.notifyDataSetChanged();
            editText.setVisibility(View.INVISIBLE);
            mainActivity.saveList();
        }
    }

    /**
     * Function for saving a Note if user is editing one
     */
    private void saveNote(){
        mainActivity.hideSoftKeyboard(editText);
        colorCode = getCurrentEditTextColor(editText);
        View recyclerV = getActivity().findViewById(R.id.recyclerView);
        recyclerV.setVisibility(View.VISIBLE);
        //If of course user doesn't press the delete button to delete it
        if(!booleanIsDeleted) {
            //Color is different from saved color/previous color
            if(colorCode != mainActivity.noteInfo.getNoteColor(i,mainActivity.listOfNotes)){
                Log.d(TAG, "color is dif");
                mainActivity.listOfNotes.set(i, new NoteInfo(editText.getText().toString(), colorCode, HelpFunctionClass.getDate()));
                mainActivity.adapter.notifyDataSetChanged();
                mainActivity.saveList();
                recyclerV.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);
            }
            else {
                //Color is not different from the saved/previous one
                Log.d(TAG, "color is not dif\n");
                mainActivity.listOfNotes.set(i, new NoteInfo(editText.getText().toString(), mainActivity.noteInfo.getNoteColor(i, mainActivity.listOfNotes), HelpFunctionClass.getDate()));
                mainActivity.adapter.notifyDataSetChanged();
                mainActivity.saveList();
                recyclerV.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);
                mainActivity.color = mainActivity.initialColor;
            }
        }
        else {
            recyclerV.setVisibility(View.VISIBLE);
            editText.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_button : {
                if(newNote) {
                    //User presses the done button , add note, hide keyboard, remove fragment and set Main activity view to visible
                    addNewNote();
                    item.setVisible(false);
                    mainActivity.hideSoftKeyboard(editText);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    mainActivity.rv.setVisibility(View.VISIBLE);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(this);
                    trans.commit();
                    manager.popBackStack();
                }
                if(editNote){
                    // User is editing an note if empty , remove it
                    if(editText.getText().toString().equals("")){
                        mainActivity.listOfNotes.remove(i);
                        mainActivity.adapter.notifyDataSetChanged();
                        mainActivity.hideSoftKeyboard(editText);
                        mainActivity.saveList();
                    }
                    //if not empty save the note
                    else {
                        saveNote();
                    }
                editNote = false;
                    mainActivity.rv.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                }
                    return true;
            }
            //User presses the color button, the color dialog window opens
            case R.id.color_button : {
                mainActivity.openColorDialog(editText,relativeLayout);
                return true;
            }
            //Doesn't work yet?
            case R.id.share_button : {
                Log.i(TAG, "you pressed SHARE");
                return true;
            }
            //If user presses the edit (pen-button) then set focus on edit text
            case R.id.edit_button : {
                editText.setSelection(editText.getText().length());
                editText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
            case R.id.remove_button : {
                //If user is making a new note and pressing the remove button then don't save the note
                if(newNote){
                    mainActivity.hideSoftKeyboard(editText);
                    booleanIsDeleted = true;
                }
                //removes note that is being edited
                if(editNote){
                    mainActivity.listOfNotes.remove(i);
                    mainActivity.hideSoftKeyboard(editText);
                    booleanIsDeleted = true;
                }
                isDeleted = true;
                editText.setVisibility(View.INVISIBLE);
                mainActivity.shutDownFragment(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        if(newNote) {
            //finds buttons that should be visible for new-note and makes them visible and others invisible
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_settings2).setVisible(false);
            menu.findItem(R.id.action_settings3).setVisible(false);
            menu.findItem(R.id.remove_all).setVisible(false);
            menu.findItem(R.id.add_button).setVisible(false);
            menu.findItem(R.id.list_button).setVisible(false);
            menu.findItem(R.id.grid_button).setVisible(false);
            menu.findItem(R.id.done_button).setVisible(true);
            menu.findItem(R.id.color_button).setVisible(true);
            menu.findItem(R.id.remove_button).setVisible(true);
            menu.findItem(R.id.grid_button).setVisible(false);
            clrButton = menu.findItem(R.id.color_button);
        }
        //finds buttons that should be visible for edit-note and makes them visible and others invisible
        if(editNote){
            menu.findItem(R.id.remove_all).setVisible(false);
            doneButton = menu.findItem(R.id.done_button).setVisible(true);
            menu.findItem(R.id.add_button).setVisible(false);
            menu.findItem(R.id.add_button).setVisible(false);
            menu.findItem(R.id.edit_button).setVisible(true);
            menu.findItem(R.id.remove_button).setVisible(true);
            menu.findItem(R.id.color_button).setVisible(true);
            menu.setGroupVisible(R.id.group_aSettings, false);
            menu.findItem(R.id.grid_button).setVisible(false);
            menu.findItem(R.id.list_button).setVisible(false);
            share = menu.findItem(R.id.share_button).setVisible(true);
            getShareIntent();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Gets a share intent for the share button
     */
    private void getShareIntent(){
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mainActivity.share);
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
    }
}
