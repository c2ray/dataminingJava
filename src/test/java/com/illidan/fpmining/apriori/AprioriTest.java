package com.illidan.fpmining.apriori;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class AprioriTest {
    
    private final Apriori apriori = new Apriori((float) 0.000001, 0);
    
    
    @Test
    void testApriori() throws IOException {
        // 百万级别
        apriori.getData("/data/fake_data_100000.csv");
        apriori.findFirstFrequentItemset();
        apriori.aprioriGen();
    }
    
    @Test
    void testGetData() throws IOException, URISyntaxException {
        apriori.getData2("/data/fake_data_10000000.csv");
    }
}

