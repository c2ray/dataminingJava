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
    private static Map<Integer, Map<Set<Integer>, Integer>> demensionAndfrequentItemSetAndCounts;
    
    /**
     * 当前最大的频繁项集及其支持度计数
     */
    private static Map<Set<Integer>, Integer> currentFrequentItemSetAndCount;
    
    /**
     * 用于存放所有的购物信息
     */
    private final List<Set<Integer>> allShoppingData;
    
    /**
     * 用标号表示商品名称以提高性能
     */
    private final Map<String, Integer> itemNameAndMark;
    
    private void setCurrentFrequentItemSetAndCount(Map<Set<Integer>,
            Integer> currentFrequentItemSetAndCount) {
        Apriori.currentFrequentItemSetAndCount = currentFrequentItemSetAndCount;
    }
    
    public Apriori(double minSupport,
                   double minConfidence) {
        if (minSupport > 1 || minSupport < 0 ||
                minConfidence > 1 || minConfidence < 0) {
            throw new IllegalArgumentException("输入参数必须在[0,1]之间");
        }
        this.minSupportRate = (float) minSupport;
        this.minConfidenceRate = (float) minConfidence;
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
            String itemsBoughtStr = record.get(0);
            Set<Integer> shoppingData = new HashSet<>();
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
            Set<Integer> shoppingData = new HashSet<>();
            
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
        List<Set<Integer>> itemSets = itemMarks.stream()
                .map(Collections::singleton)
                .collect(Collectors.toList());
        
        // 对所有商品标号进行计数
        Map<Set<Integer>, Integer> itemSetAndCount = countSupportForItemSets(itemSets);
        logger.debug("第一次过滤支持度计数之前的当前频繁项集及其计数: {}", itemSetAndCount);
        
        // 按支持度对项集进行过滤
        itemSetAndCount = filterItemSetsByMinSupport(itemSetAndCount);
        
        if (itemSetAndCount.size() > 0) {
            setCurrentFrequentItemSetAndCount(itemSetAndCount);
            addUpCurrentDemenstion();
            recordCurrentItemSets();
        } else {
            showAssociationRules(itemSetAndCount.keySet());
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
    private Map<Set<Integer>, Integer> countSupportForItemSets(List<Set<Integer>> frequentItemSets) {
        Map<Set<Integer>, Integer> frequentItemSetAndCount = new HashMap<>(frequentItemSets.size());
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
    private Map<Set<Integer>, Integer> filterItemSetsByMinSupport(
            Map<Set<Integer>, Integer> frequentItemSetAndCount) {
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
            // 连接后获取的新的项集
            List<Set<Integer>> crossItemSets = getCrossItemSet();
            logger.debug("交叉后的新的项集: {}", crossItemSets);
            
            List<Set<Integer>> frequentItemSets = filterInfrequentItemSet(crossItemSets);
            logger.debug("筛选后的新的频繁项集: {}", frequentItemSets);
            
            Map<Set<Integer>, Integer> frequentItemSetAndCount =
                    countSupportForItemSets(frequentItemSets);
            logger.debug("频繁项集及其计数: {}", frequentItemSetAndCount);
            
            Map<Set<Integer>, Integer> filteredFrequentItemSetAndCount =
                    filterItemSetsByMinSupport(frequentItemSetAndCount);
            logger.debug("按最小支持度筛选后的频繁项集及其计数: {}", frequentItemSetAndCount);
            
            if (filteredFrequentItemSetAndCount.size() > 0) {
                setCurrentFrequentItemSetAndCount(filteredFrequentItemSetAndCount);
                addUpCurrentDemenstion();
                recordCurrentItemSets();
            } else {
                // 不能挖掘出新的频繁模式, 停止循环
                
                logger.warn("最大频繁项集及其计数: {}", currentFrequentItemSetAndCount);
                showAssociationRules(currentFrequentItemSetAndCount.keySet());
                break;
            }
        }
    }
    
    
    /**
     * 过滤子项集非频繁的项集
     */
    private List<Set<Integer>> filterInfrequentItemSet(List<Set<Integer>> itemSets) {
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
    private static boolean isSubSetFrequent(Set<Integer> itemSet) {
        // 如果是二项集, 则其子项集必定是频繁项集
        if (currentItemSetDimonsion == 1) {
            return true;
        }
        
        Set<Set<Integer>> currentDemensionItemSets = currentFrequentItemSetAndCount.keySet();
        
        // 获取该项集所有的k-1项集, 判断
        for (int i = 0; i < itemSet.size(); i++) {
            // 集合和数组的remove 操作不一样, 这里需要进行转换
            List<Integer> tempItemSet = new ArrayList<>(itemSet);
            tempItemSet.remove(i);
            // 发现k-1项集是非频繁项集, 结束循环
            if (!currentDemensionItemSets.contains(new HashSet<>(tempItemSet))) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 交叉获取下一代频繁项集
     */
    private List<Set<Integer>> getCrossItemSet() {
        // 获取当前频繁项集
        List<Set<Integer>> itemSets = new ArrayList<>(currentFrequentItemSetAndCount.keySet());
        
        // 新的 k+1 项集
        Set<Set<Integer>> newItemSets = new HashSet<>();
        
        // 比较每一个元素
        for (int i = 0; i < itemSets.size() - 1; i++) {
            for (int j = i; j < itemSets.size() - 1; j++) {
                List<Integer> itemSet1 = new ArrayList<>(itemSets.get(i));
                List<Integer> itemSet2 = new ArrayList<>(itemSets.get(j + 1));
                if (isJoinable(itemSet1, itemSet2)) {
                    Set<Integer> newItemSet = joinSet(itemSet1, itemSet2);
                    newItemSets.add(newItemSet);
                }
            }
        }
        
        return new ArrayList<>(newItemSets);
    }
    
    /**
     * 判断两个项集是否是可以相交的
     */
    private boolean isJoinable(List<Integer> itemSet1, List<Integer> itemSet2) {
        // 频繁1项集默认可以交叉
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
    private Set<Integer> joinSet(List<Integer> itemSet1, List<Integer> itemSet2) {
        for (Integer item : itemSet2) {
            if (!itemSet1.contains(item)) {
                itemSet1.add(item);
            }
        }
        return new HashSet<>(itemSet1);
    }
    
    
    /**
     * @param itemSets 待挖掘的频繁项集集合
     */
    private void showAssociationRules(Set<Set<Integer>> itemSets) {
        // 如果频繁项集长度为1, 则无关联规则
        if (currentItemSetDimonsion == 1) {
            logger.info("无关联规则");
        }
        // 频繁项集长度大于1, 开始计算关联规则
        // itemSet 表示带挖掘的频繁项集
        itemSets.forEach(itemSet -> {
            // 如果当前项集长度为4: 则拆分成 3,1; 2,2;
            // 如果当前项集长度为5: 则拆分成 4,1; 3,2;
            // 如果当前项集长度为6: 则拆分成 5,1; 4,2; 3,3;
            for (int i = 1;
                 i <= currentItemSetDimonsion / 2;
                 i++) {
                Map<Set<Integer>, Integer> itemSetAndCount =
                        demensionAndfrequentItemSetAndCounts.get(i);
                // 遍历每一个维度为i的频繁项集
                itemSetAndCount.keySet()
                        // itemSet1 表示已保存计数的频繁项集
                        .forEach(itemSet1 -> {
                            if (itemSet.containsAll(itemSet1)) {
                                Set<Integer> itemSet2 = new HashSet<>(itemSet);
                                // itemSet1的补集
                                itemSet2.removeAll(itemSet1);
                                // 计算置信度
                                countConfidence(itemSet1, itemSet2, itemSet);
                                countConfidence(itemSet2, itemSet1, itemSet);
                            }
                        });
            }
        });
    }
    
    /**
     * 计算置信度
     * <p>
     * itemSet = itemSet1 + itemSet2
     */
    private void countConfidence(Set<Integer> itemSet1,
                                 Set<Integer> itemSet2,
                                 Set<Integer> itemSet) {
        // 获取事务A的支持度计数
        int itemSet1Size = itemSet1.size();
        Map<Set<Integer>, Integer> itemSetAndCount =
                demensionAndfrequentItemSetAndCounts.get(itemSet1Size);
        Integer supportA = itemSetAndCount.get(itemSet1);
        Integer supportAb = currentFrequentItemSetAndCount.get(itemSet);
        // 置信度(%)
        int confidence = supportAb * 100 / supportA;
        
        Set<String> stringItemSet1 = convertItemSet(itemSet1);
        Set<String> stringItemSet2 = convertItemSet(itemSet2);
        
        // 输出关联规则
        if (confidence >= minConfidenceRate * 100) {
            
            logger.warn("{} => {}, 置信度{}%", stringItemSet1, stringItemSet2, confidence);
        } else {
            logger.info("{} => {}, 置信度{}%", stringItemSet1, stringItemSet2, confidence);
        }
    }
    
    /**
     * 将数字项集转换成文字项集, 以获取人性化的输出
     */
    private Set<String> convertItemSet(Set<Integer> itemSet) {
        Set<String> stringSet = new HashSet<>();
        itemSet.forEach(itemInt -> {
            itemNameAndMark.forEach((key, value) -> {
                if (itemInt.equals(value)) {
                    stringSet.add(key);
                }
            });
        });
        return stringSet;
    }
    
}
