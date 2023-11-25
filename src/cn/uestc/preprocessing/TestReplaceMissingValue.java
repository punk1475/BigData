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
        //��ȡ����
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("E:\\Weka-3-6\\data\\labor.arff"); //��ȡ����Դ
        Instances instances = source.getDataSet();//��������

        //��һ��
        System.out.println("Step 3. ����ȱʧֵ����...");
        ReplaceMissingValues rmv = new ReplaceMissingValues();
        rmv.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances, rmv);
        //��ӡ�����printAttribute�����ں��������
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
        //��ӡʵ��
        int numOfInstance = instances.numInstances();
        for(int i = 0; i < numOfInstance; ++i)
        {
            Instance instance = instances.instance(i);
            System.out.print(instance.toString() + "     " + '\n');
        }
    }
}
