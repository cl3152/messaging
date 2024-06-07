package com.len.messaging.util.xmlMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.exception.SammellieferungException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElsterMapper {

    private final XmlMapper xmlMapper;

    public ElsterMapper() {
        this.xmlMapper = new XmlMapper();
    }


    public ElsterData convertXmlToElster(String xml) throws SammellieferungException {
        try {
            return xmlMapper.readValue(xml, ElsterData.class);
        } catch (JsonProcessingException e) {
            /* Im Original wird beim fehlgeschlagenen Mapping eine SammellieferungsException geworfen. (mapper.map)
             * Hier analog.
             */
            throw new SammellieferungException("Mapping schlug fehl: ", e);
        }
    }

}