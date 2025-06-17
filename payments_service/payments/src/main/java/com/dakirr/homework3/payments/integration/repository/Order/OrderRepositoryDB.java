package com.dakirr.homework3.payments.integration.repository.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; 

import com.dakirr.homework3.payments.domain.repository.OrderRepository;
import com.dakirr.homework3.payments.domain.value_object.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class OrderRepositoryDB implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    String table_data_name = "payment_request";
    String table_queue_name = "payment_request_queue";
    String table_inner_queue_name = "payment_request_inner_queue";
    String bank_table_name = "bank_accounts";
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryDB.class);


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
                                "id SERIAL PRIMARY KEY," +
                                "is_actual BOOLEAN NOT NULL DEFAULT TRUE," +
                                "data_id INT REFERENCES " + table_data_name + "(id)" +
                                ")";
        jdbcTemplate.execute(createQueueTableSql);

        String createTableInnerQueueSql = "CREATE TABLE IF NOT EXISTS " + table_inner_queue_name +  
                                " (" +
                                "id INT PRIMARY KEY," +
                                "user_id INT," +
                                "amount DOUBLE PRECISION," +
                                "description VARCHAR(255)," +
                                "status INT NOT NULL CHECK (status IN (0, 1, 2))" +
                                ")";
        jdbcTemplate.execute(createTableInnerQueueSql);
    }

    @Override
    public Order get(int id) {
        try {
            String sql = "SELECT * FROM " + table_data_name + " WHERE id = ?";
            return jdbcTemplate.queryForObject(
                sql, new Object[]{id}, (rs, rowNum) -> {
                    Order paymentRequest = new Order (
                        rs.getInt("id"), 
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getInt("status")
                    );
                    return paymentRequest;
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
                    Order paymentRequest = new Order (
                        rs.getInt("id"), 
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getInt("status")
                    );
                    return paymentRequest;
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
    public Integer add(Order paymentRequest) {
        try {
            String check_exists_userId = "SELECT COUNT(id) FROM " + bank_table_name + " WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(check_exists_userId, new Object[]{paymentRequest.getUserId()}, Integer.class);
            
            if (paymentRequest.getStatus() != 0) {
                paymentRequest.setStatus(2);
            }
            if (count != 1) {
                paymentRequest.setStatus(2);
            }
            
            String insert_sql = "INSERT INTO " + table_data_name + " (id, user_id, amount, description, status) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(
                insert_sql, 
                new Object[]{
                    paymentRequest.getId(), 
                    paymentRequest.getUserId(), 
                    paymentRequest.getAmount(), 
                    paymentRequest.getDescription(), 
                    paymentRequest.getStatus()
                }
            );

            String insert_queue_sql = "INSERT INTO " + table_queue_name + " (data_id) VALUES (?)";        
            Integer paymentRequestId = paymentRequest.getId();
            jdbcTemplate.update(insert_queue_sql, paymentRequestId);

            return paymentRequestId;
            } catch (Exception e) {
                logger.error("Database error while adding payment request for order ID: {}", e.getMessage(), e);
                return null;
            }
    }

    @Override
    @Transactional
    public Integer set_status(int id, int status) {
        if (status != 0 && status != 1 && status != 2) {
            return null;
        }
        try {
            String sql = "UPDATE " + table_data_name + " SET status = ? WHERE id = ?";
            jdbcTemplate.update(sql, status, id);
            return id;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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

    public Integer add_to_inner_queue(Order order) {
        try {
            String insert_sql = "INSERT INTO " + table_data_name + " (id, user_id, amount, description, status) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(
                insert_sql, 
                new Object[]{
                    order.getId(), 
                    order.getUserId(), 
                    order.getAmount(), 
                    order.getDescription(), 
                    order.getStatus()
                }
            );
            return order.getId();
        } catch (Exception e) {
            return null;
        }

    }

    public Order get_from_inner_queue() {
        try {
            String sql = "SELECT * FROM " + table_inner_queue_name + " ORDER BY id ASC LIMIT 1";
            Order ret = jdbcTemplate.queryForObject(
                sql, new Object[]{}, (rs, rowNum) -> {
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
            if (ret == null) {
                return null;
            }
            String sql2 = "DELETE FROM " + table_inner_queue_name + " WHERE id = ?";
            jdbcTemplate.update(sql2, ret.getId());
            return ret;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
    }
}
