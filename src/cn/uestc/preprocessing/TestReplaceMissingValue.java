package cn.uestc.preprocessing;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * @author sunjia
 * @create 2023-10-21 14:19
 **/
public class TestReplaceMissingValue {
    public static void main(String[] args) throws Exception{
        //读取数据
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("E:\\Weka-3-6\\data\\labor.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        //归一化
        System.out.println("Step 3. 数据缺失值处理...");
        ReplaceMissingValues rmv = new ReplaceMissingValues();
        rmv.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances, rmv);
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
