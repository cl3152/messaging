package com.len.messaging.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Elster")
public class ElsterData {

    @JacksonXmlProperty(localName = "Verfahren")
    private String verfahren;

    @JacksonXmlProperty(localName = "DatenArt")
    private String datenart;

    @JacksonXmlProperty(localName = "Transfer")
    private Transfer transfer;

    @JacksonXmlProperty(localName = "Arbeitnehmer")
    private Arbeitnehmer arbeitnehmer;

}