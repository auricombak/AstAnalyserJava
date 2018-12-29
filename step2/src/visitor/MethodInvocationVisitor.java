package visitor;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
	List<MethodInvocation> methods = new ArrayList<MethodInvocation>();
	List<SuperMethodInvocation> superMethods = new ArrayList<SuperMethodInvocation>();
	List<ClassInstanceCreation> newConstructors = new ArrayList<ClassInstanceCreation>();
	
	public boolean visit(MethodInvocation node) {
		methods.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		superMethods.add(node);
		return super.visit(node);
	}
	
	public boolean visit(ClassInstanceCreation node) {
		newConstructors.add(node);
		return super.visit(node);
	}


	public static List<MethodInvocation> perform(ASTNode node) {
		MethodInvocationVisitor finder = new MethodInvocationVisitor();
		node.accept(finder);
		return finder.getMethods();
	}
	
	public static List<ClassInstanceCreation> newperform(ASTNode node) {
		MethodInvocationVisitor finder = new MethodInvocationVisitor();
		node.accept(finder);
		return finder.getNew();
	}
	
	public static List<SuperMethodInvocation> superPerform(ASTNode node) {
		MethodInvocationVisitor finder = new MethodInvocationVisitor();
		node.accept(finder);
		return finder.getSuperMethod();
	}
	
	public List<MethodInvocation> getMethods() {
		return methods;
	}
	
	public List<ClassInstanceCreation> getNew() {
		return newConstructors;
	}
	
	public List<SuperMethodInvocation> getSuperMethod() {
		return superMethods;
	}
}
