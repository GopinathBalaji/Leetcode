// Method 1: Using 2 stacks
/*
One stack holds all values; the other tracks the current minima (handling duplicates with <=).
*/
class MinStack {
private:
    stack<int> stack1;
    stack<int> stack2;

public:
    MinStack() {
        
    }
    
    void push(int value) {
        stack1.push(value);

        if(stack2.empty() || value <= stack2.top()){
            stack2.push(value);
        }
    }
    
    void pop() {
        int top1 = stack1.top();
        stack1.pop();

        if(top1 == stack2.top()){
            stack2.pop();
        }
    }
    
    int top() {
        return stack1.top();
    }
    
    int getMin() {
        return stack2.top();
    }
};

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack* obj = new MinStack();
 * obj->push(value);
 * obj->pop();
 * int param_3 = obj->top();
 * int param_4 = obj->getMin();
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna