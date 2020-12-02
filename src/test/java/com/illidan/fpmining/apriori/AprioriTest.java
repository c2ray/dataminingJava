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
import java.net.URISyntaxException;
import java.util.List;

class AprioriTest {
    
    private Apriori apriori;
    
    private final Logger logger = LoggerFactory.getLogger(AprioriTest.class);
    
    @Test
    void testAprioriForBookexercise() throws IOException {
        apriori = new Apriori(0.22, 0.6);
        String fileName = "/fake/data1.csv";
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        
        long start = System.currentTimeMillis();
        
        apriori.getData(csvRecords);
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
        
        long end = System.currentTimeMillis();
        logger.warn("用时: {} ms", end - start);
    }
    
    
    @Test
    void testApriori() throws IOException {
        apriori = new Apriori(0.4, 0.6);
        
        // 读取csv格式的数据
        // String fileName = "/fake/data1.csv";
        String fileName = String.format("/fake/fake_data_%d.csv", 1_000_000);
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        
        // 开始计时
        long start = System.currentTimeMillis();
        
        apriori.getData(csvRecords);
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
        
        long end = System.currentTimeMillis();
        logger.warn("用时: {} ms", end - start);
        
    }
    
    
    @ParameterizedTest
    @MethodSource("suffixSource")
    void testApriori2(int fileSuffix) throws IOException {
        String fileName = String.format("/fake/fake_data_%d.csv", fileSuffix);
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(fileName);
        
        apriori.getData(csvRecords);
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
        
    }
    
    
    @Test
    @Disabled
    void testGetData() throws IOException, URISyntaxException {
        apriori.getData2("/fake/fake_data_10000000.csv");
    }
    
    static int[] suffixSource() {
        return new int[]{10, 100, 1000, 10_000, 100_000, 1_000_000};
    }
}

