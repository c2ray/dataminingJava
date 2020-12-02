package com.illidan.fpmining.ga.impl;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Illidan
 */
public class Individual implements Comparable<Individual> {
    /**
     * 染色体
     */
    private final int[] chromosome;
    
    /**
     * 适应度
     */
    private double fintness = -1.0;
    
    
    public void setFintness(double fintness) {
        this.fintness = fintness;
    }
    
    /**
     * 指定一个染色体给个体
     *
     * @param chromosome 指定的染色体
     */
    public Individual(int[] chromosome) {
        this.chromosome = chromosome;
    }
    
    /**
     * 按染色体长度随机生成染色体
     */
    public Individual(int chromosomeLength) {
        chromosome = generateChromosomeBylength(chromosomeLength);
    }
    
    
    public int[] getChromosome() {
        return chromosome;
    }
    
    public Integer getChromosomeLength() {
        return chromosome.length;
    }
    
    public Double getFintness() {
        return fintness;
    }
    
    /**
     * 获取指定位置的基因
     */
    public Integer getGene(int position) {
        return chromosome[position];
    }
    
    /**
     * 设置基因
     */
    public void setGene(int position, int gene) {
        chromosome[position] = gene;
    }
    
    /**
     * 按照染色体长度随机生成染色体
     */
    private int[] generateChromosomeBylength(int chromosomeLength) {
        return IntStream.generate(() -> {
            if (Math.random() < 0.5) {
                return 1;
            } else {
                return 0;
            }
        }).limit(chromosomeLength)
                .toArray();
    }
    
    /**
     * 将基因以字符串的形式展示出来
     * <p>
     * [1,0,1,0] -> 1010
     */
    @Override
    public String toString() {
        return Arrays.stream(chromosome)
                .parallel()
                .collect(StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append).toString();
    }
    
    @Override
    public int compareTo(Individual individual) {
        Double fintness1 = individual.getFintness();
        return fintness > fintness1 ? -1 : (fintness == fintness1 ? 0 : 1);
    }
    
  
}
