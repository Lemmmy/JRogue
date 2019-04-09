package jr.dungeon.events;

public enum EventInvocationTime {
    /**
     * Handler will handle the event as soon as it is triggered.
     */
    IMMEDIATELY,
    
    /**
     * Handler will handle the event when the current or next turn is complete.
     */
    TURN_COMPLETE
}
