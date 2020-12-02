package com.illidan.fpmining.ga.impl;


import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 使用说明:
 * <p>
 * GeneticAlgorithmImpl geneticAlgorithm = new GeneticAlgorithmImpl(0.22, 0.6, 100, 0.95, 0.001, 3, 200);
 * <p>
 * List<CSVRecord> csvRecords = DataReader.getCsvRecords(path);
 * <p>
 * geneticAlgorithm.recordChromosomeCount(csvRecords);
 * <p>
 * Population population = geneticAlgorithm.ga();
 *
 * @author Illidan
 */
public class GeneticAlgorithmImpl {
    
    
    private final Logger logger = LoggerFactory.getLogger(GeneticAlgorithmImpl.class);
    
    /**
     * 支持率
     */
    private final double supportRate;
    
    /**
     * 置信度
     */
    private final double confidenceRate;
    
    /**
     * 循环次数
     */
    private final int loopCount;
    
    /**
     * 当前循环计数
     */
    private int currentLoop;
    
    /**
     * 种群
     */
    private Population population;
    
    /**
     * 变异率
     */
    private final double mutationRate;
    
    /**
     * 代数
     */
    private int generation;
    
    /**
     * 交叉率
     */
    private final double crossRate;
    
    /**
     * 精英数
     */
    private final int elitism;
    
    /**
     * 种群大小
     */
    private final int populationSize;
    
    /**
     * 总的记录数
     */
    private int recordCount;
    
    /**
     * 用标号表示商品名称以提高性能
     */
    private final Map<String, Integer> itemNameAndMarks;
    
    /**
     * 染色体含1个数 --- 染色体及其计数
     * <p>
     * Integer表示染色体中含有1的个数,
     */
    private final Map<String, Integer> chromosomeAndCounts;
    
    /**
     * 用于缓存染色体的总计数(染色体自身数量 + 父集的数量)
     */
    private final Map<String, Integer> chromosomeTotalCount;
    
    public GeneticAlgorithmImpl(double supportRate,
                                double confidenceRate,
                                int popSize,
                                double crossRate,
                                double mutationRate,
                                int elitism,
                                int loopCount) {
        this.supportRate = supportRate;
        this.confidenceRate = confidenceRate;
        this.populationSize = popSize;
        this.mutationRate = mutationRate;
        this.crossRate = crossRate;
        this.elitism = elitism;
        itemNameAndMarks = new HashMap<>();
        chromosomeAndCounts = new HashMap<>();
        this.loopCount = loopCount;
        chromosomeTotalCount = new HashMap<>();
    }
    
    /**
     * 将购物数据转换成染色体
     * <p>
     * [0, 1, 3, 5] -> 43 -> 101011
     */
    private String shoppingDataToChromosome(Set<Integer> itemSet) {
        // 将itemSet表示为10进制
        int count = (int) itemSet.stream()
                .mapToDouble(i -> Math.pow(2, i))
                .sum();
        String tempChromosome = Integer.toBinaryString(count);
        // 在染色体最左边加0
        return appendZeroToLeft(tempChromosome);
    }
    
    
    /**
     * 在染色体最左边补零, 使其长度等于item的数目
     */
    private String appendZeroToLeft(String chromosomeStr) {
        int len = chromosomeStr.length();
        int chromosomeLen = itemNameAndMarks.size();
        StringBuilder chromosomeStrBuilder = new StringBuilder(chromosomeStr);
        while (len < chromosomeLen) {
            chromosomeStrBuilder.insert(0, "0");
            ++len;
        }
        return chromosomeStrBuilder.toString();
    }
    
    /**
     * 记录所有的记录数(将记录转化为101001这样的二进制字符串)
     */
    public void recordChromosomeCount(List<CSVRecord> records) {
        recordCount = records.size();
        // 将所有记录中的item 用数字表示
        List<Set<Integer>> allShoppingDataMarked = markRecord(records);
        // 将[0, 1, 3] -> 1011; 记录每个染色体出现的次数
        countChromosomeOccurrence(allShoppingDataMarked);
        logger.debug("item名称及其标号: {}", itemNameAndMarks);
        logger.debug("染色体及其计数: {}", chromosomeAndCounts);
    }
    
    /**
     * 判断subStr表示的集合是否是mainStr表示集合的子集
     * <p>
     * 10001 是 10011的真子集
     * <p>
     * 10001 是 10001的子集
     */
    private boolean isSubStr(String mainStr, String subStr) {
        // 如果字符串相等, 则必然是子集
        if (mainStr.equals(subStr)) {
            return true;
        }
        // 记录子字符串中1的下标
        int len = subStr.length();
        ArrayList<Integer> oneIndex = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (subStr.charAt(i) == '1') {
                oneIndex.add(i);
            }
        }
        boolean flag = true;
        for (int index : oneIndex) {
            if (mainStr.charAt(index) != '1') {
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    
    /**
     * 1. 将用数字标记后的购物数据用染色体表示
     * <p>
     * 2. 记录每条染色体出现的次数
     */
    @SuppressWarnings("all")
    private void countChromosomeOccurrence(List<Set<Integer>> allShoppingDataMarked) {
        List<String> chromosomes = allShoppingDataMarked.stream()
                .map(this::shoppingDataToChromosome)
                .collect(Collectors.toList());
        
        // 记录所有的染色体出现的次数
        chromosomes.forEach(chromosome -> {
            chromosomeAndCounts.compute(chromosome,
                    (chromosomeStr, count) ->
                            chromosomeAndCounts.containsKey(chromosome) ? ++count : 1);
        });
    }
    
    
    /**
     * 计算染色体中1的数量
     */
    private int countOnesInChromosome(String chromosome) {
        int count = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            if (chromosome.charAt(i) == '1') {
                ++count;
            }
        }
        return count;
    }
    
    
    /**
     * 1. 将每个item用0, 1, 2 这样的数字表示, 并且用map记录
     * <p>
     * 2. 将给定的记录中的每个item 用数字表示后返回
     */
    private List<Set<Integer>> markRecord(List<CSVRecord> records) {
        List<Set<Integer>> allShoppingData = new ArrayList<>();
        // 将商品种类转换成 0, 1, 2 这样的数字
        // 将购买记录转化为set<Integer>, ([0, 1, 4]);
        records.forEach(record -> {
            // 一条购物数据
            String itemsBoughtStr = record.get(0);
            Set<Integer> shoppingData = new HashSet<>();
            // 将String类型的商品数据转化成Set类型([0, 1, 4])
            for (String itemBought : itemsBoughtStr.split(",")) {
                // item类别计数
                int itemClassCount = itemNameAndMarks.size();
                // 将item名称用数字标号
                itemNameAndMarks.putIfAbsent(itemBought, itemClassCount);
                // item标号
                Integer itemMark = itemNameAndMarks.get(itemBought);
                shoppingData.add(itemMark);
            }
            allShoppingData.add(shoppingData);
        });
        logger.debug("所有购物数据: {}", allShoppingData);
        return allShoppingData;
    }
    
    
    /**
     * 初始化种群, 并且加载数据
     */
    private void initPopulation() {
        this.population = new Population(populationSize, itemNameAndMarks.size());
    }
    
    /**
     * 获取随机生成的染色体的支持度计数
     */
    private Integer getChromosomeCount(String chromosomeStr) {
        int totalCount;
        // 从缓存中获取染色体总计数
        totalCount = chromosomeTotalCount.getOrDefault(chromosomeStr, 0);
        
        if (totalCount != 0) {
            return totalCount;
        }
        
        for (Map.Entry<String, Integer> chromosomeAndCount : chromosomeAndCounts.entrySet()) {
            String chromosome = chromosomeAndCount.getKey();
            if (isSubStr(chromosome, chromosomeStr)) {
                Integer count = chromosomeAndCount.getValue();
                totalCount += count;
            }
        }
        // 将染色体及其总计数
        chromosomeTotalCount.put(chromosomeStr, totalCount);
        return totalCount;
    }
    
    /**
     * 计算适应度
     */
    private double calcFitness(Individual individual) {
        // 适应度 = 染色体支持率
        // 将染色体转换成字符串型 [0, 1, 1, 0] -> 0110
        int[] chromosomeList = individual.getChromosome();
        String chromosomeStr = chromosomeList2Str(chromosomeList);
        // 获取染色体的计数
        Integer chromosomeCount = getChromosomeCount(chromosomeStr);
        // 计算适应度
        double fitness;
        // 找不到对应的染色体 或 支持度不满足
        if (chromosomeCount == 0 || chromosomeCount < supportRate * recordCount) {
            fitness = 0;
        } else {
            // 记录染色体中1的数量
            long onesCount = Arrays.stream(chromosomeList)
                    .filter(i -> i == 1)
                    .count();
            fitness = (double) onesCount / itemNameAndMarks.size();
        }
        individual.setFintness(fitness);
        return fitness;
    }
    
    /**
     * 将chromosome 数组 转 字符串
     */
    private String chromosomeList2Str(int[] chromosomeList) {
        return Arrays.stream(chromosomeList)
                .collect(StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append)
                .toString();
    }
    
    /**
     * 评估种群的适应度
     */
    private void evalPopulation() {
        double populationFitness = Arrays.stream(population.getIndividuals())
                // 计算每个个体的适应度
                .mapToDouble(this::calcFitness)
                .sum();
        population.setPopulationFitness(populationFitness);
        // 每次评估完, 循环次数加1
        currentLoop++;
    }
    
    /**
     * 判断是否达到终止条件
     */
    private boolean isTerminationMet() {
        return currentLoop == loopCount;
    }
    
    /**
     * 交叉种群
     */
    private void crossoverPopulation() {
        // 新的种群
        Population newPopulation = new Population(population.size());
        Individual[] individuals = population.getIndividuals();
        for (int individualIndex = 0; individualIndex < individuals.length; individualIndex++) {
            Individual parent1 = population.getFittest(individualIndex);
            if (individualIndex >= elitism && crossRate > Math.random()) {
                // 轮盘赌
                Individual parent2 = selectParent(population);
                // 进行交叉
                Individual newIndividual = new Individual(parent1.getChromosomeLength());
                for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
                    if (Math.random() < 0.5) {
                        newIndividual.setGene(geneIndex, parent1.getGene(geneIndex));
                    } else {
                        newIndividual.setGene(geneIndex, parent2.getGene(geneIndex));
                    }
                }
                // logger.info("新的个体: {}", newIndividual);
                newPopulation.setIndividual(individualIndex, newIndividual);
            } else {
                // 直接将精英流传到下一代
                newPopulation.setIndividual(individualIndex, parent1);
            }
        }
        this.population = newPopulation;
    }
    
    /**
     * 变异种群
     */
    private void mutatePopulation() {
        Population newPopulation = new Population(populationSize);
        Individual[] individuals = population.getIndividuals();
        for (int individualIndex = 0; individualIndex < individuals.length; individualIndex++) {
            // 由于适应度在初始化的时候被赋值为-1, 所以由交叉生成的个体默认被排到精英后面去了
            // 且他们是无序的(即不按照适应度排序)
            Individual individual = population.getFittest(individualIndex);
            if (individualIndex >= elitism) {
                // 当前个体不是精英, 按概率对其基因进行变异
                int[] chromosome = individual.getChromosome();
                for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
                    if (mutationRate > Math.random()) {
                        if (chromosome[geneIndex] == 1) {
                            chromosome[geneIndex] = 0;
                        } else {
                            chromosome[geneIndex] = 1;
                        }
                    }
                }
            }
            newPopulation.setIndividual(individualIndex, individual);
        }
        this.population = newPopulation;
    }
    
    /**
     * 选择父代
     */
    private Individual selectParent(Population population) {
        Individual[] individuals = population.getIndividuals();
        // 随机指针, 当总适应度超过指针时, 选择这个个体
        double rouletteWheelPosition = Math.random() * population.getPopulationFitness();
        double spinWheel = 0;
        for (Individual individual : individuals) {
            spinWheel += individual.getFintness();
            if (spinWheel >= rouletteWheelPosition) {
                return individual;
            }
        }
        // 这行代码意义不大, 因为在for循环中一定会返回一个个体
        // return individuals.get(individuals.size() - 1);
        return individuals[(individuals.length - 1)];
    }
    
    
    /**
     * 遗传算法求解
     */
    public void ga() {
        initPopulation();
        evalPopulation();
        while (!isTerminationMet()) {
            // Apply crossover(加入只使用交叉, 可能陷入死循环, 不能得到最优解)
            crossoverPopulation();
            // 进行变异
            mutatePopulation();
            evalPopulation();
        }
    }
    
    /**
     * 获取种群
     */
    public Population getPopulation() {
        return population;
    }
    
    
    /**
     * 获取频繁项集
     * <p>
     * 1. 按适应度将染色体排序
     * <p>
     * 2. 去掉重复的染色体
     */
    public List<String> getFrequentSet() {
        List<String> chromosomes = Arrays.stream(population.getIndividuals())
                .sorted()
                // 染色体精英的适应度必定达标
                .limit(elitism)
                .map(Individual::toString)
                .distinct()
                .collect(Collectors.toList());
        // 获得的第一个项集一定是最符合的频繁项集
        String bestFrequentSet = chromosomes.get(0);
        int onesCount = countOnesInChromosome(bestFrequentSet);
        chromosomes.removeIf(s -> countOnesInChromosome(s) != onesCount);
        
        // 输出日志
        for (String chromosome : chromosomes) {
            Integer count = getChromosomeCount(chromosome);
            List<String> frequentSet = chromosomeToFrequentSet(chromosome);
            logger.info("染色体及其支持度计数: {} : {}", frequentSet, count);
        }
        return chromosomes;
    }
    
    /**
     * 将染色体转换成频繁项集, 输出结果时使用
     */
    private List<String> chromosomeToFrequentSet(String chromosomeStr) {
        ArrayList<String> list = new ArrayList<>();
        int length = chromosomeStr.length();
        for (int i = 0; i < length; i++) {
            if (chromosomeStr.charAt(length - i - 1) == '1') {
                for (Map.Entry<String, Integer> itemNameAndMark : itemNameAndMarks.entrySet()) {
                    if (itemNameAndMark.getValue() == i) {
                        list.add(itemNameAndMark.getKey());
                    }
                }
            }
        }
        return list;
    }
    
    
    // todo: 人性化的输出结果
    public void getRelationRules(List<String> frequentSet) {
    
    
    }
    
}

