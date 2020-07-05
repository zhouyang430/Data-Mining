package program;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
//import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Iterator;

//import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;  
public class Apriori {
	static private int min_support = 4;

//数据预处理
	private ArrayList<String> clean_data(ArrayList<String> Data) {
		ArrayList<String> returnArray = new ArrayList<String>();
		String[] tmpStr = new String[Data.size()];

		for (int i = 1; i < Data.size(); i++) {// 跳过items
			tmpStr[i] = Data.get(i);
			tmpStr[i] = tmpStr[i].substring(tmpStr[i].indexOf("{") + 1, tmpStr[i].indexOf("}"));
			returnArray.add(tmpStr[i]);

		}
		return returnArray;

	}

	// 获取频繁一项集
	private Map<String, Integer> find_frequent_1_itemset(ArrayList<String> Data) {
		Map<String, Integer> returnMap = new ConcurrentHashMap<String, Integer>();
		String sub[] = null;
		List<String> list = null;
		list = new ArrayList<String>();
		for (int i = 0; i < Data.size(); i++) {
			sub = Data.get(i).split(",");// split每个item
			list.addAll(Arrays.asList(sub));
		}

		Set<String> uniqueWords = new HashSet<String>(list);// 元素去重
		for (String item : uniqueWords) {
			int times = Collections.frequency(list, item);
			// System.out.println(item + "出现次数" + times);
			// 统计个数，大于等于最小支持度的就放入map
			if (returnMap.get(item) == null && times >= min_support) {
				returnMap.put(item, times);
			}

		}
		return returnMap;
	}

//生成候选集
	public Map<String, Integer> Apriorii_gen(Map<String, Integer> input) {
		Map<String, Integer> candiMap = new LinkedHashMap<>();
		ArrayList<String> list = new ArrayList<String>();// 用于存放itemsets
		String s1[] = null;
		String s2[] = null;

		if (input.size() != 0) {
			Set<String> set = input.keySet();// 只对键进行操作
			for (String item : set) {
				list.add(item);
			}
		}
		// 比较其k-1项是否一致

		for (int i = 0; i < list.size(); i++) {
			s1 = list.get(i).split(",");

			for (int j = 0; j < list.size(); j++) {

				s2 = list.get(j).split(",");//
				int flag = 0;

				for (int x = 0; x < s1.length - 1; x++) {
					// 判断前k-1项是否相等
					if (!s1[x].equals(s2[x])) {
						flag = 1;
						break;
					}
				}

				if (flag == 0 && (!s1[s1.length - 1].equals(s2[s2.length - 1]))) {/// ??

					String candidate = list.get(i) + "," + s2[s2.length - 1];

					if (has_infrequent_subset(candidate, input)) {

					} else {

						candiMap.put(candidate, 0);
					}
				}

			}
		}
		return candiMap;

	}

	public boolean has_infrequent_subset(String candidate, Map<String, Integer> input) {

		String[] strings = candidate.split(",");

		for (int i = 0; i < strings.length; i++) {
			String subString = "";
			int flag = 1;

			for (int j = 0; j < strings.length; j++) {
				if (j != i) {

					if (flag == strings.length - 1) {
						subString = subString + strings[j];
						flag++;
					} else {
						subString = subString + strings[j] + ",";
						flag++;
					}

				}
			}

			if (input.get(subString) == null) {

				return true;// 有不频繁子集
			}
		}
		return false;

	}

	public void count_candidateMap(Map<String, Integer> map, ArrayList<String> list) {

		int times;
		for (Entry<String, Integer> entry : map.entrySet()) {
			String str = entry.getKey();

			times = count(list, str);

			entry.setValue(times);
		}
		System.out.println("After counting" + map);

	}

	public int count(ArrayList<String> data, String m) {
		// m是要统计的数目
		// data是全部的csv的data
		boolean flag;
		int count = 0;
		for (String transaction : data) {

			flag = true;

			if (transaction.length() >= m.length()) {// >=才能包含
				String items[] = m.split(",");
				for (int i = 0; i < items.length; i++) {
					if (transaction.indexOf(items[i]) == -1) {// 没有找到
						flag = false;
						break;
					}
				}
				if (flag == true) {// item的每一项都找到
					count++;
				}
			}
		}
		return count;
	}

	public Map<String, Integer> judgeSupport(Map<String, Integer> map) {

		for (Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Integer> item = it.next();
			if (((Entry<String, Integer>) item).getValue() < min_support) {
				it.remove();
			}
		}
		if (map.size() == 0) {
			System.out.println("There is no frequent itemset which min_support=" + min_support);
			return null;
		}

		return map;
	}

	public static void main(String[] args) {
		long time1 = System.currentTimeMillis();
		Apriori test = new Apriori();

		ReadData read = new ReadData();// 将数据从csv中读出来
		ArrayList<String> csvData = read.getcsv();
		System.out.println("Initial data" + csvData);

		ArrayList<String> after_clean_data = new ArrayList<String>();
		after_clean_data = test.clean_data(csvData);// 数据清洗
		System.out.println("Data After Cleaning" + after_clean_data);
		////// ********************

		// ***************************
		// 用Map数据结构来表示 元素：出现次数
		Map<String, Integer> Lk_1 = new LinkedHashMap<String, Integer>();
		Lk_1.putAll(test.find_frequent_1_itemset(after_clean_data));// 得到频繁的一项集
		System.out.println("Frequent 1-Item sets are" + Lk_1);

		Map<String, Integer> candidateMap = new LinkedHashMap<String, Integer>();

		Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();

		// 循环条件
		int i = 1;

		while (Lk_1 != null && Lk_1.size() > 0) {// 循环的条件:Lk-1非空
			System.out.println("****************No." + i + "Round*********************");
			resultMap.putAll(Lk_1);// 频繁合并
			System.out.println("The current merged resultMap is" + resultMap);

			candidateMap = test.Apriorii_gen(Lk_1);// 获得候选集Ck

			test.count_candidateMap(candidateMap, after_clean_data);// 计数候选集

			System.out.println("Remove data that does not match support");
			Lk_1 = test.judgeSupport(candidateMap);
			System.out.println("After Remove" + Lk_1);
			i++;
		}
		System.out.println("The loop ends and the final result is printed to Result.txt");

		// ****************把结果输出到文件Result.txt
		try {
			String line = System.getProperty("line.separator");
			StringBuffer str = new StringBuffer();
			FileWriter fw = new FileWriter("./csv/Result.txt", true);
			Set<Entry<String, Integer>> set = resultMap.entrySet();
			Iterator<Entry<String, Integer>> iter = set.iterator();
			while (iter.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter.next();
				str.append(entry.getKey() + " : " + entry.getValue()).append(line);
			}
			fw.write(str.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(Lk_1);
		long time2 = System.currentTimeMillis();// 计算程序运行的时间
		int time = (int) ((time2 - time1));
		System.out.println("It takes：" + time + "ms！");

	}

}
