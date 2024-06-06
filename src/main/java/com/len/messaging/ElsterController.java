package com.len.messaging;


import com.len.messaging.domain.ElsterData;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.exception.SammellieferungException;
import com.len.messaging.util.xmlMapper.ElsterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/elster")
public class ElsterController {

    private final ElsterMapper elsterMapper;

    @Autowired
    public ElsterController(ElsterMapper elsterMapper) {
        this.elsterMapper = elsterMapper;
    }

    @PostMapping(value = "/convert", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElsterData convertXmlToElster(@RequestBody String xml) throws SammellieferungException {
        ElsterData elsterData = elsterMapper.convertXmlToElster(xml);
        System.out.println(elsterData);
        return elsterData;
    }
}
