package com.illidan.fpmining.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Illidan
 */
public class DataReader {
    private final static Logger logger = LoggerFactory.getLogger(DataReader.class);
    
    /**
     * 从指定的csv文件中读取数据
     *
     * @param csvPath csv 文件路径
     */
    public static List<CSVRecord> getCsvRecords(String csvPath) throws IOException {
        logger.debug("从 {} 读取数据", csvPath);
        
        CSVParser csvRecords = CSVParser
                .parse(DataReader.class.getResource(csvPath),
                        Charset.defaultCharset(),
                        CSVFormat.DEFAULT);
        
        return csvRecords.getRecords();
    }
    
}
