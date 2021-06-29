class MinStack {
public:
    /** initialize your data structure here. */
    stack<int> data, minimum;
     MinStack() {
        
    }
    
    void push(int x) {
        data.push(x);
        if (minimum.empty() || minimum.top() >= x) {
            minimum.push(x);
        }
    }
    
    void pop() {
        if (top() == getMin()) {
            minimum.pop();
        }
        data.pop();
    }
    
    int top() {
        return data.top();
    }
    
    int getMin() {
        return minimum.top();
    }
};

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack* obj = new MinStack();
 * obj->push(val);
 * obj->pop();
 * int param_3 = obj->top();
 * int param_4 = obj->getMin();
 */

// Method 2: Constant space
// whenever we encounter a situation
// where element to be pushed is less then current min element then we push
// (2*x-curr_min) into the stack and since x should be our current min element
// so we make current min element as x, so at this point our curremt min hold
// correct min value but our stack hold (2*x-curr_min) instead of x
// as of now stack top will be less than our new min element and this will
// indicate that if we perform pop operation then our min element will be
// changed, suppose we perform pop operation, we see that our s.top is less
// than current min element, it indicates that if we pop then our min element
// will be changed and we need to update the min element with the previous
// min element, so if s.top is less than current min element then how to
// get back our previous min element..that is the question
// we go back to our formula
// 2*(new_min_element)-previous_min_element=s.top()
// 2*(new min element)-s.top()=previous_min_element
// so 2*curr_min - s.top() need to be updated in current min element before pop
// however if s.top() is greater or equal then current min element then we can
// directly pop it out as it won't effect our current min element


// int get_min()
// {
//     if(s.empty())
//         return -1;
//     return min_element;
// }
// void push_element(int x)
// {
//     if(s.empty())
//     {
//         s.push(x);
//         min_element=x;
//     }
//     else
//     {
//         if(x>=min_element)
//         {
//             s.push(x);
//         }
//         else
//         {   
//             // if smaller element is getting pushed then it will change our
//             // min element so we apply our formula..and instead of x we push
//             // 2*x-ME on top of stack and update ME as x
//             s.push(2*x-min_element);
//             min_element=x;
//         }
//     }
// }
// int pop_element()
// {
//     if(s.empty())
//     {
//         return -1;
//     }
//     else
//     {
//         if(s.top()<min_element)
//         {   
//             // as smaller element is getting popped, our min element will be
//             // changed so we apply formula and update current min
//             // current min=2*ME-s.top() , it will give previous min element
//             int x=min_element;
//             min_element=2*min_element-s.top();
//             s.pop();
//             return x;
//         }
//         else
//         {
//             int x=s.top();
//             s.pop();
//             return x;
//         }   
//     }   
// }
// int get_top()
// {
//     if(s.empty())
//     {
//         return -1;
//     }
//     else
//     {
//         if(s.top()>=min_element)
//             return s.top();
//         else
//             return min_element;
//     }  
// }

   