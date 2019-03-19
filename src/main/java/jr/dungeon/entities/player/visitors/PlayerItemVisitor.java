package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Prompt;
import jr.dungeon.items.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class PlayerItemVisitor implements PlayerVisitor {
	protected InventoryUseResult useInventoryItem(Player player,
											   String promptString,
											   Predicate<ItemStack> isEligible,
											   TriConsumer<Character, Container.ContainerEntry, Container> responseCallback,
											   boolean allowHyphen) {
		if (!player.getContainer().isPresent()) {
			return InventoryUseResult.NO_CONTAINER;
		}
		
		Container inv = player.getContainer().get();
		Map<Character, ItemStack> eligibleItems = inv.getItems().entrySet().stream()
			.filter(e -> isEligible.test(e.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		if (eligibleItems.isEmpty()) {
			return InventoryUseResult.NO_ITEM;
		}
		
		char[] options = ArrayUtils.toPrimitive(eligibleItems.keySet().toArray(new Character[0]));
		options = Arrays.copyOf(options, options.length + 1);
		options[options.length - 1] = '-';
		
		player.getDungeon().prompt(new Prompt(promptString, options, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				Optional<Container.ContainerEntry> containerEntry = inv.get(response);
				
				if (!allowHyphen && !containerEntry.isPresent()) {
					player.getDungeon().log("Invalid item '[YELLOW]%s[]'.", response);
					return;
				}
				
				Container.ContainerEntry entry = containerEntry.orElse(null);
				responseCallback.accept(response, entry, inv);
			}
		}));
		
		return InventoryUseResult.SUCCESS;
	}
	
	protected InventoryUseResult useInventoryItem(Player player,
											   String promptString,
											   Predicate<ItemStack> isEligible,
											   TriConsumer<Character, Container.ContainerEntry, Container> responseCallback) {
		return useInventoryItem(player, promptString, isEligible, responseCallback, false);
	}
	
	protected enum InventoryUseResult {
		SUCCESS, NO_CONTAINER, NO_ITEM
	}
}
