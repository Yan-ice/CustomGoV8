package fw.location;

import java.util.List;
import org.bukkit.entity.Player;

public class Teleporter {
	Player player;

	/**
	 * 为一个玩家创建传送器。
	 * 传送器可以便捷地传送该玩家。
	 * @param player 传送器对应的玩家
	 */
	public Teleporter(Player player) {
		this.player = player;
	}

	/**
	 * 自动检查坐标的存在性。如果存在将传送玩家。
	 * 
	 * @param Loc
	 *            将要传送的地点
	 * @param SafeTp
	 *            如果地点不安全，是否传送到附近安全区
	 */
	public void Teleport(FLocation Loc, boolean SafeTp) {
		if (Loc == null || !Loc.isComplete()) {
			return;
		}
		player.teleport(Loc.ToMC(player));
	}

	/**
	 * 自动检查坐标的存在性,并传送到随机一个坐标点。
	 * @param Loc 需要检查并随机选择传送的坐标列表。
	 */
	public void TeleportRandom(List<FLocation> Loc) {
		if(Loc != null && Loc.size()>0){
			FLocation loc = FLocation.RandomLoc(Loc);
			if(player!=null){
				if (loc.isComplete()) {
					player.teleport(loc.ToMC(player));
				}
			}
			
		}
		
		return;
	}
}
