package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import fw.location.FLocation;

public class InteractBlock extends TriggerBase {

	public InteractBlock(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void KillPlayerListen(PlayerInteractEvent evt) {
		
		if(evt.getClickedBlock()!=null){
			Player pl = evt.getPlayer();
			if (byGroup.hasPlayer(pl) && evt.getHand()==EquipmentSlot.HAND) {
				if (Id != null & Id.size()>0) {
					if (Id.get(0) == "0") {
						Strike(pl);
						return;
					} else {
						for (int a = 0; a < Id.size(); a++) {
							if(Id.get(a).indexOf(",")!=-1 || Id.get(a).indexOf(" ")!=-1){
								if (evt.getClickedBlock().equals((new FLocation(Id.get(a),pl.getWorld()).ToMC(evt.getPlayer()).getBlock()))) {
									byGroup.Tags().addTag("BlockName", evt.getClickedBlock().getType().name());
									Execute(evt.getAction().equals(Action.RIGHT_CLICK_BLOCK),pl);
									
								}
							}else{
								try{
									if (evt.getClickedBlock().getType().name().equalsIgnoreCase(Id.get(a))  || evt.getClickedBlock().getType().getId() == Integer.valueOf(Id.get(a))) {
										byGroup.Tags().addTag("BlockName", evt.getClickedBlock().getType().name());
										Execute(evt.getAction().equals(Action.RIGHT_CLICK_BLOCK),pl);
									}
								}catch(NumberFormatException e){
								
								}
							}
							
						}
					}
				}
			}
		}
		
	}
	void Execute(boolean isRight,Player striker){
		if(Id.isEmpty()){
			Strike(striker);
		}else{
			boolean checking = true;
			if(Id.contains("sneak") && !striker.isSneaking()){
				checking=false;
			}
			if(Id.contains("unsneak") && striker.isSneaking()){
				checking=false;
			}
			if(Id.contains("rightclick") && !isRight){
				checking=false;
			}
			if(Id.contains("leftclick") && isRight){
				checking=false;
			}
			if(checking){
				Strike(striker);
			}
			
		}
	}
	
}
