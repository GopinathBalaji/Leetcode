// Method 1: Stack to hold result and sign
/*
### State you maintain

* `res` — running result of the *current* parenthesis level (start at `0`)
* `sign` — sign to apply to the *next* number/subexpression (`+1` or `-1`, start at `+1`)
* `num` — the number you’re currently parsing from digits (start at `0`)
* `stack` — stores pairs of `(prevRes, prevSign)` when you enter parentheses

### One left-to-right pass

For each character `c` in the string `s`:

1. **Digit (`'0'..'9'`)**

   * Build the whole number: `num = num * 10 + (c - '0')`.

2. **Plus or minus (`'+'` or `'-'`)**

   * Fold the number you just finished: `res = res + sign * num`.
   * Reset `num = 0`.
   * Set `sign = +1` for `'+'`, `sign = -1` for `'-'`.

3. **Opening parenthesis (`'('`)**

   * You are starting a new subexpression. Save your context:

     * Push `res` onto the stack.
     * Push `sign` onto the stack.
   * Reset for the subexpression: `res = 0`, `sign = +1`, `num = 0`.

4. **Closing parenthesis (`')'`)**

   * First, fold any in-progress number: `res = res + sign * num`; set `num = 0`.
   * Pop context in reverse order:

     * `prevSign = stack.pop()`
     * `prevRes  = stack.pop()`
   * Combine: the whole parenthesized value is `res`; apply the sign that preceded it and add back to the previous level:

     * `res = prevRes + prevSign * res`.

5. **Space**

   * Ignore and continue.

After the loop ends, if a number is still in progress, **fold it** once more:
`res = res + sign * num`.

Return `res`.

---

### Why this works

* `res` always holds the value of the current level.
* When you see `'('`, you **save** the outer level (`prevRes`) and the sign that should apply to the whole parenthesized block (`prevSign`), then start fresh.
* When you see `')'`, you finish the inner level and fold it into the saved outer level with the saved sign: `prevRes + prevSign * innerRes`.
* Leading unary minus and cases like `-(...)` are handled naturally: the `'-'` sets `sign = -1` before `'('`, which you push and later apply to the inner `res`.

**Complexity:** One pass, each char processed O(1) → **O(n)** time; stack depth ≤ nesting level → **O(n)** space worst case.


# In short:
Push order on '(': first res, then sign.
Pop order on ')': first prevSign, then prevRes.
Handles unary minus and nested parentheses naturally.
*/

class Solution {
    public int calculate(String s) {
        int res = 0;          // running result for current level
        int sign = 1;         // sign to apply to next number/subexpression
        int num = 0;          // current number being built
        Deque<Integer> st = new ArrayDeque<>(); // stack of [prevRes, prevSign] pairs

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');           // build multi-digit number
            } else if (c == '+' || c == '-') {
                res += sign * num;                    // fold completed number
                num = 0;
                sign = (c == '+') ? 1 : -1;           // set sign for next number
            } else if (c == '(') {
                // save outer context, start fresh for inner expression
                st.push(res);
                st.push(sign);
                res = 0;
                sign = 1;
                num = 0;
            } else if (c == ')') {
                res += sign * num;                    // finish inner number (if any)
                num = 0;
                int prevSign = st.pop();              // the sign before '('
                int prevRes  = st.pop();              // the result before '('
                res = prevRes + prevSign * res;       // apply and combine
            } else {
                // space or other ignorable char
            }
        }

        res += sign * num; // fold any trailing number
        return res;
    }
}



// Method 2: Sign-only stack
/*
Awesome — here’s a deep dive into the **sign-only stack** method for LeetCode 224 (Basic Calculator): a crisp mental model, correct Java code, and step-by-step walkthroughs (including a unary minus case).

---

# How the “sign-only stack” works

### Idea in one line

Carry a running sum `res`. Every number you read is multiplied by an **effective sign** that comes from:

* the current **local sign** you just saw (`+` or `-`), and
* the **parentheses context** (the product of signs outside), kept on a stack.

### State you keep

* `res` – running result
* `num` – number being built from digits
* `sign` – the local sign for the **next** number: `+1` or `-1` (starts `+1`)
* `stack` – a stack of **context signs**; `stack.peek()` is the sign contributed by surrounding parentheses
  Initialize it with `1`.

### Rules while scanning `s` left→right

* **Digit**: `num = num*10 + digit`
* **'+' or '-'**:
  fold the finished number → `res += (stack.peek() * sign * num)`; reset `num = 0`; set `sign = +1` (for '+') or `-1` (for '-')
* **'('**:
  push the **effective sign** for the inner expression: `stack.push(stack.peek() * sign)`; then reset `sign = +1` and `num = 0`
* **')'**:
  fold any pending number: `res += stack.peek() * sign * num`; reset `num = 0`; **pop** the sign context
* **Space**: ignore
* **End of string**: fold any trailing number similarly

Why it’s correct: `stack.peek()` captures how many negations (or not) are wrapping you. Putting `sign*stack.peek()` on `(` and popping on `)` makes `-(...)` (and nested cases) automatic.

### Complexity

* Each character is processed once ⇒ **O(n)** time.
* Stack depth ≤ parentheses nesting ⇒ **O(n)** space worst-case (typically much less).

---

# Walkthrough 1: `s = "1-(3-4)+5"` ⇒ expected `7`

We track: `i, c, num, sign, stackTop, action, res`.

Start: `res=0, num=0, sign=+1, stack=[1]`.

1. `c='1'` → digit → `num=1`
2. `c='-'` → fold: `res += 1*+1*1 = 1` → `res=1`, `num=0`, `sign=-1`
3. `c='('` → push context: `stack.push(1 * -1) = -1` → `stackTop=-1`, reset `sign=+1`, `num=0`
4. `c='3'` → `num=3`
5. `c='-'` → fold: `res += (-1)*+1*3 = -3` → `res=-2`, `num=0`, `sign=-1`
6. `c='4'` → `num=4`
7. `c=')'` → fold: `res += (-1)*(-1)*4 = +4` → `res=2`; pop context → `stackTop=1`, reset `sign=+1`, `num=0`
8. `c='+'` → fold: `res += 1*+1*0 = 0` (no-op), `sign=+1`
9. `c='5'` → `num=5`
10. end → fold: `res += 1*+1*5 = 5` → `res=7`

**Answer = 7**.

Key moments:

* On `'('` after `'-'`, we pushed `-1`, making inner numbers counted negatively by default.
* Inner `3-4` got multiplied by `-1`, which produced `-(3-4) = +1`, then plus 1 (outer) and plus 5 → 7.

---

# Walkthrough 2 (unary minus): `s = "-(2+3)"` ⇒ expected `-5`

Start: `res=0, num=0, sign=+1, stack=[1]`.

1. `'-'` → fold 0 (no-op), `sign=-1`
2. `'('` → push `1 * -1 = -1`, reset `sign=+1`, `num=0`
3. `'2'` → `num=2`
4. `'+'` → fold: `res += (-1)*+1*2 = -2` → `res=-2`, `num=0`, `sign=+1`
5. `'3'` → `num=3`
6. `')'` → fold: `res += (-1)*+1*3 = -3` → `res=-5`; pop; reset `sign=+1`, `num=0`
7. end → fold trailing 0 (no-op)

**Answer = -5**.

The unary minus simply set `sign=-1` before `(`; pushing `-1` onto the stack made everything inside the parentheses negated.

---

# Common pitfalls this avoids

* You never need to store partial `res` per parenthesis layer — just signs.
* Unary minus is handled naturally by the `sign` before `(`.
* Multi-digit numbers work because you build `num` until a non-digit appears, then fold once.


####

```java
res += stack.peek() * sign * num;
```

is the whole reason the **sign-only stack** trick works. Here’s the intuition, the invariant, and a couple of concrete expansions so you can *feel* why we multiply by the top of the stack.

---

## Intuition: “distribute the outside sign to everything inside”

If you have an expression like

```
… + ( + 2 + 3 - 4 )
… - ( + 2 + 3 - 4 )
```

the `+` before the parentheses means “keep signs inside as they are,” but the `-` means “flip *every* inner sign.” Algebraically:

```
X + ( 2 + 3 - 4 )  =  X + 2 + 3 - 4
X - ( 2 + 3 - 4 )  =  X - 2 - 3 + 4
```

So the sign that applies to a number inside parentheses is not just the local `+`/`-` immediately before that number; it’s the **product** of:

* the local sign (`+1` or `-1`), and
* the **accumulated sign context** from all open parentheses outside.

We keep that accumulated context on a **stack**; its **top** is exactly “the sign that all numbers *currently inside* should be multiplied by.” Hence: `stack.peek() * sign * num`.

---

## The invariant (what the stack represents)

* `stack.peek()` is the **effective sign** contributed by *all surrounding open parentheses* at your current position. (`+1` if you’re in a `+(...)` context, `-1` if you’re inside `-(...)`, or more generally the product if nested.)
* `sign` is the **local** `+1`/`-1` you set when you read a `+` or `-` just before the next number (or `(`).
* When you finish reading a number `num`, its contribution to the total is
  **`effective_sign * local_sign * num` = `stack.peek() * sign * num`**.

### What happens at a new `(`?

We push a new context:

```
stack.push( stack.peek() * sign );
sign = +1;        // reset for inside
num  = 0;
```

That push captures the idea “everything inside this pair of parentheses is multiplied by the sign just seen before it.” When we hit `)`, we fold the pending number (if any) and `pop()` the context.

This is *exactly* the distributive rule:

```
prevRes + prevSign * ( innerExpression )
```

but instead of waiting until `)`, we “pay as we go” by multiplying each inner term by `prevSign` immediately.

---

## Tiny algebra check (distribution)

Take: `1 - (2 + (3 - 4))`

Expected expansion: `1 - 2 - 3 + 4`.

* Outside context: `stack.peek() = +1`.
* After `'-'` then `'('`, we push `(+1 * -1) = -1` → inside context is `-1`.

Now each inner term’s effective sign is `-1 * (its local sign)`:

* `2` has local `+` → contribution `-1 * (+1) * 2 = -2`
* Then we see `+ ( 3 - 4 )` under the same `-1`:

  * `3` with local `+` → `-1 * (+1) * 3 = -3`
  * `4` with local `-` → `-1 * (-1) * 4 = +4`

Summing with the leading `1` gives `1 - 2 - 3 + 4`, exactly right.

---

## Step-by-step walk (sign changes visible)

Example: `s = "1-(3-4)+5"`

We’ll track `(stackTop, sign, num, res)` and only show folds:

1. Read `1`, then `-` ⇒ fold: `res +=  (+1) * (+1) * 1 = +1` → `res=1`. Set `sign=-1`.
2. Read `(` ⇒ push `(+1 * -1) = -1`; reset `sign=+1`.
3. Read `3`, then `-` ⇒ fold: `res +=  (-1) * (+1) * 3 = -3` → `res=-2`. Set `sign=-1`.
4. Read `4`, then `)` ⇒ fold: `res +=  (-1) * (-1) * 4 = +4` → `res=2`. Pop to context `+1`, reset `sign=+1`.
5. Read `+`, then `5`, end ⇒ fold: `res += (+1) * (+1) * 5 = +5` → `res=7`.

Every fold is `stack.peek() * sign * num`. The stack’s top flips the whole inner region when the outer operator is `-`.

---

## Why not just use `sign * num`?

Because `sign` is only the **local** `+/-` you most recently read. It doesn’t “remember” that you’re inside a `-(...)` (or `-(-(...))`) where the parent context flips everything. The **stack top** *is* that memory. Multiplying by it applies the correct flip(s) from any nesting depth.

Equivalently: this method is doing the same math as the classic “result/sign stack” approach where you compute `res = prevRes + prevSign * innerRes` at each `)`. The sign-only stack just distributes `prevSign` **term by term**, so you never need to store `innerRes` separately.

---

## One more nested example (to see multiple flips)

`"-(2+3-(4-5))"` → expected `-2 - 3 + 4 - 5 = -6`.

* Push on `(` after leading `-` → context `-1`.

  * `2` with local `+` → `-1 * +1 * 2 = -2`
  * `3` with local `+` → `-1 * +1 * 3 = -3`
  * Next `- ( ... )` under context `-1`:

    * Push `(-1 * -1) = +1` (double negative flips back)

      * `4` with local `+` → `+1 * +1 * 4 = +4`
      * `5` with local `-` → `+1 * -1 * 5 = -5`
    * Pop back to `-1` context.

Sum: `-2 - 3 + 4 - 5 = -6`. All we did was multiply by the top-of-stack sign each time.

---

### TL;DR

* `stack.peek()` = product of all outer `+`/`-` contexts (from open parentheses).
* `sign` = local `+/-` for the next number.
* Effective sign for the number = **`stack.peek() * sign`**.
* Therefore we fold with `res += stack.peek() * sign * num`.

That multiplication is exactly what propagates a `-` in front of parentheses to every term inside, including nested cases.

*/

// class Solution {
//     public int calculate(String s) {
//         int res = 0;
//         int num = 0;
//         int sign = 1;                 // local sign for the next number
//         Deque<Integer> st = new ArrayDeque<>();
//         st.push(1);                   // outermost context sign

//         for (int i = 0; i < s.length(); i++) {
//             char c = s.charAt(i);

//             if (Character.isDigit(c)) {
//                 num = num * 10 + (c - '0');                 // build number
//             } else if (c == '+' || c == '-') {
//                 res += st.peek() * sign * num;              // fold number
//                 num = 0;
//                 sign = (c == '+') ? 1 : -1;                 // set local sign
//             } else if (c == '(') {
//                 st.push(st.peek() * sign);                  // new context
//                 sign = 1;                                   // reset local sign
//                 num = 0;
//             } else if (c == ')') {
//                 res += st.peek() * sign * num;              // close inner expr
//                 num = 0;
//                 st.pop();                                   // leave context
//                 sign = 1;                                   // default after ')'
//             } else {
//                 // space or ignorable
//             }
//         }

//         // fold any trailing number
//         res += st.peek() * sign * num;
//         return res;
//     }
// }
