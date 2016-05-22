package com.example.umyhfilian.notepad;
import java.util.Date;

/**
 * Just a help class containing some methods to do simple stuff like getting dates etc
 */
public final class HelpFunctionClass {
    private HelpFunctionClass() {

    }
    /**
     * Gets the current date
     * @return Current date
     */
    public static Date getDate(){
        Date date = new Date();
        date.getTime();
        return date;
    }

}