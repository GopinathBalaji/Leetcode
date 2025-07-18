# Write your MySQL query statement below

-- Using self join
-- **heart of understanding self-joins**.

-- Let’s break it down step by step.

-- ---

-- ## \U0001f50d The Tables and Relationships

-- You have **one `Employee` table**, like this:

-- | id (PK) | name  | salary | managerId (FK) |
-- | ------- | ----- | ------ | -------------- |
-- | 1       | Alice | 100k   | NULL           |
-- | 2       | Bob   | 80k    | 1              |
-- | 3       | Carol | 120k   | 1              |
-- | 4       | Dave  | 75k    | 2              |

-- Each row represents an employee, and each employee may have a **`managerId`** — which points to **another `id` in the same table**.

-- So, the `managerId` refers to the **`id` of the employee’s manager**.

-- ---

-- ## \U0001f501 Why `e.managerId = m.id`?

-- In a **self-join**, you're joining the `Employee` table with itself to **compare employees to their managers**.

-- Let’s call:

-- * `e` → the employee
-- * `m` → their manager (also an employee in the same table)

-- You want to pair each employee (`e`) with their manager (`m`).
-- To do that, you match:

-- ```sql
-- e.managerId = m.id
-- ```

-- Which literally means:

-- > “Find the row in the `Employee` table where the `id` matches this employee's `managerId`.”

-- This gives you access to the manager's data (e.g., `m.salary`) so you can compare it to the employee’s data (e.g., `e.salary`).

-- ---

-- ## \U0001f9e0 Think of it like this:

-- If Bob has:

-- ```sql
-- id = 2
-- name = Bob
-- salary = 80k
-- managerId = 1
-- ```

-- Then joining on `e.managerId = m.id` gives:

-- ```sql
-- e = Bob
-- m = Alice  ← because Alice’s id = 1
-- ```

-- Now you can compare:

-- ```sql
-- e.salary (Bob) > m.salary (Alice)  → 80k > 100k? ❌
-- ```

-- ---

-- ## \U0001f501 Self-Join Relationship

-- ```sql
-- -- Self-join pattern
-- FROM Employee e
-- JOIN Employee m
--   ON e.managerId = m.id
-- ```

-- | `e.name` | `e.managerId` | `m.id` | `m.name` |
-- | -------- | ------------- | ------ | -------- |
-- | Bob      | 1             | 1      | Alice    |
-- | Carol    | 1             | 1      | Alice    |
-- | Dave     | 2             | 2      | Bob      |

-- Each row connects an employee (`e`) to their manager (`m`) through the `managerId → id` relationship.

-- ---

-- ## ✅ Summary

-- * `e.managerId = m.id` links each employee to their manager.
-- * It lets you **access the manager’s salary**, so you can compare it to the employee’s.
-- * This is a standard way of doing **hierarchical comparisons** within a single table.

-- Let me know if you want a diagram or want to practice a similar join on a different dataset!

SELECT e.name AS Employee
FROM Employee e
JOIN Employee m
     ON e.managerId = m.id
WHERE e.salary > m.salary;


-- Using WHERE CLAUSEStep	What happens
-- Outer query:	Iterates over every employee e.
-- Correlated subquery:	For that same e, finds the salary of their manager (m.id = e.managerId).
-- • If managerId is NULL, the subquery returns NULL, so the comparison e.salary > NULL is unknown → row is skipped.
-- • Otherwise, it returns a single salary value.

-- SELECT name AS Employee
-- FROM Employee e
-- WHERE e.salary > 
--             (
--                 SELECT m.salary
--                 FROM Employee m
--                 WHERE m.id = e.managerId
--             );
