# Verwende ein offizielles sbt + Scala + JDK-Image
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Setze das Arbeitsverzeichnis
WORKDIR /persistenceService

# Kopiere nur die Build-Dateien zuerst (um Docker-Cache zu nutzen)
COPY build.sbt ./
COPY project ./project

# Trigger dependency resolution early
RUN sbt update

# Jetzt den restlichen Quellcode kopieren
COPY . .

# Optional: Kompiliere das Projekt vorab
RUN sbt compile

# Standardbefehl zum Starten des Containers
CMD ["sbt", "run"]
