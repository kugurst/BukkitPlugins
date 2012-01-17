/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class FightButton extends GenericButton
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public FightButton(RandomBattle instance, SpoutPlayer player)
	{
		super();
		this.plugin = instance;
		this.setText("Fight");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		plugin.getServer().getConsoleSender()
		        .sendMessage("[RandomBattle] " + this.getText() + " was clicked");
	}
}
