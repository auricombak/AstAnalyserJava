package dendrogram;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class DendrogramPaintTest
{
    public void start(final DendroNode node)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI(node);
            }
        });
    }

    private void createAndShowGUI(DendroNode node)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DendrogramPaintPanel panel = new DendrogramPaintPanel(node);
        f.getContentPane().add(panel);

        f.setSize(1000,800);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}




