package jr.dungeon.events;

import jr.JRogue;
import jr.dungeon.Dungeon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.*;

public class EventSystem {
	private Dungeon dungeon;
	
	/**
	 * List of {@link EventListener}s that the Dungeon should send events to.
	 */
	private final Set<EventListener> listeners = new HashSet<>();
	
	/**
	 * List of {@link Event}s to be sent to {@link EventListener}s with the flag
	 * {@link EventHandler#invocationTime()} set to {@link EventInvocationTime#TURN_COMPLETE}.
	 */
	private final List<Event> eventQueueNextTurn = new LinkedList<>();
	
	public EventSystem(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	/**
	 * Adds an event listener to this dungeon.
	 * @param listener The event listener to add.
	 */
	public void addListener(EventListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes an event listener from this dungeon.
	 * @param listener The event listener to remove.
	 */
	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}
	
	public void triggerTurnCompleteEvents() {
		for (Iterator<Event> iterator = eventQueueNextTurn.iterator(); iterator.hasNext(); ) {
			Event event = iterator.next();
			triggerEvent(event, EventInvocationTime.TURN_COMPLETE);
			iterator.remove();
		}
	}
	
	/**
	 * Triggers a dungeon event, notifying all listeners.
	 * @param event The event to trigger.
	 *
	 */
	public void triggerEvent(Event event) {
		eventQueueNextTurn.add(event);
		triggerEvent(event, EventInvocationTime.IMMEDIATELY);
	}
	
	/**
	 * Triggers a dungeon event, notifying all listeners.
	 * @param event The event to trigger.
	 * @param invocationTime When to trigger the event. <code>IMMEDIATELY</code> to trigger it right now or <code>TURN_COMPLETE</code> to delay it to the next turn.
	 */
	@SuppressWarnings("unchecked")
	public void triggerEvent(Event event, EventInvocationTime invocationTime) {
		Set<EventHandlerMethodInstance> handlers = new HashSet<>();
		
		listeners.forEach(l -> fetchEventMethods(handlers, l, event, invocationTime));
		
		if (dungeon.getLevel() != null) {
			dungeon.getLevel().entityStore.getEntities().forEach(e -> {
				fetchEventMethods(handlers, e, event, invocationTime);
				
				e.getSubListeners().forEach(l2 -> {
					if (l2 != null) {
						fetchEventMethods(handlers, l2, event, invocationTime);
					}
				});
			});
		}
		
		handlers.stream().sorted(Comparator.comparing(h -> h.getHandler().priority())).forEach(h -> {
			try {
				h.getMethod().invoke(h.getListener(), event);
			} catch (Exception e) {
				JRogue.getLogger().error("Error triggering event " + event.getClass().getSimpleName(), e);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void fetchEventMethods(Set<EventHandlerMethodInstance> handlers, EventListener listener, Event event, EventInvocationTime invocationTime) {
		event.setDungeon(dungeon);
		
		Class<?> listenerClass = listener.getClass();
		List<Method> listenerMethods = new LinkedList<>();
		
		while (listenerClass != null) {
			Method[] methods = listenerClass.getDeclaredMethods();
			
			for (Method method : methods) {
				if (
					method.isAnnotationPresent(EventHandler.class) &&
						method.getParameterCount() == 1 &&
						method.getParameterTypes()[0].isAssignableFrom(event.getClass())
					) {
					listenerMethods.add(method);
				}
			}
			
			listenerClass = listenerClass.getSuperclass();
		}
		
		listenerMethods.forEach(method -> {
			method.setAccessible(true); // ha ha
			
			if (event.isCancelled()) {
				return;
			}
			
			EventHandler annotation = method.getAnnotation(EventHandler.class);
			
			if (annotation.selfOnly() && !event.isSelf(listener)) {
				return;
			}
			
			if (annotation.invocationTime() != invocationTime) {
				return;
			}
			
			handlers.add(new EventHandlerMethodInstance(method, annotation, listener));
		});
	}
	
	@Getter
	@AllArgsConstructor
	private class EventHandlerMethodInstance {
		private Method method;
		private EventHandler handler;
		private EventListener listener;
	}
}
