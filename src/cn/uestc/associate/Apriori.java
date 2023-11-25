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
  * @param minSup ��С֧�ֶ�
  * @param minConf ��С���Ŷ�
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
  * @param path �����ļ���·��
  * @throws Exception
  */
 public void storeToDB(String path) throws Exception {
  File file=new File(path);
  FileReader fr=new FileReader(file);
  BufferedReader br=new BufferedReader(fr);
  String sp=",";
  String line;
  //���ļ��е����ݶ����������ݿ�
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
  * @brief Ѱ��ȫ����Ƶ���
  */
 public void findAllFreqItemSet(){
  ItemSet itemSet=new ItemSet();
  itemSet.initOneElementItemSet(txDB, DBCount);
  itemSet.getFreqItemSet(DBCount,minSup);
  itemSet.print(1);
  int index=0;
  freqItemSets.add(itemSet);
  //ͨ���ϲ��ó���ѡ�������ں�ѡ����������ѡƵ���

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
  * @brief ����Ѱ�ҹ�������
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
  System.out.println("��������ǿ�������£�");
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
 * @brif ������������һ����
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
  * @brief ���ڼ���Ӧ�������Ƿ�������
  * @param transaction
  * @return ��ʾ������Ĳ���ֵ
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
  * @brief ���ڻ�ȡ���Item������ͬ��һ��Item����
  * @return
  */
 public Item copy(){
  Item item1=new Item(item);
  return item1;
}

 /**
  * @brief �Ƴ������е�һ��Ԫ��
  * @param string ���Ԫ����ͬ���ַ���
  */
 public void remove(String string){
  item.remove(string);
}

 /**
  * @brief ��ȡ����Ĵ�С
  * @return
  */
 public int size(){
  return item.size();
}

 /**
  * @brief ��ӡ���������
  * @param suffix �ڴ�ӡ�������ݺ�����Ϊ��ѡ����Ӻ�׺������ָ���ͬ����
  */
 public void print(String suffix){
 System.out.print(item+suffix);
}
}

/**
 * @brif ������������һ������Լ��ṩ������ķ���
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
  * @brief ���ڴ����񼯻�ȡ��Ӧ��1�
  * @param transactions ����
  * @param size ���񼯴�С
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
  * @brief ʹ�������е����ݣ��Լ�֦��ĺ�ѡ�����г�ʼ������ȡ��Ӧ�Ĵ���
  * @param transactions ����
  * @param size ���񼯴�С
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
  * ���ڼ�֦
  * @param lastItemSet k-1��ļ���
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
  * ���ڴ����ѡ��Ƶ���
  * @param DBCount ���񼯴�С
  * @param minSupport ��С֧�ֶ�
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
  * ���ڴӴ���ϲ����ó���һ����ĺ�ѡ��
  * @param itemSize �����������Ĵ�С
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
  * ���ڴ�ӡ���
  * @param size ����������
  */
 public void print(int size){
  System.out.println("Ƶ��"+size+"����£�");
  for(Item item:itemSetHashMap.keySet()){
   item.print("\n");
  }
 }

 /**
  * ���ڻ�ȡ��ж�Ӧ���������г��ֵĴ���
  * @param item ��Ӧ����
  * @return
  */
 public float getCount(Item item){
  return itemSetHashMap.get(item);
 }
}