package packageparcer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Parser {
	
	//Give here the projectPath you want to analyse
	public static final String projectPath = "/home/oguerisck/Documents/Refactoring/AstAnalyserJava/step2";
	public static final String projectSourcePath = projectPath + "/src";
	
	//Give here the jre path, "whereis java" can help you
	public static final String jrePath = "/usr/share/man/man1";

	public static void main(String[] args) throws IOException {

		// read java files
		int nbClasses =0, 
			nbMethodes = 0, 
			nbLignes = 0;
		HashSet<String> packages = new HashSet<>(); 
		ArrayList <Integer> methodsRep = new ArrayList<>();
		ArrayList <Integer> linesMethRep = new ArrayList<>();
		ArrayList <Integer> attributesRep = new ArrayList<>();
		TreeMap<Integer, Name> typeMethods = new TreeMap<>();
		TreeMap<Integer, Name> typeAttributes = new TreeMap<>();
		
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);


		//
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			//System.out.println(content + "@");

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
		getTenPerCent( typeMethods );
		
		System.out.println("\n"+"Les 10 % des classes contenant le plus d'attributs");
		getTenPerCent( typeAttributes );
		
	}

	

	
	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}
	
	// create AST
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
	
	// Display 10% of most value in TreeMap
	public static void getTenPerCent( TreeMap<Integer, Name> tm ){
		
		int sizeT = tm.size();
		int tenPerCent = (int)Math.round(sizeT/10.0);
		if(tenPerCent == 0) {tenPerCent = 1;}
		Map<Integer, Name> rm = tm.descendingMap();
		int count = 0;

		for (Map.Entry<Integer,Name> entry:rm.entrySet()) {
		     if (count >= tenPerCent) break;

		     System.out.println("     N° "+ count + 1 + " ->  Class " + entry.getValue() + " | Nb Class : " +entry.getKey() );
		     count++;
		  }
		
		
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
		return p.getName().toString();
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
		
	

}
