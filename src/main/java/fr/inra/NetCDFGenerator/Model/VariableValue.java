package fr.inra.NetCDFGenerator.Model;

import java.util.ArrayList;
import java.util.List;

public class VariableValue implements Comparable<VariableValue> {
	private String value;
	List<Integer>  contextsIndexes;
	
	public VariableValue() {
		value = new String();
		contextsIndexes = new ArrayList<Integer>();
	}
	
	public VariableValue(String value) {
		this.value = new String(value);
		contextsIndexes = new ArrayList<Integer>();
	}
	
	public VariableValue(VariableValue variableValue) {
		value = new String(variableValue.getValue());
		contextsIndexes = new ArrayList<Integer>(variableValue.getContextsIndexes());
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public List<Integer> getContextsIndexes() {
		return contextsIndexes;
	}
	
	public int[] getIndexes() {
		int[] indexTab = new int[contextsIndexes.size()];
		for (int i=0; i<contextsIndexes.size(); i++) {
			indexTab[i]=contextsIndexes.get(i);
		}
		return indexTab;
	}
	
	public void setContextsIndexes(List<Integer> contextsIndexes) {
		this.contextsIndexes = new ArrayList<Integer>(contextsIndexes);
	}
	
	public void setIndex(Integer contextIndex, Integer valueIndex) {
		contextsIndexes.set(contextIndex, valueIndex);
	}
	
	public void addIndex(Integer valueIndex) {
		contextsIndexes.add(valueIndex);
	}
	
	public void addIndex(Integer contextIndex, Integer valueIndex) {
		contextsIndexes.add(contextIndex, valueIndex);
	}

	@Override
	public int compareTo(VariableValue variableValue2) {
		for (int i=0; i<Math.min(this.getContextsIndexes().size(), variableValue2.getContextsIndexes().size()); i++) {
    		if(this.getContextsIndexes().get(i)<variableValue2.getContextsIndexes().get(i)) {
    			return -1;
    		} else if (this.getContextsIndexes().get(i)>variableValue2.getContextsIndexes().get(i)) {
    			return 1;
    		}
    	}
		return 0;
	}
	
}
