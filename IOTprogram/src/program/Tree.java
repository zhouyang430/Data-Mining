package program;
import java.util.ArrayList;
import java.util.List;

public class Tree implements Comparable<Tree>{

    private String name; // 节点名称
    private int number; // 计数
    private Tree parent; // 父节点
    private List<Tree> children; // 子节点
    private Tree nextHomonym; // 下一个同名节点
    public Tree() {
		super();
	}

	public Tree(String name, int count) {
		super();
		this.name = name;
		this.number = count;
		this.children = new ArrayList<Tree>();
		this.parent = null;
		this.nextHomonym= null;
	}

	public Tree(String name, int count, Tree parent) {
		super();
		this.name = name;
		this.number= count;
		this.children = new ArrayList<Tree>();
		this.parent = parent;
		this.nextHomonym = null;
	}


    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int num) {
		this.number = num;
	}
	
	public boolean hasParent(){
		return this.parent == null ? false : true;
	}
	public Tree getParent() {
		return parent;
	}

	public void setParent(Tree parent) {
		this.parent = parent;
	}

	public List<Tree> getChildren() {
		return children;
	}

	public void setChildren(List<Tree> children) {
		this.children = children;
	}

	public Tree getNextHomonym() {
		return nextHomonym;
	}

	public void setNextHomonym(Tree nextHomonym) {
		this.nextHomonym = nextHomonym;
	}
	public boolean hasNextHomonym(){
		return this.nextHomonym == null ? false : true;
	}

    /**
     * @param child
     */
    public void addChild(Tree child) {
        if (this.getChildren() == null) {
            List<Tree> list = new ArrayList<Tree>();
            list.add(child);
            this.setChildren(list);
        } else {
            this.getChildren().add(child);
        }
    }
    public Tree findChild(String name) {
        List<Tree> children = this.getChildren();
        if (children != null) {
            for (Tree child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void countIncrement(int n)
    {
    	this.number+=n;
    }
    @Override
    public int compareTo(Tree arg0) {
        // TODO Auto-generated method stub
        int count0 = arg0.getNumber();
        //跟默认比较大小相反，导致调Array.sort()时降序排列
        return count0 - this.number;
    }
}