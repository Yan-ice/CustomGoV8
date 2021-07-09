package fw.group.trigger;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Interact extends TriggerBase {

	public Interact(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void KillPlayerListen(PlayerInteractEntityEvent evt) {
		if(evt.getRightClicked() instanceof HumanEntity){
			Player striker = evt.getPlayer();
			HumanEntity striked = (Player) evt.getRightClicked();
			
			if(byGroup.hasPlayer(striker) && evt.getHand()==EquipmentSlot.HAND){
				if(Id.contains("cancel")){
					evt.setCancelled(true);
				}
				if(striked.getName()!=null){
					for(String id : Id){
						if(striked.getName().contains(id)){
							byGroup.Tags().addTag("Interacted", striked.getName());
							Strike(striker);
						}
					}
				}
				
				
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	void KillPlayerListen(EntityDamageByEntityEvent evt) {
		if(evt.getEntity() instanceof HumanEntity && evt.getDamager() instanceof Player){
			Player striker = (Player) evt.getDamager();
			HumanEntity striked = (Player) evt.getEntity();
			if(byGroup.hasPlayer(striker)){
				if(Id.contains("cancel")){
					evt.setCancelled(true);
				}
				if(striked.getName()!=null){
					for(String id : Id){
						if(striked.getName().contains(id)){
							byGroup.Tags().addTag("Interacted", striked.getName());
							Strike(striker);
						}
					}
				}
			}
		}
	}
	
	
}
