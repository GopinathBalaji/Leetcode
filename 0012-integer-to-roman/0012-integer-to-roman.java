// Method 1: Greedy Subtraction
/*
We walk largest→smallest value, subtracting while we can and appending the matching Roman token.

Why it works
Roman numerals are built from descending tokens, with “subtractive” pairs (900, 400, 90, 40, 9, 4) included.
Greedily taking the largest possible token at each step yields the canonical form.

Walkthrough: 1994
Start 1994: take 1000 → M, left 994
Take 900 → CM, left 94
Take 90 → XC, left 4
Take 4 → IV, left 0
Result: MCMXCIV

Another: 58 = 50 + 5 + 3 → L + V + III → LVIII.
*/
class Solution {
    public String intToRoman(int num) {
        // 1 <= num <= 3999
        int[] vals =    {1000, 900, 500, 400, 100, 90,  50,  40,  10,  9,   5,  4,  1};
        String[] syms = {"M",  "CM","D", "CD", "C","XC","L", "XL", "X", "IX","V","IV","I"};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            while (num >= vals[i]) {
                sb.append(syms[i]);
                num -= vals[i];
            }
            if (num == 0) break;
        }
        return sb.toString();
    }
}



// Method 2: Place-value table for precomputing and indexing
/*
Precompute the Roman strings for ones/tens/hundreds/thousands and index into them.

Walkthrough: 1987
thousands: 1 → M
hundreds: 9 → CM
tens: 8 → LXXX
ones: 7 → VII
Result: MCMLXXXVII
*/
// class Solution {
//     public String intToRoman(int num) {
//         String[] thousands = {"", "M", "MM", "MMM"};
//         String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
//         String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
//         String[] ones      = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

//         StringBuilder sb = new StringBuilder();
//         sb.append(thousands[num / 1000]);
//         sb.append(hundreds[(num % 1000) / 100]);
//         sb.append(tens[(num % 100) / 10]);
//         sb.append(ones[num % 10]);
//         return sb.toString();
//     }
// }
