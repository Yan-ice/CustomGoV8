package fw.group.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValueBoard {
	Map<String,Double> value = new HashMap<>();

	
	/**
	 * 在计分板上记录新的变量。
	 * 如果变量名已存在，则覆盖它。
	 * @param Name 变量名
	 * @param Value 值
	 */
	public void Value(String Name,double Value){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
		value.put(Name, Value);
	}
	
	public void ValueAdd(String Name,double Value){
		if(value.containsKey(Name)){
			
			value.replace(Name, value.get(Name)+Value);
		}else{
			value.put(Name, Value);
		}
	}
	
	
	/**
	 * 在计分板上获取一个变量。
	 * 如果不存在该变量，则获取0
	 * @param Name 变量名
	 * @return 获取到的值
	 */
	public double getValue(String Name){
		if(value.containsKey(Name)){
			return value.get(Name);
		}
		return 0;
	}
	
	/**
	 * 移除计分板上指定变量。
	 * @param Name 变量名
	 */
	public void removeValue(String Name){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
	}
	
	/**
	 * 获得计分板上的所有变量。
	 * @return 变量列表
	 */
	public Set<String> ValueList(){
		return value.keySet();
	}
}
