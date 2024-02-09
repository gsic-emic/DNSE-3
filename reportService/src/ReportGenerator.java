import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

import org.restlet.data.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import dnse3.common.CloudManager;
import dnse3.common.TaskStatusEnum;

public class ReportGenerator extends Thread {
	
	private ReportQueue queue;
	private CloudManager cm;
	private Report report;
	
	public ReportGenerator(ReportQueue queue, CloudManager cm){
		this.queue=queue;
		this.cm=cm;
	}
	
	@Override
	public void run(){
		report=queue.getNextReport();
		File outputTrace=null;
		
		try{
			if(report!=null){
				HashMap<String,String> parameters = report.getParameters();
				switch(report.getInputFile()){
				case "output":
					if(!parameters.containsKey("traceSet")||!parameters.containsKey("paramOrder")){ //No puedo trabajar con la salida si no me dicen cómo tengo que utilizar los datos
						report.setStatus(TaskStatusEnum.MALFORMED);
					}
					else{
						String traceSet = parameters.get("traceSet");
						outputTrace = new File("output.trace");
						outputTrace.createNewFile();
						
						BufferedWriter outWriter = new BufferedWriter(new FileWriter(outputTrace));
						
						String[] paramOrder = parameters.get("paramOrder").split(" "); //Tengo el órden de los parámetros de entrada, solo para output
						List<String> inputFiles = cm.listObjects("packages/"+report.getPackageID()+"/"+report.getSimDescID()+"/");
						
						boolean multipleSims=false;
						int numSimulations=1;
						
						if(parameters.containsKey("numSimulations")){
							numSimulations=Integer.parseInt(parameters.get("numSimulations"));
							multipleSims=(numSimulations>1);
						}
						
						HashMap<String,String> paramValues = new HashMap<String,String>();
						ArrayList<String> outputValues = new ArrayList<String>();
						int num=0;
						for(String i:inputFiles){
							String[] pathFile=i.split("/");
							String[] paramName=pathFile[pathFile.length-1].split("output");
							String[] paramNameValues=paramName[0].split("-");
							
							if(!multipleSims||(multipleSims&&num==0)){
								paramValues.clear();
								for(int j=0; j< paramOrder.length;j++){
									paramValues.put(paramOrder[j], paramNameValues[j]);
								}
							}
							
							String filename = cm.downloadFile(i);
							File input = new File(filename); //Abro el fichero
							
							String line;
							
							BufferedReader inputReader = new BufferedReader(new FileReader(filename));
							
							while((line=inputReader.readLine())!=null){
								outputValues.add(line); //Considero de momento que la salida sólo tiene un único valor
							}
							
							inputReader.close(); //Ya leí el fichero, lo cierro y elimino
							input.delete();
							
							num++;
							if(num==numSimulations){ //Ya he terminado de recoger todos los valores que tenía que coger
								//Genero las sucesivas líneas que debo poner
								if(traceSet.equals("output")){
									for(String s: outputValues){
										outWriter.write(s);
										outWriter.newLine();
									}
								}
								else if(traceSet.endsWith("output")){ //Solo tengo parámetros y muestro toda la salida en la misma línea
									String[] formatOutput = traceSet.split("output");
									line="";
									for(String s: formatOutput[0].split(" ")){ //Los parámetros primero
										if(!s.isEmpty()){
											String pValue = paramValues.get(s);
											line=line+((pValue==null)?"":pValue+" ");
										}
									}
									for(String s: outputValues){
										line=line+s+" ";
									}
									
									outWriter.write(line); //Escribo en el fichero
									outWriter.newLine();
									
								}
								else if (traceSet.startsWith("output")){ //"output" + parámetros
									String formatOutput = traceSet.substring("output".length()+1, traceSet.length()); //Recojo el conjunto de datos
									if(formatOutput.equals("*")){
										line="";
										for(String s: outputValues){
											line=line+s+" ";
										}
										
										outWriter.write(line); //Escribo en el fichero
										outWriter.newLine();
									}
									else{
										int steps;
										try{
											System.out.println("Formato: "+formatOutput);
											steps = Integer.parseInt(formatOutput)>0?Integer.parseInt(formatOutput):1;
										}catch (NumberFormatException e){
											steps=1;
										}
										steps=numSimulations%steps==0?steps:1; //Me aseguro que tenga formato adecuado
										System.out.println(steps);
										line = "";
										int j=0;
										for(String s: outputValues){
											line = line + s +" ";
											System.out.println(outputValues.size());
											if(++j==steps){
												outWriter.write(line);
												outWriter.newLine();
												line="";
												j=0;
												System.out.println("Escribo en el fichero");
											}
										}
									}
									
								}
								num=0;
								outputValues.clear(); //Limpio los valores anteriores
								paramValues.clear();
							}
							
						}
						outWriter.close();
						if(report.getSaveTraceFile()){
							cm.uploadFile(report.getPackageID()+"/"+report.getSimDescID()+"/generatedTrace-"+report.getOutputPattern()+".trace", outputTrace, MediaType.TEXT_PLAIN);
						}
						//Ya tengo el fichero de traza
					}
					break;
				default: //En el resto de casos, utilizo un fichero de traza a utilizar
					String input=cm.downloadFile(report.getPackageID()+"/"+report.getSimDescID()+"/"+report.getInputFile());
					outputTrace = new File(input);
				}
				
				//Comprobar que no esté malformed
				
				HashMap<String,ArrayList<String>> variables = new HashMap<String,ArrayList<String>>(); //Valor de las variables
				HashMap<String,String> variableSet = new HashMap<String,String>(); //Rango de las variables
				//Leo las variables
				if(!report.getOperations().isEmpty()){
					if(parameters.containsKey("variableSpan")){
						String[] variableSpan = parameters.get("variableSpan").split(" ");
						
						for(int i=0; i< variableSpan.length; i+=2){
							if(!variableSet.containsKey(variableSpan[i]))
								variableSet.put(variableSpan[i], variableSpan[i+1]);
						}
					}
					else
						variableSet.put("input", "1");
					
					for(String s: variableSet.keySet()){
						variables.put(s, new ArrayList<String>());
					}
					
					BufferedReader inputReader = new BufferedReader(new FileReader(outputTrace));
					String lineBuff;
					
					while((lineBuff=inputReader.readLine())!=null){
						String[] readValues = lineBuff.split(" ");
						for(Entry<String,String> e : variableSet.entrySet()){
							String[] range = e.getValue().split("-");
							if(range.length==1)
								variables.get(e.getKey()).add(readValues[Integer.parseInt(range[0])-1]);
							else if(range.length==2){
								String value ="";
								for(int i=Integer.parseInt(range[0])-1; i<Integer.parseInt(range[1]); i++){
									value=value+readValues[i]+" ";
								}
								variables.get(e.getKey()).add(value);
							}
							else
								report.setStatus(TaskStatusEnum.MALFORMED);
						}
					}
					
					inputReader.close(); //Ya terminé de leer el fichero de traza
					
					for(OperationReport o : report.getOperations()){
						String operation = o.getOperation();
						String input = o.getInputData();
						
						//Preparar petición a Estadística
					}
				}
				
			}
		}catch (URISyntaxException | IOException | NumberFormatException | IndexOutOfBoundsException e){
			
		}
	}

}
