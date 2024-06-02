package com.len.messaging.util.xmlMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.domain.ElsterData;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElsterMapper {

    private final XmlMapper xmlMapper;

    public ElsterMapper() {
        this.xmlMapper = new XmlMapper();
    }

    public ElsterData convertXmlToElster(String xml) throws IOException {
        return xmlMapper.readValue(xml, ElsterData.class);
    }
}
