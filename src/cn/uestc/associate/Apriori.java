package cn.uestc.associate;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;


/**
 * @author sunjia
 * @create 2023-10-21 15:37
 **/
public class Apriori {
 private List<Set<String>> txDB;
 private Float minSup;
 private Float minConf;
 private Integer DBCount;
 private List<ItemSet> freqItemSets;
 private Map<Item,Set<Item>> associationRules;

 /**
  *
  * @param minSup 最小支持度
  * @param minConf 最小置信度
  */
 Apriori(float minSup,float minConf){
  this.minConf=minConf;
  this.minSup=minSup;
  txDB=new ArrayList<>();
  freqItemSets=new ArrayList<>();
  associationRules=new HashMap<>();
 }

 /**
  *
  * @param path 数据文件的路径
  * @throws Exception
  */
 public void storeToDB(String path) throws Exception {
  File file=new File(path);
  FileReader fr=new FileReader(file);
  BufferedReader br=new BufferedReader(fr);
  String sp=",";
  String line;
  //将文件中的数据读入事务集数据库
  while((line=br.readLine())!=null){
   String[] temp =line.trim().split(sp);
   Set<String> set=new TreeSet<String>();
   for(int i=0;i<temp.length;i++){
    set.add(temp[i].trim());
   }
   txDB.add(set);
  }
  DBCount=txDB.size();
 }

 /**
  * @brief 寻找全部的频繁项集
  */
 public void findAllFreqItemSet(){
  ItemSet itemSet=new ItemSet();
  itemSet.initOneElementItemSet(txDB, DBCount);
  itemSet.getFreqItemSet(DBCount,minSup);
  itemSet.print(1);
  int index=0;
  freqItemSets.add(itemSet);
  //通过合并得出候选集，并在候选集基础上挑选频繁项集

  while (true){
   itemSet=itemSet.getCandidate(index+1);
   if(itemSet!=null){
    itemSet.pruning(freqItemSets.get(index));
    itemSet.initItemSet(txDB,DBCount);
    itemSet.getFreqItemSet(DBCount,minSup);
    index++;
    if(itemSet.size()!=0){
     itemSet.print(index+1);
     freqItemSets.add(itemSet);
    }
   }else{
    break;
   }
  }
 }

 /**
  * @brief 用于寻找关联规则
  */
 public void findAssociateRules(){
  for(int i=1;i<freqItemSets.size();i++){
   ItemSet freqItemSet=freqItemSets.get(i);
   Iterator<Item> it=freqItemSet.getItemSetHashMap().keySet().iterator();
   while(it.hasNext()){
    Item freqItem=it.next();
    int n=freqItem.item.size()/2;
    Set<Item> subItemComb=ProperSubsetCombination.getProperSubset(n,freqItem.item);
    for(Item subItem1:subItemComb){
     Item subItem2=new Item(freqItem.item);
     subItem2.item.removeAll(subItem1.item);
     int s1=subItem1.size();
     int s2=subItem2.size();
     float count1=freqItemSets.get(s1-1).getCount(subItem1);
     float count2=freqItemSets.get(s2-1).getCount(subItem2);
     float count=freqItemSets.get(s2+s1-1).getCount(freqItem);
     float conf1=count/count1;
     float conf2=count/count2;
     if(conf1>=minConf){
      if(associationRules.get(subItem1)==null){
       Set<Item> conclusionSet=new HashSet<>();
       conclusionSet.add(subItem2);
       associationRules.put(subItem1,conclusionSet);
      }else {
       associationRules.get(subItem1).add(subItem2);
      }
      if(conf2>=minConf){
       if(associationRules.get(subItem2)==null){
        Set<Item> conclusionSet=new HashSet<>();
        conclusionSet.add(subItem1);
        associationRules.put(subItem2,conclusionSet);
       }else {
        associationRules.get(subItem2).add(subItem1);
       }
      }
     }
    }
   }
  }
  System.out.println("关联规则（强规则）如下：");
  Iterator<Map.Entry<Item,Set<Item>>> it1=associationRules.entrySet().iterator();
  while (it1.hasNext()){
   Map.Entry<Item,Set<Item>> entry=it1.next();
   entry.getKey().print("===>{");
   for(Item item:entry.getValue()){
    item.print(";");
   }
   System.out.println("}");
  }
 }
 public static void main(String[] args) throws Exception {
 Apriori apriori =new Apriori( 0.05F, 0.05F);
 apriori.storeToDB("resource/top1000data.txt");
 apriori.findAllFreqItemSet();
 apriori.findAssociateRules();
 }

}



/**
 * @brif 该类用于描述一个项
 */
class Item{
 public Set<String> item;

 Item(){
  item=new HashSet<>();
 }

 Item(String product){
  item=new HashSet<>();
  item.add(product);
 }

 Item(Set<String> src){
  item=new HashSet<>(src);
 }

 /**
  * @brief 用于检测对应事务中是否包含该项集
  * @param transaction
  * @return 表示检测结果的布尔值
  */
 public boolean hasItemSet(Set<String> transaction){
  if(transaction.containsAll(item)){
   return true;
  }else return false;
 }

 @Override
 public boolean equals(Object o) {
  if (this == o) return true;
  if (!(o instanceof Item)) return false;
  Item item1 = (Item) o;
  return Objects.equals(item, item1.item);
 }

 @Override
 public int hashCode() {
  return item != null ? item.hashCode() : 0;
 }

 /**
  * @brief 用于获取与此Item内容相同的一个Item副本
  * @return
  */
 public Item copy(){
  Item item1=new Item(item);
  return item1;
}

 /**
  * @brief 移除该项中的一个元素
  * @param string 与该元素相同的字符串
  */
 public void remove(String string){
  item.remove(string);
}

 /**
  * @brief 获取此项的大小
  * @return
  */
 public int size(){
  return item.size();
}

 /**
  * @brief 打印该项的内容
  * @param suffix 在打印的项内容后面作为可选的添加后缀，方便分隔不同的项
  */
 public void print(String suffix){
 System.out.print(item+suffix);
}
}

/**
 * @brif 该类用于描述一个项集，以及提供用于项集的方法
 */
class ItemSet{

 private HashMap<Item,Float> itemSetHashMap;
 ItemSet(){
  itemSetHashMap=new HashMap<Item, Float>();
 }

 public HashMap<Item, Float> getItemSetHashMap() {
  return itemSetHashMap;
 }

 /**
  * @brief 用于从事务集获取对应的1项集
  * @param transactions 事务集
  * @param size 事务集大小
  */
 public void initOneElementItemSet(List<Set<String>> transactions, int size){
  for(int i=0;i<size;i++){
   Set<String> transaction=transactions.get(i);
   Iterator<String> it=transaction.iterator();
   while (it.hasNext()){
    String product=it.next();
    Item item=new Item(product);
    if(itemSetHashMap.containsKey(item)){
     float val=itemSetHashMap.get(item);
     itemSetHashMap.put(item,++val);
    }else{
     itemSetHashMap.put(item,1F);
    }
   }
  }
 }

 /**
  * @brief 使用事务集中的数据，对剪枝后的候选集进行初始化，获取对应的次数
  * @param transactions 事务集
  * @param size 事务集大小
  */
 public void initItemSet(List<Set<String>> transactions,int size){
  for(int i=0;i<size;i++){
   Set<String> transaction=transactions.get(i);
   Iterator<Item> it=itemSetHashMap.keySet().iterator();
   while (it.hasNext()){
    Item item=it.next();
    if(item.hasItemSet(transaction)){
     float val=itemSetHashMap.get(item);
     itemSetHashMap.put(item,++val);
    }
   }
  }
 }

 /**
  * 用于剪枝
  * @param lastItemSet k-1项集的集合
  */
 public void pruning(ItemSet lastItemSet){
  Iterator<Item> it=itemSetHashMap.keySet().iterator();
  while (it.hasNext()){
   Item item=it.next();
   Item item1;
   Iterator<String> baseItem=item.item.iterator();
   while (baseItem.hasNext()){
    item1=item.copy();
    item1.remove(baseItem.next());
    if(!lastItemSet.getItemSetHashMap().containsKey(item1)){
     it.remove();
     break;
    }
   }
  }
 }

 /**
  * 用于从项集中选出频繁项集
  * @param DBCount 事务集大小
  * @param minSupport 最小支持度
  */
 public void getFreqItemSet(float DBCount,float minSupport){
  Iterator<Map.Entry<Item,Float>> it=itemSetHashMap.entrySet().iterator();
  while(it.hasNext()){
   if(it.next().getValue()/DBCount<minSupport){
    it.remove();
   }
  }
 }

 public void addItemAsCandidate(Item item){
  itemSetHashMap.put(item,0F);
 }

 public int size(){
  return itemSetHashMap.size();
 }

 /**
  * 用于从此项集合并，得出下一个项集的候选集
  * @param itemSize 此项集中所有项的大小
  * @return
  */
 public ItemSet getCandidate(int itemSize){
 ItemSet itemSet=new ItemSet();
 Iterator<Item> it1=itemSetHashMap.keySet().iterator();
 while(it1.hasNext()){
  Item item1=it1.next();
  Iterator<Item> it2=itemSetHashMap.keySet().iterator();

  while (it2.hasNext()){
   Item item2=it2.next();
   Item item3=item1.copy();
   item3.item.addAll(item2.item);
   if(item3.item.size()==itemSize+1){
    itemSet.addItemAsCandidate(item3);
   }
  }
  }
 if (itemSet.size()==0){
  return null;
 }
 return itemSet;
 }

 /**
  * 用于打印此项集
  * @param size 项集中项的数量
  */
 public void print(int size){
  System.out.println("频繁"+size+"项集如下：");
  for(Item item:itemSetHashMap.keySet()){
   item.print("\n");
  }
 }

 /**
  * 用于获取项集中对应项在事务集中出现的次数
  * @param item 对应的项
  * @return
  */
 public float getCount(Item item){
  return itemSetHashMap.get(item);
 }
}