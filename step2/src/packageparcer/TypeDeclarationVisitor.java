package packageparcer;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDeclarationVisitor extends ASTVisitor {
	List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
	
	public boolean visit(TypeDeclaration node) {
		types.add(node);
		return super.visit(node);
	}
	
	public List<TypeDeclaration> getTypes() {
		return types;
	}
	
	public int getNbClass() {
		return types.size();
	}
	
	//Return an List of Integer, where the size of the List => nb of classes found
	public List<Integer> getArrayNbMethods() {
		List <Integer> nbMethodes = new ArrayList<Integer>();
		for (TypeDeclaration eachClass : types) {
			nbMethodes.add(eachClass.getMethods().length);
		}
		return nbMethodes;
	}
	
	//Return an List of Integer, where the size of the List => nb of attribute found in each class
	public List<Integer> getArrayNbAttributes() {
		List <Integer> nbAttributs = new ArrayList<Integer>();
		for (TypeDeclaration eachClass : types) {
			nbAttributs.add(eachClass.getFields().length);
		}
		return nbAttributs;
	}

	public TreeMap<Integer, Name> getTreeTypeNbMethods(){
		TreeMap<Integer, Name> typeMethods = new TreeMap<>();
		for (TypeDeclaration eachClass : types) {
			typeMethods.put(eachClass.getMethods().length, eachClass.getName());
		}
		return typeMethods;
	}
}
