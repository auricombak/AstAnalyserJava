package visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
	List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
	public boolean visit(MethodDeclaration node) {
		methods.add(node);
		return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	
	public static List<MethodDeclaration> perform(ASTNode node) {
		  MethodDeclarationVisitor finder = new MethodDeclarationVisitor();
		  node.accept(finder);
		  return finder.getMethods();
	}
	
	
	//Pour chaques methodes entre leurs nombre de lignes dans un tableau puis renvoie le tableau
	public List<Integer> getArrayNbLines() {
		List <Integer> nbLignes = new ArrayList<Integer>();
		for (MethodDeclaration eachmethod : methods) {
			nbLignes.add(countLines(eachmethod.getBody().toString()));
		}
		return nbLignes;
	}
	
	//Pour chaques methodes entre leurs nombre de lignes dans un treemap
	public TreeMap<Integer, Name> getTreeMethodSize(){
		TreeMap<Integer, Name> methodLines = new TreeMap<>();
		for (MethodDeclaration eachMethod : methods) {
			methodLines.put(countLines(eachMethod.getBody().toString()), eachMethod.getName());
		}
		return methodLines;
	}
	
	//Pour chaques methodes entre leurs nombre de lignes dans un tableau puis renvoie le tableau
	public List<Integer> getArrayNbParams() {
		List <Integer> nbParams = new ArrayList<Integer>();
		for (MethodDeclaration eachmethod : methods) {

			nbParams.add(eachmethod.parameters().size());
		}
		return nbParams;
	}
	
	//Count the number of line in a string
	private static int countLines(String str){
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}
}
