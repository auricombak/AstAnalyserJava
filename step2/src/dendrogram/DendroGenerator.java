package dendrogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Main.Parser;
import info.AppInfo;
import info.ClassInfo;

import java.util.TreeMap;

public class DendroGenerator {
	
	private ArrayList<ClassInfo> classes;
	private HashMap<DendroElt, HashMap<DendroElt, Double>> matrice;
	
	public DendroGenerator(AppInfo app) {
		this.matrice = new HashMap<>();

		//Pour réccupérer uniquement les classes couplées pour le calcul du dendrogramme
		//Sinon faire juste : this.classes = app.getClasses();
		classes = new ArrayList<>();
		for(ClassInfo ci : app.getClasses()) {
			if(!ci.isAlone()) {
				classes.add(ci);
			}
		}
	}
	
	//Ajoute un nouveau noeud au Dendro, et supprime ses fils de la matrice 
	public void addNode(DendroNode node) {
		
		Double value = node.getWeight();
		
		DendroElt nodeL= node.getLeft();
		DendroElt nodeR= node.getRight();
		
		HashMap <DendroElt, Double> hm = new HashMap<>();
		
		for(Entry<DendroElt, HashMap<DendroElt, Double>> e : matrice.entrySet()) {
			if(!e.getKey().equals(nodeL) && !e.getKey().equals(nodeR)) {
				//La valeur du nouveau croisement du noeud avec e = au max de l'ancien croisement de left avec e et right avec e.
				
					value = (matrice.get(e.getKey()).get(nodeL) + matrice.get(e.getKey()).get(nodeR)) /2;

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
		
		//On supprime les fils du nouveau noeud, puis tout les tuples (x,y) ou y coorespond à un fils du nouveau noeud
		matrice.remove(nodeL);
		matrice.remove(nodeR);
		
		for(Entry<DendroElt, HashMap<DendroElt, Double>> e : matrice.entrySet()) {
			HashMap<DendroElt, Double> hmRetrieved = new HashMap<>();
			for(Entry<DendroElt, Double> e2 : e.getValue().entrySet()) {
				
				if(!e2.getKey().equals(nodeL) && !e2.getKey().equals(nodeR) ) {
					hmRetrieved.put(e2.getKey(), e2.getValue());
				}
			}
			matrice.put(e.getKey(), hmRetrieved);
		}
	}

	//On réccupère le tuple ayant la valeur max puis on renvoie un nouveau noeud coorespondant à la concaténation de ce tuple
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
	
	//On ajoute à la matrice toutes les méthodes et pour chaque tuple de méthodes (x,y) sa valeur de couplage couplageClass(x,y)
	public void init() {
		for(int i = 0; i<this.classes.size(); i++) {
			if(!classes.get(i).isAlone()) {
				HashMap<DendroElt, Double> hm = new HashMap<>();
				for(int j = 0; j<this.classes.size(); j++) {
					if(i!=j) {
						hm.put(classes.get(j), Parser.couplageClass(classes.get(i), classes.get(j)));
					}
				}
				matrice.put(classes.get(i), hm);
			}
		}		
	}
	
	
	public void start() {
		//On initialise la matrice avec les classes du programme
		this.init();
		
		//Tant que la matrice ne contient pas le dernier élement on réccupère le max, on supprime ses enfants et on l'ajoute à la matrice
		while(matrice.size()>1) {
			DendroNode nodeMax = getMax();
			this.addNode(nodeMax);
		}
		
		//On lance l'affichage graphique du dendrogramme en lui passant le dernier element de la matrice  ( coorespondant au dendrogramme ) en paramètres 
		DendrogramPaintTest dp = new DendrogramPaintTest();
		for(Entry <DendroElt, HashMap<DendroElt, Double>> en : matrice.entrySet() ) {
			dp.start((DendroNode)en.getKey());
		}
		
	}
	
	
}
