/**
 * 
 */

package me.merdril.randombattle.hud;

import java.util.ArrayList;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public class MagicButton extends GenericButton
{
	RandomBattle	plugin;
	
	/**
	 * @param monsters
	 */
	public MagicButton(RandomBattle instance, SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.setText("Magic");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		plugin.getServer().getConsoleSender().sendMessage("[RandomBattle] " + this.getText() + " was clicked");
	}
	
}
