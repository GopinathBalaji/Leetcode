class Solution {
    public int[] plusOne(int[] digits) {
        int n = digits.length;
        
        if(digits[n-1] != 9){
            digits[n-1] = digits[n-1] + 1;
            return digits;
        }

        int endPointer = n-1;
        while(endPointer >= 0 && digits[endPointer] == 9){
            endPointer --;
        }
        
        endPointer++;
        List<Integer> ans = new ArrayList<>();
        int carry = 0;
        int value = 0;
            
        for(int i=n-1; i>=0; i--){
            if(i == n-1){
                value = digits[i] + 1;
                ans.add(value % 10);
                carry = value / 10;
            }else{
                value = digits[i] + carry;
                ans.add(value % 10);
                carry = value / 10;
            }
        }
            
        if(endPointer == 0){
            ans.add(carry);
        }
        Collections.reverse(ans);
    

        int[] result = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            result[i] = ans.get(i);
        }

        return result;
    }
}

// Cleaner GPT code
// class Solution {
//     public int[] plusOne(int[] digits) {
//         int n = digits.length;
//         List<Integer> ans = new ArrayList<>();
//         int carry = 1;  // Start with carry = 1 for the +1 operation

//         for (int i = n - 1; i >= 0; i--) {
//             int value = digits[i] + carry;
//             ans.add(value % 10);
//             carry = value / 10;
//         }

//         if (carry == 1) {
//             ans.add(carry);
//         }

//         Collections.reverse(ans);
//         int[] result = new int[ans.size()];
//         for (int i = 0; i < ans.size(); i++) {
//             result[i] = ans.get(i);
//         }

//         return result;
//     }
// }
