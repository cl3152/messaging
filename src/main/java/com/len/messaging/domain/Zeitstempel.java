package com.len.messaging.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert einen Zeitstempel in der Datenbank in der Form yyyyMMddHHmmssSSS.
 * Ermöglicht das addieren von Werten in diesem Format.
 */
public final class Zeitstempel {

    private static final String FELD_JAHR  = "jahr";
    private static final String FELD_MONAT = "monat";
    private static final String FELD_TAG = "tag";
    private static final String FELD_STD = "std";
    private static final String FELD_MIN = "min";
    private static final String FELD_SEC = "sec";
    private static final String FELD_MS  = "ms";
    private static final String TIME_PATTERN = "yyyyMMddHHmmssSSS";
    public static final long MIN_TSTAMP = 1000_00_00__00_00_00_000L;
    public static final long MAX_TSTAMP = 9999_99_99__99_99_99_999L;

    private static int[] anzahlTageImMonat = new int[]{Integer.MAX_VALUE-1,31,29,31,30,31,30,31,31,30,31,30,31};

    // Merke die einzelnen Bestandteile als Map, damit später per Referenz darauf zugegriffen werden kann.
    private Map<String, Long> daten = new HashMap<>();



    private Zeitstempel(long jahr, long monat, long tag, long std, long min, long sec, long ms) {
        ms(ms);
        sec(sec);
        min(min);
        std(std);
        tag(tag);
        monat(monat);
        jahr(jahr);
    }



    public Zeitstempel ms(long ms) {
        daten.put(FELD_MS, ms);
        return this;
    }

    public Zeitstempel sec(long sec) {
        daten.put(FELD_SEC, sec);
        return this;
    }

    public Zeitstempel min(long min) {
        daten.put(FELD_MIN, min);
        return this;
    }

    public Zeitstempel std(long std) {
        daten.put(FELD_STD, std);
        return this;
    }

    public Zeitstempel tag(long tag) {
        daten.put(FELD_TAG, tag);
        return this;
    }

    public Zeitstempel monat(long monat) {
        daten.put(FELD_MONAT, monat);
        return this;
    }

    public Zeitstempel jahr(long jahr) {
        daten.put(FELD_JAHR, jahr);
        return this;
    }

    public long ms() {
        return daten.get(FELD_MS);
    }

    public long sec() {
        return daten.get(FELD_SEC);
    }

    public long min() {
        return daten.get(FELD_MIN);
    }

    public long std() {
        return daten.get(FELD_STD);
    }

    public long tag() {
        return daten.get(FELD_TAG);
    }

    public long monat() {
        return daten.get(FELD_MONAT);
    }

    public long jahr() {
        return daten.get(FELD_JAHR);
    }






    public static Zeitstempel parse(long tstamp) {
        if (tstamp < MIN_TSTAMP || tstamp > MAX_TSTAMP) {
            throw new IllegalArgumentException(String.format(
                "Zu parsender Wert (%d) außerhalb der Grenzen (%d .. %d).",
                tstamp, MIN_TSTAMP, MAX_TSTAMP
            ));
        }
        char[] ziffern = String.valueOf(tstamp).toCharArray();
        return new Zeitstempel(
            asInt(ziffern[0], ziffern[1], ziffern[2], ziffern[3]),
            asInt(ziffern[4], ziffern[5]),
            asInt(ziffern[6], ziffern[7]),
            asInt(ziffern[8], ziffern[9]),
            asInt(ziffern[10], ziffern[11]),
            asInt(ziffern[12], ziffern[13]),
            asInt(ziffern[14], ziffern[15], ziffern[16])
        );
    }

    public static Zeitstempel parse(String tstamp) {
        return parse(Long.parseLong(tstamp));
    }

    public static Zeitstempel now() {
        return parse(new SimpleDateFormat(TIME_PATTERN).format(new Date()));
    }

    public static Zeitstempel empty() {
        return new Zeitstempel(0, 0, 0, 0, 0, 0, 0);
    }



    public long toLong() {
        return Long.parseLong(toString());
    }

    @Override
    public String toString() {
        return String.format("%04d%02d%02d%02d%02d%02d%03d", jahr(), monat(), tag(), std(), min(), sec(), ms());
    }

    public String toFormattedString() {
        return String.format("%04d.%02d.%02d %02d:%02d:%02d,%03d", jahr(), monat(), tag(), std(), min(), sec(), ms());
    }



    /**
     * Ermittelt die Differenz zwischen zwei Zeitpunkten in Millisekunden.
     * @param to
     * @return
     */
    public long differenceInMS(Zeitstempel to) {
        long diff = 0;
        try {
            SimpleDateFormat sdfFullPattern = new SimpleDateFormat(TIME_PATTERN);
            Date self  = sdfFullPattern.parse(this.toString());
            Date other = sdfFullPattern.parse(to.toString());

            diff = self.getTime() - other.getTime();

            return diff >= 0 ? diff : -diff;
        } catch (ParseException e) {
            // Kann prinzipiell nicht vorkommen, da nur mit eigenen Werten
            // auf angepassten Pattern gearbeitet wird.
            return 0;
        }
    }



    public Zeitstempel increase(long incTStamp) {
        long incMS    = incTStamp % 1000;
        long incSec   = (incTStamp / 1000) % 100;
        long incMin   = (incTStamp / 100000) % 100;
        long incStd   = (incTStamp / 10000000) % 100;
        long incTag   = (incTStamp / 1000000000) % 100;
        long incMon   = (incTStamp / 100000000000L) % 100;
        long incJahr  =  incTStamp / 10000000000000L;

        ms( ms() + incMS );
        sec( sec() + incSec );
        min( min() + incMin );
        std( std() + incStd );
        tag( tag() + incTag );
        monat( monat() + incMon );
        jahr( jahr() + incJahr );

        uebertrag(FELD_MS, FELD_SEC, 1000);
        uebertrag(FELD_SEC, FELD_MIN, 60);
        uebertrag(FELD_MIN, FELD_STD, 60);
        uebertrag(FELD_STD, FELD_TAG, 24);
        uebertrag(FELD_TAG, FELD_MONAT, anzahlTageImMonat[(int) monat() % anzahlTageImMonat.length] + 1L);
        uebertrag(FELD_MONAT, FELD_JAHR, 12);

        if (tag() == 0) {
            tag(1);
        }
        if (monat() == 0) {
            monat(1);
        }

        return this;
    }



    private static int asInt(char... cs) {
        return Integer.parseInt(new String(cs));
    }



    private void uebertrag(String von, String nach, long max) {
        while (daten.get(von) < 0) {
            daten.put(von, daten.get(von) + max);
            daten.put(nach, daten.get(nach) - 1);
        }
        while (daten.get(von) >= max) {
            daten.put(von, daten.get(von) - max);
            daten.put(nach, daten.get(nach) + 1);
        }
    }

}
