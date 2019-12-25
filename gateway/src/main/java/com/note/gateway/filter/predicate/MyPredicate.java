package com.note.gateway.filter.predicate;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class MyPredicate implements Predicate {
    @Override
    public boolean test(Object o) {
        return true;
    }
}
