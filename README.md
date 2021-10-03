# MasterProjektBackend
This is the backend to merge data from http Archive and Google BigQuery

# Vorbereitung
Ihr braucht den API Key von Google und müsst den in der application.properties hinzufügen. Die application properties, muss im resources folder liegen.

Sie muss die folgenden Felder beinhalten:
```#google API Key Location
google.api.key.location=D://Projekte//Master-projekt//key.json

#Database
spring.datasource.url=jdbc:postgresql://localhost:5432/masterProject
spring.datasource.username=postgres
spring.datasource.password=test123
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL81Dialect
spring.jpa.hibernate.ddl-auto = none 
```

Den Pfad zum Key müsst ihr natürlich anpassen.

## Datenbank

Die Datenbank ist eine Postgres DB. Ich habe eine docker-compose file hinzugefügt. Ihr könnt unter linux einfach im hauptordner sein und ```sudo docker-compose up -d ``` eingeben und dann läuft die. Wenn ihr das unter Windows machen wollt, müsst ihr euch selber erkundigen wie ihr die zum laufen bekommt.

Danach sollte alles laufen.

# Requests

Die Route für die Requests könnt ihr im Maincontroller finden. Ist aber soweit ich weiß ``` localhost:8080/technologies?startDate=2016_01&endDate=2016_01&countryCode=com```

Viel Spaß

