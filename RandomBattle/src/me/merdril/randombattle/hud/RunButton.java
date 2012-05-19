/**
 * 
 */

package me.merdril.randombattle.hud;

import me.merdril.randombattle.RandomBattle;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public class RunButton extends GenericButton
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RunButton(RandomBattle instance, SpoutPlayer player)
	{
		super();
		this.plugin = instance;
		this.setText("Run");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		plugin.getServer().getConsoleSender().sendMessage("[RandomBattle] " + this.getText() + " was clicked");
	}
	
}
