Protoyp für den SRT-Indexer 

- Funktioniert mit der H2 und dem eingebetteten JMS-Broker (abgesehen vom /actuator/jms Endpunkt)
- Alternativ kann mit docker compose up der jmsBroker hochgefahren werden
- Um den SRT-Logger zu verwenden, müssen 
    - die externen Bibliotheken eingebunden werden
    - die logback.xml nach src/main/resources verschoben werden
    - An verschiedenen Stellen Programmcode einkommentiert werden
