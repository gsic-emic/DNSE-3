package dnse3.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que permite realizar las peticiones al sistema de almacenamiento basado
 * en ownCloud
 * 
 * @author GSIC gsic.uva.es
 * @version 20191216
 */
public class CloudManager {
    /** Ruta por la que se accede al almacenamiento. */
    public static String rA;
    /** Ruta para la gestión de usuarios */
    public static String rU;
    /** Ruta para la gestión de grupos */
    public static String rG;
    /** Ruta para la descarga de directorios */
    public static String rD;
    /**
     * Objeto donde se almacenan los datos variables necesiarios para la conexión
     * tales como contraseñas, dirección del host...
     */
    private static Properties properties;

    // These atributes defines the cloud user asign to the service
    private static Instant expirationDate;
    private static HashMap<String, String> stacks;

    /*
     * private static CloseableHttpClient httpClient; private static
     * CloseableHttpResponse response; private static HttpPut hP; private static
     * HttpGet hG; private static HttpPost hPt; private static HttpDelete hD;
     */

    private static HttpClientContext cont;

    /**
     * Método para la creación de los objetos de los que depende esta clase. Se
     * deberá llamar antes de utilizar cualquiera de los otros métodos de esta
     * clase.
     */
    public static void initialize() {
        try {
            CloudManager.properties = new Properties();
            File almacen = new File("almacen.properties");
            CloudManager.properties.load(new FileInputStream(almacen));

            CloudManager.expirationDate = Instant.now();
            CloudManager.stacks = new HashMap<>();
            requestToken();

            /*
             * Hacemos todos estos pasos para que siempre envíe las credenciales de
             * autenticación
             */
            CredentialsProvider cP = new BasicCredentialsProvider();
            UsernamePasswordCredentials uPC = new UsernamePasswordCredentials(CloudManager.properties.getProperty("user"), CloudManager.properties.getProperty("pass"));
            //UsernamePasswordCredentials uPC = new UsernamePasswordCredentials("*", "*");
            cP.setCredentials(AuthScope.ANY, uPC);
            String host = CloudManager.properties.getProperty("host");
            //String host = "127.0.0.1";
            int port = Integer.parseInt(CloudManager.properties.getProperty("port"));
            //int port = 9090;
            HttpHost tH = new HttpHost(host, port, "http");
            AuthCache aC = new BasicAuthCache();
            aC.put(tH, new BasicScheme());
            cont = HttpClientContext.create();
            cont.setCredentialsProvider(cP);
            cont.setAuthCache(aC);
            // Rutas para ficheros, usuarios, grupos..
            rA = "http://" + host + ":" + port + "/remote.php/webdav/";
            rU = "http://" + host + ":" + port + "/ocs/v1.php/cloud/users";
            rG = "http://" + host + ":" + port + "/ocs/v1.php/cloud/groups";
            rD = "http://" + host + ":" + port + "/index.php/apps/files/ajax/download.php?dir=";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para descargar un fichero de Owncloud.
     * 
     * @param downloadURI Dirección de acceso al recurso que queremos descargar.
     * @param path        Ruta donde queremos almacenar el fichero.
     * @return Ruta relativa donde se ha almacenado el recurso. Null si no se ha
     *         conseguido guardar o descargar.
     */
    public static String downloadFile(URI downloadURI, File path) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String ruta = null;
        try {
            HttpGet hG = new HttpGet(downloadURI);
            response = httpClient.execute(hG, cont);
            hG = null;

            int codigo = response.getStatusLine().getStatusCode();

            switch (codigo) {
            case 200: // Recurso encontrado y descargado
                HttpEntity entity = response.getEntity();
                String[] name = downloadURI.getPath().split("/");
                String filename = URLDecoder.decode(name[name.length - 1], "UTF-8");

                File outFile = new File(path, filename);
                BufferedOutputStream oS = new BufferedOutputStream(new FileOutputStream(outFile));

                int inByte;
                while ((inByte = entity.getContent().read()) != -1)
                    oS.write(inByte);

                oS.close();
                ruta = outFile.getPath();
                EntityUtils.consume(entity);
                break;
            case 401: // Problemas de autenticación
                System.err.println("\ndownloadFile - Error en la autenticación.");
                break;
            case 404: // Recurso no encontrado en la ruta indicada
                System.err.println("\ndownloadFile - No se ha encontrado el recurso.");
                break;
            default:
                System.err.println("\ndownloadFile - Código: " + codigo);
            }
        } catch (Exception e) {
            System.err.println("\ndownloadFile - " + e.getMessage());
            // e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("\ndownloadFile - " + e.getMessage());
                // e.printStackTrace();
            }
        }
        return ruta;
    }

    /**
     * Método para descargar un fichero del árbol de Owncloud. El ficheor se
     * descarga en el directorio tmp.
     * 
     * @param downloadURI Ruta donde está el recurso.
     * @return Ruta relativa con nombre del fichero descargado. También puede
     *         devolver null cuando se produzca algún problema.
     */
    public static String downloadFile(URI downloadURI) {
        File path = new File("tmp");
        if (!path.exists()) {
            path.mkdir();
        }
        return downloadFile(downloadURI, path);
    }

    /**
     * Método para descargar un fichero almacenado en Owncloud y guardarlo en una
     * ruta especificada.
     * 
     * @param serverPath Ruta desde la raíz donde está almacenado el recurso con el
     *                   nombre del fichero.
     * @param path       Ruta donde se quiere descargar el recurso.
     * @return Ruta relativa del recurso descargado. Si ha ocurrido algún problema
     *         nos devuelve null.
     * @throws URISyntaxException Se lanza esta excepción cuando la ruta indicada
     *                            por el usuario contenga algún error sintáctico.
     */
    public static String downloadFile(String serverPath, File path) throws URISyntaxException {
        return downloadFile(new URI(rA + serverPath.replaceAll(" ", "%20")), path);
    }

    /**
     * Método para descargar un recurso de Owncloud y almacenarlo en la carpeta por
     * defecto.
     * 
     * @param path Ruta desde la raíz donde está el recurso + nombre del recurso.
     * @return Ruta relativa del recurso o null si ha ocurrido algún error.
     * @throws URISyntaxException La ruta indicada tiene algún error sintáctico.
     */
    public static String downloadFile(String path) throws URISyntaxException {
        return downloadFile(new URI(rA + path.replaceAll(" ", "%20")));
    }

    /**
     * Método para almacenar un elemento en el servidor. Si el elemento ya existía en el 
     * sistema lo elimina y vuelve a intentar guardarlo.
     * 
     * @param path        Ruta desde la raíz.
     * @param file        Recurso que se va a almacenar en el servidor.
     * @param contentType Tipo de recurso que se va a almacenar en el servidor.
     * @return Nombre del recurso almacenado o null si se produce algún problema.
     */
    public static String uploadFile(String path, File file, ContentType contentType) {

        String salida = null;
        String nombreFichero = file.getName().replaceAll(" ", "%20");
        String ruta = (path.endsWith("/") ? path : (path + "/")) + nombreFichero;
        // Primero comprobamos que la ruta de directorios existe.
        compruebaRuta(path);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = new FileEntity(file, contentType);
        for(int i = 0; i<3; i++){
            try {
                httpClient = HttpClients.createDefault();
                // Se sube el fichero
                HttpPut hP = new HttpPut(rA + ruta);
                hP.addHeader("Content-Disposition", "attachment; filename=\"" + nombreFichero + "\"");
                hP.setEntity(entity);
                response = httpClient.execute(hP, cont);
                int codigo = response.getStatusLine().getStatusCode();
                switch (codigo) {
                case 201: // Creado
                    salida = ruta;
                    EntityUtils.consume(entity);
                    return salida;
                case 204: // El elemento ya existe
                    System.err.println("uploadFile - El elemento existe en la ruta especificada, por lo que se elimina. Intento "+i);
                    deletePath(ruta);
                    break;
                case 409: // Conflicto
                    System.err.println("uploadFile - Conflicto al subir el elemento.");
                    break;
                default:
                    System.err.println("uploadFile - Código " + codigo);
                }
            } catch (Exception e) {
                System.err.println("uploadFile - Excepción - " + e.toString());
                e.printStackTrace();
            } finally {
                try {
                    if (response != null)
                        response.close();
                    if (httpClient != null)
                        httpClient.close();
                } catch (Exception e) {
                    System.err.println("uploadFile - Excepción - " + e.toString());
                    e.printStackTrace();
                }
            }
        }
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {
            System.err.println("uploadFile - Excepción - " + e.toString());
        }
        return salida;
    }
    
    /**
     * Método para borrar un recurso. El recurso puede ser un directorio.
     * @param path Ruta desde la raíz al recurso que se va a eliminar.
     */
    public static void deletePath(String path) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            HttpDelete hD = new HttpDelete(rA + path);
            response = httpClient.execute(hD, cont);
            hD = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch (codigo) {
            case 200:
            case 204:
                System.out.println("deletePath - Recurso eliminado");
                break;
            case 404:
                System.err.println("deletePath - Recurso no encontrado");
                break;
            default: 
                System.err.println("deletePath - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("deletePath - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("deletePath - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para crear un nuevo directorio.
     * @param path Ruta desde la raíz con el nombre para el nuevo directorio.
     * @return Devuelve verdadero cuando el directoro esté creado o false cuando
     * ocurra algún error. El método de llamada tendrá que decidir qué acción tomar
     */
    public static boolean newFolder(String path) {//Necesito utilizar MKCOL
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpMkcol hMk = new HttpMkcol(rA + path);
            response = httpClient.execute(hMk, cont);
            hMk = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 201:
                //System.out.println("newFolder - El directorio ha sido creado.");
                return true;
            case 405:
                //System.err.println("newFolder - El directorio introducido ya existe.");
                return true;
            case 409:
                //System.err.println("newFolder - El directorio o directorios padre no existen");
                return false;
            default:
                System.err.println("newFolder - Código " + codigo);
                return false;
            }
        } catch(Exception e) {
            System.err.println("newFolder - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("newFolder - " + e.toString());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Método para obtener el contenido de un directorio.
     * @param path Ruta desde la raíz del directorio del cual se desea obtener su contenido.
     * @return Array con el contenido del directorio.
     */
    public static List<String> listObjects(String path) {
        //Necesito usar PropFind por lo que deja de ser un servicio REST
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        List<String> salida = new ArrayList<>();
        try {
            HttpPropfind hPp = new HttpPropfind(rA + path, null, 1);
            response = httpClient.execute(hPp, cont);
            hPp = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 207:
                HttpEntity entity = response.getEntity();
                String rS = EntityUtils.toString(entity, "UTF-8");
                //Manipulamos la respuesta para obtener la lista con el contenido
                String[] partes = rS.split("<d:href>");
                rS = null;
                //Empezamos en 2 ya que en el 0 no tiene información relevante y el 1 va a ser el padre
                for(int i = 2; i < partes.length; i++)
                    salida.add(partes[i].split("</d:href>")[0].replaceAll("%20", " ").replaceFirst("/remote.php/webdav/", ""));
                EntityUtils.consume(entity);
                break;
            case 404:
                System.err.println("listObjects - Directorio no encontrado");
                salida = null;
                break;
            default:
                System.err.println("listObjects - Código " + codigo);
                salida = null;
            }
        } catch(Exception e) {
            System.err.println("listObjects - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("listFolder - " + e.toString());
                e.printStackTrace();
            }
        }
        return salida;
    }

    /**
     * Método para descargar un .zip con el contenido de la carpeta que se indique.
     * @param path Ruta desde la raíz del directorio que contiene la carpeta que deseamos descargar.
     * @param folder Directorio que deseamos descargar.
     * @param localFolder Lugar donde queremos almacenar el .zip
     */
    public static void downloadFolder(String path, String folder, String localFolder) {
        headFile("");//Necesario para que se realice la descarga del zip
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet hG = new HttpGet(rD + path + "/" + folder);
            response = httpClient.execute(hG, cont);
            hG = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 200:
                HttpEntity entity = response.getEntity();
                if(entity != null) {
                    
                    if(entity.getContentType().toString().contains("zip")) {
                        String nombre = folder + ".zip";
                        File dir;
                        if(localFolder.equals("")) {
                            dir = new File("tmp");
                            if(!dir.exists()) {
                                dir.mkdir();
                            }
                        }
                        else {
                            dir = new File(localFolder);
                            System.err.println(dir.getPath());
                        }
    
                        File outFile = new File(dir, nombre);
                        
                        try(FileOutputStream oS = new FileOutputStream(outFile)){
                            entity.writeTo(oS);
                            oS.close();
                        }
                    }
                    else {
                        System.err.println("No se ha podido descargar el directorio");
                    }
                    EntityUtils.consume(entity);
                }
                break;
            default:
                System.err.println("downloadFolder - Código " + codigo);
            }
        } catch(Exception e) {
            System.err.println("downloadFolder - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("downloadFolder - " + e.toString());
                //e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para descargar un .zip con el contenido de la carpeta que se indique.
     * @param path Ruta desde la raíz del directorio que contiene la carpeta que deseamos descargar.
     * 				El último elemento de la ruta será el directorio a descargar.
     * @param localFolder Lugar donde queremos almacenar el .zip
     */
    public static void downloadFolder(String path, String localFolder) {
        String[] ruta = path.split("/");
        String a = "";
        int tama = ruta.length - 1;
        for(int i = 0; i < tama; i++)
            a+=ruta[i]+"/";
        downloadFolder(a, ruta[tama], localFolder);
    }
    
    /**
     * Método para agregar un nuevo usuario al sistema.
     * @param id Identificador del usuario en el sistema.
     * @param passwd Contraseña del usuario.
     */
    public static void addUser(String id, String passwd) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            HttpPost hPt = new HttpPost(rU + "?format=json");
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("userid", id));
            p.add(new BasicNameValuePair("password", passwd));
            hPt.setEntity(new UrlEncodedFormEntity(p));
            p = null;
            response = httpClient.execute(hPt, cont);
            hPt = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                casosAPI("addUser", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                break;
            default:
                System.err.println("addUser - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("addUser - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("addUser - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para agregar un nuevo usuario al sistema.
     * @param id Identificador del usuario.
     * @param passwd Contraseña del usuario.
     * @param groups Grupo o grupos a los que pertenece el nuevo usuario.
     */
    public static void addUser(String id, String passwd, String[] groups) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost hPt = new HttpPost(rU + "?format=json");
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("userid", id));
            p.add(new BasicNameValuePair("password", passwd));
            for(int i = 0; i < groups.length; i++) {
                p.add(new BasicNameValuePair("groups[]", groups[i]));
            }
            hPt.setEntity(new UrlEncodedFormEntity(p));
            p = null;
            response = httpClient.execute(hPt, cont);
            hPt = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                casosAPI("addUser", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                break;
            default:
                System.err.println("addUser - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("addUser - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("addUser - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método que proporciona los usuarios registrados en el sistema.
     * @return Lista de identificadores de los usuarios registrados en el sistema.
     *         Devuelve null si ha ocurrido algún problema o el sistema no tiene registrados
     *         usuarios.
     */
    public static String[] listUsers() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet hG = new HttpGet(rU + "?format=json");
            response = httpClient.execute(hG, cont);
            hG = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                JSONArray lista = vectorRespuesta.getJSONObject("ocs").getJSONObject("data").getJSONArray("users");
                vectorRespuesta = null;
                ArrayList<String> l = new ArrayList<>();
                for(int i = 0; i< lista.length(); i++)
                    l.add(lista.getString(i));
                String[] salida = new String[l.size()];
                salida = l.toArray(salida);
                return salida;
            default:
                System.err.println("listUser - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("listUsers - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("listUsers - " + e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Método para borrar un usuario del sistema.
     * @param userId Identificador del usuario a eliminar.
     */
    public static void deleteUser(String userId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            HttpDelete hD = new HttpDelete(rU + "/" + userId + "?format=json");
            response = httpClient.execute(hD, cont);
            hD = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                casosAPI("deleteUser", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                break;
            default:
                System.err.println("deleteUser - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("deleteUser - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("deleteUser - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para editar los parámetros de un usuario del sistema.
     * @param userId Usuario del sistema que se desea modificar.
     * @param tipo Tipo de parámetro que se va a editar. Tipos permitidos email, displayname, password, quota.
     * @param v Nuevo valor que se desea que tome el parámetro indicado.
     */
    public static void editUser(String userId, String tipo, String v) {
        if(tipo.equals("email") || tipo.equals("displayname") || tipo.equals("password") || tipo.equals("quota")){
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                HttpPut hP = new HttpPut(rU + "/" + userId + "?format=json");
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("key", tipo));
                p.add(new BasicNameValuePair("value", v));
                hP.setEntity(new UrlEncodedFormEntity(p));
                p = null;
                response = httpClient.execute(hP, cont);
                hP = null;
                int codigo = response.getStatusLine().getStatusCode();
                switch(codigo) {
                case 200:
                    JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                    casosAPI("editUser", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                    break;
                default:
                    System.err.println("editUser - Código " + codigo);
                }
            } catch(Exception e) {
                System.err.println("editUser - " + e.toString());
                e.printStackTrace();
            } finally {
                try {
                    if(response!=null)
                        response.close();
                    httpClient.close();
                } catch (Exception e) {
                    System.err.println("editUser - " + e.toString());
                    e.printStackTrace();
                }
            }
        }
        else {
            System.err.println("editUser - El tipo indicado no es válido");
        }
    }
    
    /**
     * Método para agregar un nuevo grupo al sistema.
     * @param gId Identificador con el que diferenciar el nuevo grupo.
     */
    public static void addGroup(String gId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            HttpPost hPt = new HttpPost(rG + "?format=json");
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("groupid", gId));
            hPt.setEntity(new UrlEncodedFormEntity(p));
            p = null;
            
            response = httpClient.execute(hPt, cont);
            hPt = null;
            int codigo = response.getStatusLine().getStatusCode();
            
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                casosAPI("addGroup", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                break;
            default:
                System.err.println("addGroup - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("addGroup - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("addGroup - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para eliminar un grupo que exista en el sistema.
     * @param gId Identificador del grupo a borrar.
     */
    public static void deleteGroup(String gId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            HttpDelete hD = new HttpDelete(rG + "/" + gId + "?format=json");
            response = httpClient.execute(hD, cont);
            hD = null;
            int codigo = response.getStatusLine().getStatusCode();
            
            switch(codigo) {
            case 200:
                JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
                casosAPI("deleteGroup", vectorRespuesta.getJSONObject("ocs").getJSONObject("meta").getInt("statuscode"));
                break;
            default:
                System.err.println("deleteGroup - Código " + codigo);
            }
        } catch (Exception e) {
            System.err.println("deleteGroup - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("deleteGroup - " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Método para comprobar si un objeto existe.
     * 
     * @param URI ruta al objeto
     * @return Devuelve verdadero si existe el objeto en el sistema y false si no existe.
     */
    public static boolean headFile(String URI) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        boolean salida = false;
        try {
            HttpHead hH = new HttpHead(rA + URI.replaceAll(" ", "%20"));
            response = httpClient.execute(hH, cont);
            hH = null;
            int codigo = response.getStatusLine().getStatusCode();
            switch (codigo) {
            case 200:
                salida = true;
                break;
            default:
                //System.err.println("headFile - Código " + codigo);
                break;
            }
        } catch (Exception e) {
            System.err.println("headFile - " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            } catch (Exception e) {
                System.err.println("headFile - " + e.toString());
                e.printStackTrace();
            }
        }
        
        return salida;
    }
    
    // --------------------------------------------- //
    //        Código de CloudManager.java            //
    // --------------------------------------------- //
    public static void publishMetric(String meterName, String type, Number volume, String unit) {
        String route = "";
        switch (meterName) {
        case "simulation":
            route = CloudManager.properties.getProperty("ipManagerSimulation").toString() + ":8084/v0.1/cola";
            break;
        case "report":
            route = CloudManager.properties.getProperty("ipManagerReport").toString() + ":8084/v0.1/cola";
            break;
        default:
            break;
        }
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try{
            HttpPut putRequest = new HttpPut(route);
            JSONObject metric = new JSONObject();
            metric.put("timestamp", System.currentTimeMillis());
            metric.put("value", volume);
            StringEntity jsonRequest = new StringEntity(metric.toString(), "UTF-8");
            jsonRequest.setContentType("application/json");
            BufferedHttpEntity requestEntity = new BufferedHttpEntity(jsonRequest);
            putRequest.setEntity(requestEntity);
            
            response = httpClient.execute(putRequest);
            putRequest = null;
            EntityUtils.consume(requestEntity);
            EntityUtils.consume(jsonRequest);
            
            if(response!=null) {
                int codigo = response.getStatusLine().getStatusCode();
                if(codigo != 204)
                    System.err.println("Código respuesta: " + codigo);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(response!=null)
                    response.close();
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String readStackOutput(String stack, String output){ //mejorar para volver a buscar
        if(!stacks.containsKey(stack))
            if(!getStackId(stack)){
                return null;
            }
        
        String route = properties.getProperty("defaultHost")+":8004/v1/"+properties.getProperty("projectId")+"/stacks/"+stack+"/"+stacks.get(stack)+"/outputs/"+output;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String outputValue=null;
        
        if(expirationDate.isBefore(Instant.now())) //If the token has already expired
            requestToken();
        
        try{
            for(int i=0; i<2; i++){
                HttpGet getRequest = new HttpGet(route);
                getRequest.addHeader("X-Auth-Token",properties.getProperty("token"));
                getRequest.addHeader("Accept", "application/json");
                
                response = httpClient.execute(getRequest);
                
                if(response.getStatusLine().getStatusCode()==200){
                    HttpEntity entity=response.getEntity();
                    JSONObject responseObj = new JSONObject(EntityUtils.toString(entity));
                    if(responseObj.has("output")){
                        JSONObject outputObj = responseObj.getJSONObject("output");
                        if(outputObj.has("output_value")){
                            
                            //service.release();
                            outputValue = String.valueOf(outputObj.get("output_value"));
                        }
                        else{
                            System.err.println("Stack has no output value");
                            stacks.remove(stack);
                        }
                    }
                    EntityUtils.consume(response.getEntity());
                    break;
                }
                else if(response.getStatusLine().getStatusCode()==401){
                    System.err.println("Not authorized in reading output");
                    EntityUtils.consume(response.getEntity());
                    requestToken();
                }
                else if(response.getStatusLine().getStatusCode()==404){
                    System.err.println("Stack/Output not found");
                    stacks.remove(stack);
                    EntityUtils.consume(response.getEntity());
                    break;
                }
                else{
                    System.err.println("Error in get output");
                    EntityUtils.consume(response.getEntity());
                }
            }
        } catch (JSONException | ParseException | IOException e) {
            e.printStackTrace();
        }finally{
            try{
                if(response!=null)
                    response.close();
                httpClient.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        return outputValue;
    }
    
    public static void requestToken(){ //Request a valid token to use with the different operations
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try{
            HttpPost tokenPost = new HttpPost(properties.getProperty("defaultHost")+":5000/v3/auth/tokens"); 
            //We need to prepare a JSON document as specified in the KeyStone REST API
            //http://developer.openstack.org/api-ref-identity-v3.html#authenticatePasswordUnscoped
            JSONObject authRequest = new JSONObject();
            JSONObject auth = new JSONObject();
            JSONObject identity = new JSONObject();
            JSONObject scope = new JSONObject();
            JSONObject project = new JSONObject();
            JSONArray methods = new JSONArray();
            JSONObject password = new JSONObject();
            JSONObject user = new JSONObject();
            authRequest.put("auth", auth);
            auth.put("identity", identity);
            identity.put("methods", methods);
            methods.put("password");
            identity.put("password", password);
            password.put("user", user);
            user.put("id", properties.getProperty("userId"));
            user.put("password", properties.getProperty("password"));
            auth.put("scope", scope);
            scope.put("project", project);
            project.put("id", properties.getProperty("projectId"));
            
            StringEntity body = new StringEntity(authRequest.toString(),"UTF-8");
            body.setContentType("application/json");
            tokenPost.setEntity(body);
            
            response = httpClient.execute(tokenPost);
            
            EntityUtils.consume(body);
            
            System.out.println(response.getStatusLine().getStatusCode());
            
            if(response.getStatusLine().getStatusCode()==201){
                HttpEntity entity = response.getEntity();
                JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                Header header = response.getFirstHeader("X-Subject-Token");
                properties.put("token", header.getValue());
                expirationDate=Instant.parse(jsonResponse.getJSONObject("token").getString("expires_at"));
                
                EntityUtils.consume(entity);
            }
            else{
                System.err.println("Error in token request");
                EntityUtils.consume(response.getEntity());
            }
        } catch (JSONException|IOException e){
            e.printStackTrace(); //DeberÃ­a de notificar los errores
        } finally{
            try{
                if(response!=null)
                    response.close();
                httpClient.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public static boolean getStackId(String stack){
        String route=properties.getProperty("defaultHost")+":8004/v1/"+properties.getProperty("projectId")+"/stacks";
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        boolean success = false;
        
        if(expirationDate.isBefore(Instant.now())) //If the token has already expired
            requestToken();
        
        try{
            for(int i=0; i<2; i++){
                HttpGet getRequest = new HttpGet(route);
                getRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
                getRequest.addHeader("Accept", "application/json");
                
                response = httpClient.execute(getRequest);
                
                if(response.getStatusLine().getStatusCode()==200){
                    HttpEntity entity = response.getEntity();
                    JSONObject stacksObj = new JSONObject(EntityUtils.toString(entity));
                    
                    JSONArray stacksArray = stacksObj.getJSONArray("stacks");
                    
                    for(int j=0; j<stacksArray.length(); j++){
                        JSONObject stackObj = stacksArray.getJSONObject(j);
                        if(stackObj.has("stack_name") && stackObj.has("id") && stackObj.getString("stack_name").equals(stack)){
                            stacks.put(stack, stackObj.getString("id"));
                            success=true;
                        }
                    }
                    EntityUtils.consume(entity);
                    break;
                }
                else if(response.getStatusLine().getStatusCode()==401){
                    System.err.println("Not authorized.");
                    EntityUtils.consume(response.getEntity());
                    requestToken();				
                }
                else{
                    System.err.println("Error in request");
                    EntityUtils.consume(response.getEntity());
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(response!=null)
                    response.close();
                httpClient.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        return success;
    }
    
    // --------------------------------------------- //
    //        Fin código de CloudManager.java        //
    // --------------------------------------------- //
    
    /**
     * Método para iniciar la comunicación
     * @param httpClient
     * @param response
     */
    /*private static void inicio(
            CloseableHttpClient a,
            CloseableHttpResponse b) {
        //httpClient = HttpClients.custom().setDefaultCredentialsProvider(cP).build();
        a = HttpClients.createDefault();
        b = null;
    }*/
    
    /**
     * Método para cierre de la conexión.
     * @param closeableHttpResponse CloseableHttpResponse
     * @param closeableHttpClient CloseableHttpClient
     * @throws IOException Excepción que se lanza si no se han creado los objetos.
     */
    /*private static void cierra(
            CloseableHttpResponse a, 
            CloseableHttpClient b
            ) throws IOException {
        if(a!=null)
            a.close();
        b.close();
    }*/
    
    /**
     * Comprueba la existencia de una ruta especificada. Si la ruta no existe crea los directorios que falten.
     * @param path Ruta que se va a comprobar
     */
    public static void compruebaRuta(String path) {
        String[] p = path.split("/");
        String[] q = new String[p.length];
        String nr;
        for(int i = 0; i < p.length; i++) {
            nr = "";
            for(int j = 0; j < p.length - i; j++)
                nr += p[j] + "/";
            
            if(!headFile(nr)) //No existe el directorio
                q[i]=nr;
            else 
                break;
        }

        int bucle = 0;
        for(int i = q.length - 1; i >= 0; i--) {
            if(q[i]!=null){
                if(!newFolder(q[i])){//Nos aseguramos que el directorio se crea
                    if(bucle > 5){ //Se impone un máximo de intentos
                        bucle = 0;
                        continue;
                    }
                    else{
                        ++bucle;
                        i+=2;
                        try{
                            Thread.sleep(150);
                        }catch(InterruptedException e){ }
                    }
                }
            }
        }
    }
    
    /**
     * Método para descomprir un archivo tipo ZIP y posteriormente borrarlo.
     * Los ficheros que contenía el ZIP se extraen en el directorio padre que lo contenía.
     * @param rutaZip Ruta + nombre del ZIP
     */
    public static void descomprime(String rutaZip) {
        try {
            String padre = new File(rutaZip).getParent();
            
            //Descompresión
            ZipFile zipFile = new ZipFile(rutaZip);
            Enumeration<ZipArchiveEntry> lista = zipFile.getEntries();
            ZipArchiveEntry zipArchiveEntry = null;
            String fileName = null;
            File oFile = null;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            while(lista.hasMoreElements()) {
                zipArchiveEntry = lista.nextElement();
                fileName = zipArchiveEntry.getName();
                oFile = new File(padre, fileName);
                
                if(zipArchiveEntry.isDirectory())
                    oFile.mkdir();
                else {
                    inputStream = zipFile.getInputStream(zipArchiveEntry);
                    outputStream = new BufferedOutputStream(
                            new FileOutputStream(oFile));
                    
                    IOUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                }
            }
            zipFile.close();
            zipFile = null;
            lista = null;
            zipArchiveEntry = null;
            fileName = null;
            inputStream = null;
            outputStream = null;
            
            //Borrado del ZIP
            oFile = new File(rutaZip);
            oFile.delete();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    
    /**
     * Método en el cual están todos los códigos supuestos por la API proporcionada.
     * @param caso
     * @param codigo
     */
    private static void casosAPI(String caso, int codigo) {
        String error = caso + " - Código "+codigo+"\n"+
                    "Error no registrado en la API https://doc.owncloud.org/server/10.0/admin_manual/configuration/user/user_provisioning_api.html";
        String previo = caso + " - ";
        switch(caso) {
        case "addUser":
            switch (codigo) {
            case 100:
                System.out.println(previo + "Usuario creado");
                break;
            case 101:
                System.err.println(previo + "Los datos introducidos son incorrectos");
                break;
            case 102:
                System.err.println(previo + "El nombre de usuario ya está en uso");
                break;
            case 103:
                System.err.println(previo + "Se ha producido un error desconocido mientras se agregaba el usuario");
                break;
            case 104:
                System.err.println(previo + "El grupo no existe");
                break;
            default:
                System.err.println(error);
            }
            break;
        case "listUsers":
            if(codigo == 100)
                System.out.println(previo + "Lista recibida");
            else
                System.err.println(error);
            break;
        case "deleteUser":
            switch (codigo){
            case 100:
                System.out.println(previo + "Usuario eliminado del sistema");
                break;
            case 101:
                System.err.println(previo + "Fallo al borrar el usuario del sistema");
                break;
            default:
                System.err.println(error);
            }
            break;
        case "addGroup":
            switch(codigo) {
            case 100:
                System.out.println(previo + "Grupo creado correctamente");
                break;
            case 101:
                System.err.println(previo + "Los datos introducidos no son correctos");
                break;
            case 102:
                System.err.println(previo + "El grupo no se ha podido agregar ya que existe otro con mismo identificador en el sistema");
                break;
            case 103:
                System.err.println(previo + "Fallo al agregar el grupo");
                break;
            default:
                System.err.println(error);
            }
            break;
        case "deleteGroup":
            switch(codigo) {
            case 100:
                System.out.println(previo + "Grupo borrado correctamente");
                break;
            case 101:
                System.err.println(previo + "El grupo a eliminar no existe");
                break;
            case 102:
                System.err.println(previo + "Fallo al intentar eliminar el grupo");
                break;
            default:
                System.err.println(error);
            }
            break;
        case "editUser":
            switch(codigo) {
            case 100:
                System.out.println(previo + "Cambio realizado correctamente");
                break;
            case 101:
                System.err.println(previo + "Usuario no encontrado");
                break;
            case 102:
                System.err.println(previo + "Datos no válidos");
                break;
            default:
                System.err.println(error);
            }
            break;
        default:
            System.err.println(previo + "Caso no registrado");
        }
    }
}

/* Antigua clase CloudManager que se utilizaba con el almacenamiento Swift */
//package dnse3.common;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
////import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.sql.Timestamp;
////import java.nio.channels.FileChannel;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Properties;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.NameValuePair;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpHead;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.entity.BufferedHttpEntity;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.FileEntity;
//import org.apache.http.entity.StringEntity;
////import org.apache.http.entity.mime.MultipartEntityBuilder;
////import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
////import org.restlet.Client;
////import org.restlet.data.MediaType;
////import org.restlet.data.Status;
////import org.restlet.ext.json.JsonRepresentation;
////import org.restlet.representation.FileRepresentation;
//import org.restlet.representation.Representation;
////import org.restlet.representation.StringRepresentation;
////import org.restlet.resource.ClientResource;
////import org.restlet.resource.ResourceException;
////import org.restlet.util.Series;
//
//
//public class CloudManager {
//	
//	//Class designed for the different communications with the cloud services
//	//It allows the different services to access the cloud transparently
//	
//	//These atributes defines the cloud user asign to the service
//	private static Instant expirationDate;
//	private static HashMap<String,String> stacks;
//	
//	private static Properties properties;
//
//	public static void initialize(String username, String password){
//		try {
//			CloudManager.properties = new Properties();
//			File propFile = new File("cloud.properties");
//			File jarPath = new File(CloudManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//			File homePath = jarPath.getParentFile();
//			if(!propFile.exists())
//				propFile=new File(homePath,"cloud.properties");
//			
//			System.out.println(propFile.getAbsolutePath().toString());
//			InputStream input = new FileInputStream(propFile);
//			CloudManager.properties.load(input);
//			System.out.println(CloudManager.properties.getProperty("userId"));
//			CloudManager.expirationDate=Instant.now();
//			CloudManager.stacks=new HashMap<>();
//			requestToken();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public static void requestToken(){ //Request a valid token to use with the different operations
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		try{
//			HttpPost tokenPost = new HttpPost(properties.getProperty("defaultHost")+":5000/v3/auth/tokens"); 
//			//We need to prepare a JSON document as specified in the KeyStone REST API
//			//http://developer.openstack.org/api-ref-identity-v3.html#authenticatePasswordUnscoped
//			JSONObject authRequest = new JSONObject();
//			JSONObject auth = new JSONObject();
//			JSONObject identity = new JSONObject();
//			JSONObject scope = new JSONObject();
//			JSONObject project = new JSONObject();
//			JSONArray methods = new JSONArray();
//			JSONObject password = new JSONObject();
//			JSONObject user = new JSONObject();
//			authRequest.put("auth", auth);
//			auth.put("identity", identity);
//			identity.put("methods", methods);
//			methods.put("password");
//			identity.put("password", password);
//			password.put("user", user);
//			user.put("id", properties.getProperty("userId"));
//			user.put("password", properties.getProperty("password"));
//			auth.put("scope", scope);
//			scope.put("project", project);
//			project.put("id", properties.getProperty("projectId"));
//			
//			StringEntity body = new StringEntity(authRequest.toString(),"UTF-8");
//			body.setContentType("application/json");
//			tokenPost.setEntity(body);
//			
//			response = httpClient.execute(tokenPost);
//			
//			EntityUtils.consume(body);
//			
//			System.out.println(response.getStatusLine().getStatusCode());
//			
//			if(response.getStatusLine().getStatusCode()==201){
//				HttpEntity entity = response.getEntity();
//				JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
//				Header header = response.getFirstHeader("X-Subject-Token");
//				properties.put("token", header.getValue());
//				expirationDate=Instant.parse(jsonResponse.getJSONObject("token").getString("expires_at"));
//				
//				EntityUtils.consume(entity);
//			}
//			else{
//				System.err.println("Error in token request");
//				EntityUtils.consume(response.getEntity());
//			}
//		} catch (JSONException|IOException e){
//			e.printStackTrace(); //DeberÃ­a de notificar los errores
//		} finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}
//			catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public static boolean getStackId(String stack){
//		//Se pueden usar queries de búsqueda de nombres
//		String route=properties.getProperty("defaultHost")+":8004/v1/"+properties.getProperty("projectId")+"/stacks";
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		boolean success = false;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has already expired
//			requestToken();
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpGet getRequest = new HttpGet(route);
//				getRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
//				getRequest.addHeader("Accept", "application/json");
//				
//				response = httpClient.execute(getRequest);
//				
//				if(response.getStatusLine().getStatusCode()==200){
//					HttpEntity entity = response.getEntity();
//					JSONObject stacksObj = new JSONObject(EntityUtils.toString(entity));
//					
//					JSONArray stacksArray = stacksObj.getJSONArray("stacks");
//					
//					for(int j=0; j<stacksArray.length(); j++){
//						JSONObject stackObj = stacksArray.getJSONObject(j);
//						if(stackObj.has("stack_name") && stackObj.has("id") && stackObj.getString("stack_name").equals(stack)){
//							stacks.put(stack, stackObj.getString("id"));
//							success=true;
//						}
//					}
//					EntityUtils.consume(entity);
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){
//					System.err.println("Not authorized.");
//					EntityUtils.consume(response.getEntity());
//					requestToken();				
//				}
//				else{
//					System.err.println("Error in request");
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		}catch(IOException e){
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return success;
//	}
//	
//	public static String readStackOutput(String stack, String output){ //mejorar para volver a buscar
//		if(!stacks.containsKey(stack))
//			if(!getStackId(stack)){
//				return null;
//			}
//		
//		String route = properties.getProperty("defaultHost")+":8004/v1/"+properties.getProperty("projectId")+"/stacks/"+stack+"/"+stacks.get(stack)+"/outputs/"+output;
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		String outputValue=null;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has already expired
//			requestToken();
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpGet getRequest = new HttpGet(route);
//				getRequest.addHeader("X-Auth-Token",properties.getProperty("token"));
//				getRequest.addHeader("Accept", "application/json");
//				
//				response = httpClient.execute(getRequest);
//				
//				if(response.getStatusLine().getStatusCode()==200){
//					HttpEntity entity=response.getEntity();
//					JSONObject responseObj = new JSONObject(EntityUtils.toString(entity));
//					if(responseObj.has("output")){
//						JSONObject outputObj = responseObj.getJSONObject("output");
//						if(outputObj.has("output_value")){
//							
//							//service.release();
//							outputValue = String.valueOf(outputObj.get("output_value"));
//						}
//						else{
//							System.err.println("Stack has no output value");
//							stacks.remove(stack);
//						}
//					}
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){
//					System.err.println("Not authorized in reading output");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==404){
//					System.err.println("Stack/Output not found");
//					stacks.remove(stack);
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else{
//					System.err.println("Error in get output");
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		} catch (JSONException | ParseException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return outputValue;
//	}
//	
//	public static String uploadFile(String path, File file, ContentType contentType){ //Send a file to the Swift service, in this case is prepared to use the DNSE3 conatiner
//		String route=properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"/"+path;
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		if(expirationDate.isBefore(Instant.now())) //If the token has already expired
//			requestToken();
//		boolean success=false;
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpPut putRequest = new HttpPut(route);
//				putRequest.addHeader("X-Auth-Token",properties.getProperty("token"));
////				putRequest.addHeader("Accept", "application/json");
//				
//				HttpEntity entity = new FileEntity(file, contentType);
//				putRequest.setEntity(entity);
//				
//				response = httpClient.execute(putRequest);
//				
//				EntityUtils.consume(entity);
//				
//				if(response.getStatusLine().getStatusCode()==201){
//					success=true;
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){
//					System.err.println("Not authorized in uploading file");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else{
//					System.err.println("Failed file upload: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return success?route:null;
//		
//		// int i=0;
//		// while(true){ 
//		// 	try{
//		// 		FileRepresentation filerep = new FileRepresentation(file, mediatype); //Prepare the representation of the File we are going to send
//		// 		if(!token.isEmpty()){ //We set the header with ur token in case we have it
//		// 			Series<Header> headers = service.getRequest().getHeaders();
//		// 			headers.removeFirst("X-Auth-Token");
//		// 			headers.add("X-Auth-Token", token);
//		// 		}
//				
//		// 		service.put(filerep); //We send the file. If we succeded, the break sentece will be called so we exit the while loop
//		// 		break;
//		// 	} catch(ResourceException e){ //A problem was found in the upload of the file, so we try to renew the token used
//		// 		e.getStatus();
//		// 		i++;
//		// 		if(i>1) //If there was a problem in the request, we'll ask only once for a new token
//		// 			break;
//		// 		if(e.getStatus().equals(Status.CLIENT_ERROR_UNAUTHORIZED))
//		// 			requestToken();
//		// 	}
//		// }
//		
//		// discardRepresentation(service.getResponseEntity());
//		// service.release();
//		// Client c = (Client) service.getNext();
//		// try{
//		// 	c.stop();
//		// }catch(Exception e){
//			
//		// }
//		// return route; //We return the URI were the file is hosted
//	}
//	
//	public static void main(String[] args){ //Testing method
//		/*if(args.length!=2){
//			System.out.println("Introduzca usuario y contraseña correctamente");
//			System.exit(1);
//		}*/
//		CloudManager.initialize("","");
//		// for(String s:CloudManager.listObjects(""))
//		// 	System.out.println(s);
//		// while(CloudManager.deletePath("")!=0);
//		// for(int i=1; i<11; i++)
//		// System.out.println(readStackOutput("dnse3", "current_size"));
//		// System.out.println(CloudManager.readStackOutput("dnse3", "current_size"));
//		publishMetric("dnse3.queue.size", MetricType.GAUGE.toString(), (double) 20, "tasks");
//		//publishMetric("dnse3.queue.size", MetricType.GAUGE.toString(), 25, "tasks");
//		//publishMetric("dnse3.queue.size", MetricType.GAUGE.toString(), 10, "tasks");
//	}
//	
//	public static void publishMetric(String meterName, String type, Number volume, String unit){		
//		String route = CloudManager.properties.getProperty("ipManager").toString() + ":8084/v0.1/colaSimulaciones";
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		try{
//			HttpPut putRequest = new HttpPut(route);
//			JSONObject metric = new JSONObject();
//			metric.put("timestamp", System.currentTimeMillis());
//			metric.put("value", volume);
//			StringEntity jsonRequest = new StringEntity(metric.toString(), "UTF-8");
//			jsonRequest.setContentType("application/json");
//			BufferedHttpEntity requestEntity = new BufferedHttpEntity(jsonRequest);
//			putRequest.setEntity(requestEntity);
//			
//			response = httpClient.execute(putRequest);
//			putRequest = null;
//			EntityUtils.consume(requestEntity);
//			EntityUtils.consume(jsonRequest);
//			
//			if(response!=null) {
//				int codigo = response.getStatusLine().getStatusCode();
//				if(codigo != 204)
//					System.err.println("Código respuesta: " + codigo);
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		finally {
//			try {
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	/*
//	//Ajuste temporal
//	public static void publishMetric(String meterName, String type, Number volume, String unit){ //Publish a new metric in the Ceilometer Service, it was designed to use the MetrucType enumeration, but for debug purpose it was changed to String
//
//		//De momento, pongo la uri directamente del recurso
//		String route = properties.getProperty("defaultHost")+":8041/v1/metric/184c106a-5357-4bee-81f9-23ab6e70174d/measures";
//		//String route = properties.getProperty("defaultHost")+":8777/v2/meters/"+meterName;
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has already expired
//			requestToken();
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpPost postRequest = new HttpPost(route);
//				//We need to prepare a JSON document similar to the one sent with the ceilometer sample-create command
//				postRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
//				JSONArray array = new JSONArray();
//				JSONObject metric = new JSONObject();
//				// metric.put("counter_type", type.toLowerCase());
//				// metric.put("counter_name", meterName);
//				// metric.put("resource_id", properties.getProperty("resource_id"));
//				// metric.put("counter_volume", String.valueOf(volume));
//				// metric.put("counter_unit", unit);
//				// metric.put("project_id", properties.getProperty("projectId"));
//				metric.put("timestamp", (new Timestamp(System.currentTimeMillis()).toString()));
//				metric.put("value", volume);
//				array.put(metric);
//				
//				StringEntity jsonRequest = new StringEntity(array.toString(), "UTF-8");
//				jsonRequest.setContentType("application/json");
//				BufferedHttpEntity requestEntity = new BufferedHttpEntity(jsonRequest);
//				postRequest.setEntity(requestEntity);
//				
//				response = httpClient.execute(postRequest);
//				
//				EntityUtils.consume(requestEntity);
//				EntityUtils.consume(jsonRequest);
//				
//				System.out.println(response.getStatusLine().getStatusCode());
//				
//				if(response.getStatusLine().getStatusCode()==202){
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){
//					System.err.println("Not authorized in post request");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==400){
//					System.err.println("Bad request publishing metric");
//					EntityUtils.consume(response.getEntity());
//				}
//				else{
//					System.err.println("Error in post metric request");
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//			
//			
//		}catch(JSONException|IOException e){ //Future deployment
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//	}*/
//	
//	public static String downloadFile(URI downloadURI){ //Donwload a file hosted in Swift
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//			requestToken();
//		
//		String path=null;
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpGet getRequest = new HttpGet(downloadURI);
//				getRequest.addHeader("X-Auth-Token",properties.getProperty("token"));
//				
//				response = httpClient.execute(getRequest);
//				
//				if(response.getStatusLine().getStatusCode()==200){//Fichero descargado
//					HttpEntity entity = response.getEntity();
//					String[] name= downloadURI.getPath().split("/");
//					String filename=URLDecoder.decode(name[name.length-1],"UTF-8"); //Get the name of the file downloaded
//					File dir = new File("tmp");
//					if(!dir.exists()){
//						dir.mkdir();
//					}
//					
//					//Save the file recovered
//					File outFile = new File(dir,filename);
//					BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outFile));
//					
//					int inByte;
//					while((inByte=entity.getContent().read())!=-1)
//						outStream.write(inByte);
//					
//					outStream.close();
//					path=outFile.getPath();
//					EntityUtils.consume(entity);
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){//No autorizado
//					System.err.println("Not authorized in get file request");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==404){//Ruta no encontrada
//					System.err.println("File not found: "+downloadURI.getPath());
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return path;
//		
//		// ClientResource service=new ClientResource(downloadURI); //Download the source file
//		// try{
//		// 	if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//		// 		requestToken();
//			
//		// 	if(!token.isEmpty()){ //Redundante
//		// 		Series<Header> headers = service.getRequest().getHeaders();
//		// 		headers.removeFirst("X-Auth-Token");
//		// 		headers.add("X-Auth-Token", token);
//		// 	}
//			
//		// 	Representation file = service.get(); //We download the file
//			
//		// 	String[] name= downloadURI.getPath().split("/");
//			
//		// 	String filename=URLDecoder.decode(name[name.length-1],"UTF-8"); //Get the name of the file downloaded
//		// 	File dir = new File("tmp");
//		// 	if(!dir.exists()){
//		// 		dir.mkdir();
//		// 	}
//			
//		// 	//Save the file recovered
//		// 	File outFile = new File(dir,filename);
//		// 	FileOutputStream outStream = new FileOutputStream(outFile);
//		// 	FileChannel out = outStream.getChannel();
//		// 	out.transferFrom(file.getChannel(), 0, Integer.MAX_VALUE);
//		// 	out.close();
//		// 	outStream.close(); //Close the file
//			
//		// 	discardRepresentation(service.getResponseEntity());
//		// 	service.release();
//		// 	Client c = (Client) service.getNext();
//		// 	try{
//		// 		c.stop();
//		// 	}catch(Exception e){
//				
//		// 	}
//		// 	return filename;//Return the filename instead of the File object, so it can be used as desired
//		// } catch(ResourceException e){
//			
//		// } catch (FileNotFoundException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// } catch (IOException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// }
//		
//		// discardRepresentation(service.getResponseEntity());
//		// service.release();
//		// Client c = (Client) service.getNext();
//		// try{
//		// 	c.stop();
//		// }catch(Exception e){
//			
//		// }
//		// return null; //If there was a problem, no name is returned
//	}
//	
//	public static String downloadFile(URI downloadURI, File path){ //Donwload a file hosted in Swift
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//			requestToken();
//		
//		String outputPath=null;
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpGet getRequest = new HttpGet(downloadURI);
//				getRequest.addHeader("X-Auth-Token",properties.getProperty("token"));
//				
//				response = httpClient.execute(getRequest);
//				
//				if(response.getStatusLine().getStatusCode()==200){//Fichero descargado	
//					HttpEntity entity = response.getEntity();
//					String[] name= downloadURI.getPath().split("/");
//					String filename=URLDecoder.decode(name[name.length-1],"UTF-8"); //Get the name of the file downloaded
//					//Save the file recovered
//					File outFile = new File(path,filename);
//					BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outFile));
//					
//					int inByte;
//					while((inByte=entity.getContent().read())!=-1)
//						outStream.write(inByte);
//					
//					outStream.close();
//					outputPath=outFile.getPath();
//					EntityUtils.consume(entity);
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){//No autorizado
//					System.err.println("Not authorized in get file request");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==404){//Ruta no encontrada
//					System.err.println("File not found: "+downloadURI.getPath());
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else{
//					System.err.println("Error in get file: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return outputPath;
//		
//		// ClientResource service=new ClientResource(downloadURI); //Download the source file
//		// try{
//		// 	if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//		// 		requestToken();
//			
//		// 	if(!token.isEmpty()){ //Redundante
//		// 		Series<Header> headers = service.getRequest().getHeaders();
//		// 		headers.removeFirst("X-Auth-Token");
//		// 		headers.add("X-Auth-Token", token);
//		// 	}
//			
//		// 	Representation file = service.get(); //We download the file
//			
//		// 	String[] name= downloadURI.getPath().split("/");
//			
//		// 	String filename=URLDecoder.decode(name[name.length-1],"UTF-8"); //Get the name of the file downloaded
//			
//		// 	//Save the file recovered
//		// 	File outFile = new File(path,filename);
//		// 	FileOutputStream outStream = new FileOutputStream(outFile);
//		// 	FileChannel out = outStream.getChannel();
//		// 	out.transferFrom(file.getChannel(), 0, Integer.MAX_VALUE);
//		// 	out.close();
//		// 	outStream.close(); //Close the file
//			
//		// 	discardRepresentation(service.getResponseEntity());
//		// 	service.release();
//		// 	Client c = (Client) service.getNext();
//		// 	try{
//		// 		c.stop();
//		// 	}catch(Exception e){
//				
//		// 	}
//		// 	return outFile.getPath(); //Return the filename instead of the File object, so it can be used as desired
//		// } catch(ResourceException e){
//			
//		// } catch (FileNotFoundException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// } catch (IOException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// }
//		
//		// discardRepresentation(service.getResponseEntity());
//		// service.release();
//		// Client c = (Client) service.getNext();
//		// try{
//		// 	c.stop();
//		// }catch(Exception e){
//			
//		// }
//		// return null; //If there was a problem, no name is returned
//	}
//	
//	public static String downloadFile(String serverPath, File path) throws URISyntaxException{ //Donwload a file hosted in Swift
//		String[] parts = serverPath.split("/");
//		List<String> finalParts = new ArrayList<>();
//		for(String p: parts){
//			try {
//				finalParts.add(URLEncoder.encode(p, "UTF-8").replaceAll("\\+", "%20"));
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}//Mirar alternativas
//		}
//		URI uri = new URI(properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"/"+String.join("/", finalParts));
//		return downloadFile(uri,path);
//	}
//	
//	public static String downloadFile(String path) throws URISyntaxException{
//		String[] parts = path.split("/");
//		List<String> finalParts = new ArrayList<>();
//		for(String p: parts){
//			try {
//				finalParts.add(URLEncoder.encode(p, "UTF-8").replaceAll("\\+", "%20"));
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}//Mirar alternativas
//		}
//		URI uri = new URI(properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"/"+String.join("/", finalParts));
//		return downloadFile(uri);
//	}
//
//	public static List<String> listObjects(String path) { //format=json, recoger name de cada object
//		
//		String route = properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"?prefix="+path;
//		
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//			requestToken();
//		
//		List<String> objects = new ArrayList<>();
//		boolean success = false;
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpGet getRequest = new HttpGet(route);
//				getRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
//				
//				response = httpClient.execute(getRequest);
//				
//				System.out.println("List: "+response.getStatusLine().getStatusCode());
//				
//				if(response.getStatusLine().getStatusCode()==200){//Objects found
//					HttpEntity entity = response.getEntity();
//					String[] list = EntityUtils.toString(entity).split("\\r?\\n");
//					objects.addAll(Arrays.asList(list));
//					EntityUtils.consume(entity);
//					success=true;
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==204){
//					EntityUtils.consume(response.getEntity());
//					success=true;
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){//No autorizado
//					System.err.println("Not authorized in get path request");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==404){//Ruta no encontrada
//					System.err.println("Path not found: "+route);
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else{
//					System.err.println("Error in get path: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return success?objects:null;
//		
//		// //cambiar path a form query
//		// ClientResource service=new ClientResource(properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"?prefix="+path); //Download the source file
//		// try{
//		// 	if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//		// 		requestToken();
//			
//		// 	if(!token.isEmpty()){ //Not used
//		// 		Series<Header> headers = service.getRequest().getHeaders();
//		// 		headers.removeFirst("X-Auth-Token");
//		// 		headers.add("X-Auth-Token", token);
//		// 	}
//		// 	Representation response = service.get(); //We download the file
//		// 	StringRepresentation rep = new StringRepresentation(response.getText());
//		// 	System.out.println(rep.toString());
//			
//		// 	if(rep!= null && rep.getText()!= null && !rep.getText().isEmpty()){
//		// 		String[] list = rep.getText().split("\\r?\\n"); //se podrï¿½a cambiar a JSON
//		// 		discardRepresentation(service.getResponseEntity());		
//		// 		service.release();
//		// 		Client c = (Client) service.getNext();
//		// 		try{
//		// 			c.stop();
//		// 		}catch(Exception e){
//					
//		// 		}
//		// 		for(String s: list)
//		// 			System.out.println(s);
//		// 		return (List<String>) Arrays.asList(list); //Return the filename instead of the File object, so it can be used as desired
//		// 	}
//		// 	else
//		// 		System.out.println("error aquï¿½");
//		// } catch(ResourceException e){
//		// 	e.printStackTrace();
//			
//		// } catch (FileNotFoundException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// } catch (IOException e) {
//		// 	// TODO Auto-generated catch block
//		// 	e.printStackTrace();
//		// }
//		
//		// discardRepresentation(service.getResponseEntity());
//		// service.release();
//		// Client c = (Client) service.getNext();
//		// try{
//		// 	c.stop();
//		// }catch(Exception e){
//			
//		// }
//		
//		// return new ArrayList<>(); //If there was a problem, no name is returned
//	}
//
//	public static boolean headFile(String downloadURI){ //Revisar caso no exitoso
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpResponse response = null;
//		
//		boolean success = false;
//		
//		try{
//			for(int i=0; i<2; i++){
//				HttpHead headRequest = new HttpHead(downloadURI);
//				headRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
//				
//				response = httpClient.execute(headRequest);
//				
//				if(response.getStatusLine().getStatusCode()==200){
//					success=true;
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==204){
//					System.err.println("Error in HEAD: file has no content");
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else if(response.getStatusLine().getStatusCode()==401){//No autorizado
//					System.err.println("Not authorized in headrequest");
//					EntityUtils.consume(response.getEntity());
//					requestToken();
//				}
//				else if(response.getStatusLine().getStatusCode()==404){//Ruta no encontrada
//					System.err.println("Path not found: "+downloadURI);
//					EntityUtils.consume(response.getEntity());
//					break;
//				}
//				else{
//					System.err.println("Error in head file: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//					EntityUtils.consume(response.getEntity());
//				}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return success;
//		
//		// ClientResource service=new ClientResource(downloadURI); //Download the source file
//		// if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//		// 	requestToken();
//		
//		// Series<Header> headers = service.getRequest().getHeaders();
//		// headers.removeFirst("X-Auth-Token");
//		// headers.add("X-Auth-Token", token);
//		
//		// service.head(); //If it goes well, it just exits
//		// //If it's not found, an exception will raise and be thrown to the calling method
//	
//		// discardRepresentation(service.getResponseEntity());
//		// service.release();
//		// Client c = (Client) service.getNext();
//		// try{
//		// 	c.stop();
//		// }catch(Exception e){
//			
//		// }
//	}
//	
//	public static void discardRepresentation(Representation rep){
//		if(rep!=null){
//			try{
//				rep.exhaust();
//			}
//			catch (IOException e){
//				//Notificaciï¿½n 2ï¿½ error producido de forma conjunta
//			}
//			rep.release();
//		}
//	}
//	
//	public static void deletePath(String path){
//		while(true){
//			List<String> list = listObjects(path);
//			
//			if(list==null||list.isEmpty())
//				break;
//			CloseableHttpClient httpClient = HttpClients.createDefault();
//			CloseableHttpResponse response = null;
//			
//			for(String s: list){
//				if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//					requestToken();
//				
//				try{
//					String[] parts = s.split("/");
//					List<String> finalParts = new ArrayList<>();
//					for(String p: parts){
//						finalParts.add(URLEncoder.encode(p, "UTF-8").replaceAll("\\+", "%20"));//Mirar alternativas
//					}
//					String finalPath = String.join("/", finalParts);
//					
//					System.out.println(finalPath);
//					
//					for(int i=0; i<2; i++){
//						HttpDelete deleteRequest = new HttpDelete(properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"/"+finalPath);
//						deleteRequest.addHeader("X-Auth-Token", properties.getProperty("token"));
//						
//						response = httpClient.execute(deleteRequest);
//						
//						if(response.getStatusLine().getStatusCode()==204){
//							EntityUtils.consume(response.getEntity());
//							break;
//						}
//						else if(response.getStatusLine().getStatusCode()==200){
//							System.err.println("DELETE returned 200");
//							EntityUtils.consume(response.getEntity());
//							break;
//						}
//						else if(response.getStatusLine().getStatusCode()==401){//No autorizado
//							System.err.println("Not authorized in headrequest");
//							EntityUtils.consume(response.getEntity());
//							requestToken();
//						}
//						else if(response.getStatusLine().getStatusCode()==404){//Ruta no encontrada
//							System.err.println("Path not found: "+s);
//							EntityUtils.consume(response.getEntity());
//							break;
//						}
//						else{
//							System.err.println("Error in delete file: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//							EntityUtils.consume(response.getEntity());
//						}
//					}
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			try{
//				if(response!=null)
//					response.close();
//				httpClient.close();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		// while(true){
//		// 	List<String> list = listObjects(path);
//		// 	if(list==null||list.isEmpty())
//		// 		break;
//			
//		// 	for(String s: list){
//		// 		ClientResource server = new ClientResource(properties.getProperty("defaultHost")+":8080/v1/AUTH_"+properties.getProperty("projectId")+"/"+properties.getProperty("bucketName")+"/"+s);
//		// 		for(int i=0; i<2; i++){
//		// 			try{
//		// 				if(expirationDate.isBefore(Instant.now())) //If the token has expired, we ask for a new one
//		// 					requestToken();
//						
//		// 				Series<Header> headers = server.getRequest().getHeaders();
//		// 				headers.removeFirst("X-Auth-Token");
//		// 				headers.add("X-Auth-Token", token);
//		// 				//System.out.println(queueService.getHostRef().toString());
//		// 				server.delete();
//		// 				break;
//		// 			}catch(ResourceException e){
//		// 				if(e.getStatus().equals(Status.CLIENT_ERROR_UNAUTHORIZED))
//		// 					requestToken();
//		// 				else if(e.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)){
//		// 					System.err.println("Path not found");
//		// 					break;
//		// 				}
//		// 				else{
//		// 					System.err.println("Error in DELETE request:"+e.getStatus());
//		// 				}
//		// 			}
//		// 		}
//		// 		discardRepresentation(server.getResponseEntity());
//		// 		server.release();
//		// 		Client c = (Client) server.getNext();
//		// 		try{
//		// 			c.stop();
//		// 		}catch(Exception e){
//		// 			e.printStackTrace();
//		// 		}
//		// 	}
//		// }
//	}
//}

