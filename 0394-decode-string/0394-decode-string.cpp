// Method 1: Two Stack approach
/*
## Hint 1: Understand the pattern

The encoded format is:

```text
k[encoded_string]
```

This means repeat `encoded_string` exactly `k` times.

Example:

```text
3[a] -> aaa
2[ab] -> abab
3[a2[c]] -> accaccacc
```

The tricky part is that brackets can be **nested**.

---

## Hint 2: This is a stack problem

When you see an opening bracket `'['`, you are starting a new nested string.

You need to remember:

```text
1. the string built before this bracket
2. the repeat count before this bracket
```

So use two stacks:

```cpp
stack<string> strStack;
stack<int> numStack;
```

Or one stack of pairs.

---

## Hint 3: Build numbers carefully

The repeat count can have multiple digits.

Example:

```text
12[a] -> aaaaaaaaaaaa
```

So when you see a digit:

```cpp
num = num * 10 + (s[i] - '0');
```

Do not assume the repeat count is only one digit.

---

## Hint 4: Maintain a current string

Use:

```cpp
string curr = "";
int num = 0;
```

As you scan from left to right:

```text
letter -> add to curr
digit  -> update num
'['    -> save current context
']'    -> complete current block
```

---

## Hint 5: What to do when you see `'['`

Suppose you are parsing:

```text
abc3[de]
```

Before `'['`, you have:

```text
curr = "abc"
num = 3
```

At `'['`, save both:

```cpp
strStack.push(curr);
numStack.push(num);
```

Then reset:

```cpp
curr = "";
num = 0;
```

Now `curr` will build the inside of the bracket.

---

## Hint 6: What to do when you see `']'`

When you hit `']'`, the current bracket content is complete.

Example:

```text
3[ab]
```

At `']'`:

```text
curr = "ab"
repeat count = 3
previous string = ""
```

So pop from the stacks:

```cpp
int repeat = numStack.top();
numStack.pop();

string prev = strStack.top();
strStack.pop();
```

Then build:

```cpp
curr = prev + curr repeated repeat times
```

---

## Hint 7: Nested example

For:

```text
3[a2[c]]
```

Scan:

```text
3[        save repeat=3, prev=""
a         curr="a"
2[        save repeat=2, prev="a"
c         curr="c"
]         curr = "a" + "cc" = "acc"
]         curr = "" + "accaccacc"
```

Final answer:

```text
accaccacc
```

---

## Core logic

```cpp
for each char c in s:
    if digit:
        num = num * 10 + digit
    else if c == '[':
        push curr
        push num
        curr = ""
        num = 0
    else if c == ']':
        repeat = top number
        prev = top string
        curr = prev + curr repeated repeat times
    else:
        curr += c
```

At the end, `curr` is the decoded string.

## Complexity

```text
Time: O(length of decoded string)
Space: O(length of decoded string + nesting depth)
```
*/
class Solution {
public:
    string decodeString(string s) {
        int n = s.size();

        stack<string> strStack;
        stack<int> numStack;

        string curr = "";
        int num = 0;

        for(int i=0; i<n; i++){
            if(std::isdigit(s[i])){
                num = num * 10 + (s[i] - '0');
            }else if(s[i] == '['){
                strStack.push(curr);
                numStack.push(num);

                curr = "";
                num = 0;
            }else if(s[i] == ']'){
                int repeat = numStack.top();
                numStack.pop();

                string prev = strStack.top();
                strStack.pop();

                string temp = curr;
                string repeated = "";
                for(int j=0; j<repeat; j++){
                    repeated += temp;
                }

                curr = prev + repeated;
            }else{
                curr += s[i];
            }
        }

        return curr;
    }
};





// Method 2: Recursion Approach
/*
Use recursion because every bracketed expression is naturally a **subproblem**.

For example:

```text
3[a2[c]]
```

When recursion enters the first `[`, it decodes:

```text
a2[c]
```

Inside that, it enters another `[`, decodes:

```text
c
```

Then returns back upward.

## Core idea

Write a helper function:

```cpp
string decode(int& i, string& s)
```

`i` is passed by reference so recursive calls can advance the same index.

The helper decodes characters until it reaches:

```cpp
']'
```

Then it returns the decoded string for that bracket level.


## Explanation

Take:

```text
s = "3[a2[c]]"
```

Start with:

```cpp
decode(s, i)
```

At the outer level:

```text
num = 3
```

Then we see `'['`, so we recursively decode the inside:

```text
a2[c]
```

Inside that recursive call:

```text
curr = "a"
num = 2
```

Then we see another `'['`, so recurse again and decode:

```text
c
```

That returns:

```text
"c"
```

Then repeat it `2` times:

```text
"cc"
```

So the middle recursive call becomes:

```text
"a" + "cc" = "acc"
```

Then it returns `"acc"` to the outer call.

The outer call repeats it `3` times:

```text
"accaccacc"
```

So the final answer is:

```text
"accaccacc"
```

## Why `i` is passed by reference

If each recursive call had its own independent index, the outer function would not know how far the inner function parsed.

By passing `i` by reference:

```cpp
string decode(string& s, int& i)
```

when the inner call finishes at `]`, the outer call continues from the correct next character.

## Complexity

Let `N` be the length of the final decoded string.

```text
Time: O(N)
Space: O(N + recursion depth)
```

The output itself can be much larger than the input, so the runtime depends on the decoded output size.
*/
// class Solution {
// public:
//     string decodeString(string s) {
//         int i = 0;
//         return decode(s, i);
//     }

// private:
//     string decode(string& s, int& i) {
//         string curr = "";
//         int num = 0;

//         while (i < s.size()) {
//             char c = s[i];

//             if (isdigit(c)) {
//                 num = num * 10 + (c - '0');
//                 i++;
//             }
//             else if (c == '[') {
//                 i++; // skip '['

//                 string inside = decode(s, i);

//                 // repeat decoded inside string num times
//                 for (int j = 0; j < num; j++) {
//                     curr += inside;
//                 }

//                 num = 0;
//             }
//             else if (c == ']') {
//                 i++; // skip ']'
//                 return curr;
//             }
//             else {
//                 curr += c;
//                 i++;
//             }
//         }

//         return curr;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna