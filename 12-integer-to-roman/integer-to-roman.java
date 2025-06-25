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