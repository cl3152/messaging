package com.len.messaging.domain;

public interface HinweisCommon {

    Long getId();
    void setId(Long id);

    Long getSpeichts();
    void setSpeichts(Long speichts);

    Long getHinweiseId();
    void setHinweiseId(Long hinweiseId);

    Long getTransferId();
    void setTransferId(Long transferId);

    Hinweise getHinweis();
    void setHinweis(Hinweise hinweis);

}
