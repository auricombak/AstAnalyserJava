package dendrogram;

import java.util.ArrayList;
import java.util.List;

import ihm.DendrogramPaintPanel;
import info.ClassInfo;
import info.MethodInfo;
import parser.Parser;

public class DendroNode implements DendroElt{
	private DendroElt leftChild;
	private DendroElt rightChild;
	private Double weight;
	
	public DendroNode(DendroElt childL, DendroElt childR, Double weight) {
		this.leftChild = childL;
		this.rightChild = childR;
		this.weight = weight;
	}
	
	public DendroElt getRight() {
		return rightChild;
	}
	
	public DendroElt getLeft() {
		return leftChild;
	}
	
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	//Pour l'affichage, crée un Node à partir d'un DendroNode donné.
	@Override
	public Node createNode() {
		// TODO Auto-generated method stub
		return DendrogramPaintPanel.create(this.leftChild.createNode() , this.rightChild.createNode(), this.weight);
	}
	
	//Pour un noeud, renvoie la liste de tout ses enfants. ( ClassInfo)
	public List<ClassInfo> getLeafs(){
		List<ClassInfo> cl = new ArrayList<>();
		if(leftChild instanceof ClassInfo) {
			cl.add((ClassInfo)leftChild);
		}else {
			for(DendroElt elt : ((DendroNode) leftChild).getLeafs()) {
				cl.add((ClassInfo) elt);
			}
		}
		if(rightChild instanceof ClassInfo) {
			cl.add((ClassInfo)rightChild);
		}else {
			for(DendroElt elt : ((DendroNode) rightChild).getLeafs()) {
				cl.add((ClassInfo) elt);
			}
		}
		
		return cl;
	}
	
	// S(n) calcule pour chaques classes de cet ensemble la moyenne des couplages des ces classes.
	public Double S(DendroNode dn) {
		List<ClassInfo> cl  = dn.getLeafs();
		int cmpt=0;
		double sumCouplage= 0.0;
		
		for(int i=0;i<cl.size();i++) {
			for(int j=i+1; j<cl.size();j++) {
				cmpt++;
				sumCouplage+=Parser.couplageClass(cl.get(i),cl.get(j));
			}
		}
		return sumCouplage/cmpt;
	}

	// S(n1,n2) calcule la moyenne de S(n1) avec S(n2).
	public Double S(DendroNode dn1, DendroNode dn2) {		
		Double sumCouplage = S(dn1);
		Double sumCouplage2 = S(dn2);
		Double avgCouplage = (sumCouplage + sumCouplage2) / 2;
		return avgCouplage;
		
	}
	


	// Si S(n) > S(n.child1,n.child2) on renvoie la liste des methodes de n sinon on renvoie une liste vide
	public List<ClassInfo> isComposante(DendroNode dn) {
		
		List<ClassInfo> cl  = dn.getLeafs();

		if(dn.leftChild instanceof DendroNode && dn.rightChild instanceof DendroNode) {
	
			Double sumCouplage = S(dn);
			Double sumCouplageChilds = S((DendroNode)dn.leftChild, (DendroNode)dn.rightChild);

			
			if(sumCouplage > sumCouplageChilds) {
				System.out.println("Composant");
				return cl;
			}
		}
		System.out.println("Pas Composant");
		return new ArrayList<ClassInfo>();
		
	}
	
	//On parcours le noeud et pour chaque noeud on calcule isComposante(n) et on ajoute le résultat à un String pour affichage.
	public String calculateSubNodes() {
		String text = "";
		for(ClassInfo ci : isComposante(this)){
			text+= ci.getName();
		}
		text+="\n";
		if(leftChild instanceof DendroNode) {

			text+=((DendroNode) leftChild).calculateSubNodes();

		}
		if(rightChild instanceof DendroNode) {
			text+=((DendroNode) rightChild).calculateSubNodes();
		}
		return text;
	}
		
	
}
