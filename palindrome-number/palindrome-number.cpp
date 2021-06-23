class Solution {
public:
    bool isPalindrome(int x) {
//         Method 1: Converting to string
     if(x<0){
         return false;
     }   
         string a = to_string(x);
        int l1 = 0;
        int l2 = a.length()-1;
        while(l1<l2){
            if(a[l1]!=a[l2]){
                return false;
            }
            l1++;
            l2--;
        }
        return true;
    }
};

// Method 2: Without using extra space
    /*
   public class Solution {
    public bool IsPalindrome(int x) {
        // Special cases:
        // As discussed above, when x < 0, x is not a palindrome.
        // Also if the last digit of the number is 0, in order to be a palindrome,
        // the first digit of the number also needs to be 0.
        // Only 0 satisfy this property.
        if(x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        int revertedNumber = 0;
        while(x > revertedNumber) {
            revertedNumber = revertedNumber * 10 + x % 10;
            x /= 10;
        }

        // When the length is an odd number, we can get rid of the middle digit by revertedNumber/10
        // For example when the input is 12321, at the end of the while loop we get x = 12, revertedNumber = 123,
        // since the middle digit doesn't matter in palidrome(it will always equal to itself), we can simply get rid of it.
        return x == revertedNumber || x == revertedNumber/10;
    }
}
    */