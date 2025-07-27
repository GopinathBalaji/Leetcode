# Method 1: LEFT JOIN with NULL filter
-- Do a LEFT JOIN to include all customers, even those without 
-- matching orders.
-- Filter out those who have no orders by checking 
-- Orders.id IS NULL.

SELECT name AS Customers
FROM Customers
LEFT JOIN Orders
ON Customers.id = Orders.customerId
WHERE Orders.id IS NULL;

###################################

#Method 2: NOT IN
-- Select customers whose id is not present in the Orders table.

-- SELECT name AS Customers
-- FROM Customers
-- WHERE id NOT IN (SELECT customerId FROM Orders);

####################

# My first attempt. 
# Explanation of what I got wrong
-- You're using a cross join (FROM Customers, Orders) and 
-- then trying to filter out matches with NOT 
-- (Customers.id = Orders.customerId). This causes incorrect logic 
-- because it does not correctly isolate customers who never 
-- ordered, and it produces duplicates or incorrect results due 
-- to the Cartesian product.

-- SELECT name as Customers
-- FROM Customers, Orders
-- WHERE NOT (Customers.id = Orders.customerId)