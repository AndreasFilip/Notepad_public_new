package com.example.umyhfilian.notepad;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.NoteViewHolder>{

    ArrayList<NoteInfo> list = new ArrayList<>();
    MainActivity mainActivity;

    RVAdapter(ArrayList<NoteInfo> list, MainActivity mainActivity){
        this.list = list;
        this.mainActivity = mainActivity;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_cards, viewGroup, false);
         final NoteViewHolder nvh = new NoteViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getTag();
            }
        });
        return nvh;
    }
    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, final int i) {
        String note = list.get(noteViewHolder.getAdapterPosition()).note;
        int lenght = note.length();
        int maxlenght = 45;
        //Checks how big grid is and adjusts text-size and other changes to accommodate to the size
        if(mainActivity.isGrid && mainActivity.gridSize == 2){
            maxlenght = 40;
            noteViewHolder.dateTxtView.setText(list.get(i).date.toString().substring(0, 20));
        }
        if(mainActivity.isGrid && mainActivity.gridSize == 3  || mainActivity.gridSize == 4 || mainActivity.gridSize == 0){
            maxlenght = 25;
            noteViewHolder.dateTxtView.setText(list.get(i).date.toString().substring(0,16));
        }
        else{
            noteViewHolder.dateTxtView.setText(list.get(i).date.toString());
        }
        noteViewHolder.noteTxtView.setBackgroundColor(list.get(i).color);
        if (lenght < maxlenght){
            noteViewHolder.noteTxtView.setText(note);
        }
        else {
            String sub = note.substring(0,maxlenght);
            noteViewHolder.noteTxtView.setText(sub + "...");
        }
        //Puts a onClick event on the cardview so the user is taken to the edit view when pressed
        noteViewHolder.cv.setTag(i);
        noteViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView = (RecyclerView) mainActivity.findViewById(R.id.recyclerView);
                android.support.v4.app.FragmentManager fm = mainActivity.getFM();
                FragmentTransaction trans = fm.beginTransaction();
                trans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                Fragment fragment = new NoteFragment();
                Bundle args = new Bundle();
                Boolean editNote = true;
                args.putString("KEY", list.get(i).note);
                args.putInt("INT_KEY", i);
                boolean firsTimeEdit = true;
                args.putBoolean("FIRST_TIME_EDIT", firsTimeEdit);
                args.putBoolean("EDIT_NOTE", editNote);
                fragment.setArguments(args);
                trans.replace(R.id.relativeLay, fragment, "NEW_FRAGMENT");
                trans.addToBackStack(null);
                trans.commit();
                recyclerView.setVisibility(View.GONE);
            }
        });

        noteViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("TAG","LONG CLICK" );
                return  true;
            }
        });
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.card_view) CardView cv;
        @Bind(R.id.cardTxtVNote) TextView noteTxtView;
        @Bind(R.id.txtViewDate) TextView dateTxtView;

        NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}