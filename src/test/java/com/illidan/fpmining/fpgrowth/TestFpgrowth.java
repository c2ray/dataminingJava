package com.illidan.fpmining.fpgrowth;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Illidan
 */
public class TestFpgrowth {
    
    
    @Test
    void testFpgrowth() throws URISyntaxException {
        Path path = Paths.get(getClass().getResource("/fake/fake_data_1000.csv").toURI()).toAbsolutePath();
        String supportRate = "0.6";
        String[] strings = {path.toString(), supportRate};
        // FPGrowth.main(strings);
    }
}
