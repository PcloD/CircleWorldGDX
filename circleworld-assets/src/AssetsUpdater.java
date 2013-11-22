import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class AssetsUpdater {

	public static void main(String[] args) {
		Settings defaultSettings = new Settings();
		Settings trilinearSettings = new Settings();
		
		trilinearSettings.filterMag = TextureFilter.Linear;
		trilinearSettings.filterMin = TextureFilter.MipMapLinearLinear;
		
		updateAtlas("gui", defaultSettings );
		updateAtlas("planets", trilinearSettings);
		updateAtlas("player1", trilinearSettings);
		updateAtlas("ships", trilinearSettings);
		updateAtlas("tilemap", defaultSettings);
	}
	
	static private void updateAtlas(String name, Settings settings) {
		TexturePacker2.process(settings, "atlas/" + name, "assets/atlas", name);
	}
}
