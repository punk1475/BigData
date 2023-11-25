package cn.uestc.preprocessing;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 * @author sunjia
 * @create 2023-10-21 14:13
 **/
public class TestNormalize {
    public static void main(String[] args) throws Exception{
       //��ȡ����
        DataSource source = new DataSource("E:\\Weka-3-6\\data\\iris.arff"); //��ȡ����Դ
        Instances instances = source.getDataSet();//��������

        //��һ��
        System.out.println(" ��һ��...\n");
        Normalize norm = new Normalize();//����һ����һ��filter
        norm.setInputFormat(instances);//Ϊfilter��������
        Instances newInstances = Filter.useFilter(instances, norm);//�õ���һ���������
        System.out.println(" �����ӡ...\n");
        //��ӡ�����printAttribute�����ں��������
        printAttribute(newInstances);
    }
    public static void printAttribute(Instances instances) throws Exception
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
        System.out.println(" ��������ļ�...");
        ConverterUtils.DataSink.write("E:\\Weka-3-6\\data\\iris_new.arff",instances);
    }

}
