/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericButton;
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
	public GenericButton	        fight, magic, items, run;
	private InGameScreen	        mainScreen;
	
	/**
	 * @param player
	 * 
	 */
	public RandomBattleHUD(RandomBattle instance, SpoutPlayer player)
	{
		plugin = instance;
		this.player = player;
		player.closeActiveWindow();
		mainScreen = (InGameScreen) player.getMainScreen();
		mainScreen.closePopup();
		screen = new RandomBattlePopupScreen(plugin, player);
		buttons = new CommandButtonContainer(plugin, player);
		screen.attachWidget(plugin, buttons);
		buttons.setAnchor(WidgetAnchor.BOTTOM_LEFT).shiftYPos(-buttons.getHeight() - 20)
		        .shiftXPos(20);
		mainScreen.attachPopupScreen(screen);
	}
}