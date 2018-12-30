package ihm;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dendrogram.DendroNode;
import dendrogram.Node;
import info.ClassInfo;
import parser.Parser;

//Singleton
public class GuiInterface{
	
	private static JPanel actualCenterPanel;
	private static JPanel newCenterPanel;
	
	private static JScrollPane callGraph;
	private static JScrollPane couplageGraph;
	
	private static JPanel dendro;
	private static JPanel info;
	private static JPanel componants;
	
	private static JFrame frame;
    
	private static String uriFolder;
	
    public static void start() {


        
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Infos", info);
        tabbedPane.addTab("DendroGraph", dendro);
        tabbedPane.addTab("CallGraph", callGraph);
        tabbedPane.addTab("CouplageGraph", couplageGraph);
        tabbedPane.addTab("List Componants", componants);
    	frame.add(tabbedPane, BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(frame);
        //frame.setLayout(tabbedPane);

        
    }
    
	
    
    public static void setDendroNode(DendroNode node) {
        DendrogramPaintPanel panelDendogram = new DendrogramPaintPanel(node);
    	dendro = panelDendogram;
    }
    
    public static void setInfoText(String text) {
        InfoPanel panelInfo = new InfoPanel(text);
    	info = panelInfo;
    }
    
    public static void setComponantsText(String text) {
        InfoPanel panelInfo = new InfoPanel(text);
        componants = panelInfo;
    }
    
    public static void setCallGraph(String uri) {
        GraphPanel panelCall = new GraphPanel(uri);
        JScrollPane scrollPane = new JScrollPane(panelCall);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(50, 30, 300, 50);
    	callGraph = scrollPane;
    }
    
    public static void setCouplageGraph(String uri) {
        GraphPanel panelCall = new GraphPanel(uri);
        JScrollPane scrollPane = new JScrollPane(panelCall);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(50, 30, 300, 50);
    	couplageGraph = scrollPane;
    }
    
    public static void main(String[] args) throws IOException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	frame = new MainFrame("Analyse programme");
            	JPanel folderP = new PickFolderPanel();
            	frame.add(folderP, BorderLayout.PAGE_START);
                frame.setVisible(true);
            }
      });
    	

    }
    
    //Appellé seulement si un dossier a été sélectionné dans l'interface graphique.
    public static void prepare() throws IOException {
    	Parser p = new Parser(uriFolder);
    	p.generateCallGraph();
    	p.generateCouplageGraph();
    	DendroNode dn = p.generateDendrogram();
    	
	    setDendroNode(p.generateDendrogram());
	    setInfoText(p.getInfoToDisplay());
	    setCallGraph("file:./example/call_graph.svg");
	    setCouplageGraph("file:./example/couplage_graph.svg");
	    setComponantsText(dn.calculateSubNodes());
	    start();
    }
    
    public static void setUri(String uri) {
    	uriFolder= uri;
    }
    

}