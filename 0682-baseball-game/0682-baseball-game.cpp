// Method 1: Stack approach
/*
*/
class Solution {
public:
    int calPoints(vector<string>& operations) {
        stack<string> stack;

        for(string s: operations){
            if(s == "+"){
               int second = std::stoi(stack.top());
               stack.pop();
               int first = std::stoi(stack.top());
               stack.pop();
               
               stack.push(std::to_string(first));
               stack.push(std::to_string(second));
               stack.push(std::to_string(first + second));
            }else if(s == "D"){
                int last = std::stoi(stack.top());

                stack.push(std::to_string(last * 2));
            }else if(s == "C"){
                stack.pop();
            }else{
                stack.push(s);
            }
        }

        int ans = 0;

        while(!stack.empty()){
            int last = std::stoi(stack.top());
            stack.pop();

            ans += last;
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna