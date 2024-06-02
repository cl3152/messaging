/*
package com.len.messaging.util.logger;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.Setter;
import org.apache.commons.text.StringSubstitutor;

import de.elster.etem.sapi.EtemLogger;
import de.elster.etem.sapi.EtemLoggerFactory;
import lombok.Getter;

@Getter
public final class SrtLogger implements Serializable {

    private static final long serialVersionUID = 1L;



    */
/**
     * AnwendungID von ElsterLohn2.
     * @see https://work.elster.de/elsterconfluence/display/ETEM/Anwendungsnummern
     *//*

    public static final int ANWENDUNGS_ID = 305;

    */
/**
     * Spaltenname in ETEM für Meldungstexte.
     *//*

    public static final String ETEM_FIELD_MESSAGE = "meldung";

    */
/**
     * Spaltenname in ETEM für das Nutzdatenticket.
     *//*

    public static final String ETEM_FIELD_NUTZDATENTICKET = "Nutzdatenticket";

    */
/**
     * Spaltenname in ETEM für den MapperTyp.
     *//*

    public static final String ETEM_FIELD_MAPPER_TYPE = "MapperTyp";

    */
/**
     * Spaltenname in ETEM für eine Fehlermeldung (z.B. bei SAX-Fehlern).
     *//*

    public static final String ETEM_FIELD_ERROR_MESSAGE = "ErrorMessage";

    public static final String ETEM_FIELD_THREADNAME = "Thread";

    public static final String ETEM_FIELD_MESSAGE_HASH = "MessageHash";



    */
/**
     * Pattern zur Erzeugung neuer Transfertickets basierend auf dem aktuellen Datum.
     *//*

    private static final String TT_PATTERN_BY_DATE = "yyyyMMddHHmmssSSS00";

    */
/**
     * Anzahl der Versuche, beim Lazy-Init, den ETEM-Logger zu bekommen.
     *//*

    private static final int ETEM_LOGGER_INIT_RETRY_COUNT = 5;

    */
/**
     * Wartezeit in ms zwischen einzelnen Versuchen, den unterliegenden EtemLogger zu initialisieren.
     *//*

    private static final long WAIT_BETWEEN_INIT_RETRIES = 250;

    private static final String ETEM_FIELD_RELEASE_NUMMER = "Release-Nummer";

    private static String applicationReleaseNummer;

    */
/**
     * Formatter zur Erzeugung von Transfertickets auf Basis des aktuellen Zeitstempels.
     *//*

    private SimpleDateFormat ttFormatter = new SimpleDateFormat(TT_PATTERN_BY_DATE);



    private ToLog debugWithThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.debug(ANWENDUNGS_ID, getTransferticket(), logfelder, throwable);
        }
    };
    private ToLog debugWithoutThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.debug(ANWENDUNGS_ID, getTransferticket(), logfelder);
        }
    };
    private ToLog infoWithThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.info(ANWENDUNGS_ID, getTransferticket(), logfelder, throwable);
        }
    };
    private ToLog infoWithoutThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.info(ANWENDUNGS_ID, getTransferticket(), logfelder);
        }
    };
    private ToLog errorWithThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.error(ANWENDUNGS_ID, getTransferticket(), logfelder, throwable);
        }
    };
    private ToLog errorWithoutThrowable = new ToLog() {
        private static final long serialVersionUID = 1L;
        @Override
        public void log(Map<String, String> logfelder) {
            logger.error(ANWENDUNGS_ID, getTransferticket(), logfelder);
        }
    };

    */
/**
     * EtemLogger, an den die Meldungen delegiert werden.
     *//*

    private transient EtemLogger logger;

    */
/**
     * Klasse, für die der Logger zu initialisieren ist.
     * Wird für das Lazy-Initializing benötigt.
     * @see #lazyInitLogger()
     *//*

    private Class<?> forClass;

    private LogLevel loglevel = LogLevel.INFO;
    private String msg;
    private String msgFormat;
    private String msgTemplate;
    private transient Object[] msgArgs;
    private String transferticket;
    private String nutzdatenticket;
    private Throwable throwable;
    private Map<String, String> data = new HashMap<>();
    private boolean toStdout;

    private List<PreFlushAction> preFlushActions = new ArrayList<>();

    interface PreFlushAction extends Consumer<SrtLogger>, Serializable {
    }


    */
/**
     * Mögliche LogLevel.
     *//*

    public enum LogLevel {
        DEBUG, INFO, ERROR
    }



    @FunctionalInterface
    private interface ToLog extends Serializable {
        void log(Map<String, String> logfelder);
    }



    private SrtLogger(Class<?> forClass) {
        this.forClass = forClass;
    }



    public static SrtLogger getLogger(Class<?> forClass) {
        return new SrtLogger(forClass).newTransferticket();
    }



    public static SrtLogger nullLogger() {
        return new SrtLogger(null).newTransferticket();
    }



    // ========== FluentAPI ==========


    public SrtLogger loglevel(LogLevel loglevel) {
        this.loglevel = loglevel;
        return this;
    }

    public SrtLogger debug() {
        return loglevel(LogLevel.DEBUG);
    }

    public SrtLogger info() {
        return loglevel(LogLevel.INFO);
    }

    public SrtLogger error() {
        return loglevel(LogLevel.ERROR);
    }

    public SrtLogger msg(String msg) {
        this.msg = msg;
        return this;
    }

    public SrtLogger msgFormat(String format) {
        this.msgFormat = format;
        return this;
    }

    public SrtLogger msgTemplate(String template) {
        this.msgTemplate = template;
        return this;
    }

    public SrtLogger msgArgs(Object... args) {
        this.msgArgs = args;
        return this;
    }

    public SrtLogger throwable(Throwable throwable) {
        this.throwable = throwable;
        if (!data.containsKey(ETEM_FIELD_ERROR_MESSAGE) && throwable != null) {
            withData(ETEM_FIELD_ERROR_MESSAGE, throwable.getMessage());
        }
        return this;
    }

    public SrtLogger transferticket(String tt) {
        this.transferticket = tt;
        return this;
    }

    public SrtLogger newTransferticket() {
        this.transferticket = ttFormatter.format(new Date());
        return this;
    }

    public SrtLogger nutzdatenticket(String ndt) {
        this.nutzdatenticket = ndt;
        return this;
    }

    public SrtLogger withPreFlushAction(PreFlushAction preFlushAction) {
        preFlushActions.add(preFlushAction);
        return this;
    }

    public SrtLogger withData(String key, String value) {
        data.put(key, value);
        return this;
    }

    public SrtLogger withData(String key, Object value) {
        data.put(key, String.valueOf(value));
        return this;
    }

    public SrtLogger teeToStdout() {
        toStdout = true;
        return this;
    }



    */
/**
     * Überträgt die gesammelten Logdaten an das Loggingsystem und löscht die lokal vorgehaltenen
     * Daten (Transferticket, Nutzdatenticket, LogLevel, Message(Args|Format)).
     *
     * <p>Der Aufrufer dieser Methode wird in ETEM als auslösende Klasse gespeichert.</p>
     *
     * @return self (FluentAPI)
     *//*

    public SrtLogger flush() {
        performPreFlushActions();

        Map<String, String> logfelder = initLogfelder();
        lazyInitLogger();
        maybePrintToDelegateLogger(logfelder);
        maybePrintToStdout(logfelder);

        reset();
        return this;
    }



    protected Map<String, String> initLogfelder() {
        Map<String, String> logfelder = new HashMap<>();
        logfelder.putAll(data);
        logfelder.put(ETEM_FIELD_MESSAGE, getMessage());
        logfelder.put(ETEM_FIELD_THREADNAME, Thread.currentThread().getName());
        if (nutzdatenticket != null) {
            logfelder.put(ETEM_FIELD_NUTZDATENTICKET, nutzdatenticket);
        }
        if (applicationReleaseNummer != null) {
            logfelder.put(ETEM_FIELD_RELEASE_NUMMER, applicationReleaseNummer);
        }

        uebernehmeDatenAusThreadLocal(logfelder);
        return logfelder;
    }



    private void performPreFlushActions() {
        preFlushActions.forEach( consumer -> consumer.accept(this) );
    }



    */
/**
     * Wenn die Daten im ThreadLocal gespeichert sind, werden diese übernommen.
     * Sie 'überschreiben' eventuell zuvor gesetzte Werte.
     * @param logfelder
     * @see ThreadLogDaten
     *//*

    private void uebernehmeDatenAusThreadLocal(Map<String, String> logfelder) {
        if (isNotEmpty(ThreadLogDaten.nutzdatenticket())) {
            logfelder.put(ETEM_FIELD_NUTZDATENTICKET, ThreadLogDaten.nutzdatenticket());
        }
        if (isNotEmpty(ThreadLogDaten.mapperTyp())) {
            logfelder.put(ETEM_FIELD_MAPPER_TYPE, ThreadLogDaten.mapperTyp());
        }
        if (isNotEmpty(ThreadLogDaten.messageHash())) {
            logfelder.put(ETEM_FIELD_MESSAGE_HASH, ThreadLogDaten.messageHash());
        }
    }



    */
/**
     * Gibt das aktuell gültige Transferticket, ggfs. aus dem ThreadLocal.
     * @return Transferticket
     * @see ThreadLogDaten
     *//*

    public String getTransferticket() {
        if (isNotEmpty(ThreadLogDaten.transferticket())) {
            return ThreadLogDaten.transferticket();
        } else {
            return transferticket;
        }
    }



    private static boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }



    */
/**
     * Lazy Instantiierung des EtemLoggers, da dieser - wenn er zu früh angelegt wird -
     * eine Exception wirft, die das Deployment verhindert:
     * <pre>
     * Caused by: java.lang.ClassCastException: org.slf4j.helpers.SubstituteLogger cannot be cast to org.slf4j.spi.LocationAwareLogger
     *     at de.elster.etem.sapi.EtemLogger.<init>(EtemLogger.java:124)
     *     at de.elster.etem.sapi.EtemLoggerFactory.getLogger(EtemLoggerFactory.java:100)
     *     at de.konsens.lohn2.srt.logging.SrtLogger.getLogger(SrtLogger.java:65)
     * </pre>
     *
     * Die <a href="http://slf4j.org/codes.html#substituteLogger">SLF4J-FAQ</a> gibt den Hinweis, dass die Initialisierung
     * zu früh erfolgt.
     *//*

    @SuppressWarnings("squid:S106") // allow syso
    private void lazyInitLogger() {
        if (forClass != null && logger == null) {
            Exception exc = null;
            int retry = ETEM_LOGGER_INIT_RETRY_COUNT;
            while (retry > 0 && logger == null) {
                retry--;
                try {
                    logger = EtemLoggerFactory.getLogger(forClass, SrtLogger.class.getName());
                } catch (Exception e) {
                    exc = e;
                    try {
                        Thread.sleep(WAIT_BETWEEN_INIT_RETRIES);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (logger == null && exc != null) {
                // Und sollte trotzdem etwas schief gehen, so bleibt zumindest das
                // SRT-Loggingsystem intakt.
                exc.printStackTrace(System.out);
            }
        }
    }



    private void maybePrintToDelegateLogger(Map<String, String> logfelder) {
        if (logger != null) {
            switch (loglevel) {
                case DEBUG:
                    // Kein Lambda sondern anonyme Klassen, da ETEM sonst die Herkunft verschluckt.
                    maybeLog(logger.isDebugEnabled(), debugWithoutThrowable, debugWithThrowable, logfelder);
                    break;
                case INFO:
                    maybeLog(logger.isInfoEnabled(), infoWithoutThrowable, infoWithThrowable, logfelder);
                    break;
                case ERROR:
                    maybeLog(logger.isErrorEnabled(), errorWithoutThrowable, errorWithThrowable, logfelder);
                    break;
                default:
                    break;
            }
        }
    }



    private void maybeLog(boolean isEnabled, ToLog logWithoutThrowable, ToLog logWithThrowable, Map<String, String> logfelder) {
        if (isEnabled) {
            if (throwable == null) {
                logWithoutThrowable.log(logfelder);
            } else {
                logWithThrowable.log(logfelder);
            }
        }
    }



    @SuppressWarnings("squid:S106") // allow syso
    private void maybePrintToStdout(Map<String, String> logfelder) {
        if (toStdout) {
            System.out.printf("%tY.%<tm.%<td %<tH:%<tM:%<tS,%<tL %n", new Date());
            System.out.printf("- level=%s%n", loglevel);
            System.out.printf("- tt=%s%n", getTransferticket());
            System.out.printf("- ndt=%s%n", nutzdatenticket);
            System.out.printf("- msg='%s'%n", msg);
            System.out.printf("- msgFormat='%s'%n", msgFormat == null ? "<null>" : msgFormat);
            System.out.println("- msgArgs");
            if (msgArgs != null) {
                for(Object o : msgArgs) {
                    System.out.printf("  -- %s%n", o);
                }
            }
            if (msgFormat != null) {
                System.out.println("- msgFormatted=" + String.format(msgFormat, msgArgs));
            }
            System.out.printf("- exc=%s%n", throwable == null ? "none" : throwable.getMessage() );
            logfelder.forEach( (k,v) -> System.out.printf("- data(%s)=%s%n", k, v) );
        }
    }



    private String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (msg != null) {
            sb.append(msg);
        }
        if (msg != null && msgFormat != null) {
            sb.append(": ");
        }
        if (msgFormat != null) {
            sb.append(String.format(msgFormat, msgArgs));
        }
        if (msgTemplate != null) {
            StringSubstitutor sub = new StringSubstitutor(getData());
            sb.append(sub.replace(msgTemplate));
        }
        return sb.toString();
    }



    private void reset() {
        this.msg = null;
        this.msgArgs = null;
        this.msgFormat = null;
        this.msgTemplate = null;
        this.throwable = null;
        this.loglevel = LogLevel.INFO;
        this.data.clear();
    }



    // ===========  SimpleAPI  ==========



    public void debugf(String format, Object... args) {
        debug().msgFormat(format).msgArgs(args).flush();
    }

    public void infof(String format, Object... args) {
        info().msgFormat(format).msgArgs(args).flush();
    }

    public void errorf(String format, Object... args) {
        error().msgFormat(format).msgArgs(args).flush();
    }



    public boolean isDebugEnabled() {
        lazyInitLogger();
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        lazyInitLogger();
        return logger.isInfoEnabled();
    }

    public static void setApplicationReleaseNummer(String applicationReleaseNummer) {

        if (applicationReleaseNummer != null &&
                applicationReleaseNummer.contains("-") &&
                !applicationReleaseNummer.contains("-SNAPSHOT")) {

            // Die vom CI erzeugte Releasenummer für Apps hat die Form "v<Version>-<BuildTStapm>-<ShortGitHash>",  z.B. "v11.0.0-20220826103325-8c245d9".
            // Für Entwicklungsartefakte lautet diese ganz normal "v<Version>-SNAPSHOT".
            // Vom Betrieb sind aber die beiden letzten Bestandteile in Logs unerwünscht, daher für das Logging wegschneiden.
            applicationReleaseNummer = applicationReleaseNummer.substring(0, applicationReleaseNummer.indexOf("-"));
        }

        SrtLogger.applicationReleaseNummer = applicationReleaseNummer;
    }
}
*/
