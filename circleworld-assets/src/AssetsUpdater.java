import com.badlogic.gdx.tools.imagepacker.TexturePacker2;


public class AssetsUpdater {

	public static void main(String[] args) {
		
		UpdateAtlas("gui");
		UpdateAtlas("planets");
		UpdateAtlas("player1");
		UpdateAtlas("ships");
		UpdateAtlas("tilemap");
	}
	
	static private void UpdateAtlas(String name) {
		
		TexturePacker2.process("atlas/" + name, "assets/atlas", name);
		
	}
}
