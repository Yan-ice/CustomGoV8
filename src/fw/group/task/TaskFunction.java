package fw.group.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class TaskFunction implements Listener {
	public static List<TaskFunction> l = new ArrayList<>();

	List<String> Task;
	List<String> need = new ArrayList<>();
	String Name;
	File file;
	
	public File GetFile() {
		return file;
	}

	public List<String> getTask() {
		return Task;
	}
	public List<String> getParemeter() {
		return need;
	}
	
	public static TaskFunction getFunction(String name) {
		for (int a = 0; a < l.size(); a++) {
			if (l.get(a).Name.equals(name)) {
				return l.get(a);
			}
		}
		return null;
	}

	private TaskFunction(File f) {
		this.Name = f.getName().split("\\.")[0];
		FileConfiguration fil = YamlConfiguration.loadConfiguration(f);
		if(fil.contains("Parameter")){
			need = fil.getStringList("Parameter");
		}
		
		Task = fil.getStringList("Task");
		
	}


	public static void UnLoadAll() {
		while (l.size() > 0) {
			l.remove(0);
		}
	}

	public static void LoadAll(File dir) {
		UnLoadAll();
		File[] list = dir.listFiles();
		List<TaskFunction> l = new ArrayList<>();
		if (list.length > 0) {
			for (int a = 0; a < list.length; a++) {
				TaskFunction i = new TaskFunction(list[a]);
				l.add(i);
			}
			TaskFunction.l = l;
		}
	}

	public void replaceTask(List<String> x,int line) {
		x.remove(line);
		x.addAll(line, x);
	}

}
