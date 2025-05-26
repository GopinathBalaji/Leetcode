class Solution {
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for(String s: tokens){
            if(s.equals("+")){
                int val2 = stack.pop();
                int val1 = stack.pop();
                stack.push(val1 + val2);
            }else if(s.equals("-")){
                int val2 = stack.pop();
                int val1 = stack.pop();
                stack.push(val1 - val2);               
            }else if(s.equals("*")){
                int val2 = stack.pop();
                int val1 = stack.pop();
                stack.push(val1 * val2);                
            }else if(s.equals("/")){
                int val2 = stack.pop();
                int val1 = stack.pop();
                stack.push(val1 / val2);                
            }else{
                stack.push(Integer.parseInt(s));
            }
        }

        return stack.pop();
    }
}