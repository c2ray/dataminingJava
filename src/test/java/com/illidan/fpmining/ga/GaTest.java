package com.illidan.fpmining.ga;

import com.illidan.fpmining.util.DataReader;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Illidan
 */
public class GaTest {
    
    private final Logger logger = LoggerFactory.getLogger(GaTest.class);
    
    @Test
    void timeFpGeneticAlgorithm() throws IOException {
        String path = "/fake/10/fake_data_1000000.csv";
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(path);
        // 计时
        long start = System.currentTimeMillis();
        
        double supportRate = 0.4;
        double confidentRate = 0.6;
        testFpGeneticAlgorithm(csvRecords, supportRate, confidentRate);
        
        long end = System.currentTimeMillis();
        System.out.println("用时: " + (end - start) + "秒");
    }
    
    
    void testFpGeneticAlgorithm(List<CSVRecord> csvRecords,
                                double supportRate,
                                double confidenceRate) {
        FpGeneticAlgorithm fpGeneticAlgorithm =
                new FpGeneticAlgorithm(
                        supportRate,
                        confidenceRate,
                        100,
                        0.95,
                        0.001,
                        10,
                        3);
        fpGeneticAlgorithm.recordChromosomeCount(csvRecords);
        fpGeneticAlgorithm.ga();
        List<String> frequentSet = fpGeneticAlgorithm.getFrequentSet();
        fpGeneticAlgorithm.getRelationRules(frequentSet);
    }
}
