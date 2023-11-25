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
     * ˽�к����������ڴ���DBScan����ʱ��ȡ�����ļ�
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
 * ����ʵ�־����㷨
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
     * ��ӡ�����ľ�����
     */
    public void print(){
    System.out.println("cluster����"+clusters.size()+"��");
    System.out.println("����������");
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
 * ���������ļ��е�������
 */
class Data{
    int index;//���ڱ�ǣ�������ͬ�����ݣ���ʹ������¼����ֵ��С��ȫ��ͬ��ҲӦ�����������ݡ�
    double[] data;//����ֵ
    boolean visited;//�Ƿ�visit��
    Cluster cluster;//�����ľ���
    boolean kernel;//�Ƿ��Ǻ��Ķ���
    Set<Data> objs;//���data���ڽ��Ķ���

    /**
     * ����visit���󣬻�ȡ������Χ���ٽ����󣬲���������Ϊvisited
     * @param dataSet ȫ�������ݼ�
     * @param unvisitedSet ��û��visit�������ݼ�
     * @param epsilon �趨�İ뾶��ֵ
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
     * ��������data֮��ľ���
     * @param data ��һ��������
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
     * ������������Ϊ���Ķ���
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
 * ������������
 */
class Cluster{
    List<Data> kernels;//�����еĺ��Ķ���

    Cluster(){
        kernels=new ArrayList<>();
    }

    /**
     * ��Ӻ��Ķ��󵽴˾���
     * @param kernel ���Ķ���
     */
    public  void addKernel(Data kernel){
        if(kernel.cluster==null){
            kernel.cluster=this;
            kernels.add(kernel);
        }
    }
}

/**
 * ���ڿ��Ʋ���Data�����һ��������
 */
class DataFactory{
    private  static int index=0;
    public static Data createData(double[] data){
        return new Data(data,index++);
    }
}

