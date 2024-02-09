# Se inicia la creación del Docker partiendo de la imagen oficial de openJDK en su versión 14
FROM ubuntu:18.04

RUN apt update && apt upgrade -y && apt install -y openjdk-8-jre

# Se crea el usuario sin privilegios
RUN useradd -md /home/report -s /bin/bash -U report
USER report:report

# Se cambia el directorio de trabajo para comenzar a trabajar en el home del usuario sin privilegios
WORKDIR /home/report

# Ficheros necesarios para report
COPY --chown=report:report report.jar .
COPY --chown=report:report almacen.properties .

# Variables de entorno
ENV queueAddress=192.168.50.7:8081
ENV limit=1

# Se lanza el Servicio de Informes cuando se inicie el contenedor
ENTRYPOINT java -jar report.jar $queueAddress $limit
