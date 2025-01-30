package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "terms")
public class Term {

    @PrimaryKey(autoGenerate = true)
    private int termID;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;

    public Term(int termID, String termName, LocalDate startDate, LocalDate endDate) {
        this.termID = termID;
        this.termName = termName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Term term = (Term) obj;
        return Objects.equals(termID, term.termID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termID);
    }
}
