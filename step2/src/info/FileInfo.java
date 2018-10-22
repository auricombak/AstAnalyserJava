package info;

import java.util.ArrayList;

public class FileInfo extends Info {
    public ArrayList<ClassInfo> classes = new ArrayList<>();
    public int nbLines;

    public String toString() {
      String str = "";
      for (ClassInfo cls : classes) {
        str += cls.toString() + "\n";
      }
      return str.substring(0, str.length() - 1);
    }

    public int getLines() {
      return nbLines;
    }

    public int getNbClasses() {
      return classes.size();
    }

    public int getNbMethods() {
      int count = 0;
      for (ClassInfo cls : classes) {
        count += cls.getNbMethods();
      }
      return count;
    }

    public int getNbFields() {
      int count = 0;
      for (ClassInfo cls : classes) {
        count += cls.getNbFields();
      }
      return count;
    }

    public ArrayList<ClassInfo> getClasses() {
      return classes;
    }

    public ArrayList<MethodInfo> getMethods() {
      ArrayList<MethodInfo> methods = new ArrayList<>();
      for (ClassInfo cls : classes) {
        methods.addAll(cls.getMethods());
      }
      return methods;
    }
  }