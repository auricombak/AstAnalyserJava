package info;

import java.util.ArrayList;

public class PackageInfo extends Info {
    public ArrayList<FileInfo> files = new ArrayList<>();

    public String toString() {
      String str = "\tPackage: " + this.name + ":\n";
      for (FileInfo file : files) {
        str += file.toString() + "\n";
      }
      str += "\n\n";
      return str.substring(0, str.length() - 1);
    }

    public int getLines() {
      int count = 0;
      for (FileInfo file : files) {
        count += file.getLines();
      }
      return count;
    }

    public int getNbClasses() {
      int count = 0;
      for (FileInfo file : files) {
        count += file.getNbClasses();
      }
      return count;
    }

    public int getNbMethods() {
      int count = 0;
      for (FileInfo file : files) {
        count += file.getNbMethods();
      }
      return count;
    }

    public int getNbFields() {
      int count = 0;
      for (FileInfo file : files) {
        count += file.getNbFields();
      }
      return count;
    }

    public ArrayList<ClassInfo> getClasses() {
      ArrayList<ClassInfo> classes = new ArrayList<>();
      for (FileInfo file : files) {
        classes.addAll(file.getClasses());
      }
      return classes;
    }

    public ArrayList<MethodInfo> getMethods() {
      ArrayList<MethodInfo> methods = new ArrayList<>();
      for (FileInfo file : files) {
        methods.addAll(file.getMethods());
      }
      return methods;
    }
    
    public ClassInfo getClassPerName(String name) {
        for (FileInfo file : files) {
          if(file.getClassPerName(name) != null) {
        	  return file.getClassPerName(name);
          }
        }
        return null;
    }
    
    public MethodInfo getMethodPerName(String name) {
        for (FileInfo file : files) {
          if(file.getClassPerName(name) != null) {
        	  return file.getMethodPerName(name);
          }
        }
        return null;
    }
    
  }