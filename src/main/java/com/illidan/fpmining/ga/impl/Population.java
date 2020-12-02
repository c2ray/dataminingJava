package com.illidan.fpmining.ga.impl;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Illidan
 */
public class Population {
    
    private final Individual[] individuals;
    
    /**
     * 种群适应度
     */
    private Double populationFitness;
    
    /**
     * 按种群大小生成种群, 染色体长度默认为50
     *
     * @param populationSize 种群大小
     */
    public Population(int populationSize) {
        this.individuals = new Individual[populationSize];
    }
    
    /**
     * 按种群大小和染色体长度生成种群
     *
     * @param populationSize   种群大小
     * @param chromosomeLength 染色体长度
     */
    public Population(int populationSize, int chromosomeLength) {
        individuals = new Individual[populationSize];
        for (int i = 0; i < populationSize; i++) {
            individuals[i] = new Individual(chromosomeLength);
        }
    }
    
    /**
     * 种群适应度
     */
    public double getPopulationFitness() {
        return populationFitness;
    }
    
    /**
     * 设置种群适应度
     */
    public void setPopulationFitness(double populationFitness) {
        this.populationFitness = populationFitness;
    }
    
    /**
     * 将种群中指定位置的个体替换掉
     */
    public void setIndividual(int position, Individual individual) {
        // individuals.set(position, individual);
        individuals[position] = individual;
    }
    
    public Individual[] getIndividuals() {
        return individuals;
    }
    
    /**
     * 获取指定位置的个体
     */
    public Individual getIndividual(int position) {
        return individuals[position];
    }
    
    /**
     * 种群大小
     */
    public int size() {
        return individuals.length;
    }
    
    /**
     * 获取适应度为rank的个体
     */
    public Individual getFittest(int rank) {
        return Arrays.stream(individuals)
                .sorted()
                // .sorted((individual1, individual2) -> {
                //             double fintness1 = individual1.getFintness();
                //             double fintness2 = individual2.getFintness();
                //             // 递减排序
                //             return Double.compare(fintness2, fintness1);
                //         })
                .collect(Collectors.toList())
                .get(rank);
    }
    
    
    /**
     * 打乱个体排序
     */
    public void shuffle() {
        Random random = new Random();
        for (int i = individuals.length - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);
            Individual tempIndividual = individuals[index];
            individuals[index] = individuals[i];
            individuals[i] = tempIndividual;
        }
    }
    
    
}
