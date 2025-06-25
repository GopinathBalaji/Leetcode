// My solution
class Solution {
    public String intToRoman(int num) {

        String ans = "";

        
        while(num >= 1000){
            ans = ans + "M";
            num -= 1000;
        }
        while(num >= 900){
            ans = ans + "CM";
            num -= 900;
        }
        while(num >= 500){
            ans = ans + "D";
            num -= 500;
        }
        while(num >= 400){
            ans = ans + "CD";
            num -= 400;
        }
        while(num >= 100){
            ans = ans + "C";
            num -= 100;
        }
        while(num >= 90){
            ans = ans + "XC";
            num -= 90;
        }
        while(num >= 50){
            ans = ans + "L";
            num -= 50;
        }
        while(num >= 40){
            ans = ans + "XL";
            num -= 40;
        }
        while(num >= 10){
            ans = ans + "X";
            num -= 10;
        }
        while(num >= 9){
            ans = ans + "IX";
            num -= 9;
        }
        while(num >= 5){
            ans = ans + "V";
            num -= 5;
        }
        while(num >= 4){
            ans = ans + "IV";
            num -= 4;
        }
        while(num >= 1){
            ans = ans + "I";
            num -= 1;
        }
        

        return ans; 
    }
}

// More elegant GPT solution

// class Solution {
//     public String intToRoman(int num) {
//         // 1) Table of all the “greedy” value→symbol mappings
//         int[]   vals   = {1000, 900, 500, 400, 100,  90,  50,  40,  10,   9,   5,   4,   1};
//         String[] syms  = {"M",  "CM","D", "CD","C", "XC","L", "XL","X", "IX","V", "IV","I"};

//         StringBuilder sb = new StringBuilder();

//         // 2) Greedily subtract as many of each value as we can
//         for (int i = 0; i < vals.length; i++) {
//             int count = num / vals[i];      // how many times this symbol fits
//             num %= vals[i];                 // reduce the number
//             // append that symbol count times
//             while (count-- > 0) {
//                 sb.append(syms[i]);
//             }
//         }

//         return sb.toString();
//     }
// }
