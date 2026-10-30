package com.prakash.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Contains all JDBC queries for the students table. */
public class StudentDao {
    public int add(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, course, age) VALUES (?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            try {
                statement.setString(1, student.name());
                statement.setString(2, student.email());
                statement.setString(3, student.course());
                statement.setInt(4, student.age());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Student was saved but no ID was generated.");
                    }
                    int id = keys.getInt(1);
                    connection.commit();
                    return id;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, email, course, age FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(map(resultSet));
            }
        }
        return students;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, course, age FROM students WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        }
    }

    public List<Student> searchByName(String searchText) throws SQLException {
        String sql = "SELECT id, name, email, course, age FROM students "
                + "WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        List<Student> students = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + searchText + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(map(resultSet));
                }
            }
        }
        return students;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, course = ?, age = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.name());
            statement.setString(2, student.email());
            statement.setString(3, student.course());
            statement.setInt(4, student.age());
            statement.setInt(5, student.id());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private Student map(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("course"),
                resultSet.getInt("age"));
    }
}
