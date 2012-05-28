
package me.merdril.randombattle.battle;

public enum RBElem {
	
	FIRE("earth;water;", "ice;poison;", "lightning;wind;"), ICE("fire;lightning;", "water;earth;", "wind;poison;"),
	LIGHTNING("earth;", "water;ice", "wind;gravity;"), EARTH("ice;wind;", "lightning;fire;", "poison;lightning;");
	/**
	 * @param elem
	 */
	RBElem(String weak, String strong, String immune) // String dontDamage)
	{	
		
	}
}
