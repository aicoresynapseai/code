package com.aid.demo;

import org.apache.commons.collections4.ListUtils; // Will use the vulnerable commons-collections4 if not upgraded
import com.google.common.collect.Lists; // Will use guava
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("AIDepGuard Demo Application Started.");

        // Using commons-collections4 (potentially vulnerable version)
        List<String> list1 = Lists.newArrayList("A", "B", "C");
        List<String> list2 = Lists.newArrayList("C", "D", "E");
        List<String> union = ListUtils.union(list1, list2);
        logger.info("Union of lists using commons-collections4: {}", union);

        // Using Guava (potentially outdated version)
        List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5);
        List<Integer> reversedNumbers = Lists.reverse(numbers);
        logger.info("Reversed numbers using Guava: {}", reversedNumbers);

        logger.info("AIDepGuard Demo Application Finished.");
    }
}