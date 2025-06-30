// Method 1: An array of StringBuilders to append each row of characters
/*
Detailed Explanation
Handle trivial cases

If numRows == 1, there’s nowhere to zig or zag—you just return the original string.

Similarly, if the string’s length is ≤ numRows, each character would occupy its own row in the first vertical pass, so again the result is the same as the input.

Rows as buckets

We create an array of StringBuilder of size numRows.

Each rows[i] will collect all characters that belong to the ith row in the zigzag pattern.

Simulate the zigzag traversal

Initialize curRow = 0 (start at the top row) and direction = +1 (moving downward).

For each character c in s:

Append it to rows[curRow].

Check for direction change:

If curRow just reached the bottom row (numRows–1), next step must go up, so set direction = –1.

If curRow just reached the top row (0), next step must go down, so set direction = +1.

Advance curRow += direction.

Build the final string

After placing every character in its proper bucket, simply concatenate rows[0], rows[1], …, rows[numRows–1].

That gives the characters in the order required by reading the zigzag pattern row by row.

Complexity
Time: O(n), where n = s.length(). Each character is appended once and then rows are concatenated in O(n) total.

Space: O(n), for the sum of all buckets plus the output string. The number of rows numRows is at most min(n, numRows), so extra space is proportional to the input size.
*/
class Solution {
    public String convert(String s, int numRows) {
        if(numRows == 1 || s.length() <= numRows){
            return s;
        }

        StringBuilder[] rows = new StringBuilder[numRows];
        for(int i=0;i<numRows;i++){
            rows[i] = new StringBuilder();
        }

        int currRow = 0;
        int direction = 1;

        for(char c : s.toCharArray()){
            rows[currRow].append(c);

            if(currRow == numRows - 1){
                direction = -1;
            }else if(currRow == 0){
                direction = +1;
            }

            currRow += direction;
        }

        StringBuilder result = new StringBuilder();
        for(StringBuilder rowSb : rows){
            result.append(rowSb);
        }
        
        return result.toString();
    }
}

// Method 2: Adding characters row by row by using the fact that each cycle (down once and up once) lasts for 2*numRows-2
/*
## Detailed Explanation

1. **Why `cycleLen = 2*numRows - 2`?**

   * In a full “zig” down and “zag” up, you visit `numRows` characters going down, then `numRows-2` characters coming back up (excluding the top and bottom which were already counted).
   * Together that makes one **cycle** of length

 
       = 2*numRows - 2.

2. **Row-by-row extraction**

   * We treat each row `i` independently and collect exactly the characters that would land in that row in the zigzag.

3. **Vertical characters**

   * Every cycle contributes one character directly “below” in row `i` at index

     $$
       j = i + k * cycleLen
     $$

     for `k = 0, 1, 2, …` as long as `j < s.length()`.

4. **Diagonal characters (middle rows only)**

   * For rows strictly between the top (`0`) and bottom (`numRows-1`), each cycle also has a “zig” character on the way back up.
   * That character’s index is

     $$
       j + (cycleLen - 2*i)
     $$
   * We compute `diag = j + cycleLen - 2*row`. If `diag < n`, that diagonal character is valid and belongs to row `i`.

5. **Putting it all together**

   * We loop `row` from `0` to `numRows-1`.
   * For each `row`, we step through the string in strides of `cycleLen`, appending the vertical char, and—if `row` is not the first or last—also the diagonal char.
   * This visits each original character exactly once, in the same order as the zigzag reading by rows.

6. **Complexity**

   * **Time:** O(n), since we touch each character at most twice (vertical + diagonal).
   * **Space:** O(n) for the output `StringBuilder`.

This cycle‐based approach avoids explicitly simulating the up/down direction on every character and directly jumps to the two indices in each cycle that map to the current row.

*/
// class Solution {
//     public String convert(String s, int numRows) {
//         int n = s.length();
//         if (numRows == 1 || numRows >= n) {
//             // No zigzagging needed
//             return s;
//         }
        
//         // The cycle length: go down numRows, then up numRows-2
//         int cycleLen = 2 * numRows - 2;
//         StringBuilder sb = new StringBuilder(n);
        
//         // Build row by row
//         for (int row = 0; row < numRows; row++) {
//             // For each cycle starting at index 0, cycleLen, 2*cycleLen, …
//             for (int j = row; j < n; j += cycleLen) {
//                 // 1) The “vertical” character for this row
//                 sb.append(s.charAt(j));
                
//                 // 2) For middle rows, there’s an extra “diagonal” char
//                 //    at offset (cycleLen - row) within each cycle
//                 int diag = j + cycleLen - 2 * row;
//                 // Only append if it’s in‐bounds and not the same as the vertical
//                 // Intuitively:
//                 // You start at the downward position downIndex.
//                 // You then need to jump forward by the remainder of the
//                 // cycle, minus the distance you’ve already descended (i),
//                 // to land on that upward stroke.
//                 if (row > 0 && row < numRows - 1 && diag < n) {
//                     sb.append(s.charAt(diag));
//                 }
//             }
//         }
        
//         return sb.toString();
//     }
// }
