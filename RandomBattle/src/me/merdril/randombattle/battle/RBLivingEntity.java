/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import me.merdril.randombattle.battle.ai.AI;
import me.merdril.randombattle.config.RBDatabase;

import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.sun.xml.internal.stream.Entity;

/**
 * <p>
 * An interface to encapsulate both Players and Monsters. The turn system is partially blind to the
 * distinction between a human intelligence and an artificial intelligence, but simply requests stat
 * information and move information.
 * </p>
 * <p>
 * This interfaces defines methods that will be visible to the turn system, and it is my hope that
 * these methods will enable the simplest implementation of the desired battle system.
 * </p>
 * @author Merdril
 */
public interface RBLivingEntity extends Comparable<RBLivingEntity>
{
	/**
	 * The enums to indicate the statuses of a given {@link RBLivingEntity}. The form of the enum
	 * suggests that the implementations should use an EnumMap for handling this data, which should
	 * enable quick calculations for whatever class requires this information.
	 * @author Merdril
	 */
	public enum Stat {
		/**
		 * This stat contains the hit points for this entity: the amount of damage it can take
		 * before dying. Not necessary that all monsters "die" when their HP reaches 0.
		 */
		HP, /**
		 * This stat contains the magic points for this entity: the amount of "currency" to
		 * "buy" spells with. Spells should not be case with insufficient MP.
		 */
		MP, /**
		 * This stat contains the current HP for this entity: the amount of HP minus some total
		 * damage amount (not necessarily implemented that way).
		 */
		CHP, /**
		 * This stat contains the current MP for this entity: the amount of MP minus some total
		 * MP expenditure (not necessarily implemented that way).
		 */
		CMP, /**
		 * This stat contains the current strength of this entity: the relative amount of
		 * damage a physical attack should cause. The original strength should be kept by the
		 * implementing class for uses with getOriginalStat(...).
		 */
		STR, /**
		 * This stat contains the current magic power of this entity: the relative amount of
		 * damage a magical attack should cause. The original magic should be kept by the
		 * implementing class for uses with getOriginalStat(...).
		 */
		MAG, /**
		 * This stat contains the current defensive ability of this entity: the relative amount
		 * of damage nullified from a physical attack. The original defense should be kept by the
		 * implementing class for uses with getOriginalStat(...).
		 */
		DEF, /**
		 * This stat contains the current magic nullification ability of this entity: the
		 * relative amount of damage nullified from a magical attack. The original magic defense
		 * should be kept by the implementing class for uses with getOriginalStat(...).
		 */
		MDEF, /**
		 * This stat contains the current agility of this entity: the relative quickness of
		 * this entity in comparison to others in battle. The original agility should be kept by the
		 * implementing class for uses with getOriginalStat(...).
		 */
		AGL, /**
		 * This stat contains the current accuracy of this entity: the relative accuracy of
		 * physical attacks. The original accuracy should be kept by the implementing class for uses
		 * with getOriginalStat(...).
		 */
		ACC, /**
		 * This stat contains the current evasion of this entity: the relative ability for this
		 * entity to dodge physical attacks. The original evasion should be kept by the implementing
		 * class for uses with getOriginalStat(...).
		 */
		EVA, /**
		 * This stat contains the current luck of this entity: the relative frequency for
		 * magical attacks to "miss," finding good items, as well as other things... The original
		 * luck should be kept by the implementing class for uses with getOriginalStat(...).
		 */
		LUCK, /**
		 * This stat contains the current experience points of this entity: for players, this
		 * is the total number of experience points accumulated (before the last death perhaps), and
		 * for monsters, this is the base experience it drops when it is killed.
		 */
		EXP, /**
		 * This stat contains the current level of this entity: this is only relevant for
		 * players, and may not actually serve any utility in this plugin, considering the way the
		 * leveling system functions.
		 */
		LEVEL;
		public final static HashMap<String, Stat>	statMap	= new HashMap<String, Stat>();
		static {
			for (Stat stat : Stat.values())
				statMap.put(stat.toString().toLowerCase(), stat);
		}
	}
	
	/**
	 * The enums to indicate the various status conditions that an {@link RBLivingEntity} can have.
	 * In its current form, this enables the quick addition/removal/deactivation of any effects, so
	 * long as the effect is handled properly in whatever class requires the information.
	 * @author Merdril
	 */
	public enum Effect {
		/**
		 * This status indicates that this {@link RBLivingEntity} is dead and can no longer perform
		 * actions.
		 */
		DEAD, /**
		 * This status indicates that this {@link RBLivingEntity} is suffering continuous
		 * damage over some period of time.
		 */
		POISONED, /**
		 * This status indicates that this {@link RBLivingEntity} is in a mode where it
		 * cannot perform any actions aside from those that require it to be in this mode to be
		 * performed.
		 */
		SLEEPING, /**
		 * This status indicates that this {@link RBLivingEntity} is in a mode where it
		 * cannot perform any actions and cannot be damaged except by {@link Poison}.
		 */
		STONE, /**
		 * This status indicates the this {@link RBLivingEntity} is in a mode where its turn
		 * speed should be doubled. It may not necessarily move twice as often in a time block.
		 */
		HASTE, /**
		 * This status indicates that this {@link RBLivingEntity} is in a mode where its
		 * effective accuracy is reduced. The amount to reduce its accuracy is dependent on the
		 * class implementing this interface.
		 */
		BLIND, /**
		 * This status indicates that this {@link RBLivingEntity} is in a state where its
		 * turn speed should be halved. It may not necessarily move half as often in a time block.
		 */
		SLOW;
	}
	
	/**
	 * A mapping of monster names to their respective {@link EntityType}. This object maps the
	 * string name of the monster (retivable via the getName() method of the {@link Monster} class
	 * or {@link ComplexLivingEntity} class) to the corresponding class. In the case of CraftBukkit,
	 * this name is usually CraftSpider, and there should be a simple replaceAll(...) call on the
	 * {@link String} passed into this method's get call. Unless one ahs good reason (see
	 * {@link RBDatabase}).
	 */
	public Map<String, Class<? extends AI>>	MONSTERS	= new HashMap<String, Class<? extends AI>>();
	
	/**
	 * Indicates to the relevant commands to add the specified items to whatever {@link Collection}
	 * it handles.
	 */
	public int	                            ADD	     = 0;
	/**
	 * Indicates to the relevant commands to add the specified items to whatever {@link Collection}
	 * it handles.
	 */
	public int	                            REMOVE	 = 1;
	
	/**
	 * <p>
	 * Returns an integer representing the current amount of some status held by this
	 * RBLivingEntity.
	 * </p>
	 * <p>
	 * This is a simplification of creating n-different methods for each status. Each possible
	 * status is contained within the enum Stat that is a member of this class.
	 * </p>
	 * @param stat
	 *            the Stat to query.
	 * @return An int specifying the amount of the Stat this living entity contains.
	 */
	public Integer getStat(Stat stat);
	
	/**
	 * <p>
	 * Unreservedly changes the value of some Stat to the int provided. Returns the new value of the
	 * assigned stat, as a proof of transaction.
	 * </p>
	 * <p>
	 * Care should be used in assigning values to stats (other than CHP and CMP), considering that
	 * it may affect the balance of a battle.
	 * </p>
	 * @param stat
	 *            The Stat to modify.
	 * @param amount
	 *            An int representing the new amount to set this Stat to.
	 * @return The same int that was passed in, but the implementation should return the int
	 *         referenced by the Stat (which should in turn, have the same value as the int that was
	 *         passed in).
	 */
	public Integer setStat(Stat stat, int amount);
	
	/**
	 * Returns an effect for use in damage calculation or turn calculation. Effects should be used,
	 * rather than overriding the value of some stat that is provided by the class.
	 * @return A List&ltEffect&gt that this RBLivingEntity currently has.
	 */
	public List<Effect> getEffects();
	
	/**
	 * Modifies the effects of this {@link RBLivingEntity} according to the action specified. Can be
	 * one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param effect
	 *            The effects to modify on this {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link Effect}&gt that his {@link RBLivingEntity} possess.
	 */
	public List<Effect> setEffect(int action, Effect... effect);
	
	/**
	 * Returns the {@link List}&lt{@link RBSkill}&gt of this entity.
	 * @return A {@link List}&lt{@link RBSkill}&gt of the skills this LivingEntity knows. Returns an
	 *         empty list in the case the entity knows none.
	 */
	public List<RBSkill> getSkills();
	
	/**
	 * Modifies the {@link List}&lt{@link RBSkill}&gt that this entity knows. This method either
	 * adds or removes from the list.
	 * @param action
	 *            An int representing whether to add or remove the specified skills.
	 * @param skills
	 *            A {@link RBSkill}[] to either add or remove to the list of skills.
	 * @return The current {@link List}&lt{@link RBSkill}&gt that results from this modification.
	 */
	public List<RBSkill> setSkills(int action, RBSkill... skills);
	
	/**
	 * Returns the List&ltRBMagic&gt of this entity.
	 * @return A List&ltRBMagic&gt of the magicks this LivingEntity knows. Returns an empty list in
	 *         the case the entity knows none.
	 */
	public List<RBMagic> getMagicks();
	
	/**
	 * Modifies the magicks of this {@link RBLivingEntity} according to the action specified. Can be
	 * one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param magicks
	 *            The magicks to modify on this {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link RBMagic}&gt that his {@link RBLivingEntity}
	 *         possess.
	 */
	public List<RBMagic> setMagicks(int action, RBMagic... magicks);
	
	/**
	 * Returns the List&ltRBElem&gt of this entity.
	 * @return A List&ltRBElem&gt of the elements this LivingEntity is weak to. Returns an empty
	 *         list in the case it is weak to nothing.
	 */
	public List<RBElem> getWeak();
	
	/**
	 * Modifies the weaknesses of this {@link RBLivingEntity} according to the action specified. Can
	 * be one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param elems
	 *            The {@link RBElem}'s this {@link RBLivingEntity} is weak to. to modify on this
	 *            {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link RBElem}&gt that his {@link RBLivingEntity} possess.
	 */
	public List<RBElem> setWeak(RBElem... elems);
	
	/**
	 * Asks this LivingEntity to decide on a next move based on its current status.
	 * @param lock
	 *            The lock to pause execution of the turn system thread until this LivingEntity has
	 *            made a move.
	 * @return A RBMove containing the target entity(ies) and the RBAttack to execute.
	 */
	public RBMove getMove(ReentrantLock lock);
	
	/**
	 * Returns the LivingEntity that this class encapsulates.
	 * @return A LivingEntity which is affected by the actions of this class.
	 */
	public LivingEntity getEntity();
	
	/**
	 * A {@link Map}&lt{@link Stat}, {@link Integer}&gt of the current stats of the entity to the
	 * values of those stats. The mapping would make the most sense as an {@link EnumMap}.
	 * @return A {@link Map}&lt{@link Stat}, {@link Integer}&gt view of the current stats and values
	 *         of this entity. Changes to the values of this map should affect the values of the
	 *         {@link RBLivingEntity}.
	 */
	public Map<Stat, Integer> getStats();
	
	/**
	 * <p>
	 * A {@link Map}&lt{@link Stat}, {@link Integer}&gt of the original stats of the entity to the
	 * values of those stats. The mapping would make the most sense as an {@link EnumMap}. The
	 * original {@link Stat}s need not include CHP and CMP, though that is not strictly prohibited,
	 * however there is no standard interpretation of its inclusion in the mapping returned by this
	 * method.
	 * </p>
	 * @return A {@link Map}&lt{@link Stat}, {@link Integer}&gt view of the original stats and
	 *         values of this entity. Changes to the values of this map should not affect the values
	 *         or mappings of the original {@link Stat}s of the {@link RBLivingEntity}. For
	 *         permanent changes, see {@link RBDatabase}.
	 */
	public Map<Stat, Integer> getOriginalStats();
	
	/**
	 * <p>
	 * This tests for equality between two different {@link RBLivingEntity}. Another {@link Object}
	 * is considered equal if it implements {@link RBLivingEntity} and both wrap the same
	 * {@link LivingEntity}.
	 * </p>
	 * <p>
	 * For example if the {@link LivingEntity} of the two classes are both {@link Enderman}, but not
	 * the same game {@link Enderman}, then this method should return false.
	 * </p>
	 * @param obj
	 *            The other {@link Object} to test for equality.
	 * @return True if this {@link RBLivingEntity} and the {@link RBLivingEntity} of the argument
	 *         wrap the exact same {@link Entity}. False otherwise.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * <p>
	 * This method extends the equals method to allow comparisons between similar
	 * {@link RBLivingEntity}.
	 * </p>
	 * <p>
	 * This method should return 0 if <code>this.equals(other) == true</code> This method should
	 * return -1 if <code>other</code> wraps the same subclass of {@link LivingEntity}: e.g.
	 * {@link Monster}, {@link SpoutPlayer}, {@link ComplexLivingEntity}, etc... This method should
	 * return 1 if <code>other</code> does not wrap the same subclass of {@link LivingEntity}.
	 * </p>
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(RBLivingEntity other);
	
	/**
	 * This method returns the fully qualified name of the class, the name of the
	 * {@link LivingEntity}, and the current stats of this {@link RBLivingEntity}.
	 * @return A String made to the above specifications.
	 */
	@Override
	public String toString();
	
	/**
	 * This method should returns a hash code determined by the hash code of the enclosed
	 * {@link LivingEntity} as well as some factor to differentiate between different instances of
	 * the same class of {@link LivingEntity}.
	 * @return Hopefully, a unique int that is specific to the instance of the enclosed
	 *         {@link LivingEntity} of this class.
	 */
	@Override
	public int hashCode();
}
