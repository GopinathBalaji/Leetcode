// Method 1: Using Stack
/*
*/
class Solution {
public:
    int evalRPN(vector<string>& tokens) {
        stack<string> stack;

        for(string s: tokens){
            if(s == "+"){
                int second = std::stoi(stack.top());
                stack.pop();
                int first = std::stoi(stack.top());
                stack.pop();

                stack.push(std::to_string(first + second));
            }else if(s == "-"){
                int second = std::stoi(stack.top());
                stack.pop();
                int first = std::stoi(stack.top());
                stack.pop();

                stack.push(std::to_string(first - second));
            }else if(s == "*"){
                int second = std::stoi(stack.top());
                stack.pop();
                int first = std::stoi(stack.top());
                stack.pop();

                stack.push(std::to_string(first * second));
            }else if(s == "/"){
                int second = std::stoi(stack.top());
                stack.pop();
                int first = std::stoi(stack.top());
                stack.pop();

                stack.push(std::to_string(first / second));
            }else{
                stack.push(s);
            }
        }

        return std::stoi(stack.top());
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna