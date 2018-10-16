package packageparcer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class PackageNumberVisitor extends ASTVisitor {
	private PackageDeclaration packageVisited ;
	
	public boolean visit(PackageDeclaration node) {
		packageVisited = node;
		return super.visit(node);
	}
	
	public PackageDeclaration getPackage() {
		return packageVisited;
	}
	
} 
