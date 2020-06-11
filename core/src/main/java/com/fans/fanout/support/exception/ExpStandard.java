package com.fans.fanout.support.exception;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * stream 异常可抛出包装
 *
 * @author ：fsp
 * @date ：2020/4/20 20:30
 */
public class ExpStandard {

    public static <T, R> void wrapAndCheckIteratorFunction(List<T> list, ExpFunction<T, R> function) throws Exception {
        Function<T, Pair> exeFunction = wrapFunction(function);
        List<Pair> collect = list.stream().map(exeFunction).collect(Collectors.toList());
        ExpStandard.checkWrapList(collect);
    }

    public static <T> void wrapAndCheckIteratorConsumer(List<T> list, ExpConsumer<T> consumer) throws Exception {
        Function<T, Pair> exeFunction = wrapConsumer(consumer);
        List<Pair> collect = list.stream().map(exeFunction).collect(Collectors.toList());
        ExpStandard.checkWrapList(collect);
    }

    public static void checkWrapList(List<Pair> pairList) throws Exception {
        for (Pair pair : pairList) {
            if (pair.getLeft() != null) {
                throw (Exception) pair.getLeft();
            }
        }
    }

    private static <T, R> Function<T, Pair> wrapFunction(ExpFunction<T, R> function) {
        return t -> {
            try {
                return Pair.of(null, function.apply(t));
            } catch (Exception e) {
                return Pair.of(e, null);
            }
        };
    }

    private static <T> Function<T, Pair> wrapConsumer(ExpConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
                return Pair.of(null, null);
            } catch (Exception e) {
                return Pair.of(e, null);
            }
        };
    }

}