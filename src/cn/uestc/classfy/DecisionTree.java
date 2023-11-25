package cn.uestc.classfy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecisionTree {

	private ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
    private ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 存储每个属性可能的取值
    private ArrayList<String[]> dataSet = new ArrayList<String[]>();; // 原始数据

	public static void main(String[] args) {
		DecisionTree d = new DecisionTree("resource/test.arff");
		HashMap<String, Object> t = d.createTree(d.dataSet);
		String label = d.classify(t, new String[]{"yes","yes"});
		System.out.print(label);
	}
	
	public DecisionTree(String filePath)
	{
		readARFF(new File(filePath));
	}
	
	public void readARFF(File file) {
		String patternString = "@attribute (.*)[{](.*?)[}]";
		try {
			FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Pattern pattern = Pattern.compile(patternString);
            while ((line = br.readLine()) != null) {
            	if (line.startsWith("%") || line.equals("")) {
					continue;
				}
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    attribute.add(matcher.group(1).trim());
                    String[] values = matcher.group(2).split(",");
                    ArrayList<String> al = new ArrayList<String>(values.length);
                    for (String value : values) {
                        al.add(value.trim());
                    }
                    attributevalue.add(al);
                } else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if(line=="")
                            continue;
                        line = line.replace("\'", "");
                        String[] row = line.split(",");
                        dataSet.add(row);
                    }
                } else {
                    continue;
                }
            }
            br.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

	//计算log2(x)
	static public double log(double value, double base)
	{
		return Math.log(value) / Math.log(base);
	}
	
	//计算信息熵
	public double calcShannonEnt(ArrayList<String[]> data)
	{
		Map<String,Integer> typeMap=new HashMap<String,Integer>();
     for(String[] valueList:data){
		 String type=valueList[valueList.length-1];
		 if(typeMap.containsKey(type)){
			 int count=typeMap.get(type);
			 count++;
			 typeMap.put(type,count);
		 }else {
			 typeMap.put(type,1);
		 }
	 }
	 double sum=0;
	 Iterator<String> it=typeMap.keySet().iterator();
	 int size=data.size();
	 while (it.hasNext()){
		 int count=typeMap.get(it.next());
		 double p=(double) count/(double) size;
		 sum+=(-p)*log(p,2);
	 }
	 return sum;
	}
	
	//划分数据集
	private ArrayList<String[]> splitDataSet(ArrayList<String[]> data, int featureIndex, String value)
	{
		ArrayList<String[]> selectedDs=new ArrayList<>();
		for(String[] values:data){
			if(value.equals(values[featureIndex])){
				String[] copy=new String[values.length];
				System.arraycopy(values,0,copy,0,values.length);
				copy[featureIndex]=null;
				selectedDs.add(copy);
			}else if(values[featureIndex]==null){//
				return null;
			}
		}
		return selectedDs;
	}
	
	//返回信息增益最大的划分属性
	int chooseBestFeatureToSplit(ArrayList<String[]> data)
	{
		int selectedIndex=0;
		int featureCount=attribute.size()-1;
		double minEnt=Double.MAX_VALUE;
		for(int i=0;i< featureCount;i++){
			ArrayList<String> featureValues=attributevalue.get(i);
			double ent=0;
			for(String value:featureValues){
				ArrayList<String[]> splitDs=splitDataSet(data,i,value);
				if(splitDs==null){
					ent=Double.MAX_VALUE;
					break;
				}else {
					ent+=(double) splitDs.size()/(double) data.size()*calcShannonEnt(splitDs);
				}
			}
			if(ent<minEnt){
				selectedIndex=i;
				minEnt=ent;
			}
		}
		if(minEnt==Double.MAX_VALUE){
			return -1;
		}
		return selectedIndex;
	}
	
	//投票，返回支持度最高的类标签
	String majorityCount(Vector<String> classList)
	{
		HashMap<String, Integer> classCount = new HashMap<String, Integer>();
		int maxVote = 0;
		String majorityClass = null;
		
		for (String classType : classList) {
			if (!classCount.containsKey(classType)) {
				classCount.put(classType, 1);
			}
			else 
			{
				classCount.put(classType, classCount.get(classType) + 1);
			}
		}
		
		Iterator<String> iterator = classCount.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (classCount.get(key) > maxVote) {
				maxVote = classCount.get(key);
				majorityClass = key;
			}
		}
		return majorityClass;	
	}

	//创建决策树
	HashMap<String, Object> createTree(ArrayList<String[]> data)
	{
		Vector<String> classList = new Vector<String>();
		HashMap<String, Object> myTree = new HashMap<String, Object>();
		
		for (int i = 0; i < data.size(); i++) {
			String[] ithData = data.get(i);
			classList.add(ithData[ithData.length - 1]);
		}
		

		//TO-DO:

		//判断该分支下实例的类别是否全部相同，相同则停止划分，并返回该分支（叶子）
		Set<String> classSet=new HashSet<>(classList);
		if(classSet.size()==classList.size()){
			myTree.put("type",classList.get(0));
			myTree.put("isLeaf",true);
		}
		//已经没有划分属性，类别使用多数表决，返回分支
		int chooseIndex=chooseBestFeatureToSplit(data);
		if(chooseIndex==-1){
			String maxType=majorityCount(classList);

			myTree.put("type",maxType);
			myTree.put("isLeaf",true);
		}
		
		//选取最佳的划分特征
		else{
			myTree.put("isLeaf",false);
			myTree.put("chooseAttr",chooseIndex);
			for(String attr:attributevalue.get(chooseIndex)){
				attr=attr.replace("'","");
				ArrayList<String[]> childData=splitDataSet(data,chooseIndex,attr);
				HashMap<String,Object>childTree;
				if(childData.size()==0){
					childTree=new HashMap<>();
					childTree.put("type",majorityCount(classList));

					childTree.put("isLeaf",true);
				}else {
					 childTree=createTree(childData);
				}
				if(myTree.containsKey("child")){
					HashMap<String,Object>childTrees= (HashMap<String, Object>) myTree.get("child");
					childTrees.put(attr,childTree);
				}else {
					HashMap<String,Object>childTrees= new HashMap<>();
					childTrees.put(attr,childTree);
					myTree.put("child",childTrees);
				}
			}
		}
		
		//递归地构建决策树
		
		return myTree;
	}
	
	//遍历树丛根到叶子，从而得到预测结果
	String classify(HashMap<String, Object> tree, String[] testData)
	{
		while (!(boolean) tree.get("isLeaf")){
			int chooseAttr=(int) tree.get("chooseAttr");
			HashMap<String,Object> children= (HashMap<String, Object>) tree.get("child");
			tree= (HashMap<String, Object>) children.get(testData[chooseAttr]);
		}
		return (String) tree.get("type");
	}
}
