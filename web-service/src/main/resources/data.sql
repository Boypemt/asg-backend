INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('John Smith', '123 Main St, New York, NY 10001', 'john.smith@email.com', '555-0101', '1985-03-15');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Sarah Johnson', '456 Oak Ave, Los Angeles, CA 90210', 'sarah.johnson@email.com', '555-0102', '1990-07-22');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Michael Brown', '789 Pine Rd, Chicago, IL 60601', 'michael.brown@email.com', '555-0103', '1988-11-08');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Emily Davis', '321 Elm St, Houston, TX 77001', 'emily.davis@email.com', '555-0104', '1992-01-30');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('David Wilson', '654 Maple Dr, Phoenix, AZ 85001', 'david.wilson@email.com', '555-0105', '1987-09-12');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Jessica Miller', '987 Cedar Ln, Philadelphia, PA 19101', 'jessica.miller@email.com', '555-0106', '1991-05-18');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Christopher Garcia', '147 Birch St, San Antonio, TX 78201', 'chris.garcia@email.com', '555-0107', '1986-12-03');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Amanda Rodriguez', '258 Spruce Ave, San Diego, CA 92101', 'amanda.rodriguez@email.com', '555-0108', '1993-04-25');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Matthew Martinez', '369 Willow Rd, Dallas, TX 75201', 'matthew.martinez@email.com', '555-0109', '1989-08-14');
INSERT INTO customer (displayname, address, email, phone, birthday) VALUES ('Ashley Anderson', '741 Poplar St, San Jose, CA 95101', 'ashley.anderson@email.com', '555-0110', '1994-02-07');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Wireless Bluetooth Headphones', 79.99, 'Premium quality wireless headphones with noise cancellation and 30-hour battery life', '2024-01-15');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Gaming Mechanical Keyboard', 149.99, 'RGB backlit mechanical keyboard with tactile switches, perfect for gaming and typing', '2024-02-20');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Smartphone Case', 24.99, 'Durable protective case with shock absorption and wireless charging compatibility', '2024-03-10');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('4K USB Webcam', 89.99, 'High-definition webcam with auto-focus and built-in microphone for video calls', '2024-01-25');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Portable Power Bank', 39.99, '20000mAh portable charger with fast charging support for multiple devices', '2024-02-14');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Ergonomic Office Chair', 299.99, 'Adjustable office chair with lumbar support and breathable mesh back', '2024-01-08');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('LED Desk Lamp', 45.99, 'Adjustable LED desk lamp with multiple brightness levels and USB charging port', '2024-03-05');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Wireless Mouse', 34.99, 'Ergonomic wireless mouse with precision tracking and long battery life', '2024-02-28');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('Bluetooth Speaker', 64.99, 'Portable waterproof speaker with 360-degree sound and 12-hour battery', '2024-01-18');
INSERT INTO product (name, price, description, manufactureDate) VALUES ('USB-C Hub', 54.99, '7-in-1 USB-C hub with HDMI, USB ports, and SD card reader for laptops', '2024-03-12');



-- Sale orders: many-to-one to customer
INSERT INTO sale_order (orderDate, status, customer_id) VALUES ('2024-04-01', 'PAID', 1);
INSERT INTO sale_order (orderDate, status, customer_id) VALUES ('2024-04-03', 'SHIPPED', 2);
INSERT INTO sale_order (orderDate, status, customer_id) VALUES ('2024-04-05', 'NEW', 1);

-- Order items: many-to-one to sale_order AND to product (unitPrice copied at time of sale)
INSERT INTO order_item (quantity, unitPrice, saleorder_id, product_id) VALUES (1, 79.99, 1, 1);
INSERT INTO order_item (quantity, unitPrice, saleorder_id, product_id) VALUES (2, 34.99, 1, 8);
INSERT INTO order_item (quantity, unitPrice, saleorder_id, product_id) VALUES (1, 149.99, 2, 2);
INSERT INTO order_item (quantity, unitPrice, saleorder_id, product_id) VALUES (3, 24.99, 3, 3);

-- Payments: one-to-one with sale_order (order 3 is unpaid, so it has no row)
INSERT INTO payment (method, amount, paidDate, saleorder_id) VALUES ('CREDIT_CARD', 149.97, '2024-04-01', 1);
INSERT INTO payment (method, amount, paidDate, saleorder_id) VALUES ('PROMPTPAY', 149.99, '2024-04-03', 2);
