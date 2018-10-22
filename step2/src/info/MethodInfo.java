package info;

import java.util.ArrayList;

public class MethodInfo extends Info {
    public int nbLines;
    public int nbParameters;
    public ArrayList<String> calledMethods = new ArrayList<>();

    public String toString() {
      String str = "\t\t\t\tMethod: " + this.name + ":\n";
      str += "\t\t\t\tlines: " + this.nbLines + "\n";
      str += "\t\t\t\tparameters: " + this.nbParameters + "\n";
      str += "\n";
      return str.substring(0, str.length() - 1);
    }

    public int getLines() {
      return nbLines;
    }
  }
