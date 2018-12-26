package info;


import java.util.ArrayList;

import dendrogram.DendroElt;
import dendrogram.DendrogramPaintPanel;
import dendrogram.Node;

public class ClassInfo extends Info implements DendroElt{
    public ArrayList<MethodInfo> methods = new ArrayList<>();
    public int nbLines;
    public int nbFields;
    public Boolean isAlone = true;

    public String toString() {
      String str = "\t\t\tClass: " + this.name + ":\n";
      str += "\t\t\tlines: " + this.nbLines + "\n";
      for (MethodInfo method : methods) {
        str += method.toString() + "\n";
      }
      str += "\n";
      return str.substring(0, str.length() - 1);
    }

    public int getLines() {
      return nbLines;
    }

    public int getNbMethods() {
      return methods.size();
    }

    public int getNbFields() {
      return nbFields;
    }

    public ArrayList<MethodInfo> getMethods() {
      return methods;
    }
    
    public MethodInfo getMethodPerName(String name) {
        for (MethodInfo method : methods) {
            if(method.getName().equals(name)) {
          	  return method;
            }
          }
          return null;
     }
    
    public MethodInfo getMethodMain() {
        for (MethodInfo method : methods) {
        	String methName = method.getName();
    	  	String [] tokens = methName.split("\\.");
            if(tokens[1].equals("main")) {
          	  return method;
            }
          }
          return null;
     }
    
	
	public Boolean isAlone() {
		return this.isAlone;
	}

	public Node createNode() {
		// TODO Auto-generated method stub
		return DendrogramPaintPanel.create(this.name);
	}

	public void setCoupled() {
		// TODO Auto-generated method stub
		this.isAlone = false;
	}
  }