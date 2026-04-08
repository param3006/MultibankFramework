package com.multibank.utils;

import com.github.javafaker.Faker;

import java.util.HashMap;
import java.util.Map;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    private TestDataFactory() {}

    public static Map<String, Object> newUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("name",     faker.name().fullName());
        user.put("username", faker.name().username());
        user.put("email",    faker.internet().emailAddress());
        user.put("phone",    faker.phoneNumber().phoneNumber());
        user.put("website",  faker.internet().domainName());
        user.put("address",  newAddress());
        user.put("company",  newCompany());
        return user;
    }

    public static Map<String, Object> newAddress() {
        Map<String, Object> address = new HashMap<>();
        address.put("street",  faker.address().streetName());
        address.put("suite",   faker.address().secondaryAddress());
        address.put("city",    faker.address().city());
        address.put("zipcode", faker.address().zipCode());
        return address;
    }

    public static Map<String, Object> newCompany() {
        Map<String, Object> company = new HashMap<>();
        company.put("name",        faker.company().name());
        company.put("catchPhrase", faker.company().catchPhrase());
        company.put("bs",          faker.company().bs());
        return company;
    }

    public static Map<String, Object> newPost(int userId) {
        Map<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("title",  faker.lorem().sentence(6));
        post.put("body",   faker.lorem().paragraph(3));
        return post;
    }

    public static Map<String, Object> newPost() {
        return newPost(faker.number().numberBetween(1, 10));
    }

    public static Map<String, Object> newComment(int postId) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("name",   faker.name().fullName());
        comment.put("email",  faker.internet().emailAddress());
        comment.put("body",   faker.lorem().paragraph(2));
        return comment;
    }


    public static Map<String, Object> updatedTitle() {
        Map<String, Object> patch = new HashMap<>();
        patch.put("title", faker.lorem().sentence(5));
        return patch;
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomName() {
        return faker.name().fullName();
    }

    public static int randomUserId() {
        return faker.number().numberBetween(1, 10);
    }
}
