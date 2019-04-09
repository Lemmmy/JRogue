package jr.dungeon.events;

import lombok.val;

import java.util.HashSet;
import java.util.Set;

public interface EventListener {
    default Set<Object> getListenerSelves() {
        val selves = new HashSet<>();
        selves.add(this);
        return selves;
    }
}
