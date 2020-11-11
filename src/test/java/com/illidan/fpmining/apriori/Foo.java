package com.illidan.fpmining.apriori;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author Illidan
 */
public class Foo {
    @Test
    void loopString() {
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
    
    
    @Test
    void loopInteger() {
        Random random = new Random();
        Stream.generate(random::nextInt).limit(10000).filter(i -> i > 0.5).forEach(System.out::println);
    }
}
