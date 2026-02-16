package org.example.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class UsernameGenerator {

    // TODO:
    //  Considering pretty low coverage in the project (about 24%)
    //  I would say this guy here is a perfect candidate for thorough unit testing.
    public String generateUsername(String firstName, String lastName, Predicate<String> existsChecker){
        String baseUsername = firstName.trim() + "." + lastName.trim();

        if (!existsChecker.test(baseUsername)) {
            return baseUsername;
        }

        int counter = 1;
        String username = baseUsername + counter;

        while(existsChecker.test(username)){
            counter++;
            username = baseUsername + counter;
        }
        return username;
    }
}
