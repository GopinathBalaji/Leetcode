// Single Left-to-Right Pass without Regex
/*
Rules:

* Skip `""` and `"."` tokens.
* On `".."` → pop one from stack if available.
* Otherwise push the token.
* Result is `"/" + join(stack, "/")`; if stack empty, return `"/"`.

*(If you prefer splitting: iterate `for (String tok : path.split("/+"))` and apply the same rules.)*

### Thorough walkthrough

Example: `"/a/./b/../../c/"`

Tokens in order: `a`, `.`, `b`, `..`, `..`, `c`.

* Start `stack = []`
* `a` → push → `["a"]`
* `.` → no-op → `["a"]`
* `b` → push → `["a","b"]`
* `..` → pop → `["a"]`
* `..` → pop → `[]`
* `c` → push → `["c"]`

Build: `"/c"`.

Edge checks:

* `"/../"` → tokens: `..` → pop nothing → stack `[]` → `"/"`.
* `"/home//foo/"` → tokens: `home`, `foo` → `["home","foo"]` → `"/home/foo"`.
* `"/.../a/../"` → tokens: `...` (normal dir) → push, then `a` push, then `..` pop `a` → result `"/..."`.

**Complexity:** O(n) time, O(n) space (worst case all tokens kept).
*/
class Solution {
    public String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        int n = path.length();
        int i = 0;

        while (i < n) {
            // skip consecutive slashes
            while (i < n && path.charAt(i) == '/') i++;
            if (i >= n) break;

            // read the next token until the next slash
            int j = i;
            while (j < n && path.charAt(j) != '/') j++;
            String token = path.substring(i, j);

            if (token.equals("..")) {
                if (!stack.isEmpty()) stack.removeLast();
            } else if (!token.equals(".") && !token.isEmpty()) {
                stack.addLast(token);
            }

            i = j; // continue after this token
        }

        if (stack.isEmpty()) return "/";
        StringBuilder sb = new StringBuilder();
        for (String dir : stack) sb.append('/').append(dir);
        return sb.toString();
    }
}