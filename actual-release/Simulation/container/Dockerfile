# Se inicia la creación del Docker partiendo de la imagen oficial de ubuntu
FROM ubuntu:18.04

# Instalo los paquetes minimos que se citan en las instrucciones del ns3. Instalo una de las versiones de los compiladores que nos compatibles con la version utilizada del ns3
RUN apt update && apt upgrade -y && apt install -y gcc-6 g++-6 python && update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-6 10 && update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-6 10

# Agrego el ns3 en formato tar.bz2. Al realizarolo de este modo realiza automaticamente la descompresión y crea el directorio ns-allinone-3.18.2 con los ficheros necesarios. https://docs.docker.com/engine/reference/builder/#add
ADD ns-allinone-3.18.2.tar.bz2 /opt

# Lanzo el constructor segun se indica en el README. Agrego la orden CXXFLAGS="-Wall" para que ignore los warnings en la instalación https://www.nsnam.org/wiki/HOWTO_build_old_versions_of_ns-3_on_newer_compilers
RUN cd /opt/ns-allinone-3.18.2/ns-3.18.1 && CXXFLAGS="-Wall" ./waf -d debug --enable-examples --enable-tests configure && ./waf

# Enlazo bibliotecas
RUN echo "/opt/ns-allinone-3.18.2/ns-3.18.1/build" >> /etc/ld.so.conf.d/ns3.conf && ldconfig

# Creo el usuario sin privilegios
RUN useradd -md /home/simulacion -s /bin/bash -U simulacion

# Instalo el JRE
RUN apt install -y openjdk-8-jre

# Utilizo el usuario sin privilegios
USER simulacion:simulacion

# Cambiamos el directorio de trabajo por la carpeta personal del usuario sin privilegios
WORKDIR /home/simulacion

# Se copian los ficheros necesarios para la ejecución del servicio de simulación
COPY --chown=simulacion:simulacion simulation.jar .
COPY --chown=simulacion:simulacion almacen.properties .


#Damos unos valores por defecto a la entrada del .jar. Se puede cambiar posteriormente indicándolo en el .yml o al lanzar el contenedor.
ENV queueAddress=/*IP queue service*/:8081
ENV limit=1

# Comando que ejecuta el contenedor al iniciarse
ENTRYPOINT java -jar simulation.jar $queueAddress $limit
