package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .sorted((a, b) -> b.getFirstName().compareTo(a.getFirstName()))
                .map(User::getFirstName)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted((a, b) -> {
                    if (b.getAge().compareTo(a.getAge()) != 0) {
                        return b.getAge().compareTo(a.getAge());
                    } else {
                        return a.getFirstName().compareTo(b.getFirstName());
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .map(User::getPrivileges)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE))
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(u -> u.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream()
                .mapToInt(User::getAge)
                .average().orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream()
                .map(User::getLastName)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .reduce((e1, e2) -> {
                    if (e1.getValue() > e2.getValue()) {
                        return e1;
                    } else if (e2.getValue() > e1.getValue()) {
                        return e2;
                    } else {
                        return new SimpleEntry<>(null, e1.getValue());
                    }
                }).map(Map.Entry::getKey);
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(v -> Arrays.stream(predicates).allMatch(f -> f.test(v)))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                        .map(p -> new SimpleEntry<>(p, user)))
                .collect(Collectors.groupingBy(
                        SimpleEntry::getKey,
                        Collectors.mapping(SimpleEntry::getValue, Collectors.toList())
                ));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
    }
}
