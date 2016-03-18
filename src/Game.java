import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import map.MapTest;

@SuppressWarnings("serial")
public class Game extends JPanel {
    int x = 0;
    int y = 0;
    private void moveBall() {
        x = x + 1;
        y = y + 1;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Image image = loadImage("/Users/andrewmah/Desktop/Screen Shot 2015-11-06 at 10.47.08 AM");
        System.out.println("test");
        g2d.drawImage(image, x, y, 30, 30, null);
    }
    
    private BufferedImage loadImage(String name) {
        String imgFileName = name;
        URL url = MapTest.class.getResource(imgFileName);
        BufferedImage img = null;
        try {
            img =  ImageIO.read(url);
        } catch (Exception e) {
        }
        return img;
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Sample Frame");
        Game game = new Game();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (true) {
            game.moveBall();
            game.repaint();
            Thread.sleep(10);
        }
    }
}
