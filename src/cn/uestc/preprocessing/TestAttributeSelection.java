package cn.uestc.preprocessing;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * @author sunjia
 * @create 2023-10-21 14:33
 **/
public class TestAttributeSelection {
    public static void main(String[] args) throws Exception{
        //读取数据
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("E:\\Weka-3-6\\data\\labor.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        //归一化
        System.out.println("Step 3. 特征筛选...");
        InfoGainAttributeEval ae = new InfoGainAttributeEval();//选择evaluator为信息增益
        Ranker ranker = new Ranker();//评估函数选择ranker
        ranker.setNumToSelect(3);//设置筛选的最大特征数目
        ranker.setThreshold(0.0);//评估特征值低于该阈值的特征被筛去，在此表示只留下信息增益大于0的特征
        AttributeSelection as = new AttributeSelection();//建立特征筛选对象
        as.setEvaluator(ae);//设置筛选对象所用的评估函数
        as.setSearch(ranker);//设置筛选对象的选择函数
        as.setInputFormat(instances);//为该特征筛选对象传入数据源

        Instances newInstances = Filter.useFilter(instances, as);
        //打印结果（printAttribute函数在后面给出）
        printAttribute(newInstances);
    }
    public static void printAttribute(Instances instances)
    {
        int numOfAttributes = instances.numAttributes();
        for(int i = 0; i < numOfAttributes ;++i)
        {
            Attribute attribute = instances.attribute(i);
            System.out.print(attribute.name() + "     ");
        }
        System.out.println();
        //打印实例
        int numOfInstance = instances.numInstances();
        for(int i = 0; i < numOfInstance; ++i)
        {
            Instance instance = instances.instance(i);
            System.out.print(instance.toString() + "     " + '\n');
        }
    }
}
