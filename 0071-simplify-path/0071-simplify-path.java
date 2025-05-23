// Method 1: Character-by-character traversal
class Solution {
    public String simplifyPath(String path) {
        Stack<String> stack = new Stack<>();
        String res = "";

        for(int i=0;i<path.length();i++){
            if(path.charAt(i) == '/'){
                continue;
            }
            int j = i;
            String temp = "";
            while(j < path.length() && path.charAt(j) != '/'){
                temp = temp + path.charAt(j);
                j++;
            }

            i = j-1;
            if(temp.isEmpty() || temp.equals(".")){
                continue;
            }else if(temp.equals("..")){
                if(!stack.isEmpty()){
                    stack.pop();
                }
            }else{
                stack.push(temp);
            }
        }

        while(!stack.isEmpty()){
            res = "/" + stack.pop() + res;
        }

        return res.isEmpty() ? "/" : res;
    }
}

// Method 2: Array of strings by using  String[] tokens = path.split("/")