package fw.group.trigger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PickupItem extends TriggerBase {

	public PickupItem(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.MONITOR)
	void Listen(PlayerPickupItemEvent evt2) {
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null) {
				if (Id.get(0).equals("0")) {
					if(evt2.getItem().getItemStack().getType()!=Material.ARROW){
						Strike(pl);
					}
					
				} else {
					for (int a = 0; a < Id.size(); a++) {
						ItemStack i = evt2.getItem().getItemStack();
						try{
							if (i.getType().name().equalsIgnoreCase(Id.get(a)) || i.getType().getId() == Integer.valueOf(Id.get(a))) {
								byGroup.Tags().addTag("ItemID", evt2.getItem().getItemStack().getType().getId()+"");
								byGroup.Tags().addTag("ItemAmount", evt2.getItem().getItemStack().getAmount()+"");
								Strike(pl);
								break;
							}
						}catch(NumberFormatException e){
							
						}
					}
				}
			}

		}
	}
}
