class Solution {
public:
    vector<string> fizzBuzz(int n) {
//         Method 1: Naive Approach
        vector<string> v1;
        int a = 1;
        while(a<=n){
            if(a%3==0 && a%5==0){
                v1.push_back("FizzBuzz");
            }else if(a%3==0){
                v1.push_back("Fizz");
            }else if(a%5==0){
                v1.push_back("Buzz");
            }else{
                v1.push_back(to_string(a));
            }
            a++;
        }
        return v1;
    }
};

// Method 2: String Concatenation
// Condition 1: 15 % 3 == 0 , num_ans_str = "Fizz"
// Condition 2: 15 % 5 == 0 , num_ans_str += "Buzz"
// => num_ans_str = "FizzBuzz"

   /*
     public List<String> fizzBuzz(int n) {
    // ans list
    List<String> ans = new ArrayList<String>();

    for (int num = 1; num <= n; num++) {

      boolean divisibleBy3 = (num % 3 == 0);
      boolean divisibleBy5 = (num % 5 == 0);

      String numAnsStr = "";

      if (divisibleBy3) {
        // Divides by 3, add Fizz
        numAnsStr += "Fizz";
      }

      if (divisibleBy5) {
        // Divides by 5, add Buzz
        numAnsStr += "Buzz";
      }

      if (numAnsStr.equals("")) {
        // Not divisible by 3 or 5, add the number
        numAnsStr += Integer.toString(num);
      }

      // Append the current answer str to the ans list
      ans.add(numAnsStr);
    }

    return ans;
  }

   */

// Method 3: Hash Map
// Put all the mappings in a hash table. The hash table fizzBuzzHash would look something like
// { 3:'Fizz', 5: 'Buzz' }
// Iterate on the numbers from 1 ... N1...N.
// For every number, iterate over the fizzBuzzHash keys and check for divisibility.
// If the number is divisible by the key, concatenate the corresponding hash value to the answer 
// string for current number. We do this for every entry in the hash table.
// Add the answer string to the answer list.

   /*
     public List<String> fizzBuzz(int n) {

    // ans list
    List<String> ans = new ArrayList<String>();

    // Hash map to store all fizzbuzz mappings.
    HashMap<Integer, String> fizzBizzDict =
        new HashMap<Integer, String>() {
          {
            put(3, "Fizz");
            put(5, "Buzz");
          }
        };

    for (int num = 1; num <= n; num++) {

      String numAnsStr = "";

      for (Integer key : fizzBizzDict.keySet()) {

        // If the num is divisible by key,
        // then add the corresponding string mapping to current numAnsStr
        if (num % key == 0) {
          numAnsStr += fizzBizzDict.get(key);
        }
      }

      if (numAnsStr.equals("")) {
        // Not divisible by 3 or 5, add the number
        numAnsStr += Integer.toString(num);
      }

      // Append the current answer str to the ans list
      ans.add(numAnsStr);
    }

    return ans;
  }
   */