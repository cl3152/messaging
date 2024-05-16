package com.len.messaging.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

/**
 * Hilfsmethoden für String.
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Ermittelt textuell das Transferticket aus einem ElsterXML.
     * @param xml elsterXml
     * @return Transferticket
     */
    public static String transferticket(String xml) {
        return tagValue(xml, "TransferTicket");
    }

    /**
     * Ermittelt textuell das erste Nutzdatenticket aus einem ElsterXML.
     * @param xml elsterXml
     * @return Nutzdatenticket
     */
    public static String nutzdatenticket(String xml) {
        return tagValue(xml, "NutzdatenTicket");
    }

    /**
     * Ermittelt textuell den ersten Wert eines Tags
     * (<tt>&lt;<i>tagname</i>&gt;).
     * <b>Voraussetzung ist, dass das Tag keine Attribute enthält!</b>
     * @param xml
     * @param tagname
     * @return
     */
    public static String tagValue(String xml, String tagname) {
        return StringUtils.substringBetween(xml, "<" + tagname + ">", "</" + tagname + ">");
    }

    /**
     * Ermittelt textuell das Verfahren.
     * @param xml
     * @return
     */
    public static String verfahren(String xml) {
        return tagValue(xml, "Verfahren");
    }

    /**
     * Ermittelt textuell die Datenart.
     * @param xml
     * @return
     */
    public static String datenart(String xml) {
        return tagValue(xml, "DatenArt");
    }

    /**
     * Ermittelt textuell das Ziel.
     * @param xml
     * @return
     */
    public static String ziel(String xml) {
        return tagValue(xml, "Ziel");
    }

    /**
     * Konvertiert ein Object-Array in einen String-Array über die
     * {@link String.valueOf} Methode für jedes Element.
     * @param in Object-Array
     * @return String-Array
     */
    public static String[] toStringArray(Object[] in) {
        String[] rv = new String[in.length];
        for (int i = 0; i < in.length; i++) {
            rv[i] = String.valueOf(in[i]);
        }
        return rv;
    }

    /**
     * Ersetzt die Variablen in einem Text durch deren Werte aus der Map.
     * @param template
     * @param map
     * @return
     */
    public static String format(String template, Map<String,?> map) {
        return new StringSubstitutor(map).replace(template);
    }

    /**
     * Kopiert einen String von einem Objekt auf ein anderes, sofern es nicht <tt>null</tt>
     * oder leer ist.
     * @param from wie ist der String zu beziehen
     * @param to wie ist der String zu setzten
     */
    public static void copyIfNotBlank(Supplier<String> from, Consumer<String> to) {
        if (StringUtils.isNotBlank(from.get())) {
            to.accept(from.get());
        }
    }

    /**
     * Gibt den String nach einem Suchstring zurück
     * @param string zu durchsuchender String
     * @param search zu 'löschender' String
     * @return <tt>null</tt>, wenn einer der Parameter <tt>null</tt> ist, ansonsten der Teil nach dem Suchstring.
     */
    public static String getAfter(String string, String search) {
        if (string == null || search == null) {
            return string;
        } else {
            int pos = string.indexOf(search);
            return pos >= 0 ? string.substring(pos+search.length()) : string;
        }
    }

    /**
     * Parst textuell ein ElsterXML und extrahiert einige Informationen.
     * @param xml
     * @return
     */
    public static ElsterInfo parseElsterInfo(String xml) {
        return ElsterInfo.builder()
                .datenart(datenart(xml))
                .erstesNutzdatenticket(nutzdatenticket(xml))
                .transferticket(transferticket(xml))
                .verfahren(verfahren(xml))
                .ziel(ziel(xml))
                .build();
    }

    /**
     * Versucht, einen JSON-Wert zu einem gegebenen Schlüssel aus einem JavaScript zu extrahieren.
     * @param js zu "parsendes" JavaScript
     * @param key zu suchender Schlüssel
     * @return
     */
    public static Optional<String> jsonValue(String js, String key) {
        if (StringUtils.isBlank(js) || StringUtils.isBlank(key)) {
            return Optional.empty();
        }

        String[] parts = js.replaceAll("\\s+", " ").split(key);
        for(int i=0; i<parts.length; i++) {
            boolean nextOk = !parts[i].trim().endsWith("//");
            if (nextOk && i+1 <= parts.length) {
                String next = parts[i+1];

                int posColon = next.indexOf(':');
                int posComma = next.indexOf(',');

                int start = posColon + 1;
                int end   = posComma >= 0 ? posComma : next.length();
                String value = next.substring(start, end).trim();

                if (value.indexOf('\'') >= 0) {
                    value = StringUtils.substringBetween(value, "'");
                }
                return Optional.ofNullable(value);
            }
        }
        return Optional.empty();
    }

    /**
     * Kürzt einen CamelCase-String auf eine Maxiamllänge, wenn möglich.
     * Die Kürzung erfolg derart, dass von links nach recht jedes 'Wort' durch
     * den Anfangsbuchstaben ersetzt wird.
     *
     * <pre>
     * DiesIstEineKlasse -> DIstEineKlasse -> DIEineKlasse -> DIEKlasse -> DIEK
     * </pre>
     *
     * @param in
     * @param maxLength
     * @return
     */
    public static String shortenCamelCase(String in, int maxLength) {
        if (in == null || in.length() <= maxLength || maxLength <= 0) {
            return in;
        } else {
            int oldLength;
            do {
                oldLength = in.length();
                in = in.replaceFirst("([A-Z]+)[a-z]*([A-Z]*.*)", "$1$2");
            } while (in.length() != oldLength && in.length() > maxLength);
            return in;
        }
    }
}
