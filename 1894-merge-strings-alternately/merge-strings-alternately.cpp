// Method 1: Two-pointer approach
/*
*/
class Solution {
public:
    string mergeAlternately(string word1, string word2) {
        int len1 = word1.size();
        int len2 = word2.size();

        int i = 0;
        int j = 0;

        string ans = "";
        bool turn1 = true;

        while(i < len1 && j < len2){
            if(turn1){
                ans += word1[i];
                i++;
                turn1 = false;
            }else{
                ans += word2[j];
                j++;
                turn1 = true;
            }
        }

        if(i != len1){
            ans += word1.substr(i, len1 - i);
        }

        if(j != len2){
            ans += word2.substr(j, len2 - j);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna