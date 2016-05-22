package com.example.umyhfilian.notepad;

import java.util.ArrayList;
import java.util.Date;


/**
 * Note info class that contains all information a note can have
 */
public class NoteInfo {
    String note;
    int color;
    public ArrayList<NoteInfo> noteInfoList;
    Date date;

    NoteInfo(String note, int color, Date date) {
        this.note = note;
        this.color = color;
        this.date = date;
    }
    public String getNote2() {
        return note;
    }
    public Date getDate(){return date;}
    public int getColor2(){return color;}

    public String getNote(int i){
        String note = noteInfoList.get(i).note;
        return note;
    }
    public int getNoteColor(int i, ArrayList<NoteInfo> list){
        int color = list.get(i).color;
        return color;
    }
}