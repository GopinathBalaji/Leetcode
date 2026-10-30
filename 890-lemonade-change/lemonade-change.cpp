// Method 1: Greedy
/*
# LeetCode 860. Lemonade Change — Hints (C++)

This is a **Greedy Algorithm + Simulation + Counting + Resource Management** problem.

The key idea is that instead of trying every possible combination of bills to give change, you can make the **best local decision at each transaction** while keeping track of the bills currently available.

This is closely related to:

- **455. Assign Cookies:** Use a greedy strategy to allocate limited resources efficiently.
- **122. Best Time to Buy and Sell Stock II:** Make locally optimal decisions that lead to a globally optimal result.
- **134. Gas Station:** Process a sequence while maintaining whether available resources are sufficient.
- **1710. Maximum Units on a Truck:** Make greedy choices to maximize the value obtained from limited capacity.
- **135. Candy:** Distribute resources while satisfying specific constraints.
- **376. Wiggle Subsequence:** Recognize when local greedy decisions can replace more complicated dynamic programming.

The major difference is that **you must give exact change to each customer immediately, using only the bills received from previous customers**.

In particular, the tricky part is deciding how to give change when a customer pays with a `$20` bill.

You might have multiple ways to return `$15`, but **not all choices are equally good for future transactions**.

We'll derive the greedy solution step by step, understand why it works, explore a counterexample to an incorrect greedy strategy, and finish with a C++ skeleton.

---

# Hint 1: Understand the problem

You're running a lemonade stand.

Each lemonade costs:

```cpp
$5
```

Customers arrive one at a time.

Each customer purchases exactly one lemonade and pays using one of the following bills:

```cpp
5
10
20
```

You're given:

```cpp
vector<int>& bills;
```

For example:

```cpp
bills = {5, 5, 10, 20};
```

Each element represents the bill given by a customer.

You must determine whether you can provide the correct change to **every customer in the given order**.

Return:

```cpp
true
```

if every customer receives the correct change.

Otherwise, return:

```cpp
false
```

### Important constraint

Initially, you have no money.

```cpp
five = 0;
ten = 0;
```

You cannot give change using money that you haven't received yet.

You also cannot rearrange the customers.

Their order matters.

---

# Hint 2: Determine how much change each customer needs

Since lemonade costs `$5`, the amount of change depends on the bill received.

There are only three possibilities.

| Customer pays | Lemonade price | Change required |
|---|---|---|
| `$5` | `$5` | `$0` |
| `$10` | `$5` | `$5` |
| `$20` | `$5` | `$15` |

Notice that the required change is completely determined by the incoming bill.

Let's analyze each situation separately.

### Case 1: Customer pays `$5`

The customer pays the exact price.

```cpp
bill = 5;
```

No change is required.

You receive one `$5` bill.

Therefore:

```cpp
five++;
```

This transaction is always successful.

### Case 2: Customer pays `$10`

The customer needs:

```cpp
10 - 5 = 5
```

in change.

You must give back exactly one `$5` bill.

Therefore, you need:

```cpp
five >= 1
```

If you have a `$5` bill, you can complete the transaction.

Otherwise, you cannot provide the correct change.

### Case 3: Customer pays `$20`

The customer needs:

```cpp
20 - 5 = 15
```

in change.

This is the most interesting case.

How can you make `$15` using the available bill denominations?

There are two possibilities:

```text
$10 + $5 = $15
```

or:

```text
$5 + $5 + $5 = $15
```

These are the only valid combinations.

**Key observation:** The `$20` case introduces a choice, and choosing the better combination is the core greedy insight.

---

# Hint 3: Why does the order of customers matter?

Consider:

```cpp
bills = {5, 10};
```

Initially:

```text
$5 bills  = 0
$10 bills = 0
```

### Customer 1 pays `$5`

No change is required.

You receive one `$5` bill.

Now:

```text
$5 bills  = 1
$10 bills = 0
```

### Customer 2 pays `$10`

The customer needs `$5` in change.

You have one `$5` bill available.

Give it to the customer.

After the transaction:

```text
$5 bills  = 0
$10 bills = 1
```

Both customers received the correct change.

Therefore:

```cpp
answer = true;
```

Now reverse the order:

```cpp
bills = {10, 5};
```

### Customer 1 pays `$10`

The customer needs `$5` in change.

But initially:

```text
$5 bills = 0
```

You cannot provide the change.

Therefore:

```cpp
answer = false;
```

Even though a customer will later pay with a `$5` bill, you cannot use that future bill for the current transaction.

### Important observation

```text
{5, 10} → true

{10, 5} → false
```

The same bills can produce different answers depending on their order.

Therefore, **you must process the transactions from left to right**.

You should not sort the array.

---

# Hint 4: Do you need to track the total amount of money?

Suppose you have:

```text
$5 bills  = 0
$10 bills = 2
$20 bills = 1
```

The total money available is:

```text
$10 + $10 + $20 = $40
```

Now a customer pays with a `$10` bill.

They need:

```text
$5
```

in change.

You have `$40` in your cash register.

Can you provide the change?

No.

Why?

Because you don't have a `$5` bill.

You cannot return:

```text
$10
```

because that would be too much change.

You also cannot split a `$10` bill into smaller denominations.

Therefore, knowing the total amount of money is insufficient.

### Incorrect idea

```cpp
int totalMoney = 0;
```

Then checking whether:

```cpp
totalMoney >= requiredChange
```

does not solve the problem.

The total amount may be sufficient even when the required denominations are unavailable.

### Better idea

Track how many bills of each useful denomination you have.

For example:

```cpp
int five = 0;
int ten = 0;
```

Here:

```cpp
five
```

represents the number of available `$5` bills.

And:

```cpp
ten
```

represents the number of available `$10` bills.

These two counts contain all the information required to make change.

---

# Hint 5: Do you need to track `$20` bills?

This is an important optimization.

You might initially think of tracking:

```cpp
int five = 0;
int ten = 0;
int twenty = 0;
```

But ask yourself:

**Can a `$20` bill ever be used to give change?**

The lemonade costs:

```text
$5
```

The possible change amounts are:

```text
$0

$5

$15
```

A `$20` bill is larger than every possible change amount.

Therefore, a `$20` bill cannot contribute to giving change to any customer.

For example:

```text
Customer pays $10.
```

Required change:

```text
$5
```

A `$20` bill is useless.

Similarly:

```text
Customer pays $20.
```

Required change:

```text
$15
```

A `$20` bill is still useless.

### Important observation

You only need:

```cpp
int five = 0;
int ten = 0;
```

You do not need:

```cpp
int twenty;
```

because the number of `$20` bills never affects whether you can provide change.

This reduces the entire problem to tracking just two integers.

### General principle

In greedy resource-management problems, try to identify which resources can actually influence future decisions.

If a resource can never be used again, you may not need to track it.

---

# Hint 6: Case 1 — Customer pays `$5`

Suppose:

```cpp
bill = 5;
```

The lemonade costs `$5`.

Therefore:

```text
Required change = $0
```

No bills need to be returned.

You simply accept the customer's `$5` bill.

### Example

Before the transaction:

```text
five = 2
ten  = 1
```

Customer pays:

```text
$5
```

After the transaction:

```text
five = 3
ten  = 1
```

We received one additional `$5` bill.

The `$10` count remains unchanged.

### C++ logic

```cpp
if (bill == 5) {

    five++;
}
```

This is the simplest case.

### Why are `$5` bills important?

A `$5` bill is the smallest denomination available.

It is also necessary when giving change for a `$10` bill.

For example:

```text
Customer pays $10

Required change = $5
```

There is no alternative.

You must have a `$5` bill.

Similarly, giving `$15` in change to a `$20` customer also requires at least one `$5` bill.

Therefore:

**The availability of `$5` bills is especially important for future transactions.**

Keep this observation in mind when choosing between different ways of giving `$15` in change.

---

# Hint 7: Case 2 — Customer pays `$10`

Now suppose:

```cpp
bill = 10;
```

The customer needs:

```text
$10 - $5 = $5
```

in change.

You have only one possible way to provide `$5`:

```text
One $5 bill
```

Therefore, you must check whether:

```cpp
five > 0
```

Let's examine both possibilities.

### Situation 1: You have a `$5` bill

Suppose:

```text
five = 2
ten  = 0
```

Customer pays `$10`.

You give one `$5` bill in change.

So:

```text
five = 1
```

You also receive a `$10` bill.

Therefore:

```text
ten = 1
```

The updated state is:

```text
five = 1
ten  = 1
```

The transaction succeeds.

### Situation 2: You don't have a `$5` bill

Suppose:

```text
five = 0
ten  = 2
```

Customer pays `$10`.

They need `$5` in change.

But:

```text
five = 0
```

You cannot give the correct change.

Therefore:

```cpp
return false;
```

### Important distinction

When a customer pays `$10`, you must perform two operations:

```text
1. Give one $5 bill.

2. Receive one $10 bill.
```

This means:

```cpp
five--;
ten++;
```

However, these operations are valid only if you already have at least one `$5` bill.

### Mental model

```text
        Customer pays $10
                |
                v
       Needs $5 in change
                |
                v
          Is five > 0?
                |
         ┌──────┴──────┐
         |             |
        YES            NO
         |             |
         v             v
      Give $5      Cannot provide
         |          exact change
         v             |
      five--           v
         |         return false
         v
      ten++
         |
         v
      Continue
```

**Key observation:** The `$10` case is forced. There is only one valid way to give change.

---

# Hint 8: Case 3 — Customer pays `$20`

This is the core of the problem.

Suppose:

```cpp
bill = 20;
```

The customer needs:

```text
$20 - $5 = $15
```

in change.

How can we provide `$15`?

There are exactly two possibilities.

### Option 1: Give one `$10` bill and one `$5` bill

```text
$10 + $5 = $15
```

This requires:

```cpp
ten >= 1
```

and:

```cpp
five >= 1
```

If both conditions hold, the transaction can be completed.

The cash register changes as follows:

```cpp
ten--;
five--;
```

### Option 2: Give three `$5` bills

```text
$5 + $5 + $5 = $15
```

This requires:

```cpp
five >= 3
```

If you have at least three `$5` bills, this option is valid.

The cash register changes as follows:

```cpp
five -= 3;
```

### What if neither option is possible?

Suppose:

```text
five = 2
ten  = 0
```

You have:

```text
$5 + $5 = $10
```

But you need:

```text
$15
```

You cannot make the required amount.

Therefore:

```cpp
return false;
```

### Important question

Suppose both options are available.

For example:

```text
five = 3
ten  = 1
```

You can give:

```text
$10 + $5
```

or:

```text
$5 + $5 + $5
```

Both combinations provide the correct change.

**Which should you choose?**

This question leads directly to the greedy strategy.

---

# Hint 9: Why should you prefer `$10 + $5` over three `$5` bills?

Suppose the cash register contains:

```text
five = 3
ten  = 1
```

A customer pays `$20`.

You must give `$15` in change.

Let's compare the two options.

## Option A: Give three `$5` bills

Before:

```text
five = 3
ten  = 1
```

Give:

```text
$5 + $5 + $5
```

After:

```text
five = 0
ten  = 1
```

You have no `$5` bills remaining.

## Option B: Give one `$10` bill and one `$5` bill

Before:

```text
five = 3
ten  = 1
```

Give:

```text
$10 + $5
```

After:

```text
five = 2
ten  = 0
```

You preserve two `$5` bills.

### Compare the results

| Strategy | `$5` bills remaining | `$10` bills remaining |
|---|---:|---:|
| Give three `$5` bills | 0 | 1 |
| Give `$10 + $5` | 2 | 0 |

Which state is better for future customers?

Consider the next customer paying `$10`.

They need one `$5` bill in change.

Under Option A:

```text
five = 0
```

You cannot provide the change.

Under Option B:

```text
five = 2
```

You can provide the change.

### Why are `$5` bills more valuable?

A `$5` bill is required for both types of nonzero change:

```text
$10 customer → Requires one $5 bill.
```

And:

```text
$20 customer → Requires at least one $5 bill.
```

A `$10` bill is useful only when giving `$15` in change.

Furthermore, spending one `$10` bill instead of two additional `$5` bills preserves those smaller bills for future customers.

### Greedy choice

**Whenever possible, give `$10 + $5` instead of three `$5` bills.**

In other words:

```text
If both combinations are available:

Choose $10 + $5.
```

This is the central greedy decision.

---

# Hint 10: See why the opposite greedy choice can fail

Consider:

```cpp
bills = {5, 5, 5, 10, 20, 10};
```

The correct answer is:

```cpp
true;
```

However, let's see what happens if we make the wrong choice when processing `$20`.

### Customer 1 pays `$5`

```text
five = 1
ten  = 0
```

### Customer 2 pays `$5`

```text
five = 2
ten  = 0
```

### Customer 3 pays `$5`

```text
five = 3
ten  = 0
```

### Customer 4 pays `$10`

We must return one `$5` bill.

After the transaction:

```text
five = 2
ten  = 1
```

### Important detail

At this point, we have only two `$5` bills, so the `$20` customer would have only one possible change combination.

To observe a choice between both combinations, consider this slightly modified sequence:

```cpp
bills = {5, 5, 5, 5, 10, 20, 10};
```

After the first four customers:

```text
five = 4
ten  = 0
```

After the fifth customer pays `$10`:

```text
five = 3
ten  = 1
```

Now the sixth customer pays `$20`.

Both change combinations are available.

## Incorrect strategy: Use three `$5` bills first

Give:

```text
$5 + $5 + $5
```

After the transaction:

```text
five = 0
ten  = 1
```

Now the seventh customer pays `$10`.

They need:

```text
$5
```

in change.

But:

```text
five = 0
```

The transaction fails.

So this strategy would produce:

```cpp
false;
```

## Correct strategy: Use `$10 + $5` first

Return to the state before the `$20` transaction:

```text
five = 3
ten  = 1
```

Give:

```text
$10 + $5
```

After the transaction:

```text
five = 2
ten  = 0
```

Now the seventh customer pays `$10`.

They need one `$5` bill.

You have two available.

Give one `$5` bill.

After the transaction:

```text
five = 1
ten  = 1
```

Every customer received the correct change.

Therefore:

```cpp
answer = true;
```

### Important conclusion

The two strategies produce different results:

```text
Use three $5 bills first
            ↓
          false
```

```text
Use $10 + $5 first
            ↓
           true
```

This demonstrates why **the order in which you choose change combinations matters**.

The correct greedy strategy preserves as many `$5` bills as possible.

---

# Hint 11: Derive the greedy decision order for `$20`

We now understand the two valid combinations:

```text
Option 1: $10 + $5

Option 2: $5 + $5 + $5
```

We also understand that Option 1 is preferable because it preserves smaller bills.

Therefore, when processing a `$20` bill, you should consider the possibilities in this order.

### Priority 1: Can you give `$10 + $5`?

Check:

```cpp
ten > 0 && five > 0
```

If the condition is true:

```cpp
ten--;
five--;
```

The transaction succeeds.

### Priority 2: Can you give three `$5` bills?

If the first option is unavailable, check:

```cpp
five >= 3
```

If true:

```cpp
five -= 3;
```

The transaction succeeds.

### Priority 3: Neither combination is possible

If you cannot use either combination:

```cpp
return false;
```

### Greedy decision tree

```text
           Customer pays $20
                   |
                   v
          Needs $15 in change
                   |
                   v
       Have a $10 AND a $5?
                   |
            ┌──────┴──────┐
            |             |
           YES            NO
            |             |
            v             v
       Give $10+$5     Have at least
            |          three $5 bills?
            |             |
            |       ┌─────┴─────┐
            |       |           |
            |      YES          NO
            |       |           |
            |       v           v
            |    Give 3×$5   return false
            |       |
            └───┬───┘
                |
                v
          Update counts
                |
                v
        Next transaction
```

The most important detail is:

**Check for `$10 + $5` before checking for three `$5` bills.**

Reversing these conditions can cause an incorrect answer.

---

# Hint 12: Understand why this greedy strategy is correct

We have a greedy rule:

> When giving `$15` in change, prefer one `$10` bill and one `$5` bill whenever possible.

But why is this choice always safe?

Let's reason about what happens when both options are available.

Suppose:

```text
five = F
ten  = T
```

where:

```text
F >= 3
T >= 1
```

Both ways of giving `$15` are possible.

### Choice A: Give three `$5` bills

After the transaction:

```text
five = F - 3
ten  = T
```

### Choice B: Give `$10 + $5`

After the transaction:

```text
five = F - 1
ten  = T - 1
```

Compare the states.

Choice B leaves:

```text
2 additional $5 bills
```

but:

```text
1 fewer $10 bill
```

What is the relationship between these resources?

Two `$5` bills have the same total value as one `$10` bill:

```text
$5 + $5 = $10
```

But two `$5` bills are more flexible.

Why?

Because they can be used individually.

For example:

```text
A future $10 customer needs exactly $5.
```

Two `$5` bills can provide that change.

One `$10` bill cannot.

Similarly, for a future `$20` customer:

```text
$10 + $5
```

can be replaced by:

```text
$5 + $5 + $5
```

if the required three `$5` bills are available.

### Exchange argument

Suppose some successful strategy chooses to give three `$5` bills even though `$10 + $5` was available.

Imagine modifying that strategy so that it gives `$10 + $5` instead.

Immediately afterward, the modified strategy has two extra `$5` bills and one fewer `$10` bill.

For future transactions:

- If the original strategy needs a `$5` bill, the modified strategy has at least as many available.
- If the original strategy needs a `$10 + $5` combination, the modified strategy can either use that combination or substitute three `$5` bills for it.
- If the original strategy needs three `$5` bills, the modified strategy can also provide them.

Therefore, choosing `$10 + $5` does not eliminate the possibility of completing the remaining transactions.

We can replace a successful strategy's three-`$5` choice with `$10 + $5` without making its future transactions impossible.

This proves the greedy preference is safe.

### Key insight

**Two `$5` bills are at least as useful as one `$10` bill for making the change amounts required in this problem.**

Therefore, when choosing between equivalent ways of returning `$15`, preserve the smaller bills.

This is why the greedy strategy works.

---

# Hint 13: Think in terms of maintaining a valid cash-register state

Instead of thinking about individual bills scattered across the cash register, maintain two counters:

```cpp
int five = 0;
int ten = 0;
```

After each successful transaction, these counters represent:

```text
five = Number of available $5 bills.

ten  = Number of available $10 bills.
```

For every incoming bill, update these counters.

### Transaction type 1

```text
Customer pays $5.
```

Effect:

```text
five increases by 1.
```

### Transaction type 2

```text
Customer pays $10.
```

Effect:

```text
five decreases by 1.

ten increases by 1.
```

But only if:

```text
five >= 1.
```

### Transaction type 3

```text
Customer pays $20.
```

Preferred effect:

```text
five decreases by 1.

ten decreases by 1.
```

Otherwise:

```text
five decreases by 3.
```

If neither is possible, the transaction fails.

### Important invariant

After processing the first `i` customers successfully:

```text
five >= 0

ten >= 0
```

And the counters represent a valid collection of bills remaining after giving exact change to all processed customers.

This is our greedy invariant.

### Why does this help?

Suppose you're processing a customer and discover that the required bills are unavailable.

You cannot borrow money from future customers.

You cannot go back and change the order.

And because the earlier transactions used the safe greedy strategy, choosing a different earlier combination would not rescue the situation.

Therefore:

```cpp
return false;
```

is correct.

You do not need to explore alternative sequences using backtracking.

---

# Hint 14: Walk through a complete example

Consider:

```cpp
bills = {5, 5, 5, 10, 20};
```

Expected answer:

```cpp
true;
```

Let's simulate every transaction.

Initially:

```text
five = 0
ten  = 0
```

## Customer 1 pays `$5`

No change is required.

Receive one `$5` bill.

```text
five = 1
ten  = 0
```

## Customer 2 pays `$5`

No change is required.

```text
five = 2
ten  = 0
```

## Customer 3 pays `$5`

No change is required.

```text
five = 3
ten  = 0
```

## Customer 4 pays `$10`

Required change:

```text
$5
```

Check:

```text
five > 0
```

Yes.

Give one `$5` bill.

Receive one `$10` bill.

```text
five = 2
ten  = 1
```

## Customer 5 pays `$20`

Required change:

```text
$15
```

Check the preferred combination:

```text
$10 + $5
```

Do we have both bills?

```text
five = 2

ten = 1
```

Yes.

Give one of each.

After the transaction:

```text
five = 1
ten  = 0
```

All customers received the correct change.

Therefore:

```cpp
answer = true;
```

### Full simulation table

| Customer | Pays | Change required | Bills given as change | `$5` bills remaining | `$10` bills remaining |
|---|---:|---:|---|---:|---:|
| Start | — | — | — | 0 | 0 |
| 1 | 5 | 0 | None | 1 | 0 |
| 2 | 5 | 0 | None | 2 | 0 |
| 3 | 5 | 0 | None | 3 | 0 |
| 4 | 10 | 5 | One `$5` | 2 | 1 |
| 5 | 20 | 15 | One `$10` + one `$5` | 1 | 0 |

Notice that neither counter becomes negative.

Every customer receives exact change.

The final result is:

```cpp
true;
```

---

# Hint 15: Walk through a failing example

Now consider:

```cpp
bills = {5, 5, 10, 10, 20};
```

Expected answer:

```cpp
false;
```

Initially:

```text
five = 0
ten  = 0
```

### Customer 1 pays `$5`

```text
five = 1
ten  = 0
```

### Customer 2 pays `$5`

```text
five = 2
ten  = 0
```

### Customer 3 pays `$10`

Give one `$5` bill.

Receive one `$10` bill.

```text
five = 1
ten  = 1
```

### Customer 4 pays `$10`

Give one `$5` bill.

Receive one `$10` bill.

```text
five = 0
ten  = 2
```

### Customer 5 pays `$20`

Required change:

```text
$15
```

Try the first option:

```text
$10 + $5
```

You have:

```text
ten = 2
five = 0
```

You don't have a `$5` bill.

Therefore, the first option fails.

Try the second option:

```text
$5 + $5 + $5
```

But:

```text
five = 0
```

You don't have three `$5` bills either.

Neither option works.

Therefore:

```cpp
return false;
```

### Full simulation table

| Customer | Pays | Change required | Result | `$5` bills | `$10` bills |
|---|---:|---:|---|---:|---:|
| Start | — | — | — | 0 | 0 |
| 1 | 5 | 0 | Success | 1 | 0 |
| 2 | 5 | 0 | Success | 2 | 0 |
| 3 | 10 | 5 | Success | 1 | 1 |
| 4 | 10 | 5 | Success | 0 | 2 |
| 5 | 20 | 15 | **Failure** | 0 | 2 |

This example reinforces an important idea:

**Having enough total money does not guarantee that you can provide exact change.**

At the last transaction, you have:

```text
$10 + $10 = $20
```

but cannot make:

```text
$15
```

because no `$5` bills remain.

---

# Hint 16: Do you need Dynamic Programming?

At first glance, the `$20` case seems to offer multiple possibilities.

You might think of using recursion:

```text
For every customer:

    Try every possible way of giving change.

    Recursively process the remaining customers.
```

For a `$20` bill, you might branch into:

```text
Option 1: Give $10 + $5.

Option 2: Give three $5 bills.
```

This resembles backtracking or dynamic programming.

But do you actually need to explore both choices?

No.

We already established that:

```text
$10 + $5
```

is always at least as good as:

```text
$5 + $5 + $5
```

when both combinations are available.

Therefore, one choice dominates the other.

You can immediately select the better combination.

### Compare the approaches

| Approach | Main idea | Necessary? |
|---|---|---|
| Backtracking | Try all valid change combinations | No |
| Dynamic Programming | Store results for different cash-register states | No |
| Greedy | Always prefer the change combination that preserves `$5` bills | Yes |
| Simulation | Process customers sequentially and update bill counts | Yes |

### Why is greedy sufficient?

Because the local choice:

```text
Use $10 + $5 whenever possible.
```

never makes future transactions harder than using three `$5` bills.

Therefore, we don't need to consider every possible future outcome.

We can make the choice immediately.

**This is a classic sign that a greedy solution may exist: one available decision is provably at least as good as the alternatives.**

---

# Hint 17: What should happen when you encounter an impossible transaction?

Suppose:

```cpp
bills = {5, 10, 20, 5, 5};
```

Let's simulate.

### Customer 1

Pays `$5`.

```text
five = 1
ten  = 0
```

### Customer 2

Pays `$10`.

Give one `$5`.

Receive one `$10`.

```text
five = 0
ten  = 1
```

### Customer 3

Pays `$20`.

Needs `$15` in change.

Available bills:

```text
five = 0
ten  = 1
```

Can we give:

```text
$10 + $5?
```

No.

Can we give:

```text
$5 + $5 + $5?
```

No.

Therefore, this transaction fails.

### Should we continue to future customers?

No.

Even though the next customers will pay with `$5` bills, those bills are not currently available.

You cannot use future money to fix an earlier transaction.

Therefore, immediately:

```cpp
return false;
```

### Important observation

You should return `false` as soon as a customer cannot receive exact change.

There is no reason to process the remaining customers.

However, if all customers are processed successfully, return:

```cpp
true;
```

### Overall structure

```text
       Start
         |
         v
   Initialize counts
         |
         v
   Process next bill
         |
         v
   Can exact change
      be provided?
         |
    ┌────┴────┐
    |         |
   YES        NO
    |         |
    v         v
 Update     return
 counts      false
    |
    v
 More customers?
    |
 ┌──┴───┐
 |      |
YES     NO
 |      |
 v      v
Repeat return true
```

---

# Hint 18: Analyze the time and space complexity

Let:

```cpp
int n = bills.size();
```

## Time complexity

We process every customer at most once.

For every bill, we perform a constant amount of work.

### If the bill is `$5`

```text
Increment one counter.
```

Time:

\[
O(1)
\]

### If the bill is `$10`

```text
Check whether a $5 bill exists.

Update two counters.
```

Time:

\[
O(1)
\]

### If the bill is `$20`

```text
Check whether $10 + $5 is available.

Otherwise check whether three $5 bills are available.

Update counters.
```

Time:

\[
O(1)
\]

Since there are `n` customers:

\[
T(n)=n\times O(1)
\]

Therefore:

\[
\boxed{\text{Time: }O(n)}
\]

## Space complexity

We only maintain two integers:

```cpp
int five;
int ten;
```

No auxiliary array is needed.

No hash map is needed.

No recursion is needed.

No dynamic programming table is needed.

Therefore:

\[
\boxed{\text{Space: }O(1)}
\]

### Comparison with other approaches

| Approach | Time complexity | Space complexity |
|---|---|---|
| Naive backtracking | Potentially exponential | Depends on recursion depth |
| Memoization over cash-register states | Potentially polynomial with many states | More than constant |
| **Greedy + Simulation** | **O(n)** | **O(1)** |

The greedy solution is both simpler and more efficient.

---

# Skeleton — Greedy + Simulation (C++)

Try filling in the missing conditions and updates yourself.

The following skeleton intentionally leaves the important expressions incomplete.

```cpp
class Solution {
public:
    bool lemonadeChange(vector<int>& bills) {

        // Count available $5 bills.

        int five = 0;

        // Count available $10 bills.

        int ten = 0;

        // Process customers in arrival order.

        for (int bill : bills) {

            // Case 1:
            // Customer pays exactly $5.
            // No change is required.

            if (bill == 5) {

                // Receive one $5 bill.

                // TODO: Update five.


            }

            // Case 2:
            // Customer pays $10.
            // Need $5 in change.

            else if (bill == 10) {

                // Check whether a $5 bill
                // is available.

                if (_____) {

                    return false;
                }

                // Give one $5 bill.

                // TODO: Update five.


                // Receive one $10 bill.

                // TODO: Update ten.


            }

            // Case 3:
            // Customer pays $20.
            // Need $15 in change.

            else {

                // Priority 1:
                // Give one $10 bill
                // and one $5 bill.

                if (_____) {

                    // TODO: Update five.

                    // TODO: Update ten.

                }

                // Priority 2:
                // Give three $5 bills.

                else if (_____) {

                    // TODO: Update five.

                }

                // Neither combination
                // is available.

                else {

                    return false;
                }
            }
        }

        // Every customer received
        // the correct change.

        return true;
    }
};
```

The most important parts to derive are the following.

### Question 1: When should the `$10` transaction fail?

```cpp
if (_____) {

    return false;
}
```

Think about:

```text
How many $5 bills are necessary
to give change for $10?
```

### Question 2: When can you use `$10 + $5`?

```cpp
if (_____) {

    // Give $10 + $5.
}
```

Think about:

```text
Which two denominations
must both be available?
```

### Question 3: When can you give three `$5` bills?

```cpp
else if (_____) {

    // Give three $5 bills.
}
```

Think about:

```text
What is the minimum number
of $5 bills required?
```

### Question 4: Why must `$10 + $5` be checked first?

Think about what happens to the number of remaining `$5` bills under each option.

If you understand why preserving two extra `$5` bills is useful, you've understood the hardest part of this problem.

---

# Core Greedy Rules

We can summarize the entire algorithm using three rules.

## Rule 1: When receiving `$5`, save it

The customer requires no change.

Therefore:

```cpp
five++;
```

There is no possibility of failure in this case.

---

## Rule 2: When receiving `$10`, give one `$5`

You must have:

```cpp
five >= 1
```

If not, the transaction fails.

Otherwise:

```cpp
five--;
ten++;
```

The `$5` bill is given as change.

The `$10` bill is added to the cash register.

---

## Rule 3: When receiving `$20`, prefer `$10 + $5`

First, try:

```cpp
ten >= 1 && five >= 1
```

If possible:

```cpp
ten--;
five--;
```

Otherwise, try:

```cpp
five >= 3
```

If possible:

```cpp
five -= 3;
```

If neither condition holds:

```cpp
return false;
```

This is the entire greedy strategy.

---

# Mathematical Representation

Let:

\[
F_i
\]

represent the number of `$5` bills remaining after processing the first `i` customers.

Let:

\[
T_i
\]

represent the number of `$10` bills remaining after processing the first `i` customers.

Initially:

\[
F_0=0
\]

\[
T_0=0
\]

For the `i`-th customer, consider the bill received.

## Case 1: Bill is `$5`

No change is required.

We receive one `$5` bill.

Therefore:

\[
\boxed{F_i=F_{i-1}+1}
\]

\[
\boxed{T_i=T_{i-1}}
\]

---

## Case 2: Bill is `$10`

We must give one `$5` bill.

This is possible only if:

\[
F_{i-1}\geq 1
\]

Then:

\[
\boxed{F_i=F_{i-1}-1}
\]

\[
\boxed{T_i=T_{i-1}+1}
\]

Otherwise:

\[
\boxed{\text{Return false}}
\]

---

## Case 3: Bill is `$20`

We need `$15` in change.

### Preferred combination

If:

\[
F_{i-1}\geq1
\]

and:

\[
T_{i-1}\geq1
\]

then give `$10 + $5`.

The new state is:

\[
\boxed{F_i=F_{i-1}-1}
\]

\[
\boxed{T_i=T_{i-1}-1}
\]

### Alternative combination

If the preferred combination is unavailable but:

\[
F_{i-1}\geq3
\]

then give three `$5` bills.

The new state is:

\[
\boxed{F_i=F_{i-1}-3}
\]

\[
\boxed{T_i=T_{i-1}}
\]

### Neither combination is available

If both options fail:

\[
\boxed{\text{Return false}}
\]

Otherwise, continue processing customers.

After all `n` customers have been processed:

\[
\boxed{\text{Return true}}
\]

Notice that these equations describe a greedy state update, not a dynamic programming recurrence requiring multiple stored states.

We only need the current values of:

\[
F
\]

and:

\[
T
\]

Therefore, the space complexity remains constant.

---

# Connection to Previous Greedy Problems

The most useful conceptual connection is with **LeetCode 455. Assign Cookies**.

Both problems involve allocating limited resources in a way that preserves the ability to satisfy future requirements.

However, the resources and constraints are different.

## LeetCode 455 — Assign Cookies

You're given:

```cpp
vector<int> g;
vector<int> s;
```

Here:

```text
g[i] = Minimum cookie size needed by child i.

s[j] = Size of cookie j.
```

Your goal is to maximize the number of satisfied children.

A common greedy strategy is:

1. Sort the children by their minimum required cookie sizes.
2. Sort the available cookies by size.
3. Give each child the smallest available cookie that satisfies them.

Why use the smallest sufficient cookie?

Because larger cookies may be needed for children with greater requirements.

The greedy principle is:

**Avoid wasting flexible or valuable resources when a smaller sufficient resource exists.**

---

## LeetCode 860 — Lemonade Change

You have two useful denominations:

```text
$5

$10
```

Your goal is to give exact change to every customer.

When processing a `$20` bill, there are two possibilities:

```text
$10 + $5
```

or:

```text
$5 + $5 + $5
```

The greedy strategy chooses:

```text
$10 + $5
```

because it preserves more `$5` bills.

Those smaller bills may be essential for future transactions.

### Comparison

| Feature | 455. Assign Cookies | 860. Lemonade Change |
|---|---|---|
| Main technique | Greedy | Greedy |
| Resources | Cookies | Cash bills |
| Goal | Maximize satisfied children | Satisfy every customer |
| Is ordering important? | Sort to establish a useful order | Preserve original customer order |
| Greedy decision | Use smallest sufficient cookie | Prefer `$10 + $5` for `$15` change |
| Main insight | Preserve larger cookies for demanding children | Preserve flexible `$5` bills |
| Time complexity | O(n log n + m log m) | O(n) |
| Extra space | Depends on sorting implementation | O(1) |

The shared principle is:

**Choose a resource allocation that satisfies the current requirement without unnecessarily reducing your ability to satisfy future requirements.**

---

# Common Mistakes to Avoid

## Mistake 1: Sorting the bills

A common incorrect idea is:

```cpp
sort(bills.begin(), bills.end());
```

Why is this wrong?

Because customers arrive in a fixed order.

Consider:

```cpp
bills = {10, 5};
```

The first customer pays `$10`.

You have no `$5` bills.

Therefore:

```cpp
answer = false;
```

But sorting produces:

```cpp
{5, 10}
```

which would allow both transactions to succeed.

That changes the original problem.

**Never rearrange the customer order.**

---

## Mistake 2: Tracking only the total money

Consider:

```text
five = 0
ten  = 2
```

Total available:

```text
$20
```

A customer pays `$10`.

They need `$5` in change.

Even though you have `$20`, you cannot give exactly `$5`.

Therefore, this is insufficient:

```cpp
totalMoney >= requiredChange
```

You must track the available denominations.

---

## Mistake 3: Giving three `$5` bills before trying `$10 + $5`

Suppose:

```text
five = 3
ten  = 1
```

A `$20` customer arrives.

Both change combinations are possible.

The incorrect priority is:

```text
1. Try three $5 bills.

2. Try $10 + $5.
```

This unnecessarily consumes smaller bills.

The correct priority is:

```text
1. Try $10 + $5.

2. Try three $5 bills.
```

This preserves more `$5` bills for future customers.

---

## Mistake 4: Forgetting to add the received `$10` bill

Suppose:

```cpp
bill = 10;
```

You give one `$5` bill in change.

But remember:

**The customer also gives you a `$10` bill.**

Therefore, a successful transaction must update both counts:

```cpp
five--;
ten++;
```

If you only decrement `five`, your cash-register state becomes incorrect.

---

## Mistake 5: Thinking a `$10` bill can replace a `$5` bill

Suppose:

```text
five = 0
ten  = 3
```

A customer pays `$10`.

You cannot provide `$5` in change.

Similarly, if a customer pays `$20`, you cannot give `$15` using only `$10` bills.

The available amount of money is not enough to determine whether exact change is possible.

The denominations matter.

---

## Mistake 6: Waiting until the end to detect failure

Consider:

```cpp
bills = {10, 5, 5, 5};
```

The first customer pays `$10`.

You have no change.

Therefore, the answer is immediately:

```cpp
false;
```

It doesn't matter that later customers pay with `$5` bills.

You must give correct change at the moment each customer arrives.

---

## Mistake 7: Using unnecessary data structures

You might try:

```cpp
map<int, int> cash;
```

or:

```cpp
unordered_map<int, int> cash;
```

These approaches can represent the available bills, but they're unnecessary.

There are only two denominations that matter for change:

```cpp
5
10
```

Therefore:

```cpp
int five = 0;
int ten = 0;
```

is sufficient.

This is simpler and uses constant space.

---

# Edge Cases to Test

Before submitting your C++ solution, try these examples.

| Input | Expected output | Reason |
|---|---|---|
| `{5}` | `true` | No change required |
| `{10}` | `false` | No initial `$5` bill |
| `{20}` | `false` | Cannot provide `$15` |
| `{5, 10}` | `true` | Use `$5` as change |
| `{5, 20}` | `false` | One `$5` is insufficient |
| `{5, 5, 5, 20}` | `true` | Give three `$5` bills |
| `{5, 5, 10, 20}` | `false` | No `$5` remains for the `$20` customer |
| `{5, 5, 5, 10, 20}` | `true` | Give `$10 + $5` |
| `{5, 5, 10, 10, 20}` | `false` | Two `$10` bills cannot make `$15` |
| `{5, 5, 5, 5, 10, 20, 10}` | `true` | Correct greedy choice preserves `$5` bills |

The most important case to understand is:

```cpp
bills = {5, 5, 5, 5, 10, 20, 10};
```

Why?

Because it demonstrates that the greedy priority for `$20` matters.

If you give three `$5` bills when the `$20` customer arrives, the last customer cannot receive change.

If you give `$10 + $5`, every transaction succeeds.

---

# Three Important Rules to Remember

### Rule 1: A `$5` bill requires no change

```text
Customer pays $5
         |
         v
   No change needed
         |
         v
      Save $5
         |
         v
       five++
```

---

### Rule 2: A `$10` bill requires exactly one `$5`

```text
Customer pays $10
         |
         v
    Need $5 change
         |
         v
   Is five >= 1?
         |
    ┌────┴────┐
    |         |
   YES        NO
    |         |
    v         v
  five--   return false
    |
    v
  ten++
    |
    v
 Continue
```

---

### Rule 3: A `$20` bill requires `$15` in change

```text
Customer pays $20
         |
         v
    Need $15 change
         |
         v
 Have $10 AND $5?
         |
    ┌────┴────┐
    |         |
   YES        NO
    |         |
    v         v
Give $10+$5  Have 3 $5 bills?
    |         |
    |    ┌────┴────┐
    |    |         |
    |   YES        NO
    |    |         |
    |    v         v
    | Give 3×$5  return false
    |    |
    └─┬──┘
      |
      v
   Continue
```

The third rule contains the main greedy decision.

---

# One Final Mental Trick

Whenever you encounter a `$20` bill, think:

> I need to return `$15`. How can I do that while preserving as many `$5` bills as possible?

There are two choices:

```text
                   $20
                    |
                    v
             Need $15 change
                    |
          ┌─────────┴─────────┐
          |                   |
          v                   v
       $10 + $5           $5 + $5 + $5
          |                   |
          v                   v
     Uses one $5         Uses three $5
          |                   |
          v                   v
     Preserves two        Consumes two
     additional $5        additional $5
          |                   |
          v                   v
       PREFERRED             FALLBACK
          |                   |
          └─────────┬─────────┘
                    |
                    v
            Continue processing
               customers
```

That's the entire greedy insight.

### Final mental model

```text
                Given bills[]
                      |
                      v
               Initialize:
                  five = 0
                  ten = 0
                      |
                      v
              Process next bill
                      |
                      v
               Which bill?
                      |
          ┌───────────┼───────────┐
          |           |           |
          v           v           v
         $5          $10         $20
          |           |           |
          v           v           v
       five++     Need $5     Need $15
                      |           |
                      v           v
                 Have $5?     Have $10+$5?
                      |           |
                 ┌────┴───┐  ┌────┴────┐
                 |        |  |         |
                YES       NO YES       NO
                 |        |  |         |
                 v        v  v         v
              five--    FAIL Give    Have 3
              ten++          $10+$5   $5 bills?
                                |         |
                                |    ┌────┴────┐
                                |    |         |
                                |   YES        NO
                                |    |         |
                                |    v         v
                                |  Give       FAIL
                                |  3×$5
                                |    |
                    ┌───────────┘    |
                    |                |
                    └────────┬───────┘
                             |
                             v
                       Update counts
                             |
                             v
                       Next customer
                             |
                             v
                   All customers served?
                             |
                             v
                        return true
```

---

# Final Takeaway

**LeetCode 860. Lemonade Change is a classic example of Greedy + Simulation with constant-space resource tracking.**

The problem looks like it might require exploring multiple combinations of bills, but the available denominations allow us to establish a safe greedy priority.

The crucial observations are:

1. **Customer order must be preserved.** You cannot use money from future transactions.
2. **Only `$5` and `$10` bills need to be tracked.** `$20` bills cannot be used to provide change.
3. **When receiving `$10`, you must give one `$5`.** There is no alternative.
4. **When receiving `$20`, prefer `$10 + $5` over three `$5` bills.** This preserves smaller bills for future customers.
5. **Return `false` immediately when exact change cannot be provided.** Otherwise, return `true` after processing every customer.

The essential greedy rule is:

\[
\boxed{\text{For \$15 change, prefer \$10+\$5 over \$5+\$5+\$5}}
\]

because this choice preserves two additional `$5` bills.

**Time Complexity:**

\[
\boxed{O(n)}
\]

**Space Complexity:**

\[
\boxed{O(1)}
\]

**The most important conceptual skill is recognizing that, when multiple ways of satisfying the current requirement exist, a greedy solution may be possible if one choice preserves more useful resources for the future.**

Once you understand why `$10 + $5` is always the preferred way to return `$15`, the remaining implementation is a straightforward left-to-right simulation using two counters.
*/
class Solution {
public:
    bool lemonadeChange(vector<int>& bills) {
        int five = 0;
        int ten = 0;

        for(int bill : bills){
            if(bill == 5){
                five++;
            }else if(bill == 10){
                if(five >= 1){
                    five--;
                    ten++;
                }else{
                    return false;
                }
            }else{
                if(ten >= 1 && five >= 1){
                    ten--;
                    five--;
                }else if(five >= 3){
                    five -= 3;
                }else{
                    return false;
                }
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna