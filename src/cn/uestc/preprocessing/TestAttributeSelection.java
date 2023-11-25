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
        //��ȡ����
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("E:\\Weka-3-6\\data\\labor.arff"); //��ȡ����Դ
        Instances instances = source.getDataSet();//��������

        //��һ��
        System.out.println("Step 3. ����ɸѡ...");
        InfoGainAttributeEval ae = new InfoGainAttributeEval();//ѡ��evaluatorΪ��Ϣ����
        Ranker ranker = new Ranker();//��������ѡ��ranker
        ranker.setNumToSelect(3);//����ɸѡ�����������Ŀ
        ranker.setThreshold(0.0);//��������ֵ���ڸ���ֵ��������ɸȥ���ڴ˱�ʾֻ������Ϣ�������0������
        AttributeSelection as = new AttributeSelection();//��������ɸѡ����
        as.setEvaluator(ae);//����ɸѡ�������õ���������
        as.setSearch(ranker);//����ɸѡ�����ѡ����
        as.setInputFormat(instances);//Ϊ������ɸѡ����������Դ

        Instances newInstances = Filter.useFilter(instances, as);
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
