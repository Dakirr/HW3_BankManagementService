package com.dakirr.homework3.payments.integration.repository.BankAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dakirr.homework3.payments.domain.repository.BankAccountRepository;
import com.dakirr.homework3.payments.domain.value_object.BankAccount;

@Repository
public class BankAccountRepositoryDB implements BankAccountRepository {
    String table_name = "bank_accounts";
    JdbcTemplate jdbcTemplate;

    @Autowired
    public BankAccountRepositoryDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + table_name + 
                                " (" +
                                "id SERIAL PRIMARY KEY," +
                                "balance DOUBLE PRECISION" +
                                ")";
        jdbcTemplate.execute(createTableSql);
    }
    
    @Override
    public BankAccount get(int id) {
        try {
            BankAccount ret = this.jdbcTemplate.queryForObject(
            "SELECT * FROM " + table_name + " WHERE id = ?",
            new Object[]{id},
                (rs, rowNum) -> new BankAccount(
                    rs.getInt("id"),
                    rs.getDouble("balance")
                )
            );
            return ret;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }    
    }
    
    @Override
    public Integer add(int id, double balance) {
        try {
            String sql = "INSERT INTO " + table_name + " (id, balance) VALUES (?, ?)";
            this.jdbcTemplate.update(sql, id, balance);
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Integer update(int id, double delta) {
        try {
            this.jdbcTemplate.update(
                "UPDATE " + table_name + " SET balance = balance + ? WHERE id = ?",
                delta,
                id
            );
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer delete(int id) {
        try {
            this.jdbcTemplate.update(
                "DELETE FROM " + table_name + " WHERE id = ?",
                id
            );
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
