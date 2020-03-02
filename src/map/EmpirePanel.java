package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import event.EnterBase;
import event.EnterProvince;
import event.MapEvent;
import event.MapEventManager;
import event.Province;
import event.SimEncounter;
import unit.Party;

public class EmpirePanel extends MapPanel {
	private EmpireMap map;
	private EmpirePanelManager manager;
	private boolean animating;
	private transient BufferedImage avatarStandingImage;
	private transient BufferedImage avatarImage;
	private transient BufferedImage avatarMoveAnimationImages;
	

	private int animationOffsetY;
	private int animationOffsetX;
	
	public EmpirePanel(GameFrame frame) {
		frame.add(this);
        this.setBounds(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
		this.frame = frame;
		manager.setPanel(this);

		this.animationOffsetY = 0;
		this.animationOffsetX = 0;
		
		try {
			avatarStandingImage = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/testRenal.gif"));
			avatarImage = avatarStandingImage;
			avatarMoveAnimationImages = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/testRenalMoveAnimations.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setMap(EmpireMap map) {
		this.map = map;
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
        drawMap(g2d, map.getTargetingIndexX(), map.getTargetingIndexY(), GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y,
        			 GraphicsConstants.REGION_MAP_HEIGHT, GraphicsConstants.REGION_MAP_WIDTH, map.getX(), map.getY());
        drawProvinceBorders(g2d);
        drawAvatar(g2d);
        drawTarget(g2d);
    }
	
    public void drawAvatar(Graphics2D g2d) {
//        Color c = new Color(0, 255, 0);
//        g2d.setColor(c);
//        g2d.fillOval(GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y, GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
    	int offsetX = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getWidth(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2 + GraphicsConstants.REGION_CENTER_X + animationOffsetX;
    	int offsetY = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getHeight(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2 + GraphicsConstants.REGION_CENTER_Y + animationOffsetY;
    	g2d.drawImage(avatarImage, (map.getParty().getAvatarIndexX() - map.getTargetingIndexX()) * GraphicsConstants.REGION_TILE_SIZE + offsetX, (map.getParty().getAvatarIndexY() - map.getTargetingIndexY()) * GraphicsConstants.REGION_TILE_SIZE + offsetY, avatarImage.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, avatarImage.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
    }
    
    private void drawTarget(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0));
        GraphicsConstants.drawRect(g2d, GraphicsConstants.REGION_CENTER_X, 
        		 GraphicsConstants.REGION_CENTER_Y, 
        		 GraphicsConstants.REGION_TILE_SIZE, 
        		 GraphicsConstants.REGION_TILE_SIZE, 4);
    }
    
    private void drawProvinceBorders(Graphics2D g2d) {
    	List<Province> provinces = map.getEmpire().getAllProvinces();
    	Province selectedProvince = null;
    	for (Province province: provinces) {
    		Tile tile = map.getTileMap().getTile(map.getTargetingIndexY(), map.getTargetingIndexX());
    		if ((tile.hasEvent() && tile.getEvent().getCurrentEvent().getClass().equals(EnterProvince.class) && ((EnterProvince)tile.getEvent().getCurrentEvent()).getProvince().getName().equals(province.getName())) ||
    			(tile.hasEvent() && tile.getEvent().getCurrentEvent().getClass().equals(EnterBase.class) && ((EnterBase)tile.getEvent().getCurrentEvent()).getProvince().getName().equals(province.getName()))) {
    			selectedProvince = province;
    		} else {
    			g2d.setColor(new Color(0, 0, 0));
    			drawProvinceBorder(g2d, province);
    		}
    		
//    					//check above
//    					if (y == 0 || !province.isInArea(y - 1, x)) {
//    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//    									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY , 
//    									 GraphicsConstants.REGION_TILE_SIZE, 
//    									 1);
//    					}
//    					//check below
//    					if (y == province.getHeight() - 1 || !province.isInArea(y + 1, x)) {
//    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//    									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY - 2, 
//    									 GraphicsConstants.REGION_TILE_SIZE, 
//    									 1);
//    					}
//    					//check left
//    					if (x == 0 || !province.isInArea(y, x - 1)) {
//    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
//    								     1, 
//    								     GraphicsConstants.REGION_TILE_SIZE);
//    					}
//    					//check right
//    					if (x == province.getWidth() -1 || !province.isInArea(y, x + 1)) {
//    						g2d.drawRect(((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX - 2, 
//    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
//    								     1, 
//    								     GraphicsConstants.REGION_TILE_SIZE);
//    					}

    		//double for loop through all the area of each province
    			//if the square is in the province
    				//check each side of the square
    					//if the side of the square is not in the province, draw a border
    		
    	}
    	if (selectedProvince != null) {
	    	g2d.setColor(new Color(255, 0, 0));
			drawProvinceBorder(g2d, selectedProvince);
    	}
    }
    
	private void drawProvinceBorder(Graphics2D g2d, Province province) {
		int provOffsetX = province.getX() - province.getCenter().getX() - map.getTargetingIndexX();
		int provOffsetY = province.getY() - province.getCenter().getY() - map.getTargetingIndexY();
    	int offsetX = map.getX() + GraphicsConstants.REGION_CENTER_X;
    	int offsetY = map.getY() + GraphicsConstants.REGION_CENTER_Y;
		for (int y = 0; y < province.getHeight(); y++) {
			for (int x = 0; x < province.getWidth(); x++) {
				if (province.isInArea(y, x)) {
					//check above
					if (y == 0 || !province.isInArea(y - 1, x)) {
						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY , 
									 ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
					}
					//check below
					if (y == province.getHeight() - 1 || !province.isInArea(y + 1, x)) {
						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
									 ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
					}
					//check left
					if (x == 0 || !province.isInArea(y, x - 1)) {
						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
								     ((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
								     ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
					}
					//check right
					if (x == province.getWidth() -1 || !province.isInArea(y, x + 1)) {
						g2d.drawLine(((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
								     ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
								     ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
					}		
				}
			}
		}
	}
	
	private void walkingAnimation(String direction) {
		animating = true;
		frame.setAcceptingInput(false);
		CountDownLatch cdl = new CountDownLatch(1);
	    Timer timer = new Timer(0, new ActionListener() {
		   
		    int i = 0;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if(i < 4) {
	            	switch (direction) {
	            		case "up":
	            			animationOffsetY += GraphicsConstants.REGION_TILE_SIZE/4;
	            			break;
	            		case "down":
	            			animationOffsetY -= GraphicsConstants.REGION_TILE_SIZE/4;
	            			break;
	            		case "left":
	            			animationOffsetX += GraphicsConstants.REGION_TILE_SIZE/4;
	            			break;
	            		case "right":
	            			animationOffsetX -= GraphicsConstants.REGION_TILE_SIZE/4;
	            			break;
	            	}
	            	avatarImage = getAnimationImage(direction, i);
	            	repaint();
	            } else {
	            	if (!frame.hasNextInput()) 
	            		avatarImage = avatarStandingImage;
	                endAnimation(e);
	            }
	            i++;
	        }
	        
	        private void endAnimation(ActionEvent e) {
	        	animationOffsetX = 0;
	        	animationOffsetY = 0;
	        	animating = false;
	    		frame.setAcceptingInput(true);
                repaint();
                cdl.countDown();
                ((Timer)e.getSource()).stop();
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(40);
	    timer.start();
	    try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private BufferedImage getAnimationImage(String direction, int x) {
		int y = 0;
		switch (direction) {
			case "left":
				y = 0;
				break;
			case "down":
				y = 1;
				break;
			case "up":
				y = 2;
				break;
			case "right":
				y = 3;
				break;
		}
		BufferedImage image = avatarMoveAnimationImages.getSubimage(x * 32, y * 32, 32, 32);
		return image;
	}

	public void removePanel() {
		frame.remove(this);
	}
	
	public void restore() {
		frame.add(this);
		frame.setPanel(manager);
	}

	public void setManager(EmpirePanelManager manager) {
		this.manager = manager;
	}

	@Override
	public PanelManager getManager() {
		return manager;
	}
}
