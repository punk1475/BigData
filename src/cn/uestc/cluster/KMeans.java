package cn.uestc.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
//import util.PicUtility;
/**
 * @author sunjia
 * @create 2023-11-20 23:07
 **/
public class KMeans {
    ArrayList<double[]> dataSet;
    int clusterNum;
    int dim;

    KMeans(int clusterNum){
       this.clusterNum=clusterNum;
       dataSet=new ArrayList<>();
    }

    public void loadDataSet(String path)  {
        File file=new File(path);
        FileReader fr=null;
        BufferedReader bufferedReader=null;
        try{
            fr=new FileReader(file);
            bufferedReader=new BufferedReader(fr);
            String line=null;
            while((line=bufferedReader.readLine())!=null){
                String[] str=line.trim().split(" ");
                double[] data=new double[str.length];
                for(int i=0;i<data.length;i++){
                    data[i]=Double.parseDouble(str[i]);
                }
                dataSet.add(data);
            }
            dim=dataSet.get(0).length;
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

    public void cluster(){
        Random rand=new Random();
        double[][] clusterMeans=new double[clusterNum][dim];
        for(int n=0;n<clusterNum;n++){
            double[] data=new double[dim];
            for(int m=0;m<dim;m++){
                data[m]=rand.nextDouble()*100;
            }
            clusterMeans[n]=data;
        }
        boolean isContinue=true;
        while (isContinue){
            isContinue=false;
            double[][] nextClusterMeans=new double[clusterNum][dim];
            int[] clusterDataNum=new int[clusterNum];
            for(int n=0;n< dataSet.size();n++){
                double minDis=Double.MAX_VALUE;
                int whoCluster=-1;
                for(int m=0;m<clusterNum;m++){
                    double distance=getDist(clusterMeans[m],dataSet.get(n));
                    if(distance<minDis){
                        whoCluster=m;
                        minDis=distance;
                    }
                }
                clusterDataNum[whoCluster]++;
                for(int i=0;i<dim;i++){
                    nextClusterMeans[whoCluster][i]+= dataSet.get(n)[i];
                }
            }

            for(int i=0;i<clusterNum;i++){
                for(int j=0;j<dim;j++){
                    if(clusterDataNum[i]!=0)
                        nextClusterMeans[i][j]/=clusterDataNum[i];
                    else
                        nextClusterMeans[i][j]=Math.random()*100;
                }
            }

            for(int i=0;i<clusterNum;i++){
                if(this.getDist(nextClusterMeans[i],clusterMeans[i])!=0){
                    isContinue=true;
                }
            }
            clusterMeans=nextClusterMeans;

            ArrayList<ArrayList<double[]>> clusters=new ArrayList<>();
            for(int n=0;n<clusterNum;n++){
                clusters.add(new ArrayList<>());
            }
            for(int n=0;n< dataSet.size();n++){
                double minDis=Double.MAX_VALUE;
                int whoCluster=-1;
                for(int m=0;m<clusterNum;m++){
                    double distance=this.getDist(clusterMeans[m],dataSet.get(n));
                    if(distance<minDis){
                        whoCluster=m;
                        minDis=distance;
                    }
                }
                clusters.get(whoCluster).add(dataSet.get(n));
            }
            double[][][] datas=new double[clusterNum][][];
            for(int n=0;n<clusterNum;n++){
                double[][] cluster=new double[clusters.get(n).size()][];
                for(int m=0;m<cluster.length;m++){
                    cluster[m]=clusters.get(n).get(m);
                }
                datas[n]=cluster;
            }
            System.out.println("cluster mean:");
            for(int n=0;n<clusterMeans.length;n++){
                for(double x:clusterMeans[n]){
                    System.out.print(x+" ");
                }
                System.out.println();
            }
          //  PicUtility.show(datas,clusterNum);
        }
    }

    private  double getDist(double[]test,double[]data){
        double sum=0;
        for(int n=0;n<test.length;n++){
            sum+=(test[n]-data[n])*(test[n]-data[n]);
        }
        return Math.sqrt(sum);
    }
    public static void main(String[] args) {
        KMeans kMeans=new KMeans(3);
        kMeans.loadDataSet("resource/data");
        kMeans.cluster();
    }
}
