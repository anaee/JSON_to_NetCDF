package fr.inra.NetCDFGenerator.Model;

import java.util.ArrayList;
import java.util.List;

public class Context {
	
	private String name, characteristic, entity, standard, characteristicLabel, entityLabel, standardLabel, variableName;
	private List<String> values;
	private boolean isDimension;
	private List<Integer> correlatedContexts;
	
	
	public Context() {
		name = new String();
		characteristic = new String();
		entity = new String();
		standard = new String();
		values = new ArrayList<String>();
		isDimension = false;
		correlatedContexts = new ArrayList<Integer>();
	}
	
	public Context(Context context) {
		name = new String(context.getName());
		characteristic = new String(context.getCharacteristic());
		entity = new String(context.getEntity());
		standard = new String(context.getStandard());
		characteristicLabel = new String(context.getCharacteristicLabel());
		entityLabel = new String(context.getEntityLabel());
		standardLabel = new String(context.getStandardLabel());
		variableName = new String(context.getVariableName());
		values = new ArrayList<String>(context.getValues());
		isDimension = false;
		correlatedContexts = new ArrayList<Integer>();
	}
	
	public Context(String variableName, String entity, String characteristic, String standard, String value) {
		this.name = variableName.substring(variableName.lastIndexOf("/")+1)+entity.substring(entity.indexOf("#")) + characteristic.substring(characteristic.indexOf("#")) + standard.substring(standard.indexOf("#")) ;
		this.characteristic = characteristic;
		this.entity = entity;
		this.standard = standard;
		values = new ArrayList<String>();
		this.addValue(value);
		isDimension = false;
		correlatedContexts = new ArrayList<Integer>();
	}
	
	public Context(String variableName, String entity, String characteristic, String standard, String entityLabel, String characteristicLabel, String standardLabel, String value) {
		this.name = variableName.substring(variableName.lastIndexOf("/")+1) + "__" + entityLabel.replace(" ", "-") +  "_" + characteristicLabel.replace(" ", "-") + "_" + standardLabel.replace(" ", "-");
		this.characteristic = characteristic;
		this.entity = entity;
		this.standard = standard;
		this.characteristicLabel = characteristicLabel;
		this.entityLabel = entityLabel;
		this.standardLabel = standardLabel;
		this.variableName = variableName;
		values = new ArrayList<String>();
		this.addValue(value);
		isDimension = false;
		correlatedContexts = new ArrayList<Integer>();
	}
	
	public String getName() {
		if(name.equals("")) {
			name = variableName.substring(variableName.lastIndexOf("/")+1) + "__" + entityLabel.replace(" ", "-") +  "_" + characteristicLabel.replace(" ", "-") + "_" + standardLabel.replace(" ", "-");
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
	public void addValue(String value) {
		this.values.add(value);
	}
	public void addValue(int index, String value) {
		this.values.add(index, value);
	}
	public void addValues(List<String> values) {
		this.values.addAll(values);
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
	
	
	public int hashCode() {
	    return name.hashCode();
	}
	
	public boolean equals(Object obj) {
        if(obj!=null && obj instanceof Context) {
            Context tmpCont = (Context) obj;
            if(tmpCont.name!=null && tmpCont.name.equals(this.name)) {
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
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	public boolean isDimension() {
		return isDimension;
	}
	public void setAsDimension() {
		isDimension = true;
	}
	
	public void addCorrelatedContext(Integer index) {
		this.correlatedContexts.add(index);
	}
	
	public List<Integer> getCorrelatedContexts() {
		return correlatedContexts;
	}
}
