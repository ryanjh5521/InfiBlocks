package mods.ryanjh5521.microblocks;

import mods.ryanjh5521.microblocks.api.PartType;

public class PartIDInUseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public PartIDInUseException(int id, PartType<?> p1, PartType<?> p2) {
		super("Micropart ID "+id+" is already used by "+p1+" when adding "+p2);
	}
}
