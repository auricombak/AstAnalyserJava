package info;

import java.util.ArrayList;

public class ClassInfo extends Info {
    public ArrayList<MethodInfo> methods = new ArrayList<>();
    public int nbLines;
    public int nbFields;

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

  }