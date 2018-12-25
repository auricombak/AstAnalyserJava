package dendrogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import info.AppInfo;
import info.ClassInfo;

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
			if(!e.getKey().equals(nodeL) && !e.getKey().equals(nodeR)) {
				//La valeur du nouveau croisement du noeud avec e = au max de l'ancien croisement de left avec e et right avec e.
				if(matrice.get(e.getKey()).get(nodeL) >= matrice.get(e.getKey()).get(nodeR)) {
					value = matrice.get(e.getKey()).get(nodeL);
				}else {
					value = matrice.get(e.getKey()).get(nodeR);
				}
				hm.put(e.getKey(), value);
				
				//On ajoute à matrice[e, hmRet] ou hmRet = à toute les valeurs de e dans la matrice + le nouveau noeud.
				HashMap <DendroElt, Double> hmRet = new HashMap<>();
				hmRet = matrice.get(e.getKey());
				hmRet.put(node, value);
				matrice.put(e.getKey(), hmRet);
			}
		}
		//On ajoute à matrice[node, hm] ou hm contient tout les tuple [e, value]]
		matrice.put(node, hm);
		
		matrice.remove(nodeL);
		matrice.remove(nodeR);
		
		for(Entry<DendroElt, HashMap<DendroElt, Double>> e : matrice.entrySet()) {
			HashMap<DendroElt, Double> hmRetrieved = new HashMap<>();
			for(Entry<DendroElt, Double> e2 : e.getValue().entrySet()) {
				
				if(!e2.getKey().equals(nodeL) && !e2.getKey().equals(nodeR) ) {
					hmRetrieved.put(e2.getKey(), e2.getValue());
				}
			}
			//matrice.remove(e.getKey());
			matrice.put(e.getKey(), hmRetrieved);
		}
	}

	public DendroNode getMax() {
		Double max = 0.0;
		DendroElt left = null;
		DendroElt right = null;
		for(Entry <DendroElt, HashMap<DendroElt, Double>> en : matrice.entrySet()) {		
			for(Entry<DendroElt, Double> en2 : en.getValue().entrySet()) {
				if(en2.getValue()>=max) {
				max = en2.getValue();
				left = en.getKey();
				right = en2.getKey();
				}
			}
		}
		return new DendroNode(left, right, max);
	}
	
	public void init() {

		for(int i = 0; i<this.classes.size(); i++) {
			HashMap<DendroElt, Double> hm = new HashMap<>();
			for(int j = 0; j<this.classes.size(); j++) {
				if(i!=j) {
					hm.put(classes.get(j), Parser.couplageClass(classes.get(i), classes.get(j)));
				}
			}
			matrice.put(classes.get(i), hm);
		}
		
//		for(Entry <DendroElt, HashMap<DendroElt, Double>> en : matrice.entrySet() ) {
//			for(Entry<DendroElt, Double> en2 : en.getValue().entrySet()) {
//				System.out.println("[ " + en.getKey().getName() + " - " + en2.getKey().getName() + " : " + en2.getValue() + "]");
//			}
//		}
		
	}
	
	public void start() {
		this.init();
		while(matrice.size()>1) {
			DendroNode nodeMax = getMax();
			this.addNode(nodeMax);
		}
		DendrogramPaintTest dp = new DendrogramPaintTest();

		for(Entry <DendroElt, HashMap<DendroElt, Double>> en : matrice.entrySet() ) {
			dp.start((DendroNode)en.getKey());
		}
		
	}
	
	
}
