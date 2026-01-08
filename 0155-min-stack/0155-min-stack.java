// Method 1: Two stack version
/*
One stack holds all values; the other tracks the current minima (handling duplicates with <=).
*/
class MinStack {

    private final Deque<Integer> stack;
    private final Deque<Integer> mins;

    public MinStack() {
        stack = new ArrayDeque<>();
        mins = new ArrayDeque<>();
    }
    
    public void push(int val) {
        stack.push(val);
        if(mins.isEmpty() || val <= mins.peek()){
            mins.push(val);
        }
    }
    
    public void pop() {
        int x = stack.pop();
        if(x == mins.peek()){
            mins.pop();
        }
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return mins.peek();
    }
}

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(val);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.getMin();
 */




//  Method 2: One Stack version
/*
One-stack Min Stack that stores (value, currentMin) together for every entry. This keeps getMin() O(1) because the minimum for the current stack state is sitting on the top node.
*/
// class MinStack {
//     // Each element is { value, minAtThisNode }
//     private final Deque<int[]> st = new ArrayDeque<>();

//     public MinStack() {}

//     public void push(int val) {
//         int min = st.isEmpty() ? val : Math.min(val, st.peek()[1]);
//         st.push(new int[]{ val, min });
//     }

//     public void pop() {
//         st.pop(); // LeetCode guarantees valid ops; otherwise guard for empty
//     }

//     public int top() {
//         return st.peek()[0];
//     }

//     public int getMin() {
//         return st.peek()[1];
//     }
// }