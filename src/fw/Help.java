package fw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
/**
 * ���ڱ༭���ָ��������ࡣ
 *
 */
public class Help {
	private static String title(String str) {
		return ChatColor.RED + "-------[" + ChatColor.YELLOW + str + ChatColor.RED + "]-------";
	}
/**
 * ���Ͳ����ָ�������
 * @param sender Ҫ���͵Ķ���
 */
	public static void MainHelp(CommandSender sender) {
			sender.sendMessage(title("CustomGo "+Data.Version+" ����"));
			sender.sendMessage("/fw reload              ���ز��");
			sender.sendMessage("/fw list                �鿴������Ϸ");
			sender.sendMessage("/fw leave               �뿪һ������");
			sender.sendMessage("/fw leave <���>         ��ָ������뿪һ����Ϸ");
			sender.sendMessage("/fw item <��Ʒִ����>      ���һ����Ʒִ����");
			sender.sendMessage("/fw <��Ϸ��> <����>   ��Ϸ���ָ��[��ϸ��������һ��]");
	}
	/**
	 * ���Ͳ��lobby����������
	 * @param sender Ҫ���͵Ķ���
	 */
	public static void LobbyHelp(CommandSender sender) {
			sender.sendMessage(title("����ָ���������"));
			sender.sendMessage("join             ����һ�����д���");
			sender.sendMessage("join <���>       ��ָ����Ҽ���һ�����д���");
			sender.sendMessage("info             �鿴����������еĲ�����Ϣ");
			sender.sendMessage("statu            �鿴����������е�״̬");
			sender.sendMessage("load             ��ȡ/����һ������������");
	}
}
