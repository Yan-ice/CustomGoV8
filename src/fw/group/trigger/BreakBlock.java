package fw.group.trigger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock extends TriggerBase {

	public BreakBlock(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void Listen(BlockBreakEvent evt2) {
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null & Id.size()>0) {
				if (Id.get(0) == "0") {
					Strike(pl);
					return;
				} else {
					Block Blo = evt2.getBlock();
					for (int a = 0; a < Id.size(); a++) {
						try{
							if (Blo.getType().name().equalsIgnoreCase(Id.get(a)) || Blo.getTypeId() == Integer.valueOf(Id.get(a))) {
								byGroup.Tags().addTag("BlockName", Blo.getType().name());
								Strike(pl);
								return;
							}
						}catch(NumberFormatException e){
							
						}
					}
				}
			}
		}
	}
}
