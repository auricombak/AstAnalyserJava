package ihm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PickFolderPanel extends JPanel
	   implements ActionListener {
		   JButton go;
		   JLabel indication;
		   JFileChooser chooser;
		   String choosertitle;

		  public PickFolderPanel() {
		    go = new JButton("Parcourir...");
		    go.addActionListener(this);
		    indication = new JLabel("Selectionner un dossier src valide");
		    add(indication);
		    add(go);
		   }

		  public void actionPerformed(ActionEvent e) {            
		    chooser = new JFileChooser(); 
		    chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle(choosertitle);
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
		    chooser.setAcceptAllFileFilterUsed(false);
		    //    
		    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
		      GuiInterface.setUri(chooser.getCurrentDirectory().toString());
		      try {
				GuiInterface.prepare();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      }
		    else {
		      System.out.println("No Selection ");
		      }
		     }

		  public Dimension getPreferredSize(){
		    return new Dimension(200, 200);
		    }

}

