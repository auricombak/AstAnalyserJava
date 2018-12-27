package ihm;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dendrogram.DendroNode;
import dendrogram.Node;

//Singleton
public class GuiInterface{
	
	private static Boolean made = false;
	private JPanel actualCenterPanel;
	private JPanel newCenterPanel;
	
	private JPanel dendro;
	private JPanel info;
	
	private static JFrame frame;
    
	private GuiInterface() {
		
	}
	
    public void start() {
    	
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Affichage");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("Dendrogramme");
        JMenuItem m22 = new JMenuItem("Infos");
        m1.add(m11);
        m1.add(m22);
        
        m22.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	newCenterPanel = info;
		        changePanel();
		        System.out.println("info");
            }
        });
        m11.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	newCenterPanel = dendro;
		        changePanel();
		        System.out.println("dendro");
            }

        });
        frame.add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
        actualCenterPanel = info;
        refresh(info);       
        
    }
    
    public void refresh(JPanel jp) {  
        frame.add(BorderLayout.CENTER,jp);
    }
    
	public static GuiInterface getInstance() {
		if(!GuiInterface.made) {
			GuiInterface.made = true;
			GuiInterface.frame = new JFrame();
			return new GuiInterface();
		}
		return null;
	}
	
    
    public void changePanel() {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {

            	System.out.println(actualCenterPanel.toString() + "1");

            	frame.getContentPane().remove(actualCenterPanel);
            	frame.add(BorderLayout.CENTER,newCenterPanel);
//		        refresh(newCenterPanel);

            	actualCenterPanel = newCenterPanel;

		        frame.invalidate();
		        frame.revalidate();
            	System.out.println(actualCenterPanel.toString() + "2");
            }
        });
    }
    

    
    public void setDendroNode(DendroNode node) {
        DendrogramPaintPanel panelDendogram = new DendrogramPaintPanel(node);
    	this.dendro = panelDendogram;
    }
    
    public void setInfoText(String text) {
        InfoPanel panelInfo = new InfoPanel(text);
    	this.info = panelInfo;
    }
}