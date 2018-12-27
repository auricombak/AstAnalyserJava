package ihm;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import dendrogram.Node;

public class InfoPanel extends JPanel{
	String textToDisplay;
	
	public InfoPanel(String text) {
		this.textToDisplay = text;
		JTextArea ta = new JTextArea();
		ta.setText(textToDisplay);
		this.add(ta);
	}

    

}
