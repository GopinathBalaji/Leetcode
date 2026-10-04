// Method 1: 1D DP
/*
This is a **Dynamic Programming** problem.

The key idea is that at each position, you can decode either:

```text
one digit
```

or:

```text
two digits
```

but only if that chunk represents a valid letter from:

```text
1 → A
...
26 → Z
```

So the number of ways to decode the string depends on the previous one or two DP states.

### Hint 1: Define your DP state

Let:

```cpp
dp[i]
```

mean:

```text
the number of ways to decode the first i characters
```

So:

```cpp
dp[0]
```

means decoding an empty prefix, and:

```cpp
dp[n]
```

is your final answer.

---

### Hint 2: Why should `dp[0] = 1`?

This feels strange at first, but it makes the recurrence work.

Think of:

```text
"12"
```

When processing the first character `"1"`, there is exactly one way to decode everything before it:

```text
empty prefix
```

So:

```cpp
dp[0] = 1;
```

This acts as the starting point for building valid decodings.

---

### Hint 3: First possibility — decode one digit

Suppose you're calculating:

```cpp
dp[i]
```

Look at the current character:

```cpp
s[i - 1]
```

If it is not `'0'`, then it can be decoded by itself.

For example:

```text
'1' → A
'7' → G
```

So you can carry over all ways from:

```cpp
dp[i - 1]
```

Conceptually:

```cpp
if (s[i - 1] != '0') {
    dp[i] += dp[i - 1];
}
```

---

### Hint 4: Zero cannot be decoded by itself

This is the biggest edge case.

There is no mapping for:

```text
0
```

So:

```text
"0"
```

is invalid.

And something like:

```text
"06"
```

is also invalid.

But zero can be part of:

```text
"10"
"20"
```

because those represent valid numbers between `1` and `26`.

---

### Hint 5: Second possibility — decode two digits

Now look at:

```cpp
s[i - 2]
s[i - 1]
```

as one two-digit number.

For example:

```text
"12" → 12 → L
"26" → 26 → Z
```

If the number is between:

```text
10 and 26
```

then those two characters can be decoded together.

So you can add:

```cpp
dp[i - 2]
```

to the answer.

Conceptually:

```cpp
if (twoDigit >= 10 && twoDigit <= 26) {
    dp[i] += dp[i - 2];
}
```

---

### Hint 6: Build the recurrence

For every position `i`, there are two possible contributions:

```text
use one digit
→ dp[i - 1]

use two digits
→ dp[i - 2]
```

but only when each choice is valid.

So mentally:

```text
dp[i]
=
ways using current digit alone
+
ways using current two digits together
```

---

### Hint 7: Example with `"226"`

Let's work through it.

Start:

```text
dp[0] = 1
```

For `"2"`:

```text
2 is valid alone

dp[1] = dp[0] = 1
```

For `"22"`:

You can decode the second `2` alone:

```text
2 | 2
```

and `"22"` together:

```text
22
```

So:

```text
dp[2] = dp[1] + dp[0]
      = 1 + 1
      = 2
```

For `"226"`:

The `6` can be decoded alone:

```text
22 | 6
```

and `"26"` can be decoded together:

```text
2 | 26
```

So:

```text
dp[3] = dp[2] + dp[1]
      = 2 + 1
      = 3
```

The three decodings are:

```text
2 | 2 | 6
22 | 6
2 | 26
```

---

### Hint 8: Be careful with `"27"`

You might think every pair of digits can be grouped.

But:

```text
27
```

is greater than `26`.

So for:

```text
"27"
```

you can only do:

```text
2 | 7
```

not:

```text
27
```

That means the two-digit contribution should only happen when:

```cpp
10 <= value && value <= 26
```

---

### Hint 9: You don't need `stoi`

You can convert two characters directly:

```cpp
int twoDigit =
    (s[i - 2] - '0') * 10 +
    (s[i - 1] - '0');
```

Then check:

```cpp
if (twoDigit >= 10 && twoDigit <= 26) {
    // valid pair
}
```

---

### Hint 10: A useful edge case — `"2101"`

This is a good string for testing your logic.

Notice:

```text
"0"
```

cannot stand alone.

So when you reach the zero, the only valid choice is:

```text
"10"
```

Likewise, you must make sure you're not accidentally allowing things like:

```text
"01"
```

The recurrence naturally handles this if you use:

```cpp
s[i - 1] != '0'
```

for one-digit decoding and:

```cpp
10 <= twoDigit && twoDigit <= 26
```

for two-digit decoding.

---

### Hint 11: You can optimize to O(1) space

Your recurrence only needs:

```cpp
dp[i - 1]
dp[i - 2]
```

So after you understand the array version, you can replace it with:

```cpp
int prev2 = 1;  // dp[i - 2]
int prev1 = ...; // dp[i - 1]
```

Then for each character:

```cpp
int curr = 0;
```

Add the one-digit contribution if valid.

Add the two-digit contribution if valid.

Then shift:

```cpp
prev2 = prev1;
prev1 = curr;
```

---

### Skeleton

```cpp
class Solution {
public:
    int numDecodings(string s) {
        int n = s.size();

        vector<int> dp(n + 1, 0);

        dp[0] = 1;

        // initialize dp[1]
        // based on whether s[0] is valid

        for (int i = 2; i <= n; i++) {

            // Option 1:
            // decode s[i - 1] by itself
            //
            // if valid:
            //     dp[i] += dp[i - 1];

            // Option 2:
            // decode s[i - 2 ... i - 1] together
            //
            // if value is between 10 and 26:
            //     dp[i] += dp[i - 2];
        }

        return dp[n];
    }
};
```

The core recurrence is:

```text
if current digit is 1...9:
    add dp[i - 1]

if previous two digits form 10...26:
    add dp[i - 2]
```

The trickiest part is handling **`0` correctly**. A zero can never be decoded alone; it is only valid when it forms `"10"` or `"20"` with the digit before it.
*/
class Solution {
public:
    int numDecodings(string s) {
        int n = s.size();

        vector<int> dp(n+1, 0);

        dp[0] = 1;

        if(s[0] != '0'){
            dp[1] = 1;
        }       

        for(int i=2; i<=n; i++){
            if(s[i-1] != '0'){
                dp[i] += dp[i-1];
            }

            int twoDigits = ((s[i-2] - '0') * 10) + (s[i-1] - '0');

            if(twoDigits >= 10 && twoDigits <= 26){
                dp[i] += dp[i - 2];
            }
        } 

        return dp[n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna