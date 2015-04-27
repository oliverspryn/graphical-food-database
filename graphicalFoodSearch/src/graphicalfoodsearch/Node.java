
package graphicalfoodsearch;

import java.awt.FontMetrics;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Node extends JPanel{
    public float x;
    public float y;
    public String text = "Text";
  
    public Node(float xi, float yi, String newText) {
        x = xi;
        y = yi;
        text = newText;
    }
    
    public void paint(Graphics g) {
        FontMetrics metrics = g.getFontMetrics();
        float width = metrics.stringWidth(text) + 10;
        float height = 20;
       
        Graphics2D g2 = (Graphics2D) g;

        //draw a rectangle
        g2.draw(new Rectangle2D.Float(x, y, width, height));
        //draw the text
        g2.drawString(text,x + 5, y + height * 3/4);
        
    }
}
