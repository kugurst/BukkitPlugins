/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RandomBattleAttackListener extends EntityListener
{
	RandomBattle	       plugin;
	protected BattleSetter	begin;
	protected int	       randomChance	= 70;
	private Random	       generator	= new Random();
	
	/**
	 * 
	 */
	public RandomBattleAttackListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	public void onEntityDamage(EntityDamageEvent event)
	{
		EntityDamageByEntityEvent attackEvent = null;
		if (event instanceof EntityDamageByEntityEvent)
			attackEvent = (EntityDamageByEntityEvent) event;
		if (attackEvent != null)
		{
			SpoutPlayer player = null;
			Monster monster = null;
			ComplexLivingEntity dragon = null;
			ComplexEntityPart dragonPart = null;
			if (attackEvent.getDamager() instanceof Player)
			{
				player = (SpoutPlayer) attackEvent.getDamager();
				
			}
			else if (attackEvent.getDamager() instanceof Arrow)
			{
				Arrow arrow = (Arrow) attackEvent.getDamager();
				if (arrow.getShooter() instanceof Player)
					player = (SpoutPlayer) arrow.getShooter();
			}
			else if (attackEvent.getEntity() instanceof Player)
				player = (SpoutPlayer) attackEvent.getEntity();
			if (randomChance > generator.nextInt(99) && player != null)
			{
				begin = new BattleSetter(plugin, player, monster);
				player.sendMessage("[RandomBattle] " + attackEvent.getDamage());
			}
		}
	}
}
