package com.company;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class config {
    public static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.US);
    public static final DateTimeFormatter weekdayFmt = DateTimeFormatter.ofPattern("E").withLocale(Locale.US);
}
