<a href="https://dnse3.gsic.uva.es">
  <h1 align="center">
    <figure>
        <img alt="DNSE3 logo" src="/images4doc/dnse3.svg" style="min-width:300px; max-width:500px; width:50%;">
    </figure>
  </h1>
 </a>

## About DNSE3

DNSE3 (Distributed Network Simulation Environment 3) is a network simulation application designed and developed by [GSIC/EMIC research group](https://gsic.uva.es) ([Universidad de Valladolid](https://uva.es)) that makes use of cloud computing to process simulations in a distributed way to reduce simulation times. With DNSE3, individual or parameter sweep simulations can be carried out transparently using the [ns-3](https://www.nsnam.org/) simulator.

The application has been divided into services. Some of these services make use of external applications (e.g., [ownCloud](https://owncloud.com/) in the case of the Storage Service) and can be run through [Docker containers](https://www.docker.com/) for easy replication (Simulation and Report services). Queue, Orchestration and Monitoring & Scaling services are running using JRE. An overview of the services found in the application and their interaction can be seen in the following image:

<div align="center">
  <figure>
    <img alt="Overview of the services that make up DNSE3 and their relationships." src="/images4doc/arqGen.svg" style="min-width:300px; max-width:800px; width:70%;">
    <!-- <figcaption>Overview of the services that make up DNSE3 and their relationships.</figcaption> -->
  </figure>
</div>

More information about DNSE3 can be found in the publications section. You can cite DNSE3 as:
> Serrano-Iglesias. S., Gómez-Sánchez, E., Bote-Lorenzo, M.L., Asensio-Pérez, J.I., Rodríguez-Cayetano, M. **A self-scalable distributed network simulation environment based on cloud computing**. *Cluster Computing* **21**, 1899-1915 (2018). DOI: [10.1007/s10586-018-2816-5](https://doi.org/10.1007/s10586-018-2816-5)

## License

[APACHE LICENSE, VERSION 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Publications

- Serrano-Iglesias. S., Gómez-Sánchez, E., Bote-Lorenzo, M.L., Asensio-Pérez, J.I., Rodríguez-Cayetano, M. **A self-scalable distributed network simulation environment based on cloud computing**. *Cluster Computing* **21**, 1899-1915 (2018). DOI: [10.1007/s10586-018-2816-5](https://doi.org/10.1007/s10586-018-2816-5)
- Serrano-Iglesias. S., Gómez-Sánchez, E., Bote-Lorenzo, M.L., Asensio-Pérez, J.I., Rodríguez-Cayetano, M. **Entorno de simulación distribuida de redes basado en la nube computacional**. *Proceedings of the XIII Jornadas de Ingeniería Telemática, JITEL 2017*, Valencia, Spain (2017). In Spanish. [http://ocs.editorial.upv.es/index.php/JITEL/JITEL2017/paper/viewFile/6581/3220](http://ocs.editorial.upv.es/index.php/JITEL/JITEL2017/paper/viewFile/6581/3220)

### Other publications related to DNSE3 or previous works

- García-Zarza. P. **Entorno de simulación de redes distribuido basado en ns-3 y computación en nube con virtualización basada en contenedores**. *Universidad de Valladolid*. Graduate Thesis in Spanish (2019). [https://uvadoc.uva.es/handle/10324/38832](https://uvadoc.uva.es/handle/10324/38832)
- Serrano-Iglesias, S. **Implantación y evaluación de un entorno de simulación de redes distribuido basado en ns-3 y computación en nube**. *Universidad de Valladolid*. Master Thesis in Spanish (2017). [https://uvadoc.uva.es/handle/10324/27614](https://uvadoc.uva.es/handle/10324/27614)
- Serrano-Iglesias, S. **Entorno de simulación de redes distribuido basado en ns-3 y computación en nube**. *Universidad de Valladolid*. Graduate Thesis in Spanish (2016). [https://uvadoc.uva.es/handle/10324/20980](https://uvadoc.uva.es/handle/10324/20980)
- Cano-Parra, R. **Entorno de simulación de redes TCP/IP usando servicios rest basado en nube computacional** *Universidad de Valladolid*. Master Thesis in Spanish (2012). [https://uvadoc.uva.es/handle/10324/2681](https://uvadoc.uva.es/handle/10324/2681)
