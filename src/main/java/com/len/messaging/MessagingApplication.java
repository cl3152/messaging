package com.len.messaging;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.config.JMSProperties;
import com.len.messaging.jms.SimpleSender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MessagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingApplication.class, args);
	}

/*	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender, XmlMapper xmlMapper, ObjectMapper objectMapper) {
		return args -> {
			String xmlMessage = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n" +
					"<Elster xmlns=\"http://www.elster.de/2002/XMLSchema\">\n" +
					"  <TransferHeader version=\"8\">\n" +
					"    <Verfahren>ElsterLohn2</Verfahren>\n" +
					"    <DatenArt>AenderungslisteDUe</DatenArt>\n" +
					"    <Vorgang>send-NoSig</Vorgang>\n" +
					"    <!-- Transferticket, unter welchem die Daten an den Postman zum Einstellen in die\n" +
					"    AUST übergeben werden. Da das Mocksystem auf eine Anfrage des Herstellers reagiert,\n" +
					"    übernehmen wir dess Transferticket.\n" +
					"    Normalerweile kommt dieses TT von der ELStAM (bzw. vom Postman an der dortigen\n" +
					"    Schnittstelle). Aber das TT an dieser Stelle kommt gar nicht zum Hersteller zurück. -->\n" +
					"    <TransferTicket>1234567890123456789</TransferTicket>\n" +
					"    <Testmerker>700000001</Testmerker>\n" +
					"    <Empfaenger id=\"L\">\n" +
					"      <Ziel>CS</Ziel>\n" +
					"    </Empfaenger>\n" +
					"    <HerstellerID>00000</HerstellerID>\n" +
					"    <DatenLieferant>ElsterLohn2 HerstellerMockSystem @HMS-VERSION@</DatenLieferant>\n" +
					"    <EingangsDatum>20130101120000</EingangsDatum>\n" +
					"    <Datei>\n" +
					"      <Verschluesselung>none</Verschluesselung>\n" +
					"      <Kompression>none</Kompression>\n" +
					"      <DatenGroesse>1</DatenGroesse>\n" +
					"      <TransportSchluessel></TransportSchluessel>\n" +
					"    </Datei>\n" +
					"  </TransferHeader>\n" +
					"  <DatenTeil>\n" +
					"    <Nutzdatenblock>\n" +
					"         <NutzdatenHeader version=\"10\">\n" +
					"                <!-- Nutzdaten werden vom Postman ohnehin weggefiltert. Daher nichts\n" +
					"                    eingetragen. -->\n" +
					"                <NutzdatenTicket>ND-1</NutzdatenTicket>\n" +
					"                <Empfaenger id=\"L\">CS</Empfaenger>\n" +
					"                <Hersteller>\n" +
					"                    <ProduktName />\n" +
					"                    <ProduktVersion />\n" +
					"                </Hersteller>\n" +
					"                <DatenLieferant>ElsterLohn2 HerstellerMockSystem @HMS-VERSION@</DatenLieferant>\n" +
					"            </NutzdatenHeader>\n" +
					"      <Nutzdaten>\n" +
					"        <EPoS xsi:schemaLocation=\"http://www.elster.de/elo2/epos/2015 EPoS.xsd\"\n" +
					"                  xmlns=\"http://www.elster.de/elo2/epos/2015\"\n" +
					"                  xmlns:arg=\"http://www.elster.de/elo2/datenuebermittler\"\n" +
					"                  xmlns:elstamComplex=\"http://www.elster.de/elo2/datenuebermittler/Steuerdaten\"\n" +
					"                  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"                <Elo2EposCType>\n" +
					"                    <ns2:Aenderungsliste\n" +
					"                            stnrDUe=\"5192000000099\"\n" +
					"                            transferTicket=\"1234567890123456789\"\n" +
					"                            nutzdatenTicket=\"ND-1\"\n" +
					"                            art=\"ANMELDEBESTAETIGUNGSLISTE\"\n" +
					"                            laufendeNummer=\"201501001\"\n" +
					"                            anzahlTeillisten=\"1\"\n" +
					"                            schemaversion=\"2\"\n" +
					"                            xsi:schemaLocation=\"http://www.elster.de/elo2/datenuebermittler/aenderungsliste/2015 Aenderungsliste-2015.xsd\"\n" +
					"                            xmlns:ns2=\"http://www.elster.de/elo2/datenuebermittler/aenderungsliste/2015\"\n" +
					"                            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"                        <ns2:Arbeitgeber stnrAG=\"5192070000196\">\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127645382\">\n" +
					"                                  <ns2:ELStAM gueltigAb=\"20150101\">\n" +
					"                                    <ns2:Steuerklasse nummer=\"1\"/>\n" +
					"                                    <ns2:Kinderfreibetrag anzahl=\"0\"/>\n" +
					"                                    <ns2:Kirchensteuer konfession=\"--\"/>\n" +
					"                                    <ns2:Faktor wert=\"0\"/>\n" +
					"                                    <ns2:Freibetrag tag=\"0\" woche=\"0\" monat=\"0\" jahr=\"0\"/>\n" +
					"                                  </ns2:ELStAM>\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020000\" message=\"Keine besonderen Hinweise\" type=\"INFORMATION\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01197645386\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020200\" message=\"Keine Anmeldeberechtigung\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"02217645388\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020201\" message=\"Keine Anmeldung vor Beschäftigungsbeginn möglich\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"07796532843\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020202\" message=\"Arbeitnehmer unbekannt: Die IdNr des Arbeitnehmers kann nicht verifiziert werden.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"09916743524\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020203\" message=\"Erneute Anmeldung nicht möglich - Arbeitnehmer ist bereits angemeldet.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"04507896126\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020204\" message=\"refDatumAG liegt vor Verfahrensstart.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"02217856341\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020205\" message=\"refDatumAG liegt vor Beginn der Meldepflicht.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127649385\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020206\" message=\"refDatumAG liegt vor Jahresbeginn (bei Eingangsdatum ab 01.03. des Jahres).\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"07716539487\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020207\" message=\"refDatumAG liegt vor Vorjahresbeginn (bei Eingangsdatum vor 01.03. des Jahres).\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"02217945385\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020208\" message=\"refDatumAG liegt vor Beschäftigungsbeginn.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127643854\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020209\" message=\"Für ein refDatumAG nach dem Eingangsdatum der Anmeldung in der Clearingstelle ist nur der Tag des Verfahrensstarts erlaubt.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"03397645284\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020210\" message=\"Für Hauptarbeitsverhältnis kann kein Freibetrag angefordert werden.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"07716592483\">\n" +
					"                                 <ns2:ELStAM gueltigAb=\"20150101\">\n" +
					"                                    <ns2:Steuerklasse nummer=\"1\"/>\n" +
					"                                    <ns2:Kinderfreibetrag anzahl=\"0\"/>\n" +
					"                                    <ns2:Kirchensteuer konfession=\"--\"/>\n" +
					"                                    <ns2:Faktor wert=\"0\"/>\n" +
					"                                    <ns2:Freibetrag tag=\"3\" woche=\"23\" monat=\"100\" jahr=\"1200\"/>\n" +
					"                                 </ns2:ELStAM>\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020211\" message=\"Freibetrag gekürzt, da verfügbares Hinzurechnungsvolumen kleiner als angeforderter Freibetrag.\" type=\"INFORMATION\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"03317695288\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020212\" message=\"Angabe des 29.02. außerhalb eines Schaltjahres (beschaeftigungsbeginn, abmeldeDatum oder refDatumAG)\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"06617532847\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020213\" message=\"Das refDatumAG der Anmeldung muss nach dem Ende der Beschäftigung aus der letzten Abmeldung liegen. \" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"03397642851\">\n" +
					"                              <ns2:ELStAM gueltigAb=\"20150101\">\n" +
					"                                <ns2:Steuerklasse nummer=\"1\"/>\n" +
					"                                <ns2:Kinderfreibetrag anzahl=\"0\"/>\n" +
					"                                <ns2:Kirchensteuer konfession=\"--\"/>\n" +
					"                                <ns2:KirchensteuerPartner konfession=\"ev\"/>\n" +
					"                                <ns2:Faktor wert=\"0\"/>\n" +
					"                                <ns2:Freibetrag tag=\"0\" woche=\"0\" monat=\"0\" jahr=\"0\"/>\n" +
					"                              </ns2:ELStAM>\n" +
					"                              <ns2:ELStAM gueltigAb=\"20150102\">\n" +
					"                                <ns2:Steuerklasse nummer=\"4\"/>\n" +
					"                                <ns2:Kinderfreibetrag anzahl=\"1\"/>\n" +
					"                                <ns2:Kirchensteuer konfession=\"rk\"/>\n" +
					"                                <ns2:Faktor wert=\"0\"/>\n" +
					"                                <ns2:Freibetrag tag=\"0\" woche=\"0\" monat=\"0\" jahr=\"0\"/>\n" +
					"                                <ns2:Hinzurechnungsbetrag tag=\"0\" woche=\"0\" monat=\"0\" jahr=\"0\"/>\n" +
					"                              </ns2:ELStAM>\n" +
					"                              <!-- TODO: Wo soll das neue Datum eingetragen werden? -->\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020214\" message=\"Anmeldung nach Kulanzfrist, neues refDatumAG der aktuellen Beschäftigung.\" datum=\"20150103\" type=\"INFORMATION\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"07716592848\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020215\" message=\"Zu dem refDatumAG der Anmeldung liegt bereits ein Hauptarbeitsverhältnis vor.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"03317649281\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020900\" message=\"Verarbeitungsabbruch - bitte kontaktieren Sie Ihr zuständiges Betriebsstätten-Finanzamt.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127645382\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020001\" message=\"Abmeldung erfolgreich\" type=\"INFORMATION\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01197645386\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020202\" message=\"Arbeitnehmer unbekannt: Die IdNr des Arbeitnehmers kann nicht verifiziert werden.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"02217645388\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020212\" message=\"Angabe des 29.02. außerhalb eines Schaltjahres (beschaeftigungsbeginn, abmeldeDatum oder refDatumAG)\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"07796532843\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020300\" message=\"Ab-/Ummeldung des Arbeitnehmers (IdNr und Geburtsdatum) ist nicht möglich, weil kein Arbeitsverhältnis besteht.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"09916743524\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020303\" message=\"refDatumAG aus der Abmeldung stimmt nicht mit refDatumAG aus der An-/Ummeldung überein.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"04507896126\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020304\" message=\"Abmeldung nicht möglich, da Abmeldedatum kleiner als refDatumAG aus der An-/Ummeldung\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"03317856295\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020305\" message=\"Abmeldung nicht möglich, da Abmeldedatum in der Zukunft\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"02217856341\" >\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020306\" datum=\"20150315\" datumAlt=\"20150316\" message=\"Beschäftigungsende des Arbeitsverhältnisses geändert.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127649385\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020900\" message=\"Verarbeitungsabbruch - bitte kontaktieren Sie Ihr zuständiges Betriebsstätten-Finanzamt.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"                            <ns2:Arbeitnehmer idnr=\"01127649386\">\n" +
					"                                <ns2:AN-Verfahrenshinweis code=\"552020999\" message=\"Verarbeitungsabbruch - bitte kontaktieren Sie Ihr zuständiges Betriebsstätten-Finanzamt.\" type=\"ABLEHNUNG\"/>\n" +
					"                            </ns2:Arbeitnehmer>\n" +
					"\n" +
					"                            <ns2:AG-Verfahrenshinweis code=\"552010002\" message=\"Besondere Hinweise zu mindestens einem Arbeitnehmer.\" type=\"INFORMATION\" />\n" +
					"                            <ns2:Steuernummerhinweis code=\"551000009\" message=\"Bitte verwenden Sie im Verfahren ElsterLohn II die im Attribut steuernummer enthaltene Steuernummer\" type=\"INFORMATION\" steuernummer=\"5192022224444\"/>\n" +
					"                        </ns2:Arbeitgeber>\n" +
					"                    </ns2:Aenderungsliste>\n" +
					"                </Elo2EposCType>\n" +
					"                <MetaInformationen xmlns=\"http://www.elster.de/elo2/epos/2015\">\n" +
					"                    <Ordnungsbegriff wert=\"ZO1012345678\" typ=\"due-zid\" />\n" +
					"                    <MetaInformation wert=\"1234567890123456789\" typ=\"anforderungsticket\" />\n" +
					"                    <MetaInformation wert=\"1234567890123456789\" typ=\"bereitstellticket\" />\n" +
					"                    <MetaInformation wert=\"ND-1\" typ=\"nutzdatenticket\" />\n" +
					"                    <MetaInformation wert=\"5192070000196\" typ=\"ag-steuernummer\" />\n" +
					"                    <MetaInformation wert=\"1\" typ=\"laufendeNummer\" />\n" +
					"                    <Schemaversion wert=\"1\" />\n" +
					"                </MetaInformationen>\n" +
					"                <AenderungslistenInfo accountId=\"1234567\" stnrAG=\"5192070000196\" anzahlAN=\"27\" />\n" +
					"            </EPoS>\n" +
					"      </Nutzdaten>\n" +
					"    </Nutzdatenblock>\n" +
					"  </DatenTeil>\n" +
					"</Elster>";

			// Senden der Nachricht
			sender.sendMessage(props.getQueue(), xmlMessage);

			System.out.println("XML gesendet.");
		};
	}*/
/*	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender, ObjectMapper objectMapper) {
		return args -> {
			// Erstellen eines Transfer-Objekts mit einer AgvhListe
			Transfer transfer = new Transfer();
			transfer.setNumber("12345");
			Agvh agvh = new Agvh();
			agvh.setText("Beispieltext");
			agvh.setTransfer(transfer);
			transfer.setAgvhList(Arrays.asList(agvh));

			// Konvertieren des Transfer-Objekts in ein JSON-String
			String jsonMessage = objectMapper.writeValueAsString(transfer);

			System.out.println(jsonMessage);

			// Senden der Nachricht
			sender.sendMessage(props.getQueue(), jsonMessage);
		};
	}*/

/*	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender) {
		return args -> {
			sender.sendMessage(props.getQueue(), "World");
		};
	}*/
}