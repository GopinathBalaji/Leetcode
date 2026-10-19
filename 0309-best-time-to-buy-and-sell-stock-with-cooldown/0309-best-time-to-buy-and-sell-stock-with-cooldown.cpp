// Method 1: 2D DP Compressed to 1D DP
/*
# LeetCode 309. Best Time to Buy and Sell Stock with Cooldown — Hints

This is a Dynamic Programming + State Machine problem.

The key idea is that instead of trying to find the best days to buy and sell directly, you track the maximum profit you can achieve in different states.

The tricky part is the cooldown restriction:

After selling a stock, you cannot buy another stock on the following day.

This makes the problem different from LeetCode 122. Best Time to Buy and Sell Stock II, where you can buy and sell without a cooldown.

Your goal is to maximize profit while respecting this restriction.

### Hint 1: Understand why the cooldown changes the problem

Suppose:

```
prices = {1, 2, 3, 0, 2};
```

Without a cooldown, you might choose:

```
Day 0: Buy at 1
Day 2: Sell at 3

Profit = 3 - 1 = 2

Day 3: Buy at 0
Day 4: Sell at 2

Profit = 2 - 0 = 2

Total profit = 4
```

But this is invalid.

Why?

Because you sold on day `2`, so day `3` must be a cooldown day.

You cannot buy on day `3`.

Instead, consider:

```
Day 0: Buy at 1
Day 1: Sell at 2

Day 2: Cooldown

Day 3: Buy at 0
Day 4: Sell at 2
```

Your profit becomes:

```
First transaction:  2 - 1 = 1
Second transaction: 2 - 0 = 2

Total profit = 3
```

The correct answer is:

```
3
```

Important observation: Sometimes selling earlier is better than waiting for a higher price because it allows another profitable transaction.

This is why a simple greedy approach isn't enough.

### Hint 2: Think about what information you need to remember

In ordinary stock trading problems, you often track whether you're currently holding a stock.

But here, knowing whether you own a stock isn't enough.

Suppose you don't own any stock.

There are two possibilities:

1. You sold your stock today.
2. You have been resting and are allowed to buy.

These situations are different because of the cooldown.

So instead of tracking only two states, try tracking three states.

### Hint 3: Define three DP states

For every day `i`, define:

```
hold[i]
sold[i]
rest[i]
```

Their meanings are:

State 1: `hold[i]`

```
Maximum profit at the end of day i
when you are holding one stock.
```

You could reach this state by buying today or continuing to hold a stock you already owned.

State 2: `sold[i]`

```
Maximum profit at the end of day i
when you sold a stock today.
```

This state is important because tomorrow must be a cooldown day.

State 3: `rest[i]`

```
Maximum profit at the end of day i
when you are not holding a stock
and did not sell today.
```

You are either continuing to rest or have just completed your cooldown day.

Think of the states as:

```
hold → Own a stock

sold → Sold today

rest → No stock, did not sell today
```

Your goal is to find the maximum achievable profit across these states.

### Hint 4: Derive the transition for `hold[i]`

Suppose today is day `i`.

How can you end today holding a stock?

There are two possibilities.

Option 1: You were already holding a stock yesterday.

You do nothing today.

Your profit stays:

```
hold[i - 1]
```

Option 2: You buy a stock today.

If you buy today, you must have been in the `rest` state yesterday.

Why?

Because if you sold yesterday, today would be your cooldown day.

Your profit becomes:

```
rest[i - 1] - prices[i]
```

You subtract the stock price because buying costs money.

Combine both choices:

```
hold[i] = max(
    hold[i - 1],
    rest[i - 1] - prices[i]
);
```

This transition automatically respects the cooldown restriction.

Notice that you cannot buy from:

```
sold[i - 1]
```

because that would mean buying immediately after selling.

### Hint 5: Derive the transition for `sold[i]`

Suppose you sell a stock today.

What must have been true yesterday?

You must have been holding a stock.

So yesterday's state must be:

```
hold[i - 1]
```

Selling earns:

```
prices[i]
```

Therefore:

```
sold[i] = hold[i - 1] + prices[i];
```

Unlike `hold`, there is only one way to enter the `sold` state.

You must sell a stock that you were holding.

You cannot sell if you were already in the `rest` or `sold` state yesterday.

### Hint 6: Derive the transition for `rest[i]`

Suppose you end today without holding a stock, and you didn't sell today.

How could this happen?

Option 1: You were already resting yesterday.

You continue doing nothing.

```
rest[i - 1]
```

Option 2: You sold your stock yesterday.

Today is your cooldown day.

You do nothing.

```
sold[i - 1]
```

Both options leave you in the `rest` state today.

Therefore:

```
rest[i] = max(
    rest[i - 1],
    sold[i - 1]
);
```

This is how the cooldown is represented.

Notice the transition:

```
Sell yesterday
      ↓
Rest today (cooldown)
      ↓
Buy tomorrow (allowed)
```

You are forced to spend at least one day without buying after a sale.

### Hint 7: Visualize the state transitions

Each arrow represents a valid transition from one day's state to the next. Staying in REST or HOLD is also permitted.

The important restriction is:

```
SOLD → HOLD
```

is not allowed on consecutive days.

Instead, you need:

```
SOLD → REST → HOLD
```

That single restriction is the heart of this problem.

### Hint 8: Initialize the base cases

Now think about day `0`.

Suppose:

```
prices = {1, 2, 3, 0, 2};
```

On the first day, what states are possible?

Holding a stock:

You can buy the stock at price `1`.

```
hold[0] = -prices[0];
```

Resting:

You can do nothing.

```
rest[0] = 0;
```

Selling:

Can you sell a stock on day `0`?

No, because you haven't bought one yet.

So this state must be marked as impossible.

Conceptually:

```
sold[0] = -INF;
```

You can represent `-INF` using a sufficiently small negative number in C++.

Thus:

```
hold[0] = -prices[0];
sold[0] = -INF;
rest[0] = 0;
```

These values form the foundation of your DP.

### Hint 9: Walk through an example

Consider:

```
prices = {1, 2, 3, 0, 2};
```

Use the three transitions from the previous hints.

Maximum profit in each state after processing each day

| Day | Price | `hold` | `sold` | `rest` |
| --- | ----- | ------ | ------ | ------ |
| 0   | 1     | -1     | -∞     | 0      |
| 1   | 2     | -1     | 1      | 0      |
| 2   | 3     | -1     | 2      | 1      |
| 3   | 0     | 1      | -1     | 2      |
| 4   | 2     | 1      | 3      | 2      |

Try stepping through it interactively:

Day 0 — Price: 1

1 of 5

HOLD

# -1

SOLD

# −∞

REST

# 0

You can buy at 1 or do nothing. Selling is impossible.

&#x20;PreviousNext day&#x20;

Notice something interesting on day `3`:

```
hold[3] = 1;
```

How can you be holding a stock while having positive profit?

Because you already earned a profit of `1` from an earlier transaction.

You then bought another stock at price `0`.

So your total profit remains `1`.

Remember: DP values represent total profit across all transactions so far, not just the profit from the current stock.

### Hint 10: Determine which state gives the final answer

After processing all the days, you have:

```
hold[n - 1]
sold[n - 1]
rest[n - 1]
```

Which states should you consider?

If you're still holding a stock, you've paid for that stock but haven't sold it.

A completed transaction's profit is realized after selling.

So the optimal answer should come from:

```
sold[n - 1]
```

or:

```
rest[n - 1]
```

Therefore:

```
answer = max(
    sold[n - 1],
    rest[n - 1]
);
```

For our example:

```
sold[4] = 3
rest[4] = 2

Answer = max(3, 2) = 3
```

### Hint 11: Optimize space from O(n) to O(1)

Look carefully at your transitions.

```
hold[i] = max(
    hold[i - 1],
    rest[i - 1] - prices[i]
);

sold[i] = hold[i - 1] + prices[i];

rest[i] = max(
    rest[i - 1],
    sold[i - 1]
);
```

Notice that every state only depends on the previous day.

You don't need to store:

```
vector<int> hold(n);
vector<int> sold(n);
vector<int> rest(n);
```

Instead, you can maintain three variables:

```
int hold;
int sold;
int rest;
```

For every new price, compute:

```
newHold
newSold
newRest
```

Then update the old states.

Be careful about one important detail:

Don't overwrite yesterday's state before you've finished calculating today's states.

For example, if you update `hold` first and then calculate `sold` using the new `hold`, you may accidentally use today's value instead of yesterday's.

Use temporary variables.

### Hint 12: Analyze the complexity

Let:

```
n = prices.size();
```

You process each price exactly once.

For each price, you calculate three states.

Each state transition takes constant time.

Therefore:

```
Time Complexity: O(n)
```

If you use three DP arrays:

```
Space Complexity: O(n)
```

If you optimize to three variables:

```
Space Complexity: O(1)
```

So the optimal approach is:

```
Time:  O(n)
Space: O(1)
```

### Skeleton — O(1) Space DP (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    int maxProfit(vector<int>& prices) {

        // State 1:
        // Maximum profit while holding a stock
        int hold = -prices[0];

        // State 2:
        // Maximum profit when selling today
        int sold = -1000000000;

        // State 3:
        // Maximum profit while resting
        int rest = 0;

        for (int i = 1; i < prices.size(); i++) {

            // Option 1: Keep holding
            // Option 2: Buy today from REST
            int newHold = max(
                 ? ,
                 ? 
            );

            // Sell the stock today
            int newSold =  ? ;

            // Option 1: Keep resting
            // Option 2: Enter cooldown after selling
            int newRest = max(
                 ? ,
                 ? 
            );

            // Update states
            hold = newHold;
            sold = newSold;
            rest = newRest;
        }

        // Return maximum completed profit
        return  ? ;
    }
};
```

### Core recurrence

The three transitions you need to remember are:

```
hold[i] = max(
    hold[i - 1],
    rest[i - 1] - prices[i]
);

sold[i] = hold[i - 1] + prices[i];

rest[i] = max(
    rest[i - 1],
    sold[i - 1]
);
```

The base cases are:

```
hold[0] = -prices[0];
sold[0] = -INF;
rest[0] = 0;
```

And the final answer is:

```
max(sold[n - 1], rest[n - 1])
```

Or mentally:

```
Stock prices
     ↓
Track three states
     ↓
HOLD: Own a stock
SOLD: Sold today
REST: No stock, did not sell today
     ↓
For each day:
     ↓
Update HOLD (keep holding or buy)
Update SOLD (sell existing stock)
Update REST (continue resting or cooldown)
     ↓
Finish processing all days
     ↓
Return max(SOLD, REST)
```

### Connection to previous DP problems

| Problem                                 | DP states                            | Key restriction                |
| --------------------------------------- | ------------------------------------ | ------------------------------ |
| 121. Best Time to Buy and Sell Stock    | Minimum buying price, maximum profit | Only one transaction           |
| 122. Best Time to Buy and Sell Stock II | Hold, not hold                       | Unlimited transactions         |
| 309. Stock with Cooldown                | Hold, sold, rest                     | One-day cooldown after selling |
| 714. Stock with Transaction Fee         | Hold, not hold                       | Pay a fee per transaction      |

The important connection is that all these problems make decisions involving holding or not holding stocks.

What changes is the constraint on how you transition between those states.

For LeetCode 309, the key conceptual insight is:

You need to distinguish between not holding a stock because you just sold it and not holding a stock because you're free to buy again.

Once you separate those situations, the cooldown restriction becomes a natural part of the DP transitions.
*/
class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int hold = -prices[0];

        int sold = -1000000000;

        int rest = 0;

        for(int i=1; i<prices.size(); i++){
            int newHold = std::max(hold, rest - prices[i]);

            int newSold = hold + prices[i];

            int newRest = std::max(rest, sold);

            hold = newHold;
            sold = newSold;
            rest = newRest;
        }

        return std::max({hold, sold, rest});
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna