package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "terms")
public class Term {

    @PrimaryKey(autoGenerate = true)
    private int term_id;
    private String term_name;
    private LocalDate start_date;
    private LocalDate end_date;

    public Term(int term_id, String term_name, LocalDate start_date, LocalDate end_date) {
        this.term_id = term_id;
        this.term_name = term_name;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getTerm_id() {
        return this.term_id;
    }

    /**
     * Sets the term name
     * @param name the name to be set
     */

    public void setTermName(String name) {
        this.term_name = name;
    }

    /**
     * Gets the term name
     * @return the term name
     */

    public String getTerm_name() {
        return this.term_name;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
}
