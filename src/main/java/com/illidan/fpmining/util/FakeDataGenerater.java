package com.illidan.fpmining.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.random;

/**
 * @author Illidan
 */
public class FakeDataGenerater {
    
    private final Logger logger = LoggerFactory.getLogger(FakeDataGenerater.class);
    
    /**
     * 从csv文件读取的item类别
     */
    private final List<String> itemClasses;
    
    /**
     * 一条数据中最多有多少项
     */
    private int maxItemCount;
    
    public FakeDataGenerater() {
        this.itemClasses = new ArrayList<>();
    }
    
    /**
     * 从类路径下读取用于生成假数据的items
     *
     * @param path 类路径下提供item的csv文件
     */
    public void readItems(String path) throws IOException {
        CSVParser csvParser = CSVParser.parse(getClass().getResource(path),
                Charset.defaultCharset(),
                CSVFormat.DEFAULT);
        logger.debug("开始从 {} 读取数据", path);
        
        List<CSVRecord> records = csvParser.getRecords();
        records.forEach(record -> itemClasses.add(record.get(0)));
        logger.debug("从csv文件读取的item数据: {}", itemClasses);
    }
    
    /**
     * 生成n条假数据
     *
     * @param n            假数据条数
     * @param maxItemCount 一条记录中最多包含的item数
     */
    public void generateFakeDatas(int n, int maxItemCount) throws IOException, URISyntaxException {
        this.maxItemCount = maxItemCount;
        // Stream.generate().limit()
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = (StringBuffer) Stream.iterate(stringBuffer, this::generateFakeData)
                .limit(n)
                .toArray()[0];
        
        Path path = Paths.get(getClass().getResource("/").toURI()).toAbsolutePath();
        // 这里的路径不能加 /, 否则会从盘符开始: J:/...
        Path csvFilePath = path.resolve(String.format("fake/fake_data_%s.csv", n));
        logger.debug("输出文件路径: {}", csvFilePath);
        // 将结果写入到文件中
        FileWriter fileWriter = new FileWriter(csvFilePath.toFile());
        fileWriter.write(stringBuffer.toString());
        // 一定要关闭
        fileWriter.close();
    }
    
    /**
     * 生成一条假数据
     */
    private StringBuffer generateFakeData(StringBuffer stringBuffer) {
        // 添加到buffer中的item计数
        int itemAddedCount = 0;
        
        Random random = new Random();
        List<String> tempItemClasses = new ArrayList<>(itemClasses);
        stringBuffer.append("\"");
        
        // 80%的人选了1
        if (random() < 0.8) {
            String firstItem = itemClasses.get(0);
            stringBuffer.append(firstItem);
            stringBuffer.append(",");
            tempItemClasses.remove(firstItem);
            itemAddedCount++;
        }
        // 70%的人选了2
        if (random() < 0.7) {
            String secondItem = itemClasses.get(1);
            stringBuffer.append(secondItem);
            stringBuffer.append(",");
            tempItemClasses.remove(secondItem);
            itemAddedCount++;
        }
        // 60%的人选了3
        if (random() < 0.6) {
            String thirdItem = itemClasses.get(2);
            stringBuffer.append(thirdItem);
            stringBuffer.append(",");
            tempItemClasses.remove(thirdItem);
            itemAddedCount++;
        }
        
        // 30% 的人只浏览了5项以下的数据
        if (random() < 0.3) {
            // [1, 5]
            int itemCount = random.nextInt(5) + 1;
            
            for (int i = itemAddedCount; i < itemCount; i++) {
                // 随机数范围[3, itemClasses.size()]
                int itemClasseIndex = 0;
                try {
                    itemClasseIndex = random.nextInt(tempItemClasses.size());
                } catch (Exception exception) {
                    //
                }
                String itemToBeAdded = tempItemClasses.get(itemClasseIndex);
                stringBuffer.append(itemToBeAdded);
                tempItemClasses.remove(itemToBeAdded);
                // 逗号分隔
                stringBuffer.append(",");
            }
        } else {
            // 70% 的人的浏览数据在5条以上
            // [6, maxItemCount]
            int itemCount = random.nextInt(maxItemCount - 5) + 6;
            for (int i = itemAddedCount; i < itemCount; i++) {
                // 随机数范围[3, itemClasses.size()]
                int itemClasseIndex = 0;
                try {
                    itemClasseIndex = random.nextInt(tempItemClasses.size());
                } catch (Exception exception) {
                    //
                }
                String itemToBeAdded = tempItemClasses.get(itemClasseIndex);
                stringBuffer.append(itemToBeAdded);
                tempItemClasses.remove(itemToBeAdded);
                // 逗号分隔
                stringBuffer.append(",");
            }
        }
        
        //删除最后一个,
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        stringBuffer.append("\"");
        // 换行
        stringBuffer.append("\n");
        return stringBuffer;
    }
    
}
