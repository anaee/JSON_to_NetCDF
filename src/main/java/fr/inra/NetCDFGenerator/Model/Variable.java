package fr.inra.NetCDFGenerator.Model;

import java.util.ArrayList;
import java.util.List;


//Variable au sens NetCDF
public class Variable {
	
	private String name, characteristic, entity, standard, qualifier, characteristicLabel, entityLabel, standardLabel, qualifLabel;
	private List<Context> contexts; 
	private List<VariableValue> values;
	private String dataType;
	
	public Variable() {
		name = new String();
		characteristic = new String();
		entity = new String();
		standard = new String();
		characteristicLabel = new String();
		entityLabel = new String();
		standardLabel = new String();
		qualifLabel = new String();
		qualifier = new String();
		dataType = new String();
		contexts = new ArrayList<Context>(); 
		values = new ArrayList<VariableValue>();
	}
	
	public Variable(String characteristic) {
		name = new String();
		this.characteristic = characteristic;
		entity = new String();
		standard = new String();
		characteristicLabel = new String();
		entityLabel = new String();
		standardLabel = new String();
		qualifLabel = new String();
		qualifier = new String();
		dataType = new String();
		contexts = new ArrayList<Context>();
		values = new ArrayList<VariableValue>();
	}
	
	public Variable(Variable variable) {
		this.name = new String(variable.getName());
		characteristic = new String(variable.getCharacteristic());
		entity = new String(variable.getEntity());
		standard = new String(variable.getStandard());
		characteristicLabel = new String(variable.getCharacteristicLabel());
		entityLabel = new String(variable.getEntityLabel());
		standardLabel = new String(variable.getStandardLabel());
		qualifLabel = new String(variable.getQualifLabel());
		qualifier = new String(variable.getQualifier());
		dataType = new String(variable.getDataType());
		contexts = new ArrayList<Context>(variable.getContexts());
		values = new ArrayList<VariableValue>(variable.getValues());
	}
	
	public String getName() {
		if(name.equals("")) {
			name = entityLabel.replace(" ", "-") +  "_" + characteristicLabel.replace(" ", "-") + "_" + standardLabel.replace(" ", "-");
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Context> getContexts() {
		return contexts;
	}
	public void setContexts(List<Context> contexts) {
		this.contexts.clear();		
		this.contexts.addAll(contexts);
	}
	public boolean addContext(Context context) {
		return contexts.add(context); 
	}
	
	public List<VariableValue> getValues() {
		return values;
	}
	public void setValues(List<VariableValue> values) {
		this.values = values;
	}
	
	
	public boolean addValue(VariableValue value) {
		return this.values.add(value);
	}
	public boolean addValues(List<VariableValue> values) {
		return this.values.addAll(values);
	}
	
	
	
	public int findContext(String contextName) {
		for (int i=0; i<contexts.size(); i++) {
			if (contexts.get(i).getName().equals(contextName)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean equals(Object obj) {
        if(obj!=null && obj instanceof Variable) {
            Variable tmpVar = (Variable) obj;
            if(tmpVar.getName()!=null && tmpVar.getName().equals(this.name)) {
                return true;
            }
        }
        return false;
    }

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public String getEntityLabel() {
		return entityLabel;
	}

	public void setEntityLabel(String entityLabel) {
		this.entityLabel = entityLabel;
	}

	public String getCharacteristicLabel() {
		return characteristicLabel;
	}

	public void setCharacteristicLabel(String characteristicLabel) {
		this.characteristicLabel = characteristicLabel;
	}
	
	public String getStandardLabel() {
		return standardLabel;
	}

	public void setStandardLabel(String standardLabel) {
		this.standardLabel = standardLabel;
	}
	
	public String getQualifLabel() {
		return qualifLabel;
	}

	public void setQualifLabel(String qualifLabel) {
		this.qualifLabel = qualifLabel;
	}
	
	public String getDataType() {
		boolean isDouble = true;
		boolean isInteger = true;
		if (dataType.equals("")) {
			for (VariableValue value : values) {
				if (!checkInteger(value.getValue())) {
					isInteger = false;
					if (!checkDouble(value.getValue())) {
						isDouble = false;
					}
				}
			}
			if (isInteger) {
				dataType = "Integer";
			} else if (isDouble) {
				dataType = "Double";
			} else {
				dataType = "String";
			}
		}
		return dataType;
	}
	
	private static boolean checkDouble(String str) {
		boolean isDouble = false;
		try {
			Double.parseDouble(str);
			isDouble = true;
		} catch (NumberFormatException e) {
		}
		return isDouble;
	}
	
	private static boolean checkInteger(String str) {
		boolean isInteger = false;
		try {
			Integer.parseInt(str);
			isInteger = true;
		} catch (NumberFormatException e) {
		}
		return isInteger;
	}
}

