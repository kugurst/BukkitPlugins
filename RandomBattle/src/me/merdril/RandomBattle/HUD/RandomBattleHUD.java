/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

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
		buttons.setMarginBottom(buttons.getHeight());
		buttons.setMarginRight(buttons.getWidth());
		// buttons.shiftXPos(-buttons.getWidth());
		// buttons.shiftYPos(-buttons.getHeight());
		player.getMainScreen().attachPopupScreen(screen);
	}
	
}
