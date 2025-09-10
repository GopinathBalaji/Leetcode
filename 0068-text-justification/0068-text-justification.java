// Row-by-Row packing; O(total chars)
/*
NOTE: The (j - i) term in the code is just how many single spaces you must reserve to even consider adding the word.

The `+ (j - i)` in that packing loop is **not** “the size of the last gap” nor “spaces grow like 0,1,2,…”. It’s only the **count of gaps** if you were to place words `i..j` on the same line with the **minimum one space per gap**.

Think of the loop as just answering: *“Can I still fit the next word if I give each gap exactly one space?”*

* `lineLen` = sum of characters of words `i..(j-1)`
* `words[j].length()` = length of the next word we want to try
* `(j - i)` = **number of gaps** between `j - i + 1` words (i.e., minimal spaces = one per gap)

So `tentative = lineLen + words[j].length() + (j - i)` is the **minimum** width if you include `words[j]`. If that exceeds `maxWidth`, you stop packing.

You only decide **how many spaces each gap actually gets** later, when you format the line:

* `totalSpaces = maxWidth - sum(word lengths in line)`
* `gaps = (#words in line - 1)`
* Distribute with `base = totalSpaces / gaps` and `extra = totalSpaces % gaps`, giving the first `extra` gaps one extra space (so **earlier** gaps become wider, as the problem requires).

Quick mini-example:

* Words: `["This","is","an"]`, lengths `4,2,2`, `maxWidth=16`
* Packing check:

  * Try just `"This"`: `lineLen=4`, gaps so far `0` → width `4`
  * Add `"is"`: `lineLen=4+2=6`, gaps `1` → width `7`
  * Add `"an"`: `lineLen=6+2=8`, gaps `2` → width `10`
  * All fit (we didn’t assign per-gap sizes yet; we only counted gaps).
* Formatting:

  * `totalSpaces = 16 - 8 = 8`, `gaps = 2`
  * `base = 8/2 = 4`, `extra = 0`
  * Final line: `"This····is····an"` (earlier gaps aren’t smaller; both are 4).

The `(j - i)` term is just **how many single spaces you must reserve to even consider adding the word**. The actual per-gap widths are computed afterwards, and by construction they won’t grow like `0,1,2,…`; they’ll follow the base/extra rule (with **earlier** gaps getting any extras).

##########################

NOTE: Reasoning for int gaps = j - 1 - i
After the loop ends, j is the first word that didn’t fit.
The actual line uses words i .. j-1, which is (j - i) words.
Number of gaps between (j - i) words is (j - i) - 1 = j - i - 1.

Tiny example: suppose words at indices 0,1,2 fit, but 3 doesn’t.
Then i = 0, j = 3.
Words in the line = j - i = 3.
Gaps between them = 3 - 1 = 2 = j - i - 1.
If you used j - i here, you’d get 3 gaps—off by one.


#######################

Here’s exactly what the last loop is doing and why each piece is there:

```java
for (int k = i; k < j; k++) {
    sb.append(words[k]);                         // 1) place the kth word of this line
    if (k < j - 1) {                             // 2) if not the last word on this line...
        int spaces = base + ((k - i) < extra ? 1 : 0);  // 3) how many spaces for THIS gap?
        for (int s = 0; s < spaces; s++) sb.append(' '); // 4) append exactly that many
    }
}
```

### What `i`, `j`, `base`, `extra` mean

* You’ve already chosen that this line will contain words at indices **`i, i+1, …, j-1`** (i.e., `j` is exclusive).
* `lineLen = sum(length(words[i..j-1]))` (characters only, no spaces).
* `gaps = (j - i - 1)` is the number of spaces *slots* between these words.
* `totalSpaces = maxWidth - lineLen` is how many spaces you must distribute to make the line **exactly** `maxWidth`.
* `base = totalSpaces / gaps` = the minimum spaces each gap gets.
* `extra = totalSpaces % gaps` = the count of **leftmost** gaps that each get **one extra** space.

> Those `base` & `extra` values are **only used for fully-justified, multi-word, non-last** lines. (Last line or single-word lines are handled in the other branch: left-justified with trailing padding.)

### Line-by-line: what the loop does

1. `sb.append(words[k])`
   Put the next word.

2. `if (k < j - 1)`
   Only add a gap **after** the word if it’s not the **last** word on this line. For fully-justified lines there is **no trailing space** after the last word—the line reaches `maxWidth` purely by distributing spaces **between** words.

3. `int spaces = base + ((k - i) < extra ? 1 : 0);`

   * `(k - i)` is the **gap index** (0 for the first gap, 1 for the second, …).
   * The first `extra` gaps get **one more** space each.
     So gaps with indices `0 .. extra-1` use `base + 1`, the rest use `base`.
   * This matches the requirement: **earlier gaps are wider** when spaces don’t divide evenly.

4. Append exactly `spaces` spaces to `sb`.
   (Using a small loop; equivalent to `" ".repeat(spaces)` if allowed.)

### Why this sums perfectly to `totalSpaces`

Number of gaps = `gaps`.
Total spaces appended = `gaps * base + extra` = `totalSpaces` by construction.
Since the characters already account for `lineLen`, the line length ends at `lineLen + totalSpaces = maxWidth`.


## Worked example (uneven spacing)

**words on this line:** `["example","of","text"]`
lengths = 7, 2, 4; `maxWidth = 16`

* `lineLen = 7+2+4 = 13`
* `gaps = 3-1 = 2`
* `totalSpaces = 16 - 13 = 3`
* `base = 3 / 2 = 1`
* `extra = 3 % 2 = 1` → the **first** 1 gap gets an extra space

Loop:

* `k = i` (word `"example"`):

  * append `"example"`
  * not last word → gap index = `k - i = 0` < `extra (1)` → `spaces = 1 + 1 = 2`
  * append `"  "`
* `k = i+1` (word `"of"`):

  * append `"of"`
  * not last word → gap index = `1` ≥ `extra (1)` → `spaces = 1 + 0 = 1`
  * append `" "`
* `k = i+2` (word `"text"`):

  * append `"text"`
  * last word → no spaces

Result: `"example··of·text"` (· shows spaces), length 16.
Note the **earlier** gap got 2 spaces, the later gap got 1—exactly as required.

---

## Another quick check (even spacing)

**words:** `["This","is","an"]`, lengths = 4,2,2; `maxWidth=16`

* `lineLen = 8`, `gaps = 2`, `totalSpaces=8`, `base=4`, `extra=0`
* Both gaps get 4 spaces:

  * `"This····is····an"`

---

## Edge/branch notes

* If `gaps == 0` (only one word fits) **or** it’s the **last line**, you don’t use this loop. You go to the **left-justify** branch: single spaces between words and pad the **end** with spaces until `maxWidth`.
* This loop therefore only handles the **fully-justified** case (k ≥ 2 words, not last line).

Each iteration places one word and, if needed, the correctly sized gap after it, ensuring total spaces add up and earlier gaps get the extra ones.

##################

## Walkthrough on the classic example

```
words = ["This","is","an","example","of","text","justification."]
maxWidth = 16
```

### Line 1: pack words

* Try `"This is an"`: sumLen = 4+2+2 = 8; gaps = 2 → minimal width = 8 + 2 = 10 ≤ 16 ✅
* Next `"example"` would make minimal width = 8+7 + 3 = 18 > 16 ❌ → stop at `"This is an"`.

**Full justify** (not last line, gaps=2):

* totalSpaces = 16 - 8 = 8; base = 8/2 = 4; extra = 0
  → `"This····is····an"` (· = space)

### Line 2: next words

* Start `"example of text"`: sumLen = 7+2+4 = 13; gaps = 2 → minimal width = 13 + 2 = 15 ≤ 16 ✅
* Next `"justification."` would exceed → stop.

**Full justify**:

* totalSpaces = 16 - 13 = 3; base = 3/2 = 1; extra = 1
  → first gap gets 2 spaces, second gets 1:
  `"example··of·text"`

### Line 3: last line

* `"justification."` alone (last line) → **left-justify**:
  `"justification."` + 2 trailing spaces to reach length 16.

**Final output lines (length 16 each):**

```
"This    is    an"
"example  of text"
"justification.  "
```


*/
class Solution {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> res = new ArrayList<>();
        int n = words.length;
        int i = 0;

        while (i < n) {
            // 1) Greedily pack as many words as fit in this line
            int j = i, lineLen = 0; // lineLen counts only characters (no spaces)
            while (j < n) {
                int tentative = lineLen + words[j].length() + (j - i); // total tentative spaces in this line = (j - i)
                if (tentative > maxWidth) break;
                lineLen += words[j].length();
                j++;
            }

            int gaps = j-1 - i;  // j-1 because we counted an extra j previously
            boolean lastLine = (j == n);
            StringBuilder sb = new StringBuilder(maxWidth);

            if (gaps <= 0 || lastLine) {
                // 2) Left-justify: single spaces, then pad end
                for (int k = i; k < j; k++) {
                    if (k > i) sb.append(' ');
                    sb.append(words[k]);
                }
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                // 3) Full-justify: distribute spaces across gaps
                int totalSpaces = maxWidth - lineLen;
                int base = totalSpaces / gaps;
                int extra = totalSpaces % gaps; // first 'extra' gaps get one more space

                for (int k = i; k < j; k++) {
                    sb.append(words[k]);
                    if (k < j - 1) {
                        int spaces = base + ((k - i) < extra ? 1 : 0);
                        for (int s = 0; s < spaces; s++) sb.append(' ');
                    }
                }
            }

            res.add(sb.toString());
            i = j; // next line starts at j
        }

        return res;
    }
}


