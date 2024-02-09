package dnse3.orchestration.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Clase para actualizar una simulación completa que se está ejecutando en el Servicio de Colas
 */
public class UpdateJob {

    /**
     * Método utilizado para actualizar el valor de la prioridad de una simulación
     * 
     * @param queueAddress Dirección del Servicio de Colas
     * @param jsonObject Objeto JSON con la prioridad, identificador del trabajo y usuario
     */
    public void updateJob(String queueAddress, JSONObject jsonObject) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPut hp = new HttpPut("http://" + queueAddress + "/v0.1/simulationqueue/");
            StringEntity body = new StringEntity(jsonObject.toString());
            body.setContentType("application/json");
            hp.setEntity(body);
            response = httpClient.execute(hp);
            hp = null; body = null;
            int codigo = response.getStatusLine().getStatusCode();
            EntityUtils.consume(response.getEntity());
            switch (codigo) {
            case 200:
            case 204:
                response.close();
                System.out.println("Trabajo actualizado");
                break;
            default:
                throw new Exception("Código no esperado " + codigo);
            }
        } catch (Exception e) {
            System.out.println("No se pudo actualizar la prioridad del trabajo. " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
                System.err.println("No se pudo cerrar correctamente la conexión");
                e.printStackTrace();
            }
        }
    }
}