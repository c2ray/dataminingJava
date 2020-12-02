package com.illidan.fpmining.apriori;

import com.illidan.fpmining.ga.impl.Individual;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Illidan
 */
public class MessTest {
    /**
     * 测试集合不重复的特性
     */
    @Test
    void testSetDifference() {
        List<Integer> arr1 = Arrays.asList(1, 2, 3);
        List<Integer> arr2 = Arrays.asList(2, 1, 3);
        
        // HashSet<Integer> set1 = new HashSet<>(arr1);
        // HashSet<Integer> set2 = new HashSet<>(arr2);
        // HashSet<HashSet<Integer>> set = new HashSet<>();
        HashSet<List<Integer>> set = new HashSet<>();
        
        set.add(arr1);
        set.add(arr2);
        
        System.out.println(set);
        // System.out.println(arr1.containsAll(arr2));
    }
    
    /**
     * 测试集合自动排序的特性
     */
    @Test
    void testSetSort() {
        List<Integer> arr1 = Arrays.asList(1, 2, 3);
        List<Integer> arr2 = Arrays.asList(2, 1, 3);
        
        HashSet<Integer> set1 = new HashSet<>(arr1);
        HashSet<Integer> set2 = new HashSet<>(arr2);
        
        System.out.println(set1);
        System.out.println(set2);
        
    }
    
    @Test
    void loopInteger() {
        Random random = new Random();
        Stream.generate(random::nextInt)
                .limit(10000)
                .filter(i -> i > 0.5)
                .forEach(System.out::println);
    }
    
    /**
     * 测试二进制转十进制
     * <p>
     * 十进制转二进制
     */
    @Test
    void testBinaryToDecimal() {
        int i = Integer.parseInt("101011", 2);
        String s = Integer.toBinaryString(43);
        System.out.println(i);
        System.out.println(s);
    }
    
    @Test
    void testStringSplit() {
    }
    
    @Test
    void testForEach() {
        Stream.of(1, 2, 3, 4, 5)
                .forEach(integer -> {
                    if (integer == 3) {
                        return;
                    }
                    System.out.println(integer);
                });
    }
    
    
    @Test
    void testSetForString() {
        ArrayList<String> strs = new ArrayList<>();
        
        strs.add("aaa");
        strs.add("bbb");
        strs.add("aaa");
        HashSet<String> strings = new HashSet<>(strs);
        
        System.out.println(strings);
    }
    
    @Test
    void testCollectionsMin() {
        ArrayList<Integer> ints = new ArrayList<>();
        
        ints.add(1);
        ints.add(2);
        ints.add(3);
        
        Integer min = Collections.min(ints);
        System.out.println(min);
    }
    
    
    @Test
    void testComparable() {
        Individual individual1 = new Individual(0);
        Individual individual2 = new Individual(0);
        Individual individual3 = new Individual(0);
        individual1.setFintness(2);
        individual2.setFintness(1);
        individual3.setFintness(3);
        
        ArrayList<Individual> individuals = new ArrayList<>();
        individuals.add(individual1);
        individuals.add(individual2);
        individuals.add(individual3);
        List<Individual> individualsSorted = individuals.stream()
                .sorted()
                .collect(Collectors.toList());
        
        individualsSorted.forEach(individual -> {
            System.out.println(individual.getFintness());
        });
        
    }
}
