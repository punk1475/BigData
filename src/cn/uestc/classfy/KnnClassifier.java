package cn.uestc.classfy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class KnnClassifier {
	ArrayList<Data> trainSet;
	ArrayList<Data> testSet;
	int k;
	
	public static void main(String args[])
	{
		KnnClassifier knn = new KnnClassifier("resource/iris.2D.test.arff",2);
		knn.predict("resource/iris.2D.test.arff");
	}
	
	public KnnClassifier(String filePath, int k)
	{
		trainSet = readARFF(new File(filePath));
		this.k = k;
	}
	
	//��ȡarff�ļ�����attribute��attributevalue��data��ֵ
	public ArrayList<Data> readARFF(File file)
	{
		ArrayList<Data> data = new ArrayList<Data>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) 
			{
				if (line.startsWith("@data")) 
				{
					while ((line = br.readLine()) != null) 
					{
						if(line=="")	
							continue;
						String[] row = line.split(",");
						double[] value = new double[row.length - 1];
						String label = row[row.length-1];
						for (int i = 0; i < row.length - 1; i++)
						{
							value[i] = Double.valueOf(row[i]);
						}
						Data d = new Data(value, label);
						data.add(d);
					}
				} else 
				{
					continue;
				}
			}
			br.close();
			return data;
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
			return null;
		}
	}

	public void predict(String testPath){
		testSet = readARFF(new File(testPath));
		int correct = 0;
		for(int n=0;n<testSet.size();n++){
			Data data = testSet.get(n);
			String predict = classify(data);
			boolean flag;
			if( flag = predict.equals(data.label)){
				correct++;
			}
			System.out.println(flag + ":\t***Predicted label:"+predict+"\tdata label:" + data.label+"***");
		}
		System.out.println("Accuracy: "+correct*100.0/testSet.size() +"%");
	}
	
	public String classify(Data test)
	{
		double[] distances = new double[trainSet.size()];

		for (int n = 0; n < trainSet.size(); n++)
		{
			distances[n] = getDist(test, trainSet.get(n));
		}
		HashMap<String, Integer> voteMap = new HashMap<String, Integer>();

		for (int n = 0; n < k; n++)
		{
			double min = Integer.MAX_VALUE;
			int minIndex = -1;
			for (int m = 0; m < trainSet.size(); m++)
			{
				if (distances[m] < min)
				{
					min = distances[m];
					minIndex = m;
				}
			}
			if (minIndex == -1)
			{
				System.out.println("error");
				System.exit(0);
			}
			distances[minIndex] = Integer.MAX_VALUE;
			
			String key = trainSet.get(minIndex).label;
			if (voteMap.containsKey(key))
			{
				Integer value = voteMap.get(trainSet.get(minIndex).label);
				voteMap.put(trainSet.get(minIndex).label, value + 1);
			}
			else
			{
				voteMap.put(key, 1);
			}
		}

		Iterator<String> i = voteMap.keySet().iterator();
		String predict = null;
		int maxVote = 0;
		while (i.hasNext())
		{
			String key = i.next();
			if (voteMap.get(key) > maxVote)
			{
				maxVote = voteMap.get(key);
				predict = key;
			}
		}
		return predict;
	}

	private double getDist(Data test, Data data)
	{
		double sum = 0;
		for (int n = 0; n < test.data.length; n++)
		{
			sum +=  (test.data[n]-data.data[n])*(test.data[n]-data.data[n]);
		}
		return Math.sqrt(sum);
	}

	private class Data {
		double[] data;
		String label;

		Data(double[] data, String label)
		{
			this.data = data;
			this.label = label;
		}
	}
}
