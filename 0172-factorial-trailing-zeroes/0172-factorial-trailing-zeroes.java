// Answer by factorization by 5. (Recursive answer does not work)
/*
## ✅ What are "trailing zeroes"?

Trailing zeroes in a number are the `0`s at the end of the number.
For example:

* `5! = 120` → 1 trailing zero
* `10! = 3628800` → 2 trailing zeroes
* `25! = 15511210043330985984000000` → 6 trailing zeroes

Trailing zeroes are caused by **multiplying by 10**, and:

```
10 = 2 × 5
```

Every time the factorial multiplies a pair of `(2 × 5)`, we get a trailing zero.

---

## ✅ Why only count 5s?

In `n!`, we multiply all numbers from `1` to `n`.

Every even number gives us a **factor of 2** — so there are **plenty** of 2s.

But **5s** are rarer than 2s. So every time we find a 5 in the factorization, and we have a 2 (which we always do), we form a trailing zero.

So:
➡️ **Number of trailing zeroes = Number of 5s in the prime factorization of n!**

---

## ✅ Counting the number of 5s in n!

You might think we just do:
`n / 5`

But there’s a catch!

Some numbers contribute **more than one 5**:

* `25 = 5 × 5` → contributes **2** 5s
* `125 = 5 × 5 × 5` → contributes **3** 5s

So we do:

```java
count += n / 5;
count += n / 25;
count += n / 125;
...
```

In code:

```java
while (n > 0) {
    n = n / 5;
    count += n;
}
```

This keeps dividing `n` by `5`, `25`, `125`, ... until `n` becomes 0.

---

### ✅ Example: `n = 100`

```
n / 5 = 100 / 5 = 20        → counts 5, 10, 15, ..., 100 (1 five each)
n / 25 = 100 / 25 = 4       → counts 25, 50, 75, 100 (each has 2 fives)
n / 125 = 100 / 125 = 0     → stop

→ Total trailing zeroes = 20 + 4 = 24
```

So `100!` has **24 trailing zeroes**.

---

## ✅ Time Complexity

* Runs in **O(log₅n)** time, since we keep dividing `n` by 5.
* Much faster than computing factorial, which is **O(n)** and can overflow quickly.

---

## ✅ Space Complexity

* Constant space: **O(1)**.

---

## ✅ Summary

| Insight                                | Why It Matters                             |
| -------------------------------------- | ------------------------------------------ |
| Trailing zeroes come from `10 = 2 × 5` | But 2s are abundant, 5s are the bottleneck |
| Count how many 5s are in `n!`          | Count how often 5, 25, 125... divide `n`   |
| Use integer division to avoid overflow | Never compute `n!` directly                |

---

Let me know if you'd like to see the same logic implemented **recursively** or in a different language.
*/
class Solution {
    public int trailingZeroes(int n) {
        int count = 0;

        while(n > 0){
            n = n / 5;
            count += n;
        }

        return count;
    }
}