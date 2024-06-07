package com.len.messaging.jms;

import com.len.messaging.util.ElsterInfo;
import com.len.messaging.util.MapBuilder;
import com.len.messaging.util.StringUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
public class MessageEvaluator {

    // Original: private static final String XML_HEADER_START = "<?xml";
    private static final String XML_HEADER_START = "<";

    public enum Action {
        NO_DATA(-1),
        KEIN_XML(-1),
        AUSSTEUERN(-1),
        DIREKT_VERARBEITEN(0),
        KURZ_VERZOEGERN(1),
        MITTEL_VERZOEGERN(2),
        LANG_VERZOEGERN(3);

        long delayFactor = 0;

        private Action(long delayFactor) {
            this.delayFactor = delayFactor;
        }


        public long getDelayFactor() {
            return this.delayFactor;
        }
    }

    public Action evaluate(String message) {
        Queue<Action> actions = new LinkedList<>();
        if (!requestHasData(message)) {
            actions.add(Action.NO_DATA);
        } else if (!requestIsXml(message)) {
            actions.add(Action.KEIN_XML);
        } else if (!requestIsXml(message)) { // Duplikat?
            actions.add(Action.AUSSTEUERN);
            /*nur für die alte Verzögerungslogik
        } else if (canProcessByTStamp(message)) {
            actions.add(Action.DIREKT_VERARBEITEN);*/
        } else if (StringUtil.parseElsterInfo(message).isElo()) {
            actions.add(actionByWorkflowStep(message));
            //Für Testzwecke
/*        } else if (requestIsXml(message)){
            actions.add(Action.DIREKT_VERARBEITEN);*/
        } else {
            System.out.println("Default Evaluator: " + message);
            actions.add(Action.AUSSTEUERN);
        }

        return actions.poll(); // Gibt das erste Element zurück und entfernt es aus der Queue
    }

    private boolean requestHasData(String message) {
        return message != null && !message.trim().isEmpty();
    }

    private boolean requestIsXml(String message) {
        return message.contains(XML_HEADER_START); // Vereinfachte Überprüfung, ob es sich um XML handelt
    }

/*    *//**
     * Die Methode wird im Original dazu verwendet, um eine Header-Eigenschaft aus der Message zu lesen, welche
     * den frühesten Verarbeitungszeitpunkt enthält.
     * Für die alte Verarbeitungslogik wichtig.
     * *//*
    private boolean canProcessByTStamp(String message) {
        // Inhalt gelöscht.
        // Wenn ein Zeitstempel im Header der Nachricht existiert, dann wurde er hier ausgewertet.
        return false;
    }*/


    private Action actionByWorkflowStep(String message) {
        return actionFor.get(StringUtil.parseElsterInfo(message).getStep());
    }

    private static Map<ElsterInfo.Step, Action> actionFor = MapBuilder.create(ElsterInfo.Step.class, Action.class)
            .put(ElsterInfo.Step.DUE_REQUEST, Action.DIREKT_VERARBEITEN)
            .put(ElsterInfo.Step.ELSTAM_REQUEST, Action.KURZ_VERZOEGERN)
            .put(ElsterInfo.Step.ELSTAM_RESPONSE, Action.MITTEL_VERZOEGERN)
            .put(ElsterInfo.Step.DUE_RESPONSE, Action.LANG_VERZOEGERN)
            .toMap();



}