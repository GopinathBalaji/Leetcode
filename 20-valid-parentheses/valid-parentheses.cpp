// Method 1: Stack approach
/*
*/
class Solution {
public:
    bool isValid(string s) {
        stack<char> stack;

        for(char c: s){
            if(c == '(' || c == '{' || c == '['){
                stack.push(c);
            }else if(c == ')'){
                if(stack.empty() || stack.top() != '('){
                    return false;
                }
                stack.pop();
            }else if(c == '}'){
                if(stack.empty() || stack.top() != '{'){
                    return false;
                }
                stack.pop();
            }else if(c == ']'){
                if(stack.empty() || stack.top() != '['){
                    return false;
                }
                stack.pop();
            }
        }

        return stack.empty();
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna