package dnse3.orchestration.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.auxiliar.project.Simulationpackage;
import dnse3.orchestration.auxiliar.project.Stringtype;
import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.project.ProjectSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ProjectsResource extends ServerResource {
    
    private String username;
    
    /**
     * Método que se lanza al inicio. Comprueba de la existencia del usuario sobre el que se 
     * van a generar las operaciones. Si existe el usuario establece el valor de la variable 
     * username
     */
    @Override
    public void doInit(){
        String username = getAttribute("username");
        if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasUser(username))
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "The server could not find the requested resource");
        this.username = username;
    }
    
    /**
     * Método para obtener la información del proyecto sobre el que se está realizando la consulta.
     * @return Devuelve un JSON con la información del proyecto que se ha consultado
     * @throws ResourceException Excepción cuando el proyecto no existe en el sistema
     */
    @Get("json")
    public JsonRepresentation getJson() throws ResourceException {
        try{
            List<Project> projects = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().getProjects(username);
            if (projects.isEmpty())
                return null;
            
            Gson gson = new GsonBuilder().registerTypeAdapter(Project.class, new ProjectSerializer(new Reference(getOriginalRef()),((DNSE3OrchestrationApplication) getApplication()).getDataController(),true)).setPrettyPrinting().create();
            JsonRepresentation response = new JsonRepresentation(gson.toJson(projects));
            response.setIndenting(true);
            return response;
        }
        catch (Exception e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "The server could not find the requested resource"); //Repetido del doInit 
        }
    }
    
    /**
     * Método para crear un proyecto en el sistema asociado a un usuario
     * @param request Modelo a simular
     */
    @Post
    public void postFile(Representation request) throws ResourceException{
        
        File tempPath = null;
        
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1000240);
            RestletFileUpload upload = new RestletFileUpload(factory);
            
            FileItemIterator fileIterator;
            fileIterator = upload.getItemIterator(request);
            
            //Revisión de la carga
            if(!fileIterator.hasNext()){ //No hay fichero subido
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
            
            FileItemStream fileStream = fileIterator.next();
            if(!fileStream.getFieldName().equals("file") || fileStream.getName()==null || !fileStream.getName().endsWith(".zip")){ //Mal formato
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
            
            //Volcado del .zip
            tempPath = new File("tmp", Long.toString(System.nanoTime()));
            tempPath.mkdirs();
            
            File file = new File(tempPath, fileStream.getName());
            InputStream inputStream = fileStream.openStream();
            OutputStream outputStream = new FileOutputStream(file);
            
            int read = 0;
            byte[] bytes = new byte[1024];
            while((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            
            inputStream.close();
            outputStream.close();
            //Extracción del .zip
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(tempPath, entry.getName());
                if(entry.isDirectory())
                    entryDestination.mkdirs();
                else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
            zipFile.close();
            
            //Búsqueda del directorio raíz
            File[] directories = tempPath.listFiles(new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            
            File[] files = tempPath.listFiles(new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory() && !pathname.getName().equals(file.getName()); //Ficheros distintos al .zip
                }
            });
            File tempDirectory = null;
            if(files.length!=0) //Si hay ficheros, se entiende que es este nivel
                tempDirectory=tempPath;
            else if (directories.length==1) //Si no hya ficheros en el primer nivel y solo hay un directorio
                tempDirectory=directories[0]; //Entro en la carpeta
            else{ //No hay ficheros y hay varias carpetas. Error por no saber distinguir
                cleanTempFiles(tempPath);
                System.err.println("No hay ficheros y hay varias carpetas");
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
            
            //PLACEHOLDER
            System.out.println("Comienza la búsqueda del XML");
            
            File xmlFile = new File(tempDirectory, "project.xml");
            if(!xmlFile.exists()){
                cleanTempFiles(tempPath);
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
            //Revisión del paquete subido
            
            //PLACEHOLDER
            System.out.println("Revisión del XML");
            
            JAXBContext jaxbContext = JAXBContext.newInstance(Simulationpackage.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Simulationpackage simulationpackage = (Simulationpackage) jaxbUnmarshaller.unmarshal(xmlFile);
            
            System.out.println("Fin de la revisión del XML");
            //Tengo que buscar el motivo de esto
            List<Stringtype> a = simulationpackage.getParametertypes().getStringtype();
            for(Stringtype b : a) {
            	b.getPossiblevalue();
            }
            a=null;
            //Si lo quito no compila los proyectos que tengan un parámetro de tipo stringtype
            
            int projectId = ((DNSE3OrchestrationApplication) getApplication()).getDataController().createProject(simulationpackage, tempDirectory, file, username);
            cleanTempFiles(tempPath);
            setLocationRef(getReference().getTargetRef().toString() + projectId + "/");
            setStatus(Status.SUCCESS_CREATED);
        }
        catch(JAXBException e){
            cleanTempFiles(tempPath);
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getLocalizedMessage());
        }
        catch(IOException e){
            cleanTempFiles(tempPath);
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
        catch(Exception e){
            cleanTempFiles(tempPath);
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
    
    /**
     * Método para eliminar ficheros y directorios
     * @param directory Ruta del directorio a eliminar
     */
    public void cleanTempFiles(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (File f : files) {
                if (f.isDirectory())
                    cleanTempFiles(f);
                else
                    f.delete();
            }
            
            directory.delete();
        }
    }

}
