package cn.uestc.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author sunjia
 * @create 2023-11-22 13:12
 **/
public class DBScan {
private double epsilon;
private int minSamples;
private Set<Data> unvisitedDataSet;
private Set<Data> dataSet;
private List<Cluster> clusters;
private Set<Data> noise;
DBScan(String filePath,double epsilon,int minSamples){
    this.epsilon=epsilon;
    this.minSamples=minSamples;
    unvisitedDataSet=new HashSet<>();
    dataSet=new HashSet<>();
    clusters=new ArrayList<>();
    noise=new HashSet<>();
    readFile(filePath);
}

    /**
     * 私有函数，用于在创建DBScan对象时读取数据文件
     * @param path
     */
    private void readFile(String path){
    File file=new File(path);
    FileReader fr=null;
    BufferedReader bufferedReader=null;
    try{
        fr=new FileReader(file);
        bufferedReader=new BufferedReader(fr);
        String line;
        while((line=bufferedReader.readLine())!=null){
            String[] str=line.trim().split(" ");
            Data data=DataFactory.createData(new double[str.length]);
            for(int i=0;i<data.data.length;i++){
                data.data[i]=Double.parseDouble(str[i]);
            }
            dataSet.add(data);
            unvisitedDataSet.add(data);
        }
    }catch (Exception e){
        e.printStackTrace();
    }finally {
        if(bufferedReader!=null){
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(fr!=null){
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
/**
 * 用于实现聚类算法
 */
public void cluster(){
    while(!unvisitedDataSet.isEmpty()){
        Data choice=unvisitedDataSet.iterator().next();
        choice.visit(dataSet,unvisitedDataSet,epsilon);
        if(choice.objs.size()>=minSamples){
            choice.initToKernel();
            Cluster cluster =new Cluster();
            cluster.addKernel(choice);
            clusters.add(choice.cluster);
            Set<Data> temp=new HashSet<>(choice.objs);
            while (!temp.isEmpty()){
                noise.removeAll(temp);
                Iterator<Data> it=temp.iterator();
                Set<Data> replace=new HashSet<>();
                while (it.hasNext()){
                    Data obj=it.next();
                    if(!obj.visited){
                        obj.visit(dataSet,unvisitedDataSet,epsilon);
                        if(obj.objs.size()>=minSamples){
                            obj.initToKernel();
                            choice.cluster.addKernel(obj);
                            replace.addAll(obj.objs);
                        }
                    }
                }
                replace.removeAll(temp);
                temp=replace;
            }
        }else {
            noise.add(choice);
        }
    }
}

    /**
     * 打印产生的聚类结果
     */
    public void print(){
    System.out.println("cluster共计"+clusters.size()+"个");
    System.out.println("噪声点如下");
    Iterator<Data> it=noise.iterator();
    while (it.hasNext()){
        System.out.println(Arrays.toString(it.next().data));
    }
}

    public static void main(String[] args) {
        DBScan dbScan=new DBScan("resource/data",0.9,6);
        dbScan.cluster();
        dbScan.print();
    }
}

/**
 * 用于描述文件中的数据项
 */
class Data{
    int index;//用于标记，以区别不同的数据，即使两条记录的数值大小完全相同，也应该是两个数据。
    double[] data;//数据值
    boolean visited;//是否visit过
    Cluster cluster;//所属的聚类
    boolean kernel;//是否是核心对象
    Set<Data> objs;//与该data点邻近的对象

    /**
     * 用于visit对象，获取对象周围的临近对象，并将对象标记为visited
     * @param dataSet 全部的数据集
     * @param unvisitedSet 还没有visit过的数据集
     * @param epsilon 设定的半径阈值
     */
    public void visit(Set<Data> dataSet,Set<Data> unvisitedSet,double epsilon){
        Iterator<Data> it=dataSet.iterator();
        while (it.hasNext()){
            Data elem=it.next();
            double dis=distance(elem);
            if(dis<=epsilon){
                objs.add(elem);
            }
        }
        visited=true;
        unvisitedSet.remove(this);
    }

    Data(double[] data,int index){
        this.data=data;
        cluster=null;
        kernel=false;
        objs=new HashSet<>();
        visited=false;
        this.index=index;
    }

    /**
     * 计算两个data之间的距离
     * @param data 另一个数据项
     * @return
     */
    private double distance(Data data) {
        double sum = 0;
        int size=this.data.length;
        for(int i=0;i<size;i++){
            sum+=(Math.pow(this.data[i]-data.data[i],2));
        }
        //System.out.println(Math.sqrt(sum)+":"+Arrays.toString(this.data)+","+Arrays.toString(data.data));
        return Math.sqrt(sum);
    }

    /**
     * 将此数据项标记为核心对象
     */
    public void initToKernel(){
        kernel=true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;

        Data data1 = (Data) o;

        if (index != data1.index) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        return result;
    }
}

/**
 * 用于描述聚类
 */
class Cluster{
    List<Data> kernels;//聚类中的核心对象

    Cluster(){
        kernels=new ArrayList<>();
    }

    /**
     * 添加核心对象到此聚类
     * @param kernel 核心对象
     */
    public  void addKernel(Data kernel){
        if(kernel.cluster==null){
            kernel.cluster=this;
            kernels.add(kernel);
        }
    }
}

/**
 * 用于控制产生Data对象的一个工具类
 */
class DataFactory{
    private  static int index=0;
    public static Data createData(double[] data){
        return new Data(data,index++);
    }
}

