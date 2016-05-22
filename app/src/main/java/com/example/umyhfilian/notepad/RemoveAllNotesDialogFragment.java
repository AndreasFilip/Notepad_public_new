package com.example.umyhfilian.notepad;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;



/**
 * Dialog fragment for when you press delete all notes
 */
public class RemoveAllNotesDialogFragment extends DialogFragment {
MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Remove all notes")
                .setMessage("Are you sure you want to remove all notes?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                        Log.i("TAG", "you pressed no");
                    }
                })
                .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something
                        Log.i("TAG", "you pressed yes");
                        mainActivity.listOfNotes.clear();
                        mainActivity.adapter.notifyDataSetChanged();
                    }
                })
                .create();
    }
}
