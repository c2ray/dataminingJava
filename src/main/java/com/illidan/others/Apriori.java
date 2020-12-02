package com.illidan.others;

import apriori4j.AnalysisResult;
import apriori4j.AprioriAlgorithm;
import apriori4j.AprioriTimeoutException;
import apriori4j.Transaction;
import com.illidan.fpmining.util.DataReader;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Illidan
 */
public class Apriori {
    
    public static void main(String[] args) throws IOException, AprioriTimeoutException {
        // 读取数据
        // String filePath = "/fake/data1.csv";
        String filePath = "/fake/fake_data_100000.csv";
        List<CSVRecord> csvRecords = DataReader.getCsvRecords(filePath);
        
        long start = System.currentTimeMillis();
        testApriori(csvRecords);
        long end = System.currentTimeMillis();
        System.out.println("用时: " + (end - start) + " ms");
    }
    
    public static void testApriori(List<CSVRecord> csvRecords) throws AprioriTimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        
        csvRecords.forEach(csvRecord -> {
            String items = csvRecord.get(0);
            String[] eachItem = items.split(",");
            HashSet<String> itemSet = new HashSet<>(Arrays.asList(eachItem));
            transactions.add(new Transaction(itemSet));
        });
        
        Double minSupport = 0.4;
        Double minConfidence = 0.6;
        
        AprioriAlgorithm apriori = new AprioriAlgorithm(minSupport, minConfidence);
        apriori.setTimeoutMillis(5 * 60 * 1000);
        AnalysisResult result = apriori.analyze(transactions);
        System.out.println(result.getFrequentItemSets());
        System.out.println(result.getAssociationRules());
    }
    
}
