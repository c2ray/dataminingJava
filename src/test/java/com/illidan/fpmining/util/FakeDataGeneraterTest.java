package com.illidan.fpmining.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FakeDataGeneraterTest {
    
    private final FakeDataGenerater fakeDataGenerater = new FakeDataGenerater();
    
    @DisplayName("测试生成假数据")
    @Test
    void testFakeDataGenerater() throws IOException, URISyntaxException {
        fakeDataGenerater.readItems("/fake/10items.csv");
        fakeDataGenerater.generateFakeDatas(10, 10);
    }
    
    @DisplayName("测试生成数据的数量")
    @Test
    void getLineNumber() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/fake/fake_data_1000000.csv").toURI());
        // 读取文件行数
        System.out.println(Files.lines(path).skip(999_999).findFirst());
    }
}
