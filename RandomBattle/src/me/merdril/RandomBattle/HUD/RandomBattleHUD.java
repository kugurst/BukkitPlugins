/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.InGameScreen;
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
	private InGameScreen	        mainScreen;
	
	/**
	 * @param player
	 * 
	 */
	public RandomBattleHUD(RandomBattle instance, SpoutPlayer player)
	{
		this.plugin = instance;
		player.closeActiveWindow();
		this.mainScreen = (InGameScreen) player.getMainScreen();
		mainScreen.closePopup();
		this.screen = new RandomBattlePopupScreen(plugin, player);
		this.buttons = new CommandButtonContainer(plugin, screen);
		screen.attachWidgets(plugin, buttons);
		buttons.setAnchor(WidgetAnchor.BOTTOM_RIGHT);
		buttons.shiftXPos(-buttons.getWidth() - 20);
		buttons.shiftYPos(-buttons.getHeight() - 20);
		mainScreen.attachPopupScreen(screen);
	}
	
}
