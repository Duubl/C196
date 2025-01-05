package com.duubl.c196.util;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.util.Date;

public class Converter {
    @TypeConverter
    public static LocalDate fromLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long localDateToLong(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
