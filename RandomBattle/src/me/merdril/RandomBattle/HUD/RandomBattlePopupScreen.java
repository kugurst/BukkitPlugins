/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RandomBattlePopupScreen extends GenericPopup
{
	RandomBattle	     plugin;
	private SpoutPlayer	 player;
	private InGameScreen	screen;
	
	/**
	 * 
	 */
	public RandomBattlePopupScreen(RandomBattle instance, SpoutPlayer player)
	{
		super();
		this.plugin = instance;
		this.player = player;
		this.screen = (InGameScreen) player.getMainScreen();
		this.setTransparent(true);
	}
}
