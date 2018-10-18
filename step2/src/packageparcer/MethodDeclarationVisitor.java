package packageparcer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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
	
	//Pour chaques methodes entre leurs nombre de lignes dans un tableau puis renvoie le tableau
	public List<Integer> getArrayNbLines() {
		List <Integer> nbLignes = new ArrayList<Integer>();
		for (MethodDeclaration eachmethod : methods) {
			nbLignes.add(countLines(eachmethod.getBody().toString()));
		}
		return nbLignes;
	}
	
	//Count the number of line in a string
	private static int countLines(String str){
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}
}
