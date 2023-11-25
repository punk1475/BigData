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
       //读取数据
        DataSource source = new DataSource("E:\\Weka-3-6\\data\\iris.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        //归一化
        System.out.println(" 归一化...\n");
        Normalize norm = new Normalize();//建立一个归一化filter
        norm.setInputFormat(instances);//为filter导入数据
        Instances newInstances = Filter.useFilter(instances, norm);//得到归一化后的数据
        System.out.println(" 结果打印...\n");
        //打印结果（printAttribute函数在后面给出）
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
        //打印实例
        int numOfInstance = instances.numInstances();
        for(int i = 0; i < numOfInstance; ++i)
        {
            Instance instance = instances.instance(i);
            System.out.print(instance.toString() + "     " + '\n');
        }
        System.out.println(" 输出到新文件...");
        ConverterUtils.DataSink.write("E:\\Weka-3-6\\data\\iris_new.arff",instances);
    }

}
