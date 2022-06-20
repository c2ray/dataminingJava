package com.illidan.fpmining.apriori;

import com.illidan.fpmining.util.DataReader;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

class AprioriTest {
    
    private Apriori apriori;
    
    private final Logger logger = LoggerFactory.getLogger(AprioriTest.class);
    
    @Disabled
    @Test
    void testAprioriForBookexercise() throws IOException {
        String fileName = "/fake/data1.csv";
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        // 开始计时
        long start = System.currentTimeMillis();
        
        double minSupport = 0.22;
        double minConfidence = 0.6;
        testApriori(csvRecords, minSupport, minConfidence);
        
        long end = System.currentTimeMillis();
        logger.warn("用时: {} ms", end - start);
    }
    
    
    @Test
    void timeApriori() throws IOException {
        // 读取csv格式的数据
        String fileName = String.format("/fake/10/fake_data_%d.csv", 1_000000);
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        // 开始计时
        long start = System.currentTimeMillis();
        
        double minSupport = 0.4;
        double minConfidence = 0.6;
        testApriori(csvRecords, minSupport, minConfidence);
        
        long end = System.currentTimeMillis();
        System.out.println("用时: " + (end - start) + " ms");
    }
    
    void testApriori(List<CSVRecord> csvRecords, double minSupport, double minConfidence) {
        apriori = new Apriori(minSupport, minConfidence);
        apriori.getData(csvRecords);
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
    }
    
    @Disabled
    @ParameterizedTest
    @MethodSource("suffixSource")
    void testApriori2(int fileSuffix) throws IOException {
        String fileName = String.format("/fake/fake_data_%d.csv", fileSuffix);
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        apriori.getData(csvRecords);
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
    }
    
    
    static int[] suffixSource() {
        return new int[]{10, 100, 1000, 10_000, 100_000, 1_000_000};
    }
}

