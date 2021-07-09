package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ConsoleCommand extends TriggerBase {
	public ConsoleCommand(String ID) {
		super(ID);
	}

	@EventHandler(priority=EventPriority.HIGH)
	protected void Listen(PlayerCommandPreprocessEvent evt2) {
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		if(evt2 instanceof Player){
			return;
		}
		if (Id != null) {
			if (Id.get(0) == "0") {
				Strike(null);
			} else {
				String x = evt2.getMessage().split(" ")[0];
				for (String i : Id) {
					if (x.indexOf(i)!=-1) {
						evt2.setCancelled(true);
						for(int a = 1;a<i.split(" ").length;a++){
//							byGroup.ValueBoard().Value(x+"_"+a, i.split("")[a]);
						}
						Strike(null);
						break;
					}
				}
			}
		}
	}
}
