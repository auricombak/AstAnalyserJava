package info;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInfo extends Info {
    public int nbLines;
    public int nbParameters;
    public ArrayList<String> calledMethods = new ArrayList<>();
    public ArrayList<String> calledMethodsSignature = new ArrayList<>();
    public ArrayList<MethodInfo> calledMethodInfos = new ArrayList<>();
    
    
    public HashMap<String, Integer> weightedCalls = new HashMap<String,Integer>();

    public String toString() {
      String str = "\t\t\t\tMethod: " + this.name + ":\n";
      str += "\t\t\t\tlines: " + this.nbLines + "\n";
      str += "\t\t\t\tparameters: " + this.nbParameters + "\n";
      str += "\n";
      return str.substring(0, str.length() - 1);
    }

    public void incrementCall(String calledMeth) {
    	int i=0;
    	if(weightedCalls.containsKey(calledMeth)) {
    		i = this.weightedCalls.get(calledMeth);
    		i++;
    	}else {
    		i=1;    		
    	}
    	weightedCalls.put(calledMeth, i);
    }
    
    public HashMap<String, Integer> getWeightedCalls(){
    	return weightedCalls;
    }
    
    public int getLines() {
      return nbLines;
    }
  }
