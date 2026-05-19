// Method 1: Greedy two-pointer approach
/*
### Hint 1: Each boat can carry at most 2 people

You are given:

```cpp
people = [3, 2, 2, 1]
limit = 3
```

Each boat can carry:

```cpp
1 person
```

or:

```cpp
2 people
```

as long as their total weight is `<= limit`.

You want the **minimum number of boats**.

---

### Hint 2: Sorting helps

If you sort the weights:

```cpp
people = [1, 2, 2, 3]
limit = 3
```

Now it becomes easier to decide who should share a boat.

The lightest person is at the start, and the heaviest person is at the end.

---

### Hint 3: Always try to place the heaviest person

The heaviest remaining person must go on some boat.

So look at:

```cpp
people[right]
```

Now ask:

Can the heaviest person share a boat with the lightest person?

```cpp
people[left] + people[right] <= limit
```

If yes, put both together.

If no, the heaviest person must go alone.

---

### Hint 4: Use two pointers

After sorting:

```cpp
left = 0;
right = n - 1;
```

Then while:

```cpp
left <= right
```

you use one boat each iteration.

Case 1:

```cpp
people[left] + people[right] <= limit
```

Then both can share:

```cpp
left++;
right--;
boats++;
```

Case 2:

```cpp
people[left] + people[right] > limit
```

Then the heaviest person cannot pair with anyone, because even the lightest person is too heavy with them.

So:

```cpp
right--;
boats++;
```

---

### Hint 5: Why this greedy works

Suppose the heaviest person cannot pair with the lightest person:

```cpp
people[left] + people[right] > limit
```

Then the heaviest person cannot pair with anyone else either, because everyone else is at least as heavy as `people[left]`.

So sending the heaviest person alone is forced.

If the heaviest person can pair with the lightest person, that is always safe because it saves one boat and uses the weakest possible partner for the heaviest person.

---

### Core idea

```cpp
Sort the array.
Use two pointers.
Always place the heaviest remaining person.
Pair them with the lightest remaining person if possible.
```

Time complexity:

```cpp
O(n log n)
```

because of sorting.

Space complexity:

```cpp
O(1)
```

ignoring sorting implementation details.
*/
class Solution {
public:
    int numRescueBoats(vector<int>& people, int limit) {
        int n = people.size();
        int boats = 0;

        std::sort(people.begin(), people.end());

        int left = 0;
        int right = n - 1;
        
        while(left <= right){
            if(people[left] + people[right] <= limit){
                left++;
                right--;
                boats++;
            }else{
                right--;
                boats++;
            }
        }

        return boats;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna