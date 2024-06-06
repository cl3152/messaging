package com.len.messaging.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class ExceptionUtil {

    private ExceptionUtil() {}


    public static Stream<Throwable> flatten(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        for(Throwable current = throwable; current != null; current = current.getCause()) {
            list.add(current);
        }
        return list.stream();
    }



    /**
     * Ermittle die SAX-Meldung aus einem geschachteltem Throwable.
     * Falls keine SAX-Meldung enthalten ist, wird die Meldung des Throwables selber
     * zurückgegeben.
     * @param throwable zu untersuchendes Throwable
     * @return SAX-Meldung oder Throwable-Meldung
     */
    public static String extractSaxExceptionMessage(Throwable throwable) {
        return extractInnerExceptionMessage(throwable, SAXException.class);
    }



    /**
     * Ermittle die Meldung aus einem geschachteltem Throwable, die zu einem Throwable-Typ gehört.
     * Falls keine solche Meldung enthalten ist, wird die Meldung des Throwables selber
     * zurückgegeben.
     * @param throwable zu untersuchendes Throwable
     * @param searchClass Typ, der zu suchen ist
     * @return Meldung oder Throwable-Meldung
     */
    public static String extractInnerExceptionMessage(Throwable throwable, Class<? extends Throwable> searchClass) {
        if (throwable == null) {
            return "";
        }
        if (searchClass == null) {
            return throwable.getMessage();
        }
        Throwable t = throwable;
        while(t != null) {
            if (searchClass.isInstance(t)) {
                return t.getMessage();
            }
            t = t.getCause();
        }
        return throwable.getMessage();
    }




    /**
     * Ermittelt die innerste Fehlermeldung.
     * @param throwable zu untersuchendes Throwable
     * @return Meldung
     */
    public static String extractInnerstExceptionMessage(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        String msg = null;
        Throwable t = throwable;
        while (t != null) {
            if (t.getMessage() != null) {
                msg = t.getMessage();
            }
            t = t.getCause();
        }
        return msg;
    }



    public static boolean contains(Throwable throwable, Class<? extends Throwable> search) {
        if (throwable == null) {
            return false;
        }
        Throwable t = throwable;
        while (t != null) {
            if (search.isInstance(t)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }



    public static boolean contains(Throwable throwable, String substring) {
        Throwable t = throwable;
        while (t != null) {
            if (t.getMessage() != null && t.getMessage().contains(substring)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }



    @SuppressWarnings("squid:S00112") // allow RuntimeException
    public static void throwRuntimeException(String msgFormat, Object... args) {
        throw create(RuntimeException::new, msgFormat, args);
    }

    public static <T> T create(Function<String, T> stringConstructor, String msgFormat, Object... args) {
        Objects.requireNonNull(msgFormat);
        String msg = String.format(msgFormat, args);
        return stringConstructor.apply(msg);
    }

    public static <T> T create(BiFunction<String, Throwable, T> stringConstructorWithCause, Throwable cause, String msgFormat, Object... args) {
        Objects.requireNonNull(msgFormat);
        String msg = String.format(msgFormat, args);
        return stringConstructorWithCause.apply(msg, cause);
    }



    private static Map<Class<? extends Throwable>, Function<Throwable, String>> messageExtractors = new HashMap<>();
    static {
        // Die 'location' der ParseException (Zeile+Spalte) wird ansonsten verschluckt.
        // toString = "<Class>; <Location>; <Message>"
        messageExtractors.put(SAXParseException.class, Throwable::toString);
    }

    /**
     * Ermittelt den relevanten Nachrichtentext aus der Exception.
     * Die ist in der Regel die 'message', kann aber davon abweichen.
     * @param throwable
     * @return
     * @see #messageExtractors Tabelle mit den Ausnahmen
     */
    public static String toMeaningfulString(Throwable throwable) {
        return messageExtractors
                .getOrDefault(throwable.getClass(), Throwable::getMessage)
                .apply(throwable);
    }

    /**
     * Kürzt eine Fehlermeldung, wenn möglich sinnvoll.
     * Vor allem die JAXB-Meldungen hinsichtlich Pattern-Verletzungen können sehr lang werden, da das
     * Pattern angegeben wird, welches sehr groß sein kann. Dieses wird dann ausgelassen.
     * @param errorMessage
     * @param maxLength
     * @return
     */
    public static String shortenErrorMessage(String errorMessage, int maxLength) {
        if (errorMessage != null && errorMessage.length() > maxLength) {
            if (errorMessage.contains("Facet-gültig in Bezug auf Muster")) {
                String newMessage = errorMessage.replaceAll("auf Muster '.*' (.*)", "auf Muster '...' $1");
                if (newMessage.length() > maxLength) {
                    return shortenErrorMessage(newMessage, maxLength);
                } else {
                    return newMessage;
                }
            } else {
                return errorMessage.substring(0, maxLength);
            }
        } else {
            return errorMessage;
        }
    }

}
