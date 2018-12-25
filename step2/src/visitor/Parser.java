package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.*;

import info.AppInfo;
import info.ClassInfo;
import info.DendroGenerator;
import info.FileInfo;
import info.Info;
import info.MethodInfo;
import info.PackageInfo;

public class Parser {
	
	//Give here the projectPath you want to analyse
	
	//public static final String projectPath = "/home/oguerisck/Documents/Refactoring/AstAnalyserJava/step2";
	//public static final String projectPath = "/auto_home/lfaidherbe/workspace/Patern_Slate";
	public static final String projectPath = "/home/oguerisck/Bureau/Reutilisation/AstAnalyserJava/step2";
	
	public static final String projectSourcePath = projectPath + "/src";
	
	//Give here the jre path, "whereis java" can help you
	public static final String jrePath = "/usr/share/man/man1";
	
	public static AppInfo app;
	
	public static ArrayList<MutableNode> callGraphVisit;

	public static void main(String[] args) throws IOException {
		  // read java files
	    final File root = new File(projectSourcePath);

	    //print nbr of class, nbr of line, nbr of methods for the app
	    app = (AppInfo) getInfo(root);

	    //1. Nombre de classes de l’application.
	    System.out.println("Nombre de classes: " + app.getNbClasses());

	    //2. Nombre de lignes de code de l’application.
	    System.out.println("Nombre total de lignes de code: " + app.getLines());

	    //3. Nombre total de méthodes de l’application.
	    System.out.println("Nombre de méthodes: " + app.getNbMethods());

	    //4. Nombre total de packages de l’application.
	    System.out.println("Nombre de packages: " + app.packages.size());

	    //5. Nombre moyen de méthodes par classe.
	    System.out.println("Nombre moyen de méthodes par classe: " + ((float) app.getNbMethods()) / app.getNbClasses());

	    //6. Nombre moyen de lignes de code par méthode.
	    System.out.println("Nombre moyen de lignes de code par méthode: " + ((float) app.getLines()) / app.getNbMethods());

	    //7. Nombre moyen d’attributs par classe.
	    System.out.println("Nombre moyen d'attributs par classe: " + ((float) app.getNbFields()) / app.getNbClasses());

	    //8. Les 10% des classes qui possèdent le plus grand nombre de méthodes.
	    double percent = (double) app.getNbClasses() * 0.1;
	    int i = 0;
	    ArrayList<ClassInfo> classesOrderedByMethods = sortClassesByMethodNumber(app.getClasses());
	    ArrayList<ClassInfo> topClassesByMethods = new ArrayList<>();
	    while (i < percent - 1) {
	      topClassesByMethods.add(classesOrderedByMethods.get(i));
	      i++;
	    }
	    System.out.print("10% de classes avec le plus de méthodes: ");
	    for (ClassInfo cls : topClassesByMethods) {
	      System.out.print(cls.name + " | ");
	    }
	    System.out.println();

	    //9. Les 10% des classes qui possèdent le plus grand nombre d’attributs.
	    i = 0;
	    ArrayList<ClassInfo> classesOrderedByFields = sortClassesByFieldsNumber(app.getClasses());
	    ArrayList<ClassInfo> topClassesByFields = new ArrayList<>();
	    while (i < percent - 1) {
	      topClassesByFields.add(classesOrderedByFields.get(i));
	      i++;
	    }
	    System.out.print("10% de classes avec le plus d'attributs: ");
	    for (ClassInfo cls : topClassesByFields) {
	      System.out.print(cls.name + " | ");
	    }
	    System.out.println();

	    //10. Les classes qui font partie en même temps des deux catégories précédentes.
	    ArrayList<ClassInfo> winners = new ArrayList<>();
	    for (ClassInfo winner : app.getClasses()) {
	      if (topClassesByFields.contains(winner) && topClassesByMethods.contains(winner)) {
	        winners.add(winner);
	      }
	    }
	    System.out.print("Classes qui appartiennent aux deux catégories: ");
	    for (ClassInfo cls : winners) {
	      System.out.print(cls.name + " | ");
	    }
	    System.out.println();

	    //11. Les classes qui possèdent plus de X méthodes (la valeur de X est donnée).
	    System.out.print("Classes avec plus de 3 méthodes: ");
	    for (ClassInfo cls : app.getClasses()) {
	      System.out.print(cls.methods.size() > 3 ? cls.name + " | " : "");
	    }
	    System.out.println();

	    //12. Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe).
	    i = 0;
	    percent = (double) app.getNbMethods() * 0.1;
	    ArrayList<MethodInfo> methodsOrderedByParameterNumber = sortMethodsByParameterNumber(app.getMethods());
	    ArrayList<MethodInfo> topMethodsByParameters = new ArrayList<>();
	    while (i < percent - 1) {
	      topMethodsByParameters.add(methodsOrderedByParameterNumber.get(i));
	      i++;
	    }
	    System.out.print("10% de methodes avec le plus de paramètres: ");
	    for (MethodInfo meth : topMethodsByParameters) {
	      System.out.print(meth.name + " | ");
	    }
	    System.out.println();

	    //13. Le nombre maximal de paramètres par rapport à toutes les méthodes de l’application.
	    int max = 0;
	    for (MethodInfo meth : app.getMethods()) {
	      if (meth.nbParameters > max) {
	        max = meth.nbParameters;
	      }
	    }
	    System.out.print("Nombre maximal de paramètres dans une méthode: " + max + "\n\n");
	       
	    //__________________________________________________________________________________________//
	    
	    //Affiche le Graphe d'appels du programme
	    generateCallGraph();
	    
	    //Affiche les methodes pour une classes donnée, et pour une methode donnée les appels
	    //displayMethodCall();

	    //Pour deux classes données, calcule le couplage de celles-ci
	    //getCouplage2Classes();

	    //Génère un graphe de couplage pondéré
	    generateCouplageGraph();
	    
	    //Génère un dendogramme
	    DendroGenerator dg = new DendroGenerator(app);
	    dg.start();
	    
	    System.out.println("END");

	}

	public static void prepareCouplage() throws IOException {
	    //Graphe d'appels du programme
		MutableGraph g = mutGraph("callGraph").setStrict(true);
//		MethodInfo main = null;
//	    main = app.getMethodPerName("main");
//	    MutableNode methodNode = mutNode(main.getName()).add(Color.RED);
		callGraphVisit = new ArrayList<MutableNode>();
		for(MethodInfo mi : app.getMethods()) {
			MutableNode methodNode = mutNode(mi.getName()).add(Color.RED);
		    callGraphVisit.add(methodNode);
		    recursiveIncrementCalls(methodNode, mi);
		    g.add(methodNode);
		}       
	    Graphviz.fromGraph(g).width(1500).render(Format.SVG).toFile(new File("example/call_graph_meth.svg"));
	}
	
	public static void recursiveIncrementCalls(MutableNode node,MethodInfo method ) {
		if(method == null ) {
			return;
		}
		for(String called: method.calledMethods){

		    	MutableNode callNode = mutNode(called);
		    	node.addLink(callNode);
		    	method.incrementCall(called);
		    	
			if(!callGraphVisit.contains(mutNode(called))) {
				callGraphVisit.add(mutNode(called));
	    	  	String [] tokens = called.split("\\.");
	    	  	ClassInfo classFound = app.getClassPerName(tokens[0]);

	    	  	if(classFound != null ) {

	    	  		linkNodes(callNode, classFound.getMethodPerName(called));
	    	  	}
		    	
		        //System.out.println("\t" + called);
			}
	      }
	}
	
	public static void generateCouplageGraph() throws IOException {
		prepareCouplage();
		MutableGraph g = mutGraph("couplageGraph").setStrict(true);
		ArrayList<ClassInfo> classes = app.getClasses();
		for(int i = 0; i<classes.size();i++) {
			MutableNode classNodeA = mutNode(classes.get(i).getName());			
			for(int j = i+1; j<classes.size()-1;j++) {
				Double labelLink = couplageClass(classes.get(i), classes.get(j));
				if(labelLink > 0.0) {
					MutableNode classNodeB = mutNode(classes.get(j).getName());
					//classNodeA.addLink(classNodeB);
					classNodeA.addLink(to(classNodeB).with(Label.html(String.format( "%.2f",labelLink)+"%")));
				}
			}
			g.add(classNodeA);
		}
		 Graphviz.fromGraph(g).width(1500).render(Format.SVG).toFile(new File("example/couplage_graph.svg"));
	
	}

	public static void getCouplage2Classes() {
		System.out.println("Tapez le nom de la premiere classe à comparer");
		Scanner sc = new Scanner(System.in);
		String classToSearch = sc.nextLine();
		System.out.println("Tapez le nom de la deuxième classe à comparer");
		String classToSearch2 = sc.nextLine();
		ClassInfo comparableClassA = null;
		ClassInfo comparableClassB = null;

		for (ClassInfo cls : app.getClasses()) {
			if (cls.name.equals(classToSearch)) {
		        comparableClassA = cls;
		    }
		    if (cls.name.equals(classToSearch2)) {
		    	comparableClassB = cls;
			}
		}
		if(comparableClassA != null && comparableClassB != null) {
			couplageClass(comparableClassA,comparableClassB);
		}
	}
	
	public static double couplageClass(ClassInfo A, ClassInfo B) {
		Double nbCallA = 0.0, nbCallB = 0.0;
		Double nbCallAB = 0.0, nbCallBA = 0.0;
		String classNameA = A.getName();
		String classNameB = B.getName();
        for (MethodInfo meth : A.getMethods()) {
        	  HashMap<String, Integer> weightedCalls = meth.getWeightedCalls();
        	  for(Entry<String, Integer> e : weightedCalls.entrySet()) {
        		  String called = e.getKey();
        		  String [] tokens = called.split("\\.");
		        	if(!tokens[0].equals(classNameA)) {
			        	//System.out.println(tokens[0]);
			        	if(tokens[0].equals(classNameB)) {
			        		nbCallAB += e.getValue();
			        	}
			        	nbCallA += e.getValue();
			        	
		        	}
        	  }
        }
        for (MethodInfo meth : B.getMethods()) {

      	  HashMap<String, Integer> weightedCalls = meth.getWeightedCalls();
      	  for(Entry<String, Integer> e : weightedCalls.entrySet()) {
      		  String called = e.getKey();
      		  String [] tokens = called.split("\\.");
		        	if(!tokens[0].equals(classNameB)) {
			        	//System.out.println(tokens[0]);
			        	if(tokens[0].equals(classNameA)) {
			        		nbCallBA += e.getValue();
			        	}
			        	nbCallB += e.getValue();
			        	
		        	}
      	  }
        }
        
        Double result;
        if(nbCallA == 0.0 || nbCallB == 0.0) {
        	result = 0.0;
        }else {
            Double ratioA = nbCallAB/nbCallA;
            Double ratioB = nbCallBA/nbCallB;
            result = (nbCallAB+nbCallBA)*100/(nbCallA+nbCallB);
        }


        return result;
	}
	
	public static void displayMethodCall() {
	    while (true) {
		      System.out.println("Tapez le nom d'une classe à analyser");
		      Scanner sc = new Scanner(System.in);
		      String classToSearch = sc.nextLine();
		      for (ClassInfo cls : app.getClasses()) {
		        if (cls.name.equals(classToSearch)) {
		          System.out.println("Methodes dans la class " + classToSearch);
		          for (MethodInfo meth : cls.getMethods()) {
		            System.out.println(meth.name);
		          }
		        }
		      }
		      System.out.println();
		      System.out.println("Tapez le nom d'une méthode à analyser");
		      String methodToSearch = sc.nextLine();
		      for (MethodInfo meth : app.getMethods()) {
		        if (meth.name.equals(methodToSearch)) {
		          System.out.println("Methodes appelées par la méthode " + meth.name);
		          for (String called : meth.calledMethods) {
		            System.out.println(called);
		          }
		        }
		      }
		    }
	}
	
	public static void generateCallGraph() throws IOException {
	    //Graphe d'appels du programme
		MutableGraph g = mutGraph("callGraph").setStrict(true);
		
		MethodInfo main = null;
	    main = app.getMethodMain();
	    
	    MutableNode methodNode = mutNode(main.getName()).add(Color.RED);
	    
		callGraphVisit = new ArrayList<MutableNode>();
	    callGraphVisit.add(methodNode);
	    
	    linkNodes(methodNode, main);
	    
	    g.add(methodNode);    
	    Graphviz.fromGraph(g).width(1500).render(Format.SVG).toFile(new File("example/call_graph.svg"));
	}
	
	public static void linkNodes(MutableNode node,MethodInfo method ) {
		if(method == null ) {
			return;
		}
		for(String called: method.calledMethods){

		    	MutableNode callNode = mutNode(called);
		    	node.addLink(callNode);
		    	
			if(!callGraphVisit.contains(mutNode(called))) {
				callGraphVisit.add(mutNode(called));
	    	  	String [] tokens = called.split("\\.");
	    	  	ClassInfo classFound = app.getClassPerName(tokens[0]);

	    	  	if(classFound != null ) {

	    	  		linkNodes(callNode, classFound.getMethodPerName(called));
	    	  	}
			}
	      }
	}
		
	// Read all info from src and store informations
	public static Info getInfo(final File folder) throws IOException {
		if (folder.getName().equals("src")) {
		      AppInfo info = new AppInfo();
		      info.name = "app";
		      for (File fileEntry : folder.listFiles()) {
		        info.packages.add((PackageInfo) getInfo(fileEntry));
		      }
		      return info;

		    } else if (folder.isDirectory()) {
		      PackageInfo info = new PackageInfo();
		      info.name = folder.getName();
			      for (File fileEntry : folder.listFiles()) {
					info.files.add((FileInfo) getInfo(fileEntry));		    	  
			      }
		      return info;

		    } else if (folder.getName().endsWith(".java")) {
		      FileInfo info = new FileInfo();
		      String content = FileUtils.readFileToString(folder);
		      CompilationUnit parser = parse(content.toCharArray());
		      info.nbLines = parser.getLineNumber(parser.getLength() - 1);
		      for (TypeDeclaration type : TypeDeclarationVisitor.perform(parser)) {
		        ClassInfo clsInfo = new ClassInfo();
		        if (!(type.isInterface())) {
		          clsInfo.name = type.getName().toString();
		          clsInfo.nbLines = type.toString().split("\n").length;
		          for (MethodDeclaration meth : type.getMethods()) {
		            MethodInfo methodInfo = new MethodInfo();
		            methodInfo.name = clsInfo.name + "." + meth.getName().toString();
		            methodInfo.nbParameters = meth.parameters().size();
		            methodInfo.nbLines = meth.getBody().toString().split("\n").length;
		            for(MethodInvocation inv: MethodInvocationVisitor.perform(meth)) {
		              if(inv.resolveMethodBinding() != null) {		         
		            	  methodInfo.calledMethods.add(inv.resolveMethodBinding().getDeclaringClass().getName().toString() + "." + inv.getName().toString());
		              }else{
		            	  methodInfo.calledMethods.add(clsInfo.name +"."+ inv.getName().toString());
		              }  
		            }
		            clsInfo.methods.add(methodInfo);
		          }
		          clsInfo.nbFields = type.getFields().length;
		          info.classes.add(clsInfo);
		        }
		      }
		      return info;
		    }
		    return null;
	}

	//Create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
			
	public static ArrayList<ClassInfo> sortClassesByMethodNumber(ArrayList<ClassInfo> classes) {

		    for (int i = 0; i < classes.size() - 1; i++) {
		      for (int j = i + 1; j < classes.size(); j++) {
		        if (classes.get(i).getNbMethods() < classes.get(j).getNbMethods()) {
		          ClassInfo t = classes.get(i);
		          classes.set(i, classes.get(j));
		          classes.set(j, t);
		        }
		      }
		    }
		    return classes;
	}

	public static ArrayList<ClassInfo> sortClassesByFieldsNumber(ArrayList<ClassInfo> classes) {

		    for (int i = 0; i < classes.size() - 1; i++) {
		      for (int j = i + 1; j < classes.size(); j++) {
		        if (classes.get(i).getNbFields() < classes.get(j).getNbFields()) {
		          ClassInfo t = classes.get(i);
		          classes.set(i, classes.get(j));
		          classes.set(j, t);
		        }
		      }
		    }
		    return classes;
		  }

	public static ArrayList<MethodInfo> sortMethodsByParameterNumber(ArrayList<MethodInfo> methods) {
		    for (int i = 0; i < methods.size() - 1; i++) {
		      for (int j = i + 1; j < methods.size(); j++) {
		        if (methods.get(i).nbParameters < methods.get(j).nbParameters) {
		          MethodInfo t = methods.get(i);
		          methods.set(i, methods.get(j));
		          methods.set(j, t);
		        }
		      }
		    }
		    return methods;
		  }

}
