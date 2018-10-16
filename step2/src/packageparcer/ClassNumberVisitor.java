package step2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ClassNumberVisitor extends ASTVisitor {
	
	private List<TypeDeclaration> classes = new ArrayList<>();
	
	public boolean visit(TypeDeclaration node) {
		classes.add(node);
		return super.visit(node);
	}
	
	public int getNbClass() {
		return classes.size();
	}
	
}
