package ihm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.batik.swing.JSVGCanvas;


@SuppressWarnings("serial")
public class GraphPanel extends JPanel {

    protected JLabel label = new JLabel();
    protected JSVGCanvas svgCanvas = new JSVGCanvas();
    protected JPanel thisPanel = this;
    
	public GraphPanel (String uri) {
		// Create a panel and add the button, status label and the SVG canvas.

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(label);
        this.add("North", p);

        svgCanvas.setURI(uri);

        

        this.add("Center", svgCanvas);
       
	}
}
