# NOTE: Any condition befre GROUP BY use "WHERE". After GROUP BY, use "HAVING"
# Write your MySQL query statement below
SELECT email
FROM Person
GROUP BY email
HAVING COUNT(email) > 1