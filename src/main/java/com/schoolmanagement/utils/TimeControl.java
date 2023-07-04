package com.schoolmanagement.utils;

import java.time.LocalTime;

public class TimeControl {

    public static boolean check(LocalTime start, LocalTime stop) {
        return !start.isBefore(stop);
    }
}
