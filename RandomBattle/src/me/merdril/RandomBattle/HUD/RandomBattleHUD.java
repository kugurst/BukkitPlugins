/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleHUD
{
	RandomBattle	                plugin;
	SpoutPlayer	                    player;
	private RandomBattlePopupScreen	screen;
	private CommandButtonContainer	buttons;
	
	/**
	 * @param player
	 * 
	 */
	public RandomBattleHUD(RandomBattle instance, SpoutPlayer player)
	{
		this.plugin = instance;
		player.closeActiveWindow();
		player.getMainScreen().closePopup();
		screen = new RandomBattlePopupScreen(plugin, player);
		buttons = new CommandButtonContainer(plugin, screen);
		screen.attachWidgets(plugin, buttons);
		buttons.setAnchor(WidgetAnchor.BOTTOM_RIGHT);
		player.getMainScreen().attachPopupScreen(screen);
	}
	
}
