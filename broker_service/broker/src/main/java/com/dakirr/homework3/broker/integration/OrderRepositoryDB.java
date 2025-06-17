package com.dakirr.homework3.broker.integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dakirr.homework3.broker.domain.repository.OrderRepository;
import com.dakirr.homework3.broker.domain.value_object.Order;

import jakarta.transaction.Transactional;

@Repository
public class OrderRepositoryDB implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    String table_data_name = "broker_orders";
    String table_queue_name = "broker_staging";

    @Autowired
    public OrderRepositoryDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + table_data_name + 
                                " (" +
                                "id INT PRIMARY KEY," +
                                "user_id INT," +
                                "amount DOUBLE PRECISION," +
                                "description VARCHAR(255)," +
                                "status INT NOT NULL CHECK (status IN (0, 1, 2))" +
                                ")";
        jdbcTemplate.execute(createTableSql);

        String createQueueTableSql = "CREATE TABLE IF NOT EXISTS " + table_queue_name + 
                                " (" +
                                "id INT PRIMARY KEY," +
                                "stage INT NOT NULL CHECK (stage IN (0, 1, 2, 3, 4, 5, 6)) DEFAULT 0" +
                                ")";
        jdbcTemplate.execute(createQueueTableSql);
    }


    @Transactional
    @Override
    public Integer add (Order order) {
        String insert_sql_data = "INSERT INTO " + table_data_name + " (id, user_id, amount, description, status) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insert_sql_data, order.getId(), order.getUserId(), order.getAmount(), order.getDescription(), order.getStatus());
        String insert_sql_queue = "INSERT INTO " + table_queue_name + " (id) VALUES (?)";
        jdbcTemplate.update(insert_sql_queue, order.getId());
        return order.getId();
    };

    @Override
    public Order get (int id) {
         try {
            String sql = "SELECT * FROM " + table_data_name + " WHERE id = ?";
            return jdbcTemplate.queryForObject(
                sql, new Object[]{id}, (rs, rowNum) -> {
                    Order order = new Order (
                        rs.getInt("id"), 
                        rs.getInt("user_id"), 
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getInt("status")
                    );
                    return order;
                }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    };

    @Override
    public Integer set_status(int id, int status) {
        try {
            String sql = "UPDATE " + table_data_name + " SET status = ? WHERE id = ?";
            jdbcTemplate.update(sql, status, id);
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer get_stage(int id) {
        try {
            String sql = "SELECT stage FROM " + table_queue_name + " WHERE id = ?";
            return jdbcTemplate.queryForObject(
                sql, new Object[]{id}, Integer.class
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    };

    @Override
    public Integer stage(int id, int stage) {
        try {
            String sql = "UPDATE " + table_queue_name + " SET stage = ? WHERE id = ?";
            jdbcTemplate.update(sql, stage, id);
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    };
    
    @Override
    public Order get_by_stage(int stage) {
        try {
            String sql = "SELECT * FROM " + table_queue_name + 
                         //" JOIN " + table_data_name + " ON " +
                         //table_queue_name + ".id = " + table_data_name + ".id " +
                         " WHERE "  + table_queue_name +  ".stage = ?" +
                         //"AND " + table_data_name + ".status = 0" +
                         "ORDER BY id LIMIT 1";
            return jdbcTemplate.queryForObject(                
                sql, 
                new Object[]{stage}, (rs, rowNum) -> {
                    int orderId = rs.getInt("id");
                    return get(orderId);
                }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
