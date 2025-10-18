CREATE DATABASE IF NOT EXISTS sale_management;
USE sale_management;

SET FOREIGN_KEY_CHECKS = 0; 

-- Tạo bảng Customer
CREATE TABLE Customer(
    customer_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    address VARCHAR(255),
    email VARCHAR(255) UNIQUE
);

-- Tạo bảng Employee
CREATE TABLE Employee(
    employee_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    salary double NOT NULL,
    hire_date DATE NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Tạo bảng Category
CREATE TABLE Category(
    category_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Tạo bảng Product
CREATE TABLE Product(
    product_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price double NOT NULL,
    quantity INT NOT NULL,
    description TEXT,
    category_id VARCHAR(20) NOT NULL,
    FOREIGN KEY(category_id)
        REFERENCES Category(category_id)
        ON DELETE RESTRICT
);

-- Tạo bảng Orders
CREATE TABLE Orders(
    order_id VARCHAR(20) PRIMARY KEY,
    order_date DATETIME,
    total_amount double,
    status VARCHAR(50),
    customer_id VARCHAR(20) NOT NULL,
    employee_id VARCHAR(20),
    FOREIGN KEY (customer_id) 
        REFERENCES Customer(customer_id)
        ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) 
        REFERENCES Employee(employee_id)
        ON DELETE SET NULL
);

-- Tạo bảng Order_Detail
CREATE TABLE Order_Detail(
    orderdetail_id VARCHAR(20) PRIMARY KEY,
    order_id VARCHAR(20) NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    price double NOT NULL,
    FOREIGN KEY(order_id)
        REFERENCES Orders(order_id)
        ON DELETE CASCADE,
    FOREIGN KEY(product_id)
        REFERENCES Product(product_id)
        ON DELETE RESTRICT
);

SET FOREIGN_KEY_CHECKS = 1;