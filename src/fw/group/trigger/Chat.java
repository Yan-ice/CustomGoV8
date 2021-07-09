package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;

public class Chat extends TriggerBase {
	public Chat(String ID) {
		super(ID);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void Listen(PlayerChatEvent evt2) {
		if(evt2.getMessage().startsWith("/")){
			return;
		}
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null) {
				if (Id.get(0) == "0") {
					Strike(pl);
				} else {
					String x = evt2.getMessage().split(" ")[0];
					for (String i : Id) {
						if (x.indexOf(i)!=-1) {
//							byGroup.ValueBoard().Value("message",evt2.getMessage());
							Strike(pl);
							break;
						}
					}
				}
			}
		}
	}
}
