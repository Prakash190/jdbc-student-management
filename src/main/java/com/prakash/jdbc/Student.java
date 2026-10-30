package com.prakash.jdbc;

/** Represents one row in the students table. */
public record Student(int id, String name, String email, String course, int age) {
}
