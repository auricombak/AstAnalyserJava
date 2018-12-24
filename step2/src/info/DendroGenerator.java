package info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import visitor.Parser;

public class DendroGenerator {
	
	private ArrayList<ClassInfo> classes;
	private HashMap<DendroElt, HashMap<DendroElt, Double>> matrice;
	
	public DendroGenerator(AppInfo app) {
		this.matrice = new HashMap<>();
		this.classes = app.getClasses();
	}
	
	//Ajoute un noeud au Dendro
	public void addNode(DendroNode node) {
		Double value = node.getWeight();
		DendroElt nodeL= node.getLeft();
		DendroElt nodeR= node.getRight();
		HashMap <DendroElt, Double> hm = new HashMap<>();
		for(Entry<DendroElt, HashMap<DendroElt, Double>> e : matrice.entrySet()) {
			if(!e.getKey().equals(nodeL) || !e.getKey().equals(nodeR)) {
				if(matrice.get(e.getKey()).get(nodeL)>=matrice.get(e.getKey()).get(nodeR)) {
					value = matrice.get(e.getKey()).get(nodeL);
				}else {
					value = matrice.get(e.getKey()).get(nodeR);
				}
				hm.put(e.getKey(), value);
					
			}
		}
		matrice.put(node, hm);
		
		matrice.remove(nodeL);
		matrice.remove(nodeR);
		
	}

	public DendroNode getMax() {
		Double max = 0.0;
		DendroElt left = null;
		DendroElt right = null;
		for(Entry <DendroElt, HashMap<DendroElt, Double>> en : matrice.entrySet()) {		
			for(Entry<DendroElt, Double> en2 : en.getValue().entrySet()) {
				max = en2.getValue();
				left = en.getKey();
				right = en2.getKey();
			}
		}
		
		return new DendroNode(left, right, max);
	}
	
	public void init() {
		HashMap<DendroElt, Double> hm = new HashMap<>();
		for(int i = 0; i<this.classes.size(); i++) {
			for(int j = i+1; j<this.classes.size()-1; j++) {
				hm.put(classes.get(j), Parser.couplageClass(classes.get(i), classes.get(j)));
			}
			matrice.put(classes.get(i), hm);
		}
		
		
	}
	
	public void start() {
		while(matrice.size()>1) {
			this.init();
			DendroNode nodeMax = getMax();
			this.addNode(nodeMax);
		}
		
	}
	
	
}
