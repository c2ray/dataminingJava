package com.illidan.fpmining.apriori;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class AprioriTest {
    
    private final Apriori apriori = new Apriori(0.3, 0.6);
    
    @Test
    void testApriori() throws IOException {
        // 支持率为0.4
        // 十级别(0.04)
        // 百级别(0.049)
        // 千级别(0.1s)
        // 万级别(0.38s)
        // 十万级别(4.5s)
        // 百万级别(60s)
        
        // 砍掉io开销
        // 万级别(0.2s)
        // 十万级别(4s)
        // 百万级别(38.4s)
        apriori.getData("/fake/fake_data_100.csv");
        long start = System.currentTimeMillis();
        
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
        
        long end = System.currentTimeMillis();
        System.out.println("用时: " + (end - start) + " ms");
        
    }
    
    @Test
    void testGetData() throws IOException, URISyntaxException {
        apriori.getData2("/fake/fake_data_10000000.csv");
    }
}

