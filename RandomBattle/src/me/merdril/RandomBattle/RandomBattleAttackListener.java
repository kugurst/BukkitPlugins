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
			int entityID = 0;
			if (attackEvent.getDamager() instanceof Player)
			{
				player = (SpoutPlayer) attackEvent.getDamager();
				if (attackEvent.getEntity() instanceof Monster)
				{
					monster = (Monster) attackEvent.getEntity();
					entityID = monster.getEntityId();
				}
				else if (attackEvent.getEntity() instanceof ComplexLivingEntity)
				{
					dragon = (ComplexLivingEntity) attackEvent.getEntity();
					entityID = dragon.getEntityId();
				}
				else if (attackEvent.getEntity() instanceof ComplexEntityPart)
				{
					dragonPart = (ComplexEntityPart) attackEvent.getEntity();
					entityID = dragonPart.getEntityId();
				}
			}
			else if (attackEvent.getDamager() instanceof Arrow)
			{
				Arrow arrow = (Arrow) attackEvent.getDamager();
				if (arrow.getShooter() instanceof Player)
				{
					player = (SpoutPlayer) arrow.getShooter();
					if (attackEvent.getEntity() instanceof Monster)
					{
						monster = (Monster) attackEvent.getEntity();
						entityID = monster.getEntityId();
					}
					else if (attackEvent.getEntity() instanceof ComplexLivingEntity)
					{
						dragon = (ComplexLivingEntity) attackEvent.getEntity();
						entityID = dragon.getEntityId();
					}
					else if (attackEvent.getEntity() instanceof ComplexEntityPart)
					{
						dragonPart = (ComplexEntityPart) attackEvent.getEntity();
						entityID = dragonPart.getEntityId();
					}
				}
			}
			else if (attackEvent.getEntity() instanceof Player)
			{
				player = (SpoutPlayer) attackEvent.getEntity();
				if (attackEvent.getDamager() instanceof Monster)
				{
					monster = (Monster) attackEvent.getDamager();
					entityID = monster.getEntityId();
				}
				else if (attackEvent.getDamager() instanceof ComplexLivingEntity)
				{
					dragon = (ComplexLivingEntity) attackEvent.getDamager();
					entityID = dragon.getEntityId();
				}
				else if (attackEvent.getDamager() instanceof ComplexEntityPart)
				{
					dragonPart = (ComplexEntityPart) attackEvent.getDamager();
					entityID = dragonPart.getEntityId();
				}
			}
			if (randomChance > generator.nextInt(99) && player != null
			        && (monster != null || dragon != null || dragonPart != null))
			{
				if (monster != null)
					begin = new BattleSetter(plugin, player, monster);
				else if (dragon != null)
					begin = new BattleSetter(plugin, player, dragon);
				else if (dragonPart != null)
					begin = new BattleSetter(plugin, player, dragonPart);
				player.sendMessage("[RandomBattle] " + attackEvent.getDamage());
			}
		}
	}
}
