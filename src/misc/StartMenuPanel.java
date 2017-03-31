package misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import unit.Hero;
import unit.Party;
import unit.Unit;
import event.Ability;
import event.MenuItem;
import map.BattlePanelManager;
import map.EmpireInfoPanel;
import map.EmpireMap;
import map.EmpirePanelManager;
import map.GameFrame;
import map.GamePanel;
import map.GameStateManager;
import map.GraphicsConstants;
import map.MenuPanel;
import map.ProvinceMenuPanel;
import map.RegionInfoPanel;
import map.RegionMap;
import map.ExploreMenuPanel;
import map.RegionPanelManager;

public class StartMenuPanel extends MenuPanel {
	
    private int selectorIndexX;
	private int selectorIndexY;
	private String bgmName;
	private Clip bgm;

    public StartMenuPanel(String name, GameFrame frame, StartPanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		this.manager = manager;
		if (layer < 2) manager.setMenuPanel(this);
		this.setBounds(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
		this.name = name;
		this.frame = frame;	
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
		loadBGM("testStartMenuBGM");
		startBGM();
	}
	
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
//        BufferedImage img = null;
//		try {
//			img = ImageIO.read(new File("/Users/andrewmah/Downloads/testimage.jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        g2d.drawImage(img, 0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT, 0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT, null);
        drawMenu(g2d);
	}
	
	private void drawMenu(Graphics2D g2d) {
		for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
      	    if (selectorIndexY == i)
		      	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 20 + i * 30);
    	}		
	}

	
	public void keyPressed(KeyEvent e) {
		if (subMenu != null) {
			subMenu.keyPressed(e);
			return;
		}
		int keyCode = e.getKeyCode();
		System.out.println(keyCode);
		switch (keyCode) {
			case 87:
			case 38:
				moveUp();
				break;
			case 83:
			case 40:
				moveDown();
				break;
			case 69:
			case 10:
				selectItem();
				break;
			case 81:
			case 27:
				break;
		}
		frame.refresh();
	}
	
	private void moveUp() {
		selectorIndexY--;
		if (selectorIndexY < 0)
			selectorIndexY = menuItems.size() - 1;
	}
	
	private void moveDown() {
		selectorIndexY++;
		if (selectorIndexY >= menuItems.size())
			selectorIndexY = 0;
	}
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}
	
	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Load Save Game";
			
			@Override
			public void execute(GamePanel panel) {
				StartMenuPanel menuPanel = (StartMenuPanel)panel;
				menuPanel.loadGame();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Start New Game";
			
			@Override
			public void execute(GamePanel panel) {
				StartMenuPanel menuPanel= (StartMenuPanel)panel;
				menuPanel.startGame();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Start Empire Test";
			
			@Override
			public void execute(GamePanel panel) {
				StartMenuPanel menuPanel= (StartMenuPanel)panel;
				menuPanel.startEmpireTest();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Quit";
			
			@Override
			public void execute(GamePanel panel) {
				System.exit(0);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);		
		return menuItems;	
	}
	
	public void startGame() {
		frame.removeAll();
        RegionPanelManager manager = new RegionPanelManager(frame);
        Party party = new Party();
        Hero hero = new Hero("Renalt", "ALLY", "Renalt", party);
        party.addUnit(hero);
        hero = new Hero("Sarana", "ALLY", "Sarana", party);
        party.addUnit(hero);
        hero = new Hero("Zell", "ALLY", "Zell", party);
        party.addUnit(hero);
        hero = new Hero("Fox", "ALLY", "Fox", party);
        party.addUnit(hero);
        RegionMap panel = new RegionMap("testMap", frame, manager, party);
        party.addGold(2000);
        stopBGM();
        panel.startBGM();
        panel.setCoordinates(1,1);
        GameStateManager gameStateManager = new GameStateManager();
        party.setGameStateManager(gameStateManager);
        RegionInfoPanel infoPanel = new RegionInfoPanel("testInfoPanel", frame, manager);
        ExploreMenuPanel menuPanel = new ExploreMenuPanel("testMenuPanel", frame, manager, null, 1, ExploreMenuPanel.getStandardMenu(), 0);
        manager.setDominantPanel(panel);
        frame.refresh();
	}
	
	public void loadGame() {
		Party party = null;
		try {
//			InputStream saveFile = getClass().getClassLoader().getResourceAsStream("/saves/testSave.sav");
			FileInputStream saveFile = new FileInputStream(System.getProperty("user.home")+"/saves/testSave.sav");
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			party = (Party)restore.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return;
		}
		frame.removeAll();
	    RegionPanelManager manager = new RegionPanelManager(frame);
	    RegionMap panel = (RegionMap)party.getGameStateManager().getMap(party.getMapName());
	    if (panel == null) {
			panel = new RegionMap(party.getMapName(), frame, manager, party);
	    }
	    stopBGM();
	    panel.startBGM();
	    panel.setManager(manager);
	    panel.setFrame(frame);
	    panel.restoreMap();
	    panel.setCoordinates(party.getAvatarIndexY(),party.getAvatarIndexX());
	    RegionInfoPanel infoPanel = new RegionInfoPanel("testInfoPanel", frame, manager);
	    ExploreMenuPanel menuPanel = new ExploreMenuPanel("testMenuPanel", frame, manager, null, 1, ExploreMenuPanel.getStandardMenu(), 0);
	    manager.setDominantPanel(panel);
	    frame.refresh();
	}
	
	private void startEmpireTest() {
		frame.removeAll();
        EmpirePanelManager manager = new EmpirePanelManager(frame);
        Party party = new Party();
        Hero hero = new Hero("Renalt", "ALLY", "Renalt", party);
        party.addUnit(hero);
        hero = new Hero("Sarana", "ALLY", "Sarana", party);
        party.addUnit(hero);
        hero = new Hero("Zell", "ALLY", "Zell", party);
        party.addUnit(hero);
        hero = new Hero("Fox", "ALLY", "Fox", party);
        party.addUnit(hero);
//        Unit unit = new Unit("Swordsman", "ALLY", "Swordsman 1", party);
//        party.addUnit(unit);
//        unit = new Unit("Swordsman", "ALLY", "Swordsman 2", party);
//        party.addUnit(unit);
        EmpireMap panel = new EmpireMap("testEmpireMap", frame, manager, party);
        stopBGM();
        panel.startBGM();
        panel.setCoordinates(8,6);
        GameStateManager gameStateManager = new GameStateManager();
        party.setGameStateManager(gameStateManager);
        EmpireInfoPanel infoPanel = new EmpireInfoPanel("empireInfoPanel", frame, manager);
        ExploreMenuPanel menuPanel = new ExploreMenuPanel("testMenuPanel", frame, manager, null, 1, ExploreMenuPanel.getStandardMenu(), 0);
        ProvinceMenuPanel provinceMenuPanel = new ProvinceMenuPanel("provinceMenuPanel", frame, manager, ProvinceMenuPanel.getStandardMenu(), 0);
        manager.setDominantPanel(panel);
        frame.refresh();
	}
	
	private void startBGM() {
		if (bgm == null)
			loadBGM(bgmName);
		bgm.setFramePosition(0);
		bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	private void loadBGM(String filename) {
		try {
	    	InputStream in = StartGame.class.getResourceAsStream("/testSounds/" + filename + ".wav"); 
	    	BufferedInputStream bin = new BufferedInputStream(in);
	    	AudioInputStream audioIn = AudioSystem.getAudioInputStream(bin);
	    	bgm = AudioSystem.getClip();
	    	bgm.open(audioIn);
	        FloatControl gainControl = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
	        gainControl.setValue(-20.0f);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void stopBGM() {
    	if (bgm.isRunning()) bgm.stop();
	}
}
