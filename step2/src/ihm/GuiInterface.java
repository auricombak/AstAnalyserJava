package ihm;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dendrogram.DendroNode;
import dendrogram.Node;

//Singleton
public class GuiInterface{
	
	private static Boolean made = false;
	private JPanel actualCenterPanel;
	private JPanel newCenterPanel;
	private JPanel callGraph;
	
	private JPanel dendro;
	private JPanel info;
	
	private static JFrame frame;
    
	private GuiInterface() {
		
	}
	
    public void start() {
    	
        frame = new MainFrame("Analyse programme");

        
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Infos", info);
        tabbedPane.addTab("DendroGraph", dendro);
        tabbedPane.addTab("CallGraph", callGraph);
        frame.add(tabbedPane);
        //frame.setLayout(tabbedPane);

        
    }
    
    
	public static GuiInterface getInstance() {
		if(!GuiInterface.made) {
			GuiInterface.made = true;
			GuiInterface.frame = new JFrame();
			return new GuiInterface();
		}
		return null;
	}
	
    
    public void setDendroNode(DendroNode node) {
        DendrogramPaintPanel panelDendogram = new DendrogramPaintPanel(node);
    	this.dendro = panelDendogram;
    }
    
    public void setInfoText(String text) {
        InfoPanel panelInfo = new InfoPanel(text);
    	this.info = panelInfo;
    }
    
    public void setCallGraph(String uri) {
        CallGraphPanel panelCall = new CallGraphPanel();
    	this.callGraph = panelCall;
    }

}