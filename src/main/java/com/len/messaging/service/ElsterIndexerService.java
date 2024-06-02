/*
package com.len.messaging.service;

import static de.konsens.lavendel.srt.marshaller.ElsterUtil.getFirstNutzdatenTicket;
import static de.konsens.lavendel.srt.marshaller.ElsterUtil.getTransferTicket;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.konsens.lavendel.srt.entities.Agvh;
import de.konsens.lavendel.srt.entities.Anvh;
import de.konsens.lavendel.srt.entities.Arbeitnehmer;
import de.konsens.lavendel.srt.entities.DueNachricht;
import de.konsens.lavendel.srt.entities.Elstam;
import de.konsens.lavendel.srt.entities.ElstamNachricht;
import de.konsens.lavendel.srt.entities.HinweisCommon;
import de.konsens.lavendel.srt.entities.Hinweise;
import de.konsens.lavendel.srt.entities.Sammellieferung;
import de.konsens.lavendel.srt.entities.Schemaversion;
import de.konsens.lavendel.srt.entities.Stnrh;
import de.konsens.lavendel.srt.entities.Teillieferung;
import de.konsens.lavendel.srt.entities.Transfer;
import de.konsens.lavendel.srt.indexer.daos.AgvhDao;
import de.konsens.lavendel.srt.indexer.daos.AnvhDao;
import de.konsens.lavendel.srt.indexer.daos.ArbeitnehmerDao;
import de.konsens.lavendel.srt.indexer.daos.DueNachrichtenDao;
import de.konsens.lavendel.srt.indexer.daos.ElstamDao;
import de.konsens.lavendel.srt.indexer.daos.ElstamNachrichtenDao;
import de.konsens.lavendel.srt.indexer.daos.FehlerDao;
import de.konsens.lavendel.srt.indexer.daos.HinweiseDao;
import de.konsens.lavendel.srt.indexer.daos.KonfigDao;
import de.konsens.lavendel.srt.indexer.daos.SammellieferungDao;
import de.konsens.lavendel.srt.indexer.daos.SchemaversionDao;
import de.konsens.lavendel.srt.indexer.daos.StnrhDao;
import de.konsens.lavendel.srt.indexer.daos.TransferDao;
import de.konsens.lavendel.srt.logging.SrtLogger;
import de.konsens.lavendel.srt.logging.ThreadLogDaten;
import de.konsens.lavendel.srt.mapper.Mapper;
import de.konsens.lavendel.srt.mapper.SammellieferungException;
import de.konsens.lavendel.srt.mapper.SammellieferungMapper;
import de.konsens.lavendel.srt.mapper.factory.MapperFactory;
import de.konsens.lavendel.srt.marshaller.ElsterUtil;
import de.konsens.lavendel.srt.xml.ElsterRootElement;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

*/
/**
 * @author f007777
 *
 *//*

@Named
public class ElsterIndexerService {


    public void index(ElsterRootElement elster, long speichts) {
            Mapper mapper = MapperFactory.getMapper(elster);
            if (mapper != null) {
                map(elster, speichts, mapper);
            } else {
                String errorMsg = String.format(
                        "Konnte keinen Mapper ermitteln (TT=%s).",
                        ThreadLogDaten.transferticket()
                );
                getLogger().errorf(errorMsg);
                // Aussteuern, damit die Rohdaten gesichert werden, falls in der MapperFactory ein Fehler ist.
                throw new AussteuernException(errorMsg);
            }
        } finally {
            commitOpenTransaction();
            getLogger().infof(
                    "Verarbeitung der Daten zu TT=\"%s\", Datenart=\"%s\" und 1.Nutzdatenticket=\"%s\" beendet.",
                    ThreadLogDaten.transferticket(),
                    datenart,
                    ThreadLogDaten.nutzdatenticket()
            );
        }
    }


    @SuppressWarnings("squid:CommentedOutCodeLine") // Kommentar ist wichtig!
    private void map(ElsterRootElement elster, long speichts, Mapper mapper) {
        String art = mapper.getClass().getSimpleName().replace("Mapper", "").replace("Impl", "");
        ThreadLogDaten.mapperTyp(art);
        try {
            Transfer mappedTransfer = mapper.map(elster);
            if (mappedTransfer != null) {
                saveMappedTransfer(elster, speichts, mapper, mappedTransfer);
                // Durch das nicht Aktualisieren der TL verlieren wir die Referenz Teillieferung->Transfer.
                // Diese wird "nur" für die Navigation im Viewer von der TL auf den Transfer benötigt.
                // Dies geht auch über eine Suche mit TT+NDT (Sammellieferung.TT+Teillieferung.NDT), und dies nur
                // in der von den Benutzern benötigten Anzahl.
                // Dafür werden aber viele Indizierungen erspart: im März 2017 ca. 10 Millionen!
                // : updateTeillieferungen(mappedTransfer.getTransferticket(), Optional.ofNullable(sammellieferung));
            } else {
*/
/*                String errorMsg = String.format(
                        "Konnte Daten nicht mappen (TT=%s).",
                        ThreadLogDaten.transferticket()
                );
                getLogger().errorf(errorMsg);
                // Aussteuern, damit die Rohdaten gesichert werden, falls in der MapperFactory ein Fehler ist.
                throw new AussteuernException(errorMsg);*//*

            }
        } catch (Exception e) {
            //saveSammellieferung(elster, speichts);
        }
    }

    private void saveMappedTransfer(ElsterRootElement elster, long speichts, Mapper mapper, Transfer mappedTransfer) {
        try {
            Transfer dbTransfer = transferDao.findByTransferticketAndNutzdatenticket(
                    getTransferTicket(elster),
                    getFirstNutzdatenTicket(elster)
            );

            tx.begin();

            if (dbTransfer != null) {
                getLogger().debugf(
                        "Aktualisiere Datensatz zu TT=\"%s\" und NDT=\"%s\"",
                        dbTransfer.getTransferticket(), dbTransfer.getNutzdatenticket()
                )
                merge(dbTransfer, mappedTransfer);
                persistRelation(dbTransfer, mapper, speichts);
            } else {
                getLogger().debugf(
                        "Speichere neuen Datensatz zu TT=\"%s\" und NDT=\"%s\"",
                        mappedTransfer.getTransferticket(), mappedTransfer.getNutzdatenticket()
                );
                mappedTransfer.setSpeichts(speichts);
                dbTransfer = save(mappedTransfer);

                if (dbTransfer != null) {
                    persistRelation(dbTransfer, mapper, speichts);
                } else {
                    String errorMsg = "Gerade gespeicherter neuer Transfer-Datensatz nicht in der DB gefunden.";
                    getLogger().error().msg(errorMsg).flush();
                    throw new AussteuernException(errorMsg);
                }
            }

            tx.commit();

        } catch (IllegalStateException | RollbackException | SecurityException | HeuristicMixedException | HeuristicRollbackException | javax.transaction.RollbackException | SystemException | NotSupportedException e) {
            String errorMsg = String.format(
                    "Fehler beim Schreiben von TT=\"%s\" und NDT=\"%s\": %s",
                    mappedTransfer.getTransferticket(),
                    mappedTransfer.getNutzdatenticket(),
                    e.getMessage()
            );
            getLogger().error()
                    .msg(errorMsg)
                    .throwable(e)
                    .flush();
            throw new AussteuernException(errorMsg, e);
        }
    }



    */
/**
     * Speichert einen Transfer und überprüft, dass die ID gesetzt ist.
     * @param transfer zu speicherndes Transfer-Objekt
     * @return (ggfs. nachgeladenes) Transferobjekt
     *//*

    private Transfer save(Transfer transfer) {
        Objects.requireNonNull(transfer, "Zu speichernder Transfer darf nicht null sein.");
        Objects.requireNonNull(transfer.getTransferticket(), "Zu speichernder Transfer muss Transferticket haben.");
        Objects.requireNonNull(transfer.getNutzdatenticket(), "Zu speichernder Transfer muss Nutzdatenticket haben.");

        Transfer savedTransfer = transferDao.saveAndFlush(transfer);
        if (savedTransfer.getId() == null) {
            // Neu Laden, damit wir auch die ID haben und das Speichern selber verifizieren.
            return transferDao.findByTransferticketAndNutzdatenticket(transfer.getTransferticket(), transfer.getNutzdatenticket());
        } else {
            // ID ist nach dem Speichern bekannt, daher können wir diese direkt nutzten.
            return savedTransfer;
        }
    }



    */
/**
     * Manuelles Speichern der Beziehungen.
     * Die Entities haben die Beziehungen nicht mehr modelliert, in der DB sind die
     * Fremdschlüssel aber vorgesehen und werden auch vom Viewer benötigt.
     *
     * @param dbTransfer
     * @param mapper
     * @param speichts
     *//*

    private void persistRelation(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("persistRelationship mapper=%s transfer.id=%s", mapper, dbTransfer.getId());
        Objects.requireNonNull(dbTransfer, "Kann keine Relationen zu nicht existierendem Transfer anlegen.");
        Objects.requireNonNull(dbTransfer.getId(), "Kann keine Relationen zu Transfer ohne ID anlegen.");
        txResetter = new TxResetter(1000, tx);
        persistRelationElstamNachrichten(dbTransfer, mapper, speichts);
        persistRelationDueNachrichten(dbTransfer, mapper, speichts);
        persistRelationArbeitnehmer(dbTransfer, mapper, speichts);
        persistRelationElstam(dbTransfer, mapper, speichts);
        persistRelationStnrh(dbTransfer, mapper, speichts);
        persistRelationAgvh(dbTransfer, mapper, speichts);
        persistRelationAnvh(dbTransfer, mapper, speichts);
        mapper.init();
    }



    private void persistRelationAnvh(Transfer dbTransfer, Mapper mapper, long speichts) {
        persistRelationHinweis(
                mapper.getAnvhs(), "Anvh", dbTransfer.getId(), speichts,
                h -> anvhDao.saveAndFlush((Anvh) h),
                (id, h) -> inDBVorhanden(id, (Anvh)h)
        );
    }



    private void persistRelationAgvh(Transfer dbTransfer, Mapper mapper, long speichts) {
        persistRelationHinweis(
                mapper.getAgvhs(), "Agvh", dbTransfer.getId(), speichts,
                h -> agvhDao.saveAndFlush((Agvh) h),
                (id, h) -> inDBVorhanden(id, (Agvh)h)
        );
    }







    */
/**
     * Speichert einen AGVH/ANVH/STNRH, legt ggfs. den Basis-Hinweis neu an und aktualisiert
     * die Referenzen.
     * @param list Liste der zu speichernden Hinweise
     * @param typ Typ der zu speichernden Hinweise (für das Logging)
     * @param dbTransferId ID des Transfers
     * @param speichts zu verwendender Speicher-Zeitstempel
     * @param howToSave Lambda, wie der Hinweis zu speichern ist
     * @param howToCheckExistence Funktion, wie überprüft werden kann, ob der Hinweis schon gespeichert wurde.
     *//*

    private void persistRelationHinweis(
            List<? extends HinweisCommon> list, String typ, Long dbTransferId, Long speichts,
            Consumer<HinweisCommon> howToSave,
            BiFunction<Long, HinweisCommon, Boolean> howToCheckExistence) {

        getLogger().debugf("Aktualisiere anhängende %s (%s)", typ, list.size());
        for (HinweisCommon hinweis : list) {
            Boolean hinweisExistsInDB = howToCheckExistence.apply(dbTransferId, hinweis);
            if (!hinweisExistsInDB) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(hinweis)).flush();
                hinweis.setTransferId(dbTransferId);
                hinweis.setSpeichts(speichts);
                maybeSaveNewBaseHinweis(hinweis);
                howToSave.accept(hinweis);
                txResetter.maybeResetTransaction();
            } else {
                logBereitsVorhanden(hinweis);
            }
        }
    }



    private void maybeSaveNewBaseHinweis(HinweisCommon hinweis) {
        Hinweise baseHinweis = hinweis.getHinweis();
        if (baseHinweis != null) {
            Hinweise listHinweis = findHinweisFromListByCode(baseHinweis.getCode());
            if (listHinweis == null) {
                baseHinweis = hinweiseDao.saveAndFlushAndRefresh(baseHinweis);
                hinweis.setHinweiseId(baseHinweis.getId());
                // globale Hinweisliste erweitern/neu laden
                hinweiseList.add(baseHinweis);
                resetMapperFactory();
            } else {
                // BaseHinweis schon in einem vorherigen Schleifendurchlauf angelegt.
                hinweis.setHinweiseId(listHinweis.getId());
            }
        } else {
            // bereits bekannter Hinweis
        }
    }



    private void persistRelationStnrh(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("Aktualisiere anhängende Stnrh (%s)", mapper.getStnrhs().size());
        for (Stnrh hinweis : mapper.getStnrhs()) {
            if (!inDBVorhanden(dbTransfer.getId(), hinweis)) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(hinweis)).flush();
                hinweis.setTransferId(dbTransfer.getId());
                hinweis.setSpeichts(speichts);
                this.stnrhDao.saveAndFlush(hinweis);
            } else {
                logBereitsVorhanden(hinweis);
            }
        }
    }



    private void persistRelationElstam(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("Aktualisiere anhängende Elstam (%s)", mapper.getElstams().size());
        for (Elstam elstam : mapper.getElstams()) {
            if (!inDBVorhanden(dbTransfer.getId(), elstam)) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(elstam)).flush();
                elstam.setTransferId(dbTransfer.getId());
                elstam.setSpeichts(speichts);
                if (elstam.getFb() != null) {
                    elstam.getFb().setSpeichts(speichts);
                }
                if (elstam.getHinzub() != null) {
                    elstam.getHinzub().setSpeichts(speichts);
                }
                this.elstamDao.saveAndFlush(elstam);
                txResetter.maybeResetTransaction();
            } else {
                logBereitsVorhanden(elstam);
            }
        }
    }



    private void persistRelationArbeitnehmer(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("Aktualisiere anhängende Arbeitnehmer (%s)", mapper.getArbeitnehmer().size());
        for (Arbeitnehmer arbeitnehmer : mapper.getArbeitnehmer()) {
            if (!inDBVorhanden(dbTransfer.getId(), arbeitnehmer)) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(arbeitnehmer)).flush();
                arbeitnehmer.setTransferId(dbTransfer.getId());
                arbeitnehmer.setSpeichts(speichts);
                this.arbeitnehmerDao.saveAndFlush(arbeitnehmer);
                txResetter.maybeResetTransaction();
            } else {
                logBereitsVorhanden(arbeitnehmer);
            }
        }
    }



    private void persistRelationDueNachrichten(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("Aktualisiere anhängende DueNachrichten (%s)", mapper.getDueNachrichten().size());
        for (DueNachricht nachricht : mapper.getDueNachrichten()) {
            if (!inDBVorhanden(dbTransfer.getId(), nachricht)) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(nachricht)).flush();
                nachricht.setTransferId(dbTransfer.getId());
                nachricht.setSpeichts(speichts);
                this.duenachrichtenDao.saveAndFlush(nachricht);
            } else {
                logBereitsVorhanden(nachricht);
            }
        }
    }



    private void persistRelationElstamNachrichten(Transfer dbTransfer, Mapper mapper, long speichts) {
        getLogger().debugf("Aktualisiere anhängende ElstamNachrichten (%s)", mapper.getElstamnachrichten().size());
        for (ElstamNachricht nachricht : mapper.getElstamnachrichten()) {
            if (!inDBVorhanden(dbTransfer.getId(), nachricht)) {
                getLogger().debug().msgFormat("- %s").msgArgs(toString(nachricht)).flush();
                nachricht.setTransferId(dbTransfer.getId());
                nachricht.setSpeichts(speichts);
                this.elstamnachrichtenDao.saveAndFlush(nachricht);
            } else {
                logBereitsVorhanden(nachricht);
            }
        }
    }



    private void logBereitsVorhanden(Object object) {
        getLogger().debug().msgFormat("- %s bereits vorhanden.").msgArgs(toString(object)).flush();
    }



    private Hinweise findHinweisFromListByCode(String code) {
        return hinweiseList
                .stream()
                .filter(h -> h.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }



    private static void merge(Transfer dbTransfer, Transfer mappedTransfer) {
        // Mapping der einfachen Felder
        dbTransfer.setEingangts(mappedValue(dbTransfer.getEingangts(), mappedTransfer.getEingangts()));
        dbTransfer.setHerstellerid(mappedValue(dbTransfer.getHerstellerid(), mappedTransfer.getHerstellerid()));
        dbTransfer.setNutzdatenticket(mappedValue(dbTransfer.getNutzdatenticket(), mappedTransfer.getNutzdatenticket()));
        dbTransfer.setSpeichts(mappedValue(dbTransfer.getSpeichts(), mappedTransfer.getSpeichts()));
        dbTransfer.setTransferticket(mappedValue(dbTransfer.getTransferticket(), mappedTransfer.getTransferticket()));
    }



    private boolean inDBVorhanden(long transferId, Anvh hinweis) {
        return anvhDao.findSingle(transferId, hinweis.getHinweiseId(), hinweis.getIdnr()) != null;
    }



    private boolean inDBVorhanden(long transferId, Agvh hinweis) {
        return agvhDao.findSingle(transferId, hinweis.getHinweiseId()) != null;
    }



    private boolean inDBVorhanden(long transferId, Stnrh hinweis) {
        return stnrhDao.findSingle(transferId, hinweis.getHinweiseId()) != null;
    }



    private boolean inDBVorhanden(long transferId, Elstam elstam) {
        return elstamDao.findSingle(transferId, elstam.getIdnr(), elstam.getGueltigab()) != null;
    }



    private boolean inDBVorhanden(long transferId, Arbeitnehmer arbeitnehmer) {
        return arbeitnehmerDao.findSingle(transferId, arbeitnehmer.getIdnr(), arbeitnehmer.getTyp()) != null;
    }



    private boolean inDBVorhanden(long transferId, DueNachricht nachricht) {
        return duenachrichtenDao.findSingle(transferId, nachricht.getTyp()) != null;
    }



    private boolean inDBVorhanden(long transferId, ElstamNachricht nachricht) {
        return elstamnachrichtenDao.findSingle(transferId, nachricht.getTyp()) != null;
    }



    private void resetMapperFactory() {
        MapperFactory.init(hinweiseDao.findAll(), schemaversionDao.findAll());
    }



    private static <T> T mappedValue(T dbValue, T mappedValue) {
        return dbValue != null ? dbValue : mappedValue;
    }


    private static String toString(Object o) {
        return ToStringBuilder.reflectionToString(o, ToStringStyle.DEFAULT_STYLE);
    }

}
*/
