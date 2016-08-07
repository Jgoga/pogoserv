package pm.cat.pogoserv.game.event.impl;

import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.CatchPokemonResponse.CatchStatus;
import pm.cat.pogoserv.game.event.Event;
import pm.cat.pogoserv.game.model.player.Award;

public class CatchPokemonEvent extends Event implements AwardEvent {
	
	public final long encounterId;
	public final ItemId pokeballType;
	public final double normalizedReticleSize; // what is this?
	public final String spawnPointId;
	public final boolean hitPokemon;
	public final double spinModifier;
	public final double normalizedHitPosition;
	
	public CatchStatus catchStatus;
	public double missPercent;
	public long capturedUid;
	public Award award;
	
	public CatchPokemonEvent(long encounterId, ItemId pokeballType, double normalizedReticleSize, 
			String spawnPointId, boolean hitPokemon, double spinModifier, double normalizedHitPosition){
		
		this.encounterId = encounterId;
		this.pokeballType = pokeballType;
		this.normalizedReticleSize = normalizedReticleSize;
		this.spawnPointId = spawnPointId;
		this.hitPokemon = hitPokemon;
		this.spinModifier = spinModifier;
		this.normalizedHitPosition = normalizedHitPosition;
	}

	@Override
	public Award getAward() {
		return award;
	}
	
	
}
