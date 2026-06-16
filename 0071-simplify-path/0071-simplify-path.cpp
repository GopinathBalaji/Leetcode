// Method 1: Stack approach 
/*
## Hint 1: Understand Unix-style paths

You are given an absolute path like:

```text
/home//foo/
```

You need to return its simplified canonical form:

```text
/home/foo
```

Important rules:

```text
"."  means current directory, ignore it
".." means go to parent directory
"//" means same as "/", ignore extra slashes
```

---

## Hint 2: Split the path by `/`

A path is made of directory names separated by `/`.

Example:

```text
"/home/user/Documents/../Pictures"
```

Splitting by `/` gives pieces like:

```text
"", "home", "user", "Documents", "..", "Pictures"
```

Some pieces are empty because of leading `/` or repeated slashes.

You only care about meaningful parts.

---

## Hint 3: Use a stack

Use a stack/vector to store the valid directory names.

For each part:

```text
normal directory name -> push it
"." or ""             -> ignore it
".."                  -> pop one directory if possible
```

The stack represents the current simplified path.

---

## Hint 4: Be careful with `".."`

If you see `".."`, go one level up.

Example:

```text
/a/b/..
```

becomes:

```text
/a
```

So you pop `"b"` from the stack.

But if the stack is already empty, do nothing.

Example:

```text
/../../
```

still becomes:

```text
/
```

You cannot go above root.

---

## Hint 5: Directory names can contain dots

Only these exact strings are special:

```text
"."
".."
```

But names like these are normal directory names:

```text
"..."
"..hidden"
"a.b"
```

So do not treat every string containing dots as special.

---

## Hint 6: Build the answer at the end

After processing all parts, suppose your stack contains:

```text
["home", "user", "Pictures"]
```

The simplified path should be:

```text
/home/user/Pictures
```

So join the stack with `/` and add a leading `/`.

If the stack is empty, return:

```text
/
```

---

## Core logic

```cpp
for each part between slashes:
    if part == "" or part == ".":
        ignore
    else if part == "..":
        if stack not empty:
            pop
    else:
        push part
```

This gives an `O(n)` solution, where `n` is the length of the path.
*/
class Solution {
public:
    string simplifyPath(string path) {
        int n = path.size();

        vector<string> stack;

        stringstream ss(path);
        string part;

        while(getline(ss, part, '/')) {
            if(part.empty()){
                continue;
            }

            if(part == "."){
                continue;
            }else if(part == ".."){
                if(!stack.empty()){
                    stack.pop_back();
                }
            }else{
                stack.push_back(part);
            }
        }

        string ans = "";
        for(string dir : stack){
            ans += "/" + dir;
        }

        return ans.empty() ? "/" : ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna