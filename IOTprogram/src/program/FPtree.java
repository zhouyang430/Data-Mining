package program;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.ObjectOutputStream;
import java.io.PrintStream;
//import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
//import java.util.List;
import java.util.Map;

public class FPtree {
	private int min_support = 2;
	// 保存第一次的次序
	public Map<String, Integer> head = new HashMap<String, Integer>();

	public LinkedList<LinkedList<String>> readCSV() throws IOException {
		LinkedList<LinkedList<String>> records = new LinkedList<LinkedList<String>>();

		File csv = new File("./csv/test.csv");

		try {
			BufferedReader textFile = new BufferedReader(new FileReader(csv));
			String lineData = "";
			while ((lineData = textFile.readLine()) != null) {
				lineData = lineData.substring(lineData.indexOf("{") + 1, lineData.indexOf("}"));
				String[] str = lineData.split(",");
				LinkedList<String> litm = new LinkedList<String>();
				for (int i = 0; i < str.length; i++) {
					litm.add(str[i].trim());
				}
				records.add(litm);
			}
			textFile.close();

		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		return records;
	}

	// header构建
	public LinkedList<Tree> build_head_table(LinkedList<LinkedList<String>> data) {
		Map<String, Tree> map = new HashMap<String, Tree>();

		LinkedList<Tree> rootNode = null;
		if (data.size() == 0) {// 循环终止条件
			return null;

		} else {
			rootNode = new LinkedList<Tree>();// 每个rootNode都代表由它衍生出的树
		}
		// 对于当前输入的数据库里的每一项进行遍历
		for (LinkedList<String> onelist : data) {
			for (String item : onelist) {

				if (map.get(item) == null) {
					// 创建新节点
					Tree onenode = new Tree();
					onenode.setName(item);
					onenode.setNumber(1);
					map.put(item, onenode);
				} else {
					map.get(item).countIncrement(1);

				}

			}
		}

		System.out.println("Remove terms that are less than the minimum support\n");
		for (Map.Entry<String, Tree> entry : map.entrySet()) {
			if (entry.getValue().getNumber() >= min_support) {

				rootNode.add(entry.getValue());
			}

		}
		for (Tree item : rootNode) {
			System.out.println(item.getName() + " " + item.getNumber() + " ");
		}

		Collections.sort(rootNode);// table 排序

		System.out.println("Sorted head table");
		for (Tree item : rootNode) {
			System.out.println(item.getName() + " " + item.getNumber() + " ");
		}
		for (int i = 0; i < rootNode.size(); i++) {
			Tree node = rootNode.get(i);
			head.put(node.getName(), i);
		}
		return rootNode;
	}

	// 保存排序后的所有事务
	public LinkedList<LinkedList<String>> sortByFreqItem(LinkedList<LinkedList<String>> transactions,
			LinkedList<Tree> itemSortByFreq) {

		LinkedList<LinkedList<String>> sortedTransactions = new LinkedList<LinkedList<String>>();
		for (LinkedList<String> transaction : transactions) {
			LinkedList<String> sortedItem = new LinkedList<String>();
			int itemNum = transaction.size();
			for (Tree node : itemSortByFreq) {
				if (transaction.contains(node.getName())) {
					sortedItem.add(node.getName());
					itemNum--;
				}
				if (itemNum == 0)
					break;
			}
			sortedTransactions.add(sortedItem);
		}

		return sortedTransactions;
	}

	public Tree buildFPTree(LinkedList<LinkedList<String>> transactions, LinkedList<Tree> headerTable) {
		Tree rootNode = new Tree("rootNode", 0, null); // 树根结点空，没有父结点
//单条事务
		for (LinkedList<String> items : transactions) {
			Tree parent = rootNode; // 每条事务的第一个结点看作父结点
			for (String item : items) {
				Tree itemNode = exist(item, parent);
				addSameNode(headerTable, itemNode);
				parent = itemNode; // 往上找父节点
			}
		}

		return rootNode;
	}

	public Tree exist(String item, Tree parent) {
		for (Tree child : parent.getChildren())
			if (child.getName().equals(item)) {// 已经有了这个节点
				child.setNumber(child.getNumber() + 1);// num+1

				return child;
			}
//如果没有它
		Tree node = new Tree(item, 1, parent); // 新创建结点 为当前结点添加父结点
		parent.addChild(node);
		return node;
	}

	public void addSameNode(LinkedList<Tree> headerTable, Tree itemNode) {
		for (Tree head : headerTable)
			if (head.getName().equals(itemNode.getName())) {
				while (head.hasNextHomonym()) {
					head = head.getNextHomonym();
					if (head == itemNode)
						return;
				}
				head.setNextHomonym(itemNode);
				return;
			}
	}

	public LinkedList<String> fp_growth(LinkedList<LinkedList<String>> transactions, String item) {
		LinkedList<String> freqItems = new LinkedList<String>(); // 频繁项集
		LinkedList<Tree> itemSortByFreq = build_head_table(transactions);// 建立headtable
		transactions = sortByFreqItem(transactions, itemSortByFreq);// 对原来的所有事务 按照刚才得出的头表排序
		Tree rootNode = buildFPTree(transactions, itemSortByFreq);// 如果fp树是空 返回
		if (rootNode.getChildren().size() == 0 || rootNode.getChildren() == null) // 递归终止条件
			return freqItems;
		if (item == null) {
			for (Tree node : itemSortByFreq) // 第一次递归 加入频繁一项集
				freqItems.add(node.getName() + ":" + node.getNumber());
		} else { // 不是第一次递归，遍历headtable与当前项组合成一个频繁项集
			for (int i = itemSortByFreq.size() - 1; i >= 0; i--) {
				Tree node = itemSortByFreq.get(i);
				freqItems.add(node.getName() + "," + item + ":" + node.getNumber());
			}
		}

		for (int i = itemSortByFreq.size() - 1; i >= 0; i--) { // 找当前项的条件模式基
			LinkedList<LinkedList<String>> newTransactions = new LinkedList<LinkedList<String>>();
			Tree node = itemSortByFreq.get(i); //
			String newItem = item == null ? node.getName() : node.getName() + "," + item;
			// 将当前项和遍历取出的项---》新项集
			while (node.hasNextHomonym()) {
				node = node.getNextHomonym();
				for (int j = 0; j < node.getNumber(); j++) ////// 每个项的各条路径对应的条件模式基
					newTransactions.add(getCPB(node));
			}

			freqItems.addAll(fp_growth(newTransactions, newItem)); //// 输入新的项集对应的所有事务和新项集递归
		}
		System.out.println("The frequent item sets are" + freqItems);

		return freqItems; // 返回本次调用求出的频繁项集
	}

	public LinkedList<String> getCPB(Tree node) {// 返回该结点对应的条件模式基
		LinkedList<String> transaction = new LinkedList<String>();
		Tree parent = node;
		while (parent.hasParent()) {
			if (parent.getParent().getName().equals("rootNode"))
				break;
			else {
				parent = parent.getParent();
				transaction.add(parent.getName());
			}

		}

		return transaction;
	}

	/*
	 * private void combine(LinkedList<Tree> residualPath, List<List<Tree>> results)
	 * { if (residualPath.size() > 0) { //如果residualPath太长，则会有太多的组合，内存会被耗尽的 Tree
	 * head = residualPath.poll(); List<List<Tree>> newResults = new
	 * ArrayList<List<Tree>>(); for (List<Tree> list : results) { List<Tree>
	 * listCopy = new ArrayList<Tree>(list); newResults.add(listCopy); }
	 * 
	 * for (List<Tree> newPath : newResults) { newPath.add(head); }
	 * results.addAll(newResults); List<Tree> list = new ArrayList<Tree>();
	 * list.add(head); results.add(list); combine(residualPath, results); } }
	 * 
	 * private boolean isSingleBranch(Tree rootNode) { boolean rect = true; while
	 * (rootNode.getChildren() != null) { if (rootNode.getChildren().size() > 1) {
	 * rect = false; break; } rootNode = rootNode.getChildren().get(0); } return
	 * rect; }
	 */
	public static void main(String[] args) throws IOException {
		// 计时
		long time1 = System.currentTimeMillis();
		FPtree fptree = new FPtree();
		LinkedList<LinkedList<String>> data = fptree.readCSV();
		System.out.println("Data after cleaning");
		System.out.println(data);
		// String Stringfinal=" ";
		// 将结果写回文件
		try {
			File file = new File("./csv/FPResult.txt");
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			for (String s : fptree.fp_growth(data, null)) {
				// Stringfinal.concat(s);
				// System.out.println(s);
				ps.append(s + '\n');
			}
			// ps.println(s);// 往文件里写入字符串
			// 在已有的基础上添加字符串
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long time2 = System.currentTimeMillis();
		int time = (int) ((time2 - time1));
		System.out.println("It takes：" + time + "ms！");

	}

}
