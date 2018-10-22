package info;

import java.util.ArrayList;

public class AppInfo extends Info{
	
	public ArrayList<PackageInfo> packages = new ArrayList<>();

    public String toString() {
      String str = "App: " + this.name + ":\n";
      str += "Lines: " + this.getLines() + "\n";
      for (PackageInfo pkg : packages) {
        str += pkg.toString() + "\n";
      }
      return str.substring(0, str.length() - 1);
    }

    public int getLines() {
      int count = 0;
      for (PackageInfo pkg : packages) {
        count += pkg.getLines();
      }
      return count;
    }

    public int getNbClasses() {
      int count = 0;
      for (PackageInfo pkg : packages) {
        count += pkg.getNbClasses();
      }
      return count;
    }

    public int getNbMethods() {
      int count = 0;
      for (PackageInfo pkg : packages) {
        count += pkg.getNbMethods();
      }
      return count;
    }

    public int getNbFields() {
      int count = 0;
      for (PackageInfo pkg : packages) {
        count += pkg.getNbFields();
      }
      return count;
    }

    public ArrayList<ClassInfo> getClasses() {
      ArrayList<ClassInfo> classes = new ArrayList<>();
      for (PackageInfo pkg : packages) {
        classes.addAll(pkg.getClasses());
      }
      return classes;
    }

    public ArrayList<MethodInfo> getMethods() {
      ArrayList<MethodInfo> methods = new ArrayList<>();
      for (PackageInfo pkg : packages) {
        methods.addAll(pkg.getMethods());
      }
      return methods;
    }
}
