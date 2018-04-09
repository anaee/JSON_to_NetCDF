package fr.inra.NetCDFGenerator;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import fr.inra.NetCDFGenerator.Model.Context;
import fr.inra.NetCDFGenerator.Model.Variable;
import fr.inra.NetCDFGenerator.Model.VariableValue;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayString;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.IndexIterator;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.jni.netcdf.Nc4Iosp;


public class NetCDFGenerator {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(NetCDFGenerator.class);
		
//		Nc4Iosp.setLibraryAndPath("./test/", "netcdf");
		
		String filename = new String(args[1]);
//		String filename = "test.nc";
		NetcdfFileWriter dataFile = null;
	    		
	    File file = new File(args[0]);
//		File file = new File("sortieTestLabel.json");
//		logger.debug("Chargement "+file.getPath());
		System.out.println("Chargement "+file.getPath());
		
		String name  = new String();
		List<Variable> variables = new ArrayList<Variable>();
		List<String> header = new ArrayList<String>();
		List<String> entContexts = new ArrayList<String>();
		List<String> charContexts = new ArrayList<String>();
		List<String> stdContexts = new ArrayList<String>();
		List<String> entContextsLabels = new ArrayList<String>();
		List<String> charContextsLabels = new ArrayList<String>();
		List<String> stdContextsLabels = new ArrayList<String>();
		List<String> valContexts = new ArrayList<String>();
		boolean parsingEntContexts = false;
		boolean parsingCharContexts = false;
		boolean parsingStdContexts = false;
		boolean parsingValContexts = false;
		boolean parsingVariableChar = false;
		boolean parsingEntContextsLabels = false;
		boolean parsingCharContextsLabels = false;
		boolean parsingStdContextsLabels = false;
		boolean parsingVariableCharLabel = false;
		boolean parsingVariableEnt = false;
		boolean parsingVariableStd = false;
		boolean parsingQualifier = false;
		boolean parsingVariableEntLabel = false;
		boolean parsingVariableStdLabel = false;
		boolean parsingVariableQualifLabel = false;
		boolean parsingValue = false;
		boolean storeObs = false;
		boolean newContext = false;
		boolean newVariable = true;
		boolean contextAdded = false;
//		String obs = new String();
		Variable currentVar = new Variable();
		VariableValue varValue = new VariableValue();
		Context tmpContext;
//		String contextName;
		int nbObs=1;
		
		long start= System.currentTimeMillis(); 
        long time=0;  
		
		try {
			
			FileInputStream input = new FileInputStream(file);
			JsonReader jsonReader = new JsonReader(new InputStreamReader(input, "UTF-8"));
	        JsonToken nextToken = jsonReader.peek();
	        
	        while (!JsonToken.END_DOCUMENT.equals(nextToken)) {
      		
	            switch (nextToken) {
	            	
	            	case BEGIN_OBJECT : 
		                jsonReader.beginObject();
		                break;
		            
		            //fin d'objet quand "}"
	            	case END_OBJECT :
		                jsonReader.endObject();
		                parsingStdContexts = false;
		                parsingVariableChar = false;
		                parsingVariableEnt = false;
		                parsingVariableStd = false;
		                parsingEntContexts = false;
		                parsingCharContexts = false;
		                parsingValContexts = false;
		                parsingQualifier = false;
		                parsingValue = false;
		                parsingEntContextsLabels = false;
		        		parsingCharContextsLabels = false;
		        		parsingStdContextsLabels = false;
		        		parsingVariableCharLabel = false;
		        		parsingVariableEntLabel = false;
		        		parsingVariableStdLabel = false;
		        		parsingVariableQualifLabel = false;
		                break;
		            
	            	case NAME :
		                name  =  jsonReader.nextName();
//		                logger.debug(name);
		                
		                //observation contextualisée
		                if (name.equals("obs") && !currentVar.getCharacteristic().equals("")) {
		                	nbObs++;
		                	newContext = true;
		                	
//		                	logger.debug(currentVar.getName());
		                	
		                	if (variables.isEmpty()) {
		                		newVariable = true;
		                	} else {
		                		newVariable = true;
		                		for (Variable var : variables) {
		                			if (var.equals(currentVar)) {
		                				newVariable = false;
		                				break;
		                			}
		                		}
		                	}
		                	
		                	//nouvelle variable
		                	if (newVariable) {
		            			for (int j=0; j<entContexts.size(); j++) {
		                			varValue.getContextsIndexes().add(-1);
		            				contextAdded = false;
//		                			contextName = new String(currentVar.getName().substring(currentVar.getName().lastIndexOf("/")+1)+entContexts.get(j).substring(entContexts.get(j).indexOf("#")) 
//		                					+ charContexts.get(j).substring(charContexts.get(j).indexOf("#")) + stdContexts.get(j).substring(stdContexts.get(j).indexOf("#")));
		                				tmpContext = new Context(currentVar.getName(), entContexts.get(j), charContexts.get(j), stdContexts.get(j), entContextsLabels.get(j), charContextsLabels.get(j), stdContextsLabels.get(j), valContexts.get(j));
		                				currentVar.addContext(tmpContext);
		                				varValue.setIndex(varValue.getContextsIndexes().size()-1, tmpContext.getValues().size()-1);
//		                				logger.debug("nouveau contexte "+tmpContext.getName()+" "+tmpContext.getValues().get(0));
		                				
//		                			}
		                    	}
		            			
		            			currentVar.addValue(varValue);
		            			variables.add(currentVar);
//		            			logger.debug("nouvelle variable ajoutee "+currentVar.getName() + " nb contextes "+currentVar.getContexts().size()+" valeur "+varValue);	
		                	} else {
		                    	for (int i=0; i < variables.size(); i++) {
		                    		//variable existante
		                    		if (variables.get(i).getName().equals(currentVar.getName())) {
//		                    			logger.debug("valeur n"+(variables.get(i).getValues().size()+1));
		                    			for (int j=0; j<variables.get(i).getContexts().size(); j++) {
		                    				varValue.getContextsIndexes().add(-1);
		                    			}
		                        		for (int j=0; j < entContexts.size(); j++) {
		                        			tmpContext = new Context(currentVar.getName(), entContexts.get(j), charContexts.get(j), stdContexts.get(j), entContextsLabels.get(j), charContextsLabels.get(j), stdContextsLabels.get(j), valContexts.get(j));
		                        			contextAdded = false;
		                        			
		                        			for (Context context : variables.get(i).getContexts()) {
		                        				
		                        				//contexte existant
		                        				if (context.equals(tmpContext)) {
		                        					newContext = false;
		                        					for (int k=0; k<context.getValues().size(); k++) {
		                        						//valeur existante
		                        						if(context.getValues().contains(valContexts.get(j))) {
		                                					varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().indexOf(valContexts.get(j)));
//		                	                				logger.debug("contexte et valeurs existants, index ajoute "+context.getValues().indexOf(valContexts.get(j))+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
		                	                				contextAdded = true;
		                	                				break;	
		                        						//nouvelle valeur, ajoutée à sa position
		                        						} else if(context.getValues().get(k).compareTo(valContexts.get(j))>=0) {
		                        							context.addValue(valContexts.get(j));
		                        							varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().indexOf(valContexts.get(j)));
		        	                						contextAdded = true;
//		        	                						logger.debug("contexte existant, valeur et index ajoutes "+valContexts.get(j)+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
		        	                						break;
		        	                					}
		                        					}
		                        					//valeur ajoutée en fin de liste
		                        					if (!contextAdded) {
		                        						context.addValue(valContexts.get(j));
		                        						varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().size()-1);
//		        	                					logger.debug("contexte existant, valeur sup et index ajoutes "+valContexts.get(j)+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
		        	                				}
		                        					newContext = false;
		        	                				break;
		                        				}
		                        			}	
		                        			
		                        			//nouveau contexte
		                        			if (newContext) {
		                        				variables.get(i).addContext(tmpContext);
		                        				varValue.setIndex(varValue.getContextsIndexes().size()-1, tmpContext.getValues().size()-1);
//		                        				logger.debug("nouveau contexte "+tmpContext.getName()+" "+tmpContext.getValues().get(0));
		                        				
		                        			}
		                        			
		                        			
		        	                	}
		                        		variables.get(i).addValue(varValue);

//		                        		logger.debug(variables.get(i).getIndexes().size());
		                        		
		                        		break;
		                    		} 
		                    	}
		                    	
		                	}
		                	
		                	newVariable = false;
		                	entContexts.clear();
		                	charContexts.clear();
		                	stdContexts.clear();
		                	entContextsLabels.clear();
		                	charContextsLabels.clear();
		                	stdContextsLabels.clear();
		                	valContexts.clear();
		                	storeObs = true;
		                	currentVar = new Variable();
		                } else if (name.equals("char")) {
		                	parsingVariableChar = true;
		                } else if (name.equals("ent")) {
		                	parsingVariableEnt = true;
		                } else if (name.equals("std")) {
		                	parsingVariableStd = true;
		                } else if (name.equals("charLabel")) {
		                	parsingVariableCharLabel = true;
		                } else if (name.equals("entLabel")) {
		                	parsingVariableEntLabel = true;
		                } else if (name.equals("stdLabel")) {
		                	parsingVariableStdLabel = true;
		                } else if (name.equals("qualifLabel")) {
		                	parsingVariableQualifLabel = true;
		                } else if (name.equals("val")) {
		                	parsingValue = true;
		                } else if (name.equals("entityContext")) {
		                	parsingEntContexts = true;
		                } else if (name.equals("charContext")) {
		                	parsingCharContexts = true;
		                } else if (name.equals("valContext")) {
		                	parsingValContexts = true;
		                } else if (name.equals("stdContext")) {
		                	parsingStdContexts = true;
		                } else if (name.equals("entityContextLabel")) {
		                	parsingEntContextsLabels = true;
		                } else if (name.equals("charContextLabel")) {
		                	parsingCharContextsLabels = true;
		                } else if (name.equals("stdContextLabel")) {
		                	parsingStdContextsLabels = true;
		                } else if (name.equals("qualif")) {
		                	parsingQualifier = true;
		                }
		                break;
		                
	            	case STRING :
		                String value =  jsonReader.nextString();
		                
		                //header
		                if(name.equals("vars")) {
		                	header.add(value);
		                } else if (storeObs && value.startsWith("http")) {
		                	storeObs=false;
//		                	obs = value;
		                //variable
//		                } else if (parsingVariableChar && value.startsWith("http") && !(obs.contains("experimentalSite"))) {
		                } else if (parsingVariableChar && value.startsWith("http")) {
//		                	//1er passage
//		                	if (variables.isEmpty()) {
//		                		currentVar = new Variable(value);
//		                		newVariable = true;
//		                	}
//		                	//reste du fichier
//		                	for (int i=0; i < variables.size(); i++) {
//		                		if (variables.get(i).getCharacteristic().equals(value)) {
//		                			currentVar = variables.get(i);
//		                			newVariable = false;
//		                			break;
//		                		}
//		                		currentVar = new Variable(value);
//		                		newVariable = true;
//		                	}
		                	currentVar.setCharacteristic(value);
		                	
		                } else if (parsingVariableEnt && value.startsWith("http")) {
		                	currentVar.setEntity(value);
		                	
		                } else if (parsingVariableStd && value.startsWith("http")) {
		                	currentVar.setStandard(value);
		               
		                } else if (parsingVariableEntLabel && name.equals("value")) {
		                	currentVar.setEntityLabel(value);
		                	
		                } else if (parsingVariableStdLabel && name.equals("value")) {
		                	currentVar.setStandardLabel(value);
		                
		                } else if (parsingVariableCharLabel && name.equals("value")) {
		                	currentVar.setCharacteristicLabel(value);
		                	
		                } else if (parsingVariableQualifLabel && name.equals("value")) {
		                	currentVar.setQualifLabel(value);	
		                			
		                //qualifier variable	
		                } else if (parsingQualifier && value.startsWith("http")) {
		                	currentVar.setQualifier(value);
	                			
		                //contextes URI
		                } else if (parsingEntContexts && value.startsWith("http")) {
		                	String[] values=value.split(";");
	                		for (String contextEnt : values) {
	                			//n'arrive jamais ?
	                			if (contextEnt.startsWith("***")) {
		                			entContexts.add("ENT#Entity");
		                		} else {
		                			entContexts.add(contextEnt);
		                			
		                		}
		                	}
			            //caracteristiques contextes    
		                } else if (parsingCharContexts && value.startsWith("http")) {
		                	String[] values = value.split(";");
		                	for (String contextChar : values) {	
		                		if(contextChar.startsWith("***")) {
		                			charContexts.add("CHAR#Characteristic");
		                			
		                		} else {
		                			charContexts.add(contextChar);
		                		}
	                		}
		                //standards contextes	
		                } else if (parsingStdContexts && value.startsWith("http")) {
		                	String[] values = value.split(";");
		                	for (String contextStd : values) {	
		                		if(contextStd.startsWith("***")) {
		                			stdContexts.add("STD#Standard");
		                		} else {
		                			stdContexts.add(contextStd);
		                		}
	                		}
		                	
		                	 //Labels contextes URI
		                } else if (parsingEntContextsLabels  && name.equals("value")) {
		                	String[] values=value.split(";");
	                		for (String contextEntLabel : values) {
	                			//n'arrive jamais ?
	                			if (contextEntLabel.startsWith("***")) {
		                			entContextsLabels.add("EntityLabel");
		                		} else {
		                			entContextsLabels.add(contextEntLabel);
		                			
		                		}
		                	}
			            //caracteristiques contextes    
		                } else if (parsingCharContextsLabels  && name.equals("value")) {
		                	String[] values = value.split(";");
		                	for (String contextCharLabel : values) {	
		                		if(contextCharLabel.startsWith("***")) {
		                			charContextsLabels.add("CharacteristicLabel");
		                			
		                		} else {
		                			charContextsLabels.add(contextCharLabel);
		                		}
	                		}
		                //standards contextes	
		                } else if (parsingStdContextsLabels  && name.equals("value")) {
		                	String[] values = value.split(";");
		                	for (String contextStdLabel : values) {	
		                		if(contextStdLabel.startsWith("***")) {
		                			stdContextsLabels.add("StandardLabel");
		                		} else {
		                			stdContextsLabels.add(contextStdLabel);
		                		}
	                		}
		                //valeurs contextes							
		                } else if (parsingValContexts && name.equals("value")) {
		                	String[] values = value.split(";");
		                	for (String contextVal : values) {	
			                	valContexts.add(contextVal);
	                		}
		                } else if (parsingValue && name.equals("value")) {              	
		                	varValue = new VariableValue(value);
		                }
		                
		                break;
		                
	            	case BEGIN_ARRAY :
		                jsonReader.beginArray();
		                break;
		                
	            	case END_ARRAY :
		                jsonReader.endArray();
		                break;
		                
		            default :
//		            	logger.debug("---end wtf---");
		                jsonReader.close();
		                break;
	            }
	            
      		nextToken = jsonReader.peek();
	        }
	        
	        //gestion du dernier token
	        if(JsonToken.END_OBJECT.equals(jsonReader.peek())) {
	        	jsonReader.endObject();
	        	
	        } else if (JsonToken.END_ARRAY.equals(jsonReader.peek())) {
	        	jsonReader.endArray();
	        	jsonReader.endObject();
	        	
	        } else if(JsonToken.END_DOCUMENT.equals(nextToken)){
	        	jsonReader.close();
//	        	logger.debug("fin fichier");
	        	System.out.println("fin fichier JSON");
              
	        } else 
//	        	logger.debug("????wat????");
	        
	       
//	        logger.debug(currentVar.getName());
        	
        	if (variables.isEmpty()) {
        		newVariable = true;
        	} else {
        		newVariable = true;
        		for (Variable var : variables) {
        			if (var.equals(currentVar)) {
        				newVariable = false;
        				break;
        			}
        		}
        	}
        	
        	//nouvelle variable
        	if (newVariable) {
    			for (int j=0; j<entContexts.size(); j++) {
        			varValue.getContextsIndexes().add(-1);
    				contextAdded = false;
//        			contextName = new String(currentVar.getName().substring(currentVar.getName().lastIndexOf("/")+1)+entContexts.get(j).substring(entContexts.get(j).indexOf("#")) 
//        					+ charContexts.get(j).substring(charContexts.get(j).indexOf("#")) + stdContexts.get(j).substring(stdContexts.get(j).indexOf("#")));
        				tmpContext = new Context(currentVar.getName(), entContexts.get(j), charContexts.get(j), stdContexts.get(j), entContextsLabels.get(j), charContextsLabels.get(j), stdContextsLabels.get(j), valContexts.get(j));
        				currentVar.addContext(tmpContext);
        				varValue.setIndex(varValue.getContextsIndexes().size()-1, tmpContext.getValues().size()-1);
//        				logger.debug("nouveau contexte "+tmpContext.getName()+" "+tmpContext.getValues().get(0));
        				
//        			}
            	}
    			
    			currentVar.addValue(varValue);
    			variables.add(currentVar);
//    			logger.debug("nouvelle variable ajoutee "+currentVar.getName() + " nb contextes "+currentVar.getContexts().size()+" valeur "+varValue);	
        	} else {
            	for (int i=0; i < variables.size(); i++) {
            		//variable existante
            		if (variables.get(i).getName().equals(currentVar.getName())) {
//            			logger.debug("valeur n"+(variables.get(i).getValues().size()+1));
            			for (int j=0; j<variables.get(i).getContexts().size(); j++) {
            				varValue.getContextsIndexes().add(-1);
            			}
                		for (int j=0; j < entContexts.size(); j++) {
                			tmpContext = new Context(currentVar.getName(), entContexts.get(j), charContexts.get(j), stdContexts.get(j), entContextsLabels.get(j), charContextsLabels.get(j), stdContextsLabels.get(j), valContexts.get(j));
                			contextAdded = false;
                			
                			for (Context context : variables.get(i).getContexts()) {
                				
                				//contexte existant
                				if (context.equals(tmpContext)) {
                					newContext = false;
                					for (int k=0; k<context.getValues().size(); k++) {
                						//valeur existante
                						if(context.getValues().contains(valContexts.get(j))) {
                        					varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().indexOf(valContexts.get(j)));
//        	                				logger.debug("contexte et valeurs existants, index ajoute "+context.getValues().indexOf(valContexts.get(j))+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
        	                				contextAdded = true;
        	                				break;	
                						//nouvelle valeur, ajoutée à sa position
                						} else if(context.getValues().get(k).compareTo(valContexts.get(j))>=0) {
                							context.addValue(valContexts.get(j));
                							varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().indexOf(valContexts.get(j)));
	                						contextAdded = true;
//	                						logger.debug("contexte existant, valeur et index ajoutes "+valContexts.get(j)+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
	                						break;
	                					}
                					}
                					//valeur ajoutée en fin de liste
                					if (!contextAdded) {
                						context.addValue(valContexts.get(j));
                						varValue.setIndex(variables.get(i).getContexts().indexOf(context), context.getValues().size()-1);
//	                					logger.debug("contexte existant, valeur sup et index ajoutes "+valContexts.get(j)+" "+context.getValues().indexOf(valContexts.get(j))+" "+context.getName());
	                				}
                					newContext = false;
	                				break;
                				}
                			}	
                			
                			//nouveau contexte
                			if (newContext) {
                				variables.get(i).addContext(tmpContext);
                				varValue.setIndex(varValue.getContextsIndexes().size()-1, tmpContext.getValues().size()-1);
//                				logger.debug("nouveau contexte "+tmpContext.getName()+" "+tmpContext.getValues().get(0));
                				
                			}
                			
                			
	                	}
                		variables.get(i).addValue(varValue);

//                		logger.debug(variables.get(i).getIndexes().size());
                		
                		break;
            		} 
            	}
            	
        	}
	        
        	//tri des valeurs contextes
        	for (Variable tempVar : variables) { 
        		ArrayList<Context> tempContextsList = new ArrayList<Context>();
        		for (Context tempContext : tempVar.getContexts()) {
        			tempContextsList.add(new Context(tempContext));
        			Collections.sort(tempContext.getValues());        			
        		}
        		//copie du tri sur les indexes
        		for (VariableValue val : tempVar.getValues()) {
        			for (int j=0; j<val.getContextsIndexes().size(); j++) {
        				val.setIndex(j, tempVar.getContexts().get(j).getValues().indexOf(tempContextsList.get(j).getValues().get(val.getContextsIndexes().get(j))));
        			}
        		}
        	}
        	
//        	logger.debug("CHECK CONTEXTES");
//        	//verif si contextes corr�l�s
//        	for (Variable tempVar : variables) { 
//        		//parcours de contextes
//				for (int i=0; i<tempVar.getContexts().size(); i++) {
////        			logger.debug(tempVar.getContexts().get(i).getName()+" "+tempVar.getContexts().get(i).getValues().size()+" "+i);
//        			//si 1 valeur, dimension
//        			if (tempVar.getContexts().get(i).getValues().size() == 1) {
////        				logger.debug("dim");
//        				tempVar.getContexts().get(i).setAsDimension();
//        			} else {
//        				//parcours des autres contextes
//        				for (int j=0; j<tempVar.getContexts().size(); j++) {
//	        				if(!tempVar.getContexts().get(i).equals(tempVar.getContexts().get(j)) && !tempVar.getContexts().get(j).isDimension()) {
//	        					if (tempVar.getContexts().get(i).getValues().size() == tempVar.getContexts().get(j).getValues().size()) {
//	        						tempVar.getContexts().get(i).addCorrelatedContext(j);
////	        						logger.debug("mm nb de val que "+tempVar.getContexts().get(j).getName()+" "+j);
//	        					}
//	        				}
//	        			}
//        			}
//        			
//        		}
//        		logger.debug("-----------");
//        		for (VariableValue val : tempVar.getValues()) {
//        			//parcours contextes
//        			for (int i=0; i<val.getContextsIndexes().size(); i++) {
//        				if(!tempVar.getContexts().get(i).isDimension() && !tempVar.getContexts().get(i).getCorrelatedContexts().isEmpty()) {
////        					logger.debug(tempVar.getContexts().get(i).getName()+" "+tempVar.getContexts().get(i).getValues().size()+" "+i);
//        					for (Integer index : tempVar.getContexts().get(i).getCorrelatedContexts()) {
//        						if (val.getContextsIndexes().get(index) != val.getContextsIndexes().get(i)) {
////            						logger.debug("> "+tempVar.getContexts().get(index).getName()+ " "+val.getContextsIndexes().get(index)+ " "+val.getContextsIndexes().get(i));
//        							tempVar.getContexts().get(i).getCorrelatedContexts().remove(index);
//        							break;
//        						}
//            				}
//        				}
//        			}
//        		}
//        	}
//        	
//        	for (Variable tempVar : variables) {
//        		for (Context context : tempVar.getContexts()) {
//        			logger.debug("> "+context.getName());
//        			for (Integer index : context.getCorrelatedContexts()) {
//        				logger.debug("- "+tempVar.getContexts().get(index).getName());
//        			}
//        		}
//        	}
        	
//        	//affichage des données
//	        logger.debug("Header = " + Arrays.deepToString(header.toArray()));
//	        for (Variable tempVar : variables) {
//	        	logger.debug("VAR "+tempVar.getName()+" - "+tempVar.getValues().size()+" - "+tempVar.getEntity()+" "+ tempVar.getStandard()+" "+tempVar.getQualifier());
//	            for (int k=0; k<tempVar.getValues().size(); k++) {
//	            	
//	            	logger.debug("valeur n° "+(tempVar.getValues().indexOf(tempVar.getValues().get(k))+1)+" "+ tempVar.getValues().get(k).getValue());
//	            	for(int j=0; j<tempVar.getContexts().size(); j++) {
//	            		logger.debug("> contexte "+tempVar.getContexts().get(j).getName()+" valeur "+tempVar.getContexts().get(j).getValues().get(tempVar.getValues().get(k).getContextsIndexes().get(j))+" "+tempVar.getValues().get(k).getContextsIndexes().get(j));
//	            		
//	            	}
//	            
//	            }
//	            for (Context context : tempVar.getContexts()) {
//	            	logger.debug("CONT "+context.getEntity() + " " +context.getCharacteristic()+" "+context.getStandard()+context);
//	            	logger.debug("> "+context.getName()+" "+context.getValues().size()+" valeurs :");
//	            	for (String value : context.getValues()) {
//	            		logger.debug(" "+value);
//	            	}
//	            	
//	            }
//	        }
	        
	        time= System.currentTimeMillis();
//	        logger.debug(nbObs + " observations en "+(time-start)+" ms");
        	System.out.println(nbObs + " observations en "+(time-start)+" ms");	   
        	System.out.println("preparation des donnees");
	        
	        
	        dataFile = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf4, filename);
	        
	        Group group = dataFile.addGroup(null, "TestGroup");
	        
	        Attribute att;
	        List<ucar.nc2.Variable> vars = new ArrayList<ucar.nc2.Variable>();
	        List<ArrayList<Dimension>> dim = new ArrayList<ArrayList<Dimension>>();
	        List<ArrayList<ucar.nc2.Variable>> dimVars = new ArrayList<ArrayList<ucar.nc2.Variable>>();
	        List<ArrayList<ArrayString.D1>> dimData = new ArrayList<ArrayList<ArrayString.D1>>();
	        List<ArrayList<Integer>> contextsValuesNb = new ArrayList<ArrayList<Integer>>();
	        List<Array> varData = new ArrayList<Array>();
	        List<String> varDims = new ArrayList<String>();
	        List<Context> sortContexts = new ArrayList<Context>();
	        List<Integer> sortIndexes = new ArrayList<Integer>(); 
	        List<Integer> contextsIndexes = new ArrayList<Integer>();
	        
	        //parcours des variables	
	        for (int i=0; i<variables.size(); i++) {
//	        	logger.debug(variables.get(i).getName());
	        	varDims.add(new String());
	            sortContexts.clear();
	            contextsIndexes.clear(); 
	            dim.add(new ArrayList<Dimension>());
	            dimVars.add(new ArrayList<ucar.nc2.Variable>());
	            dimData.add(new ArrayList<ArrayString.D1>());
	            contextsValuesNb.add(new ArrayList<Integer>());
	            
	          //parcours des contextes pour creation attributs
	            for (Context context : variables.get(i).getContexts()) {
	            	contextsValuesNb.get(i).add(context.getValues().size());
	            	//si taille>1 dimension, sinon attribut
	            	if (context.getValues().size() > 1) {
		        	        dim.get(i).add(dataFile.addDimension(group, context.getName(), context.getValues().size()));
		        	        dimVars.get(i).add(dataFile.addVariable(group, context.getName(), DataType.STRING, context.getName().replace("#", "_")));	   
		        	        dataFile.addVariableAttribute(dimVars.get(i).get(dimVars.get(i).size()-1), new Attribute("entity", context.getEntity()));
		        	        dataFile.addVariableAttribute(dimVars.get(i).get(dimVars.get(i).size()-1), new Attribute("characteristic", context.getCharacteristic()));
		        	        dataFile.addVariableAttribute(dimVars.get(i).get(dimVars.get(i).size()-1), new Attribute("standard", context.getStandard()));
	            		
		        	        dimData.get(i).add(new ArrayString.D1(context.getValues().size()));
		        	        
		        	        for (int j=0; j<context.getValues().size(); j++) {
		        	        	
			            		dimData.get(i).get(dimData.get(i).size()-1).set(j, context.getValues().get(j));
			            	}
		        	        //unité-std
	//	        	        dataFile.addVariableAttribute(dimVar, new Attribute("units", "degrees_north"));
		        	        
	            	} else {
	        	        	att = new Attribute(context.getName(), context.getValues().get(0));
	        	        	dataFile.addGroupAttribute(group, att);
	            	}
	            	
	            }
	            
	            //classement dimensions par taille
	            for (int j=0; j<contextsValuesNb.get(i).size(); j++) {
	            	contextAdded = false;
	            	if (contextsValuesNb.get(i).get(j)>1) {
		            	for (int k=0; k<contextsIndexes.size(); k++) {
			            		if (contextsValuesNb.get(i).get(j) <= contextsValuesNb.get(i).get(contextsIndexes.get(k))) {
		            				contextsIndexes.add(k, j);
			            			contextAdded = true;
			            			break;
			            		} 
		            	}
		            	if(!contextAdded) {
		            		contextsIndexes.add(j);
//		            		logger.debug(j);
		            	}
	            	}
	            }
	            
	            
	            
	            //ajout dimensions variable
	            for (Integer ind : contextsIndexes) {
//	            	logger.debug(variables.get(i).getContexts().get(ind).getName());
	            	if (variables.get(i).getContexts().get(ind).getValues().size()>1) {
		            	if (varDims.get(i).equals("")) {
		        			varDims.set(i, variables.get(i).getContexts().get(ind).getName().replace("#", "_"));
		        		} else {
		        			varDims.set(i, varDims.get(i) + " " + variables.get(i).getContexts().get(ind).getName().replace("#", "_"));
		        		}
	            	}
	            }
	            
//	            logger.debug("dimensions : "+varDims.get(i));
	            
	            //modif ordre des contextes en fonction des dim
	            for (int k = 0; k<contextsIndexes.size(); k++) {
            		sortContexts.add(variables.get(i).getContexts().get(contextsIndexes.get(k)));
            	}
	            //on rajoute les contexes attributs globaux
	            for (Context context : variables.get(i).getContexts()) {
	            	if (!sortContexts.contains(context)) {
	            		sortContexts.add(context);
	            	}
	            }
	            
	            variables.get(i).setContexts(sortContexts);
	            
	            //modif ordre des indexes en fonction des dim
	            for (int j=0; j<variables.get(i).getValues().size(); j++) {
	            	sortIndexes.clear();
	            	for (int k = 0; k<contextsIndexes.size(); k++) {
	            		sortIndexes.add(variables.get(i).getValues().get(j).getContextsIndexes().get(contextsIndexes.get(k)));
	            	}
	            	//ajout des indexes manquants (=0 car une seule valeur pour les attributs)
            		while (sortIndexes.size()<variables.get(i).getValues().get(j).getContextsIndexes().size()) {
            			sortIndexes.add(0);
            		}
    	            variables.get(i).getValues().get(j).setContextsIndexes(sortIndexes);
	            }
	            	            
	            //tri des valeurs variable
		        Collections.sort(variables.get(i).getValues());
		                
	        }
	        
//        	//affichage des données
//	        logger.debug("Header = " + Arrays.deepToString(header.toArray()));
//	        for (Variable tempVar : variables) {
//	        	logger.debug("VAR "+tempVar.getName()+" - "+tempVar.getValues().size()+" - "+tempVar.getEntity()+" "+ tempVar.getStandard()+" "+tempVar.getQualifier);
//	            for (int k=0; k<tempVar.getValues().size(); k++) {
//	            	
//	            	logger.debug("valeur n° "+(tempVar.getValues().indexOf(tempVar.getValues().get(k))+1)+" "+ tempVar.getValues().get(k).getValue());
//	            	for(int j=0; j<tempVar.getContexts().size(); j++) {
//	            		logger.debug("> contexte "+tempVar.getContexts().get(j).getName()+" valeur "+tempVar.getContexts().get(j).getValues().get(tempVar.getValues().get(k).getContextsIndexes().get(j))+" "+tempVar.getValues().get(k).getContextsIndexes().get(j));
//	            		
//	            	}
//	            
//	            }
//	            for (Context context : tempVar.getContexts()) {
//	            	logger.debug("CONT "+context.getEntity() + " " +context.getCharacteristic()+" "+context.getStandard()+context);
//	            	logger.debug("> "+context.getName()+" "+context.getValues().size()+" valeurs :");
//	            	for (String value : context.getValues()) {
//	            		logger.debug(" "+value);
//	            	}
//	            	
//	            }
//	        }
	        
	        
	        start= System.currentTimeMillis();
//	        logger.debug("preparation des donnees "+(start-time)+" ms");
	        System.out.println("preparation des donnees "+(start-time)+" ms");
	        System.out.println("ecriture des donnees");
	        
	        for (int i=0; i<variables.size(); i++) {    
	        	//test type de donn�es
	        	String dataType = variables.get(i).getDataType();
	        	
//	        	logger.debug(variables.get(i).getValues().size());
	        	
	            //creation variable en fonction du nb de dim
	            switch(varDims.get(i).split(" ").length) {
	            	case 1 : {
//	            		logger.debug("switch : 1 contexte");
	            		
	            		switch (dataType) {
	            			case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
	    	            		varData.add(new ArrayDouble.D1(variables.get(i).getContexts().get(0).getValues().size()));
	        	            	Index index = varData.get(i).getIndex();
	        	            	
	        	            	IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext(Double.NaN);
	    	    	       		}
	        	            	
	        	            	for (VariableValue val : variables.get(i).getValues()) {
	        	            		index.set(val.getIndexes()[0]);
	        	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
	        	            	}
	        	            		
	    	            		break;
	            			}
	            			case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
	    	            		varData.add(new ArrayInt.D1(variables.get(i).getContexts().get(0).getValues().size()));
	        	            	Index index = varData.get(i).getIndex();
	        	            	
//	        	            	IndexIterator iter = varData.get(i).getIndexIterator();
//	    	           		 	while (iter.hasNext()) {
//	    	           		 		iter.setObjectNext(Integer.NaN);
//	    	    	       		}
	        	            	
	        	            	for (VariableValue val : variables.get(i).getValues()) {
	        	            		index.set(val.getIndexes()[0]);
	        	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
	        	            	}
	        	            		
	    	            		break;
	            			}
	            			case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));

	    	            		varData.add(new ArrayString.D1(variables.get(i).getContexts().get(0).getValues().size()));
	        	            	Index index = varData.get(i).getIndex();
	        	            	
	        	            	IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext("NA");
	    	    	       		}
	        	            	
	        	            	for (VariableValue val : variables.get(i).getValues()) {
	        	            		index.set(val.getIndexes()[0]);
	        	            		varData.get(i).setObject(index, val.getValue());
	        	            	}
	        	            		
	    	            		break;
	            			}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            		}
	            		break;
	            	}	
	            	case 2 : {
//	            		logger.debug("switch : 2 contextes");
	            		
	            		switch (dataType) {
            				case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
            					varData.add(new ArrayDouble.D2(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size()));
        	            		Index index = varData.get(i).getIndex();
        	            		
        	            		IndexIterator iter = varData.get(i).getIndexIterator();
        	           		 	while (iter.hasNext()) {
        	           		 		iter.setObjectNext(Double.NaN);
        	    	       		}
        	            		
        	            		for (VariableValue val : variables.get(i).getValues()) {
        	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 2));
            	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
            	            	}
        			           
        	            		break;
            				}
            				
            				case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
            					varData.add(new ArrayInt.D2(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size()));
        	            		Index index = varData.get(i).getIndex();
        	            		
//        	            		IndexIterator iter = varData.get(i).getIndexIterator();
//        	           		 	while (iter.hasNext()) {
//        	           		 		iter.setObjectNext(Double.NaN);
//        	    	       		}
        	            		
        	            		for (VariableValue val : variables.get(i).getValues()) {
        	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 2));
            	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
            	            	}
        			           
        	            		break;
            				}
            			
            				case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
            					varData.add(new ArrayString.D2(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size()));
        	            		Index index = varData.get(i).getIndex();
        	            		
        	            		IndexIterator iter = varData.get(i).getIndexIterator();
        	           		 	while (iter.hasNext()) {
        	           		 		iter.setObjectNext("NA");
        	    	       		}
        	            		
        	            		for (VariableValue val : variables.get(i).getValues()) {
        	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 2));
            	            		varData.get(i).setObject(index, val.getValue());
            	            	}
        			           
        	            		break;
            				}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
            				
	            		}
	            		break;
	            		
	            	}
	            	case 3 : {
//	            		logger.debug("switch : 3 conte0xtes");
	            		
	            		switch (dataType) {
	            			case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
	            				varData.add(new ArrayDouble.D3(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext(Double.NaN);
	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 3));
	        	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
	            				varData.add(new ArrayInt.D3(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
//	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
//	    	           		 	while (iter.hasNext()) {
//	    	           		 		iter.setObjectNext(Double.NaN);
//	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 3));
	        	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			
	            			case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
	            				varData.add(new ArrayString.D3(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext("NA");
	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 3));
	        	            		varData.get(i).setObject(index, val.getValue());
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            		}
	            		break;
	            		
	            	}
	            	case 4 : {
//	            		logger.debug("switch : 4 contextes");
	            		
	            		switch (dataType) {
	            			case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
	            				varData.add(new ArrayDouble.D4(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext(Double.NaN);
	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 4));
	        	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
	        	            	}
	    	            		
	    	            		break;
	            			}
	            			case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
	            				varData.add(new ArrayInt.D4(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
//	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
//	    	           		 	while (iter.hasNext()) {
//	    	           		 		iter.setObjectNext(Double.NaN);
//	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 4));
	        	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
	        	            	}
	    	            		
	    	            		break;
	            			}
	            			case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
	            				varData.add(new ArrayString.D4(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext("NA");
	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 4));
	        	            		varData.get(i).setObject(index, val.getValue());
	        	            	}
	    	            		
	    	            		break;
	            			}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            			
	            		}
	            		
	            		break;
	            		
	            	}
	            	case 5 : {
//	            		logger.debug("switch : 5 contextes");
	            		
	            		switch (dataType) {
		            		case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
		            			varData.add(new ArrayDouble.D5(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
			            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size()));
			            		Index index = varData.get(i).getIndex();
			            		
			            		IndexIterator iter = varData.get(i).getIndexIterator();
			           		 	while (iter.hasNext()) {
			           		 		iter.setObjectNext(Double.NaN);
			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 5));
		    	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
		    	            	}
			            		break;
		            		}
		            		case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
		            			varData.add(new ArrayInt.D5(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
			            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size()));
			            		Index index = varData.get(i).getIndex();
			            		
//			            		IndexIterator iter = varData.get(i).getIndexIterator();
//			           		 	while (iter.hasNext()) {
//			           		 		iter.setObjectNext(Integer.MAX_VALUE);
//			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 5));
		    	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
		    	            	}
			            		break;
		            		}
		            		case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
		            			varData.add(new ArrayString.D5(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
			            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size()));
			            		Index index = varData.get(i).getIndex();
			            		
			            		IndexIterator iter = varData.get(i).getIndexIterator();
			           		 	while (iter.hasNext()) {
			           		 		iter.setObjectNext("NA");
			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 5));
		    	            		varData.get(i).setObject(index, val.getValue());
		    	            	}
			            		break;
		            		}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            		}
	            
	            		break;
	            	}
	            	case 6 : {
//	            		logger.debug("switch : 6 contextes");
	            		
	            		switch (dataType) {
	            			case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
	            				varData.add(new ArrayDouble.D6(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext(Double.NaN);
			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 6));
		    	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
	            				varData.add(new ArrayInt.D6(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
//	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
//	    	           		 	while (iter.hasNext()) {
//	    	           		 		iter.setObjectNext(Double.NaN);
//			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 6));
		    	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
	            				varData.add(new ArrayString.D6(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext("NA");
	    	    	       		}
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 6));
	        	            		varData.get(i).setObject(index, val.getValue());
	        	            	}
//	    	            		
	    	            		break;
	            			}
	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            		}
	            		break;
	            		
	            	}
	            	case 7 : {
//	            		logger.debug("switch : 7 contextes");

	            		switch (dataType) {
	            			case "Double" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.DOUBLE, varDims.get(i)));
	            				varData.add(new ArrayDouble.D7(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size(), variables.get(i).getContexts().get(6).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext(Double.NaN);
			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 7));
		    	            		varData.get(i).setObject(index, Double.parseDouble(val.getValue()));
	        	            	}
//	    	            		
	    	            	
	    	            		break;
	    	            	}
	            			case "Integer" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.INT, varDims.get(i)));
	            				varData.add(new ArrayInt.D7(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size(), variables.get(i).getContexts().get(6).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
//	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
//	    	           		 	while (iter.hasNext()) {
//	    	           		 		iter.setObjectNext(Double.NaN);
//			    	       		}
			           		 	
			            		
			            		for (VariableValue val : variables.get(i).getValues()) {
		      			      		index.set(Arrays.copyOfRange(val.getIndexes(), 0, 7));
		    	            		varData.get(i).setObject(index, Integer.parseInt(val.getValue()));
	        	            	}
//	    	            		
	    	            	
	    	            		break;
	    	            	}
	            			
	            			case "String" : {
//		            			logger.debug("ajout var "+variables.get(i).getName()+" "+varDims.get(i));
		        	            vars.add(dataFile.addVariable(group, variables.get(i).getName(), DataType.STRING, varDims.get(i)));
	            				varData.add(new ArrayString.D7(variables.get(i).getContexts().get(0).getValues().size(), variables.get(i).getContexts().get(1).getValues().size(), 
	    	            				variables.get(i).getContexts().get(2).getValues().size(), variables.get(i).getContexts().get(3).getValues().size(), variables.get(i).getContexts().get(4).getValues().size(),
	    	            				variables.get(i).getContexts().get(5).getValues().size(), variables.get(i).getContexts().get(6).getValues().size()));
	    	            		Index index = varData.get(i).getIndex();
	    	            		
	    	            		IndexIterator iter = varData.get(i).getIndexIterator();
	    	           		 	while (iter.hasNext()) {
	    	           		 		iter.setObjectNext("NA");
	    	    	       		} 
	    	            		
	    	            		for (VariableValue val : variables.get(i).getValues()) {
	    	            			index.set(Arrays.copyOfRange(val.getIndexes(), 0, 7));
	        	            		varData.get(i).setObject(index, val.getValue());
	        	            	}
//	    	            		
	    	            	
	    	            		break;
	    	            	}

	            			default : {
//	            				logger.debug("pb datatype");
	            			}
	            		}
	            		break;
	            	}
	            	default : {
//	            		logger.debug("pb nb de contextes");
	            		break;
        			}
	            }  
	            dataFile.addVariableAttribute(vars.get(i), new Attribute("characteristic", variables.get(i).getCharacteristic()));
	            dataFile.addVariableAttribute(vars.get(i), new Attribute("entity", variables.get(i).getEntity()));
    	        dataFile.addVariableAttribute(vars.get(i), new Attribute("standard", variables.get(i).getStandard()));
    	        dataFile.addVariableAttribute(vars.get(i), new Attribute("qualifier", variables.get(i).getQualifier()));
	        }
	        
	        dataFile.create();
	        
	        for (int i=0; i < variables.size(); i++) {
	        	
	        	//ecriture données dimensions
	            for (int j=0; j<dimVars.get(i).size(); j++) {
//	            	logger.debug("ecriture dim "+j+" "+dimVars.get(i).get(j).getNameAndDimensions());
	            	dataFile.write(dimVars.get(i).get(j), dimData.get(i).get(j));
	  	      	}
	//  	     	//ecriture données variable
//		  	    logger.debug("ecriture var "+i+" "+varData.get(i).shapeToString()+" "+vars.get(i).getNameAndDimensions());
		  	    dataFile.write(vars.get(i), varData.get(i));
		  	    varData.get(i).resetLocalIterator();
//		  	    int cpt=0;
//		  	    while (varData.get(i).hasNext()) {
//		  	    	logger.debug("### "+varData.get(i).next()+" "+cpt++);
//		  	    }
	        }
	        
//        	//affichage des données
//	        logger.debug("Header = " + Arrays.deepToString(header.toArray()));
//	        for (Variable tempVar : variables) {
//	        	logger.debug("VAR "+tempVar.getName()+" - "+tempVar.getValues().size()+" - "+tempVar.getEntity()+" "+ tempVar.getStandard()+" "+tempVar.getQualifier);
//	            for (int k=0; k<tempVar.getValues().size(); k++) {
//	            	
//	            	logger.debug("valeur n° "+(tempVar.getValues().indexOf(tempVar.getValues().get(k))+1)+" "+ tempVar.getValues().get(k).getValue());
//	            	for(int j=0; j<tempVar.getContexts().size(); j++) {
//	            		logger.debug("> contexte "+tempVar.getContexts().get(j).getName()+" valeur "+tempVar.getContexts().get(j).getValues().get(tempVar.getValues().get(k).getContextsIndexes().get(j))+" "+tempVar.getValues().get(k).getContextsIndexes().get(j));
//	            		
//	            	}
//	            
//	            }
//	            for (Context context : tempVar.getContexts()) {
//	            	logger.debug("CONT "+context.getEntity() + " " +context.getCharacteristic()+" "+context.getStandard()+context);
//	            	logger.debug("> "+context.getName()+" "+context.getValues().size()+" valeurs :");
//	            	for (String value : context.getValues()) {
//	            		logger.debug(" "+value);
//	            	}
//	            	
//	            }
//	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dataFile != null)
				try {
					dataFile.close();
					
					time= System.currentTimeMillis();
//			        logger.debug("fichier NetCDF genere en "+(time-start)+" ms");
					System.out.println("fichier NetCDF genere en "+(time-start)+" ms");
		        } catch (IOException ioe) {
		        ioe.printStackTrace();
		        }
		}
		
		System.exit(0);
	}

}

