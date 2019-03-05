package org.opentripplanner.api.model;

public enum LocationType{
	STOP(0),
	STATION(1),
	ENTRANCE(2);

    private final int value;

    LocationType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}