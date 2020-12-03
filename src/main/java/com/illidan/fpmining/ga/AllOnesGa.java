package com.illidan.fpmining.ga;

import com.illidan.fpmining.util.DataReader;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Illidan
 */
public class AllOnesGa {
    
    private static final Logger logger = LoggerFactory.getLogger(AllOnesGa.class);
    
    
    public static void main(String[] args) throws IOException {
        
        String path = "/fake/fake_data_100000.csv";
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(path);
        
        long start = System.currentTimeMillis();
        FpGeneticAlgorithm fpGeneticAlgorithm =
                new FpGeneticAlgorithm(0.4,
                        0.6, 100, 0.95,
                        0.001, 5, 100);
        fpGeneticAlgorithm.recordChromosomeCount(csvRecords);
        fpGeneticAlgorithm.ga();
        List<String> frequentSet = fpGeneticAlgorithm.getFrequentSet();
        fpGeneticAlgorithm.getRelationRules(frequentSet);
        
        long end = System.currentTimeMillis();
        logger.info("用时: {} ms", (end - start));
    }
    
}
