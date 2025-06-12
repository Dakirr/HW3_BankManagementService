package com.dakirr.homework3.orders.integration.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dakirr.homework3.orders.domain.repository.OrderRepository;
import com.dakirr.homework3.orders.domain.value_object.Order;

import jakarta.transaction.Transactional;

@Repository
public class OrderRepositoryDB implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    String table_data_name = "orders";
    String table_queue_name = "orders_queue";

    @Autowired
    public OrderRepositoryDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + table_data_name + 
                                " (" +
                                "id SERIAL PRIMARY KEY," +
                                "user_id INT," +
                                "amount DOUBLE PRECISION," +
                                "description VARCHAR(255)," +
                                "status INT NOT NULL CHECK (status IN (0, 1, 2))" +
                                ")";
        jdbcTemplate.execute(createTableSql);

        String createQueueTableSql = "CREATE TABLE IF NOT EXISTS " + table_queue_name + 
                                " (" +
                                "id SERIAL PRIMARY KEY," +
                                "is_actual BOOLEAN NOT NULL DEFAULT TRUE," +
                                "data_id INT REFERENCES " + table_data_name + "(id)" +
                                ")";
        jdbcTemplate.execute(createQueueTableSql);
    }

    @Override
    public Order get(int id) {
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
    }

    @Override
    @Transactional
    public Order get_from_queue() {
        try {
            String sql = "SELECT * FROM " + table_queue_name + " WHERE is_actual = TRUE ORDER BY id ASC LIMIT 1";
            QueueItem queueItem = jdbcTemplate.queryForObject(
                sql, new Object[]{}, (rs, rowNum) -> {
                    int data_id = rs.getInt("data_id");
                    int queue_id = rs.getInt("id");
                    return new QueueItem(queue_id, data_id);
                }

            );
            int queue_id = queueItem.getQueueId();
            int data_id = queueItem.getDataId();
    
            String sql2 = "SELECT * FROM " + table_data_name + " WHERE id = ?";
            Order ret = jdbcTemplate.queryForObject(
                sql2, new Object[]{data_id}, (rs, rowNum) -> {
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

            String sql3 = "UPDATE " + table_queue_name + " SET is_actual = FALSE WHERE id = ?";
            jdbcTemplate.update(sql3, queue_id);
            return ret;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    @Transactional
    public Integer add(int user_id, double amount, String description) {
        String insert_sql = "INSERT INTO " + table_data_name + " (user_id, amount, description, status) VALUES (?, ?, ?, ?) RETURNING id";
        Integer order_id = jdbcTemplate.queryForObject(
            insert_sql, 
            new Object[]{user_id, amount, description, 0}, 
            Integer.class
        );

        String insert_queue_sql = "INSERT INTO " + table_queue_name + " (data_id) VALUES (?)";
        jdbcTemplate.update(insert_queue_sql, order_id);

        return order_id;
    }

    private static class QueueItem {
        private final int queueId;
        private final int dataId;

        public QueueItem(int queueId, int dataId) {
            this.queueId = queueId;
            this.dataId = dataId;
        }

        public int getQueueId() {
            return queueId;
        }

        public int getDataId() {
            return dataId;
        }
    }

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
}

