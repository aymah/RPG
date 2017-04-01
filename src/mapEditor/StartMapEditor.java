package mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import map.GameFrame;

public class StartMapEditor {
	static GameFrame frame = null;
	
	public static void main(String[] args) throws InterruptedException {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame();
		
		frame = new GameFrame();
        EditorPanelManager manager = new EditorPanelManager(frame);
	    frame.setLayout(new BorderLayout());
	    
	    JScrollPane mapPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    mapPanel.setLayout(null);
//	    mapPanel.setPreferredSize(new Dimension(EditorConstants.MAP_WIDTH, EditorConstants.MAP_HEIGHT));
//	    mapPanel.setBounds(0, 0, EditorConstants.MAP_WIDTH, EditorConstants.MAP_HEIGHT);
	    
	    mapPanel.setPreferredSize(new Dimension(350, 350));
	    mapPanel.setBounds(50, 50, 400, 400);
	    
	    JMenuBar menuBar = new JMenuBar();
	    JMenu menu = new JMenu("File");
	    menu.setMnemonic(KeyEvent.VK_A);
	    menu.getAccessibleContext().setAccessibleDescription("???");
	    menuBar.add(menu);
	    
	    JMenuItem menuItem1 = new JMenuItem("New Map");
	    menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
	    menuItem1.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
	    menuItem1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		int returnValue = fileChooser.showOpenDialog(null);
	    		if (returnValue == JFileChooser.APPROVE_OPTION) {
	    			File selectedFile = fileChooser.getSelectedFile();
	    		    frame.removeAll();
	    		    frame.add(mapPanel, BorderLayout.WEST);
	    		    EditorMap map = new EditorMap(selectedFile.getName().substring(0, selectedFile.getName().length() - 4), frame, manager, mapPanel);
	    		    manager.setDominantPanel(map);
	    		}
	    	}	
	    });
	    menu.add(menuItem1);
//	    menuItem.setMnemonic("KeyEvent.VK_B");
	    
	    JMenuItem menuItem2 = new JMenuItem("Load Map");
	    menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
	    menuItem2.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
	    menuItem2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		int returnValue = fileChooser.showOpenDialog(null);
	    		if (returnValue == JFileChooser.APPROVE_OPTION) {
	    			File selectedFile = fileChooser.getSelectedFile();
	    		    frame.removeAll();
	    		    frame.add(mapPanel, BorderLayout.WEST);
	    		    EditorMap map = new EditorMap(selectedFile.getName().substring(0, selectedFile.getName().length() - 4), frame, manager, mapPanel);
	    		    manager.setDominantPanel(map);
	    		}
	    	}	
	    });
	    menu.add(menuItem2);
	    	    
//	    JButton button = new JButton("Select File");
//	    button.addActionListener(new ActionListener() {
//	    	public void actionPerformed(ActionEvent ae) {
//	    		JFileChooser fileChooser = new JFileChooser();
//	    		int returnValue = fileChooser.showOpenDialog(null);
//	    		if (returnValue == JFileChooser.APPROVE_OPTION) {
//	    			File selectedFile = fileChooser.getSelectedFile();
//	    		    frame.removeAll();
//	    		    EditorMap map = new EditorMap(selectedFile.getName().substring(0, selectedFile.getName().length() - 4), frame, manager);
//	    		    manager.setDominantPanel(map);
//	    		}
//	    	}
//	    });
	    
//	    mapPanel.setBackground(new Color(0,0,0));
//	    frame.add(mapPanel, BorderLayout.CENTER);
	    frame.add(menuBar, BorderLayout.NORTH);
//	    f.add(menuBar);
//	    f.add(button);
	    f.pack();
	    

	    
        StartMapEditor m = new StartMapEditor();
        m.setup(f);
        

   	}
	
    public void setup(JFrame f) {
        f.add(frame);
//        f.setSize(EditorConstants.FRAME_WIDTH + 10, EditorConstants.FRAME_HEIGHT + 32);
        f.setSize(400 + 10, 400 + 32);

        f.setVisible(true);
        frame.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(frame);    
	}
}
