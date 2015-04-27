
package graphicalfoodsearch;

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
        float width = 50;
        float height = 25;
        
        Graphics2D g2 = (Graphics2D) g;
       
        //draw a rectangle
        g2.draw(new Rectangle2D.Float(x-width/2, y-height/2, width/2, height/2));
        //draw the text
        g2.drawString(text,x-width/2 + 5, y-height/2);
        
    }
}
