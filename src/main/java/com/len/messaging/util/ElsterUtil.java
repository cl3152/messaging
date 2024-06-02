/*
package com.len.messaging.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.len.messaging.util.xml.ElsterRootElement;
import org.apache.commons.lang3.BooleanUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.elster.elsterxml.schema.headerbasis.v3.BundeslandSType;


public final class ElsterUtil {

    private ElsterUtil() {
    }



    public static boolean isTH11(ElsterRootElement elster) {
        return elster instanceof de.elster.elsterxml.schema.v11.Elster;
    }

    public static boolean isTH9(ElsterRootElement elster) {
        return elster instanceof de.elster._2002.xmlschema.Elster;
    }


    private static de.elster.elsterxml.schema.v11.Elster asTH11(ElsterRootElement elster) {
        return (de.elster.elsterxml.schema.v11.Elster) elster;
    }

    private static de.elster._2002.xmlschema.Elster asTH9(ElsterRootElement elster) {
        return (de.elster._2002.xmlschema.Elster) elster;
    }



    public static <T> T nutzdaten(ElsterRootElement elster, int ndBlock, Class<T> type) {
        return versioned(
                elster,
                e ->  type.cast(e.getDatenTeil().getNutzdatenblock().get(ndBlock).getNutzdaten().getContent().get(0)),
                e -> type.cast(e.getDatenTeil().getNutzdatenblock().get(ndBlock).getNutzdaten().getContent().get(0))
        );
    }


    public static <T> T nutzdaten(ElsterRootElement elster, Class<T> type) {
        return nutzdaten(elster, 0, type);
    }



    enum AccountKey {
        ACCOUNT_ID("AuthAccountID"),
        ZUORDNUNGS_KRITERIUM("AuthZuordnungskriterium"),
        STEUERNUMMER("AuthSteuernummer"),
        REG_DATUM("AuthRegDatum"),
        DUE_ZOBEL_ID("AuthDueZobelID"),
        DUE_LEBEND("AuthDueZobelIDIstLebend"),
        DUE_AKTUELLE_STEUERNUMMER("AuthAktuelleSteuernummer");

        private String tagName;
        private AccountKey(String s) {
            tagName = s;
        }
    }



    @SuppressWarnings("squid:S3776") // Erlaube das tiefe Schachteln.
    public static String getAccountInfo(ElsterRootElement elster, AccountKey accKey) {
        // Suche das <AuthInfo>-Element durch Iterieren über die einzelnen DOM-Knoten.
        try {
            List<Object> content = versioned(
                    elster,
                    e -> e.getTransferHeader().getSigUser().getContent(),
                    e -> e.getTransferHeader().getSigUser().getContent()
            );
            for(Object o : content) {
                if (o instanceof Element) {
                    Element element = (Element) o;
                    if ("AuthInfo".equals(element.getLocalName())) {
                        NodeList children = element.getChildNodes();
                        for(int i=0; i < children.getLength(); i++) {
                            Node node = children.item(i);
                            if (accKey.tagName.equals(node.getLocalName())) {
                                return getTextContent(node, true).trim();
                            }
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }



    private static String getTextContent(Node node, boolean trimEachChild) {
        StringBuilder sb = new StringBuilder();
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue();
                sb.append( trimEachChild ? text.trim() : text );
            }
        }
        return sb.toString();
    }



    public static Long getAccountId(ElsterRootElement elster) {
        String id = getAccountInfo(elster, AccountKey.ACCOUNT_ID);
        try {
            return Long.parseLong(id);
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }



    public static String getAuthSteuernummer(ElsterRootElement elster) {
        return getAccountInfo(elster, AccountKey.STEUERNUMMER);
    }



    public static String getDueZobelId(ElsterRootElement elster) {
        return getAccountInfo(elster, AccountKey.DUE_ZOBEL_ID);
    }



    public static Boolean isDueLebend(ElsterRootElement elster) {
        String lebend = getAccountInfo(elster, AccountKey.DUE_LEBEND);
        return BooleanUtils.toBooleanObject(lebend);
    }



    public static String getDueAktuelleSteuernummer(ElsterRootElement elster) {
        return getAccountInfo(elster, AccountKey.DUE_AKTUELLE_STEUERNUMMER);
    }



    public static boolean isEchtdaten(String testmerker) {
        return testmerker == null
                || "".equals(testmerker)
                || "0".equals(testmerker)
                || testmerker.matches("0+");
    }



    public static String getTransferTicket(ElsterRootElement elster) {
        try {
            return versioned(
                    elster,
                    e -> e.getTransferHeader().getTransferTicket(),
                    e -> e.getTransferHeader().getTransferTicket()
            );
        } catch (NullPointerException e) {
            return null;
        }
    }



    public static String getDatenart(ElsterRootElement elster) {
        return versioned(
                elster,
                e -> e.getTransferHeader().getDatenArt().value(),
                e -> e.getTransferHeader().getDatenArt().value()
        );
    }



    public static List<String> getEmpfaenger(ElsterRootElement elster) {
        return versioned(
                elster,
                e -> Arrays.asList(
                        e.getTransferHeader().getEmpfaenger().getZiel().toString()
                ),
                e -> e.getTransferHeader().getEmpfaenger().getZiel().stream()
                        .map(BundeslandSType::toString)
                        .collect(Collectors.toList())
        );
    }



    public static String getFirstNutzdatenTicket(ElsterRootElement elster) {
        try {
            return versioned(
                    elster,
                    e -> e.getDatenTeil().getNutzdatenblock().get(0).getNutzdatenHeader().getNutzdatenTicket(),
                    e -> e.getDatenTeil().getNutzdatenblock().get(0).getNutzdatenHeader().getNutzdatenTicket()
            );
        } catch (NullPointerException e) {
            return null;
        }
    }



    public static List<String> getNutzdatenTickets(ElsterRootElement elster) {
        return versioned(
                elster,
                e -> e.getDatenTeil().getNutzdatenblock().stream()
                        .map( ndb -> ndb.getNutzdatenHeader().getNutzdatenTicket() )
                        .collect(Collectors.toList()),
                e -> e.getDatenTeil().getNutzdatenblock().stream()
                        .map( ndb -> ndb.getNutzdatenHeader().getNutzdatenTicket() )
                        .collect(Collectors.toList())
        );
    }



    public static <T> T versioned(
            ElsterRootElement elster,
            Function<de.elster._2002.xmlschema.Elster, T> th9Function,
            Function<de.elster.elsterxml.schema.v11.Elster, T> th11Function
    ) {
        if (isTH9(elster)) {
            return th9Function.apply(asTH9(elster));
        }
        if (isTH11(elster)) {
            return th11Function.apply(asTH11(elster));
        }
        throw new IllegalArgumentException("Nicht unterstützte Elster-Version");
    }

}
*/
