package com.codecat.udtf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;

public class MyUDTF extends GenericUDTF {
    /*
        将一个任意分割符的字符串切割成独立的单词
    */

    private ArrayList<String> outList = new ArrayList<>();

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        // 1. 定义输出数据的列名和类型
        ArrayList<String> fieldNames = new ArrayList<>();
        List<ObjectInspector> fieldOIs = new ArrayList<>();

        // 2. 增加输出数据的列名和类型
        fieldNames.add("lineToWord");

        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] objects) throws HiveException {
        // 1. 获取原始数据
        String arg = objects[0].toString();

        // 2. 获取数据传入的第二个参数，此处为分隔符
        String splitKey = objects[1].toString();

        // 3. 将原始数据按照传入的分隔符进行切分
        String[] fields = arg.split(splitKey);

        // 4. 遍历切分后的结果，并写出
        for (String field : fields) {
            // 集合为复用的，首先清空集合
            outList.clear();

            // 将每一个单词添加至集合
            outList.add(field);

            // 将集合内容写出
            forward(outList);
        }
    }

    @Override
    public void close() throws HiveException {

    }
}
