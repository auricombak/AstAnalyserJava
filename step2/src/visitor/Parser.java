package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

import info.AppInfo;
import info.ClassInfo;
import info.FileInfo;
import info.Info;
import info.MethodInfo;
import info.PackageInfo;

public class Parser {
	
	//Give here the projectPath you want to analyse
	//public static final String projectPath = "/home/oguerisck/Documents/Refactoring/AstAnalyserJava/step2";
	//public static final String projectPath = "/auto_home/lfaidherbe/workspace/Patern_Slate";
	public static final String projectPath = "/home/oguerisck/Documents/Refactoring/AstAnalyserJava/step2";
	
	public static final String projectSourcePath = projectPath + "/src";
	
	//Give here the jre path, "whereis java" can help you
	public static final String jrePath = "/usr/share/man/man1";
	
	public static AppInfo app;

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

	    //Graphe d'appels du programme
	    System.out.println("Graphe d'appels du programme:");
	    for(MethodInfo meth: app.getMethods()){
	      System.out.println(meth.name + ":");
	      for(String called: meth.calledMethods){
	        System.out.println("\t" + called);
	      }
	    }
	    System.out.println();

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
		/* read java files
		int nbClasses =0, 
			nbMethodes = 0, 
			nbLignes = 0;
		HashSet<String> packages = new HashSet<>(); 
		ArrayList <Integer> methodsRep = new ArrayList<>();
		ArrayList <Integer> linesMethRep = new ArrayList<>();
		ArrayList <Integer> attributesRep = new ArrayList<>();
		TreeMap<Integer, Name> typeMethods = new TreeMap<>();
		TreeMap<Integer, Name> typeAttributes = new TreeMap<>();
		TreeMap<Integer, Name> methodLines = new TreeMap<>();
		HashSet<Integer> nbParams = new HashSet<>(); 
		
		final File folder = new File(projectSourcePath);

		
		//ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		//
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());

			nbLignes += countLines(content);
			
			//for each file visited, count the number of classes
			nbClasses += NbClassesPerFile(parse);
			
			//for each file visited, count the number of methods
			nbMethodes += NbMethodsPerFile(parse);
			
			//Add package name in HashSet to avoid duplicates
			String pack = printPackageInfo(parse);
			packages.add(printPackageInfo(parse));
			
			//Merge array of number of methods per class per file in the methodsRep ( repartition array ) 
			for(Integer j: NbMethodesPerClassPerFile(parse)) {
				methodsRep.add(j);
			}
			
			//Merge array of number of lines per methods per file in the linesMethRep ( repartition array ) 
			for(Integer j: NbLinesPerMethodsPerFile(parse)) {
				linesMethRep.add(j);
			}
			
			//Merge array of number of lines per methods per file in the attributesRep ( repartition array ) 
			for(Integer j: NbAttributesPerClassPerFile(parse)) {
				attributesRep.add(j);
			}
			
			typeMethods.putAll(TreeClassNbMethods(parse));
			
			typeAttributes.putAll(TreeClassNbAttributes(parse));
			
			methodLines.putAll(TreeClassMethodsNbLines(parse));
			
			//Merge array of number of lines per methods per file in the linesMethRep ( repartition array ) 
			for(Integer j: NbParamsPerMethodsPerFile(parse)) {
				nbParams.add(j);
			}

		}
		
		
		//print nb classes
		System.out.println("Il y a : " + nbClasses + " Classe(s)");
		
		//print nb methodes
		System.out.println("Il y a : " + nbMethodes + " Méthode(s)");
		
		//print nb lines
		System.out.println("Il y a : " + nbLignes + " Ligne(s) de code");
		
		//print nb packages non vides
		System.out.println("Il y a : " + packages.size()  + " Package(s)");
		
		//print method average per class
		System.out.println("Il y a une moyenne de " + calculateAverage(methodsRep)  + " méthodes par classes");
		
		//print method average per class
		System.out.println("Il y a une moyenne de " + calculateAverage(linesMethRep)  + " lignes par methodes");
		
		//print method average per class
		System.out.println("Il y a une moyenne de " + calculateAverage(attributesRep)  + " attributs par classe");
		
		System.out.println("\n"+"Les 10 % des classes contenant le plus de methodes");
		System.out.println(getTenPerCent( typeMethods ).toString());
		
		System.out.println("\n"+"Les 10 % des classes contenant le plus d'attributs");
		System.out.println(getTenPerCent( typeAttributes ).toString());
		
		System.out.println("\n"+"Les classes faisant partie des 10% contenant le plus d'attributs & 10% contenant le plus de methodes");
		printIntersectionMap(getTenPerCent( typeAttributes ), getTenPerCent( typeMethods ));
		
		System.out.println();
		printClassMostXMeth(typeMethods, 4);
		
		System.out.println("\n"+"Les 10 % des methodes contenant le plus de lignes");
		System.out.println(getTenPerCent( methodLines ).toString());
		
		int paramMax = 0;
		for (Integer i : nbParams) {
		    if(i > paramMax){
		    	paramMax = i;
		    }
		}
		System.out.println("\n"+"Le nombre max de paramètres par rapport à toutes les methodes : " + paramMax);
		*/
	}

	
	//##################### read all java files from specific folder ###################
	
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
		            methodInfo.name = meth.getName().toString();
		            methodInfo.nbParameters = meth.parameters().size();
		            methodInfo.nbLines = meth.getBody().toString().split("\n").length;
		            for(MethodInvocation inv: MethodInvocationVisitor.perform(meth)) {
		              methodInfo.calledMethods.add(inv.getName().toString());
		            }
		            clsInfo.methods.add(methodInfo);
		          }
		          clsInfo.nbFields = type.getFields().length;
		        }
		        info.classes.add(clsInfo);
		      }
		      return info;
		    }
		    return null;
	}

	//###################################################################################
	
	//################################### create AST ####################################
	

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
	
	//###################################################################################
	
	
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	/* Display 10% of most value in TreeMap
	public static Map<Integer, Name> getTenPerCent( TreeMap<Integer, Name> tm ){
		
		int sizeT = tm.size();
		int tenPerCent = (int)Math.round(sizeT/10.0);
		if(tenPerCent == 0) {tenPerCent = 1;}
		Map<Integer, Name> rm = tm.descendingMap();
		int count = 0;
		Map<Integer, Name> rm2 = new TreeMap<>();
		for (Map.Entry<Integer,Name> entry:rm.entrySet()) {
		     if (count >= tenPerCent) break;
		     rm2.put(entry.getKey(), entry.getValue());
		     //System.out.println("     N° "+ count + 1 + " ->  Class " + entry.getValue() + " | Nb Class : " +entry.getKey() );
		     count++;
		  }
		return rm2;
		
		
	}
	
	//Count the number of line in a string
	private static int countLines(String str){
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}

	//calculate average of an List
	private static double calculateAverage(List <Integer> marks) {
		  Integer sum = 0;
		  if(!marks.isEmpty()) {
		    for (Integer mark : marks) {
		        sum += mark;
		    }
		    return sum.doubleValue() / marks.size();
		  }
		  return sum;
	}

	//Print the intersection of 2 Map
	public static void printIntersectionMap(Map<Integer, Name> mapAtt, Map<Integer, Name> mapMet ){
		for (Map.Entry<Integer,Name> entry:mapAtt.entrySet()) {
		     if (mapMet.containsValue(entry.getValue())){
		    	 System.out.println("     Classe " + entry.getValue() );

		     }
		    
		  }
		
	}

	//Print the intersection of 2 Map
	public static void printClassMostXMeth(Map<Integer, Name> mapMet, int x ){
		System.out.println("Classes ayant plus ou autant de " +x+" methodes");
		for (Map.Entry<Integer,Name> entry:mapMet.entrySet()) {
		     if (entry.getKey() >= x){
		    	 System.out.println("     Classe " + entry.getValue() + " Avec " + entry.getKey() + " methodes" );

		     }		    
		  }		
	}
	
	// navigate into class
	public static List<Integer> NbAttributesPerClassPerFile(CompilationUnit parse) {
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);

		// System.out.println("Il y a : " + visitor.getNbClass() + " classes");
		return visitor.getArrayNbAttributes();

	}
	
	// navigate into class
	public static int NbClassesPerFile(CompilationUnit parse) {
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);

		// System.out.println("Il y a : " + visitor.getNbClass() + " classes");
		return visitor.getNbClass();

	}
	
	// navigate into class
	public static List<Integer> NbMethodesPerClassPerFile(CompilationUnit parse) {
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);

		// System.out.println("Il y a : " + visitor.getNbClass() + " classes");
		return visitor.getArrayNbMethods();

	}
		
	// navigate into class
	public static TreeMap<Integer, Name> TreeClassNbMethods(CompilationUnit parse) {
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);

		return visitor.getTreeTypeNbMethods();

	}
	
	// navigate into class
	public static TreeMap<Integer, Name> TreeClassNbAttributes(CompilationUnit parse) {
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);

		return visitor.getTreeTypeNbAttributes();

	}
	
	// navigate into method
	public static TreeMap<Integer, Name> TreeClassMethodsNbLines(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		return visitor.getTreeMethodSize();

	}
	
	// navigate into methods
	public static int NbMethodsPerFile(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		// System.out.println("Il y a : " + visitor.getNbClass() + " classes");
		return visitor.getMethods().size();

	}
	
	// navigate into methods
	public static List<Integer> NbLinesPerMethodsPerFile(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		// System.out.println("Il y a : " + visitor.getNbClass() + " classes");
		return visitor.getArrayNbLines();

	}	
	
	// navigate into methods
	public static List<Integer> NbParamsPerMethodsPerFile(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		return visitor.getArrayNbParams();

	}	
	
	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}

	}
	
	// navigate package information
	public static String printPackageInfo(CompilationUnit parse) {
		PackageVisitor visitor = new PackageVisitor();
		parse.accept(visitor);

		PackageDeclaration p = visitor.getPackage();
		try{
		return p.getName().toString();
		}finally{
			return "PAs de package";
		}
	}

	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ " variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	// navigate method invocations inside method
	public static void printMethodInvocationInfo(CompilationUnit parse) {

			MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
			parse.accept(visitor1);
			for (MethodDeclaration method : visitor1.getMethods()) {

				MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
				method.accept(visitor2);

				for (MethodInvocation methodInvocation : visitor2.getMethods()) {
					System.out.println("method " + method.getName() + " invoc method "
							+ methodInvocation.getName());
				}

			}
		}
		
	
*/
}
