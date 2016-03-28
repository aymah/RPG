package map;

public class LayeredPanel extends GamePanel{
	
	protected Integer layer;

	public LayeredPanel(int layer) {
		this.layer = layer;
	}
	
    public void displayPanel() {
		frame.add(this, layer, 0);
    }
    
}
