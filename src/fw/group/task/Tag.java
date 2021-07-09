package fw.group.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tag {
	Map<String,String> value = new HashMap<>();
	
	/**
	 * 在记录板上记录新标签。
	 * 如果变量名已存在，则覆盖它。
	 * @param Name 变量名
	 * @param Value 值
	 */
	public void addTag(String Name,String Value){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
		value.put(Name, Value);
	}
	
	/**
	 * 在计分板上获取一个变量。
	 * 如果不存在该变量，则获取0
	 * @param Name 变量名
	 * @return 获取到的值
	 */
	public String getTag(String Name){
		if(value.containsKey(Name)){
			return value.get(Name);
		}
		return "null";
	}
	
	/**
	 * 移除计分板上指定变量。
	 * @param Name 变量名
	 */
	public void removeTag(String Name){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
	}
	
	/**
	 * 获得计分板上的所有变量。
	 * @return 变量列表
	 */
	public Set<String> TagList(){
		return value.keySet();
	}
}
