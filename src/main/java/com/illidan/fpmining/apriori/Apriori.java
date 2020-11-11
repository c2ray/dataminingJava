package com.illidan.fpmining.apriori;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Illidan
 */
public class Apriori implements FpAlgorithm {
    
    private final Logger logger = LoggerFactory.getLogger(Apriori.class);
    
    /**
     * 当前项集维数
     */
    private static int currentItemSetDimonsion = 0;
    
    /**
     * 最小支持阈值
     */
    private final float minSupportRate;
    
    /**
     * 最小支持度阈值
     */
    private final float minConfidenceRate;
    
    /**
     * {@code Integer} 对应频繁项集的维数
     * <p>
     * {@code Map<List<Integer>, Integer>} -> 频繁项集及其计数
     * <p>
     * 记录(频繁项集及其计数)集合 及其维数
     */
    private static Map<Integer, Map<List<Integer>, Integer>> demensionAndfrequentItemSetAndCounts;
    
    /**
     * 当前最大的频繁项集及其支持度计数
     */
    private static Map<List<Integer>, Integer> currentFrequentItemSetAndCount;
    
    /**
     * 用于存放所有的购物信息
     */
    private final List<List<Integer>> allShoppingData;
    
    /**
     * 用标号表示商品名称以提高性能
     */
    private final Map<String, Integer> itemNameAndMark;
    
    private void setCurrentFrequentItemSetAndCount(Map<List<Integer>,
            Integer> currentFrequentItemSetAndCount) {
        Apriori.currentFrequentItemSetAndCount = currentFrequentItemSetAndCount;
    }
    
    public Apriori(float minSupport,
                   float minConfidence) {
        this.minSupportRate = minSupport;
        this.minConfidenceRate = minConfidence;
        itemNameAndMark = new HashMap<>();
        allShoppingData = new ArrayList<>();
        currentFrequentItemSetAndCount = new HashMap<>();
        demensionAndfrequentItemSetAndCounts = new HashMap<>();
    }
    
    
    /**
     * 从指定csv文件获取数据
     *
     * @param csvPath csv 数据文件
     * @throws IOException 数据读取异常
     */
    public void getData(String csvPath) throws IOException {
        logger.debug("从 {} 读取数据", csvPath);
        
        CSVParser csvRecords = CSVParser
                .parse(getClass().getResource(csvPath),
                        Charset.defaultCharset(),
                        CSVFormat.DEFAULT);
        List<CSVRecord> records = csvRecords.getRecords();
        
        // 从csv文件中提取购买的货物
        records.forEach(record -> {
            // 一条购物数据
            // String itemsBoughtStr = record.get(1);
            String itemsBoughtStr = record.get(0);
            List<Integer> shoppingData = new ArrayList<>();
            // 将String类型的商品数据转化成Set类型
            for (String itemBought : itemsBoughtStr.split(",")) {
                // item类别计数
                int itemClassCount = itemNameAndMark.size();
                // 将item名称用数字标号
                itemNameAndMark.putIfAbsent(itemBought, itemClassCount);
                // item标号
                Integer itemMark = itemNameAndMark.get(itemBought);
                shoppingData.add(itemMark);
            }
            allShoppingData.add(shoppingData);
        });
        logger.debug("所有购物数据: {}", allShoppingData);
        logger.debug("item名称及其标号: {}", itemNameAndMark);
    }
    
    /**
     * 从指定csv文件获取数据
     *
     * @param csvPath csv 数据文件
     * @throws IOException 数据读取异常
     */
    public void getData2(String csvPath) throws URISyntaxException, IOException {
        List<String> records = Files.readAllLines(Paths.get(getClass().getResource(csvPath).toURI()));
        
        records.forEach(record -> {
            List<Integer> shoppingData = new ArrayList<>();
            
            for (String itemBought : record.split(",")) {
                // item类别计数
                int itemClassCount = itemNameAndMark.size();
                // 将item名称用数字标号
                itemNameAndMark.putIfAbsent(itemBought, itemClassCount);
                // item标号
                Integer itemMark = itemNameAndMark.get(itemBought);
                shoppingData.add(itemMark);
            }
            allShoppingData.add(shoppingData);
        });
        
        logger.debug("所有购物数据: {}", allShoppingData);
        logger.debug("item名称及其标号: {}", itemNameAndMark);
    }
    
    
    /**
     * 挖掘第一个频繁项集
     */
    @SuppressWarnings("all")
    public void findFirstFrequentItemset() {
        // 所有商品标号
        Collection<Integer> itemMarks = itemNameAndMark.values();
        
        // 将商品标号(Integer)转化为项集(List<Integer>)
        List<List<Integer>> itemSets = itemMarks.stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        
        // 对所有商品标号进行计数
        Map<List<Integer>, Integer> itemSetAndCount = countSupportForItemSets(itemSets);
        logger.debug("第一次过滤支持度计数之前的当前频繁项集及其计数: {}", itemSetAndCount);
        
        // 按支持度对项集进行过滤
        itemSetAndCount = filterItemSetsByMinSupport(itemSetAndCount);
        
        if (itemSetAndCount.size() > 0) {
            setCurrentFrequentItemSetAndCount(itemSetAndCount);
            addUpCurrentDemenstion();
            recordCurrentItemSets();
        }
        
        logger.debug("第一次过滤支持度计数之后的当前频繁项集及其计数: {}", itemSetAndCount);
    }
    
    /**
     * 当前频繁项集维数 +1
     */
    private void addUpCurrentDemenstion() {
        ++currentItemSetDimonsion;
    }
    
    
    private void recordCurrentItemSets() {
        // 将当前频繁项集及其计数记录到所有频繁项集及其计数中, 记录这组频繁项集及其计数的维数
        demensionAndfrequentItemSetAndCounts.put(currentItemSetDimonsion, currentFrequentItemSetAndCount);
        logger.debug("维数以及频繁项集计数: {}", demensionAndfrequentItemSetAndCounts);
    }
    
    
    /**
     * 为项集记录支持度
     */
    @SuppressWarnings("all")
    private Map<List<Integer>, Integer> countSupportForItemSets(List<List<Integer>> frequentItemSets) {
        Map<List<Integer>, Integer> frequentItemSetAndCount = new HashMap<>(frequentItemSets.size());
        allShoppingData.forEach(shoppingData -> {
                    frequentItemSets.forEach(frequentItemSet -> {
                        // 如果记录中包含这条项集, 就给他的计数加1
                        if (shoppingData.containsAll(frequentItemSet)) {
                            frequentItemSetAndCount.compute(frequentItemSet,
                                    (itemSet, count) -> frequentItemSetAndCount
                                            .containsKey(frequentItemSet) ? ++count : 0);
                        }
                    });
                }
        );
        return frequentItemSetAndCount;
    }
    
    /**
     * 过滤不符合支持度的项集
     */
    private Map<List<Integer>, Integer> filterItemSetsByMinSupport(
            Map<List<Integer>, Integer> frequentItemSetAndCount) {
        int minSupport = Math.round(minSupportRate * allShoppingData.size());
        frequentItemSetAndCount.entrySet()
                .removeIf(itemSetCountEntry -> itemSetCountEntry.getValue() < minSupport);
        return frequentItemSetAndCount;
    }
    
    
    /**
     * 生成下一代频繁项集
     */
    public void aprioriGen() {
        while (true) {
            // 交k叉后获取的新的项集
            List<List<Integer>> crossItemSets = getCrossItemSet();
            logger.debug("交叉后的新的项集: {}", crossItemSets);
            
            List<List<Integer>> frequentItemSets = filterInfrequentItemSet(crossItemSets);
            logger.debug("筛选后的新的频繁项集: {}", crossItemSets);
            
            Map<List<Integer>, Integer> frequentItemSetAndCount =
                    countSupportForItemSets(frequentItemSets);
            logger.debug("频繁项集及其计数: {}", frequentItemSetAndCount);
            
            Map<List<Integer>, Integer> filteredFrequentItemSetAndCount =
                    filterItemSetsByMinSupport(frequentItemSetAndCount);
            logger.debug("按最小支持度筛选后的频繁项集及其计数: {}", frequentItemSetAndCount);
            
            if (filteredFrequentItemSetAndCount.size() > 0) {
                setCurrentFrequentItemSetAndCount(filteredFrequentItemSetAndCount);
                addUpCurrentDemenstion();
                recordCurrentItemSets();
            } else {
                // 不能挖掘出新的频繁模式, 停止循环
                logger.info("最大频繁项集及其计数: {}", currentFrequentItemSetAndCount);
                break;
            }
        }
    }
    
    
    /**
     * 过滤子项集非频繁的项集
     */
    @SuppressWarnings("all")
    private List<List<Integer>> filterInfrequentItemSet(List<List<Integer>> itemSets) {
        // 为了去掉连接生成的重复的项集, 先转成set
        return itemSets.stream()
                .filter(Apriori::isSubSetFrequent)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 判断一个项集的子集是否是频繁的(如果项集维数为1, 则可以跳过)
     */
    @SuppressWarnings("all")
    private static boolean isSubSetFrequent(List<Integer> itemSet) {
        // 如果是二项集, 则其子项集必定是频繁项集
        if (currentItemSetDimonsion == 1) {
            return true;
        }
        
        Set<List<Integer>> currentDemensionItemSets = currentFrequentItemSetAndCount.keySet();
        
        // 获取该项集所有的k-1项集, 判断
        for (int i = 0; i < itemSet.size(); i++) {
            List<Integer> tempItemSet = new ArrayList<>(itemSet);
            tempItemSet.remove(i);
            // 发现k-1项集是非频繁项集, 结束循环
            if (!currentDemensionItemSets.contains(tempItemSet)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 交叉获取下一代频繁项集
     */
    private List<List<Integer>> getCrossItemSet() {
        // 获取当前频繁项集
        List<List<Integer>> itemSets = new ArrayList<>(currentFrequentItemSetAndCount.keySet());
        
        // 新的 k+1 项集
        List<List<Integer>> newItemSets = new ArrayList<>();
        
        // 比较每一个元素
        for (int i = 0; i < itemSets.size() - 1; i++) {
            for (int j = i; j < itemSets.size() - 1; j++) {
                List<Integer> itemSet1 = new ArrayList<>(itemSets.get(i));
                List<Integer> itemSet2 = new ArrayList<>(itemSets.get(j + 1));
                if (isJoinable(itemSet1, itemSet2)) {
                    List<Integer> newItemSet = joinSet(itemSet1, itemSet2);
                    newItemSets.add(newItemSet);
                }
            }
        }
        return newItemSets;
    }
    
    /**
     * 判断两个项集是否是可以相交的
     */
    private boolean isJoinable(List<Integer> itemSet1, List<Integer> itemSet2) {
        // 频繁一项集默认可以交叉
        if (currentItemSetDimonsion == 1) {
            return true;
        }
        
        // 处理一般情况
        int sameCount = 0;
        for (Integer item : itemSet1) {
            if (itemSet2.contains(item)) {
                sameCount++;
            }
        }
        
        // 如果两个itemSet有 k-1 个元素相同, 则表示可以交叉
        return sameCount == currentItemSetDimonsion - 1;
    }
    
    /**
     * 将两个项集相交, 获取k+1项集
     *
     * @return k+1项集
     */
    private List<Integer> joinSet(List<Integer> itemSet1, List<Integer> itemSet2) {
        for (Integer item : itemSet2) {
            if (!itemSet1.contains(item)) {
                itemSet1.add(item);
            }
        }
        return itemSet1;
    }
    
    // todo 切分当前项集, 枚举其所有的子项集并对其进行计数(计算置信度)
    
    
    // todo 产生关联规则
    // private gen
    
    public static void main(String[] args) {
    
    }
}
