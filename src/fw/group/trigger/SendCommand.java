package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SendCommand extends TriggerBase {
	public SendCommand(String ID) {
		super(ID);
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void Listen(PlayerCommandPreprocessEvent evt2) {
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		if(!evt2.getMessage().startsWith("/")){
			return;
		}
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null) {
				if (Id.get(0) == "0") {
					Strike(pl);
				} else {
					String x = evt2.getMessage().split(" ")[0].substring(1);
					for (String i : Id) {
						if (x.indexOf(i)!=-1) {
							evt2.setCancelled(true);
							for(int a = 1;a<i.split(" ").length;a++){
								byGroup.Tags().addTag(x+"_"+a, i.split(" ")[a]);
							}
							Strike(pl);
							break;
						}
					}
				}
			}
		}
	}
}
