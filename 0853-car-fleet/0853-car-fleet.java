// Method 1: My approach using sorting based on position + tracking max time
/*
## Key observation

Two cars form a single fleet if the car behind **catches up** to the one in front **before reaching the target**. After they meet, they move together at the slower speed, so they arrive at the target at the same time.

Instead of simulating movement, we can reason using **time-to-target**.

For a car at position `p` with speed `s`, its arrival time is:

[
\text{time} = \frac{\text{target} - p}{s}
]

---

## Why sorting by position works

Sort cars by starting position in **descending** order (closest to target first).

Now scan from front to back:

* The frontmost car obviously forms a fleet.
* Consider the next car behind:

  * If it would arrive **later** than the fleet in front (larger time), it **cannot catch up** → it forms a **new fleet**.
  * If it would arrive **earlier or same time** (smaller or equal time), it **must catch up** before the target (because it’s effectively “faster” in terms of reaching the target) → it **merges** into the fleet ahead.

### The crucial invariant

Maintain:

* `maxTimeSoFar` = the arrival time of the current fleet ahead.

As you move backward:

* If `time > maxTimeSoFar`: new fleet (`maxTimeSoFar = time`)
* Else: merges (do nothing)

Because when a car merges, it adopts the fleet’s slower arrival time, i.e. it’s “capped” at `maxTimeSoFar`.


## Thorough example walkthrough

Use the canonical example:

* `target = 12`
* `position = [10, 8, 0, 5, 3]`
* `speed    = [ 2, 4, 1, 1, 3]`

### Step 1: Pair and sort by position descending

Pairs `(pos, speed)`:

* (10,2), (8,4), (0,1), (5,1), (3,3)

Sort descending by position:

1. (10,2)
2. (8,4)
3. (5,1)
4. (3,3)
5. (0,1)

We scan in this order.

---

### Step 2: Compute times and form fleets

We maintain:

* `maxTimeSoFar` = time of the fleet in front
* `fleets`

Initialize:

* `maxTimeSoFar = -1`
* `fleets = 0`

#### Car 1: (10,2)

[
time = (12-10)/2 = 1.0
]

* `1.0 > -1` → new fleet
* `fleets = 1`
* `maxTimeSoFar = 1.0`

Interpretation: A fleet led by car at 10 arrives in 1 hour.

---

#### Car 2: (8,4)

[
time = (12-8)/4 = 1.0
]

* `1.0 > 1.0`? No → merges

Interpretation: This car would arrive in 1 hour on its own, same as the fleet ahead, so it catches up exactly at the target (or earlier) → same fleet.

Still:

* `fleets = 1`
* `maxTimeSoFar = 1.0`

---

#### Car 3: (5,1)

[
time = (12-5)/1 = 7.0
]

* `7.0 > 1.0` → new fleet
* `fleets = 2`
* `maxTimeSoFar = 7.0`

Interpretation: This car is very slow. It will never catch the fleet ahead (which arrives at 1.0). So it’s its own fleet arriving at 7.0.

---

#### Car 4: (3,3)

[
time = (12-3)/3 = 3.0
]

* `3.0 > 7.0`? No → merges

Interpretation: This car would arrive in 3 hours on its own (faster than the slow fleet that arrives in 7). Since it’s behind that slow fleet, it will catch it before the target, then be forced to go at that fleet’s pace → arrival time becomes 7.0 (same as `maxTimeSoFar`).

Still:

* `fleets = 2`
* `maxTimeSoFar = 7.0`

---

#### Car 5: (0,1)

[
time = (12-0)/1 = 12.0
]

* `12.0 > 7.0` → new fleet
* `fleets = 3`
* `maxTimeSoFar = 12.0`

Interpretation: This car is slow and far. It can’t catch the fleet that arrives at 12? Actually it *is* 12, and `maxTimeSoFar` was 7, so it arrives later → cannot catch → new fleet.

Final answer: **3 fleets**

✅ Matches expected output.

---

## Why `time > maxTimeSoFar` means “new fleet”

When scanning from front to back:

* `maxTimeSoFar` is the time of the fleet in front.
* If a car behind has `time <= maxTimeSoFar`, it would arrive earlier (or same time) if alone → it must catch up to the slower fleet ahead before target → merges.
* If `time > maxTimeSoFar`, it is slower (arrives later) → can’t catch up → becomes new fleet.

---

## Common pitfalls (and what you fixed)

1. **Integer division bug**

```java
(target - pos) / spd
```

must be cast to double:

```java
(double)(target - pos) / spd
```

2. Confusing `Double.MIN_VALUE`
   Use `-1` or `Double.NEGATIVE_INFINITY`.

---

If you want, I can also show the equivalent “stack of times” formulation (push times, pop/merge) — it’s the same logic, just expressed differently.
*/
import java.util.AbstractMap.SimpleEntry;

class Solution {
    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;

        List<SimpleEntry<Integer, Integer>> pairList = new ArrayList<>();
        for(int i=0; i<n; i++){
            pairList.add(new SimpleEntry<>(position[i], speed[i]));
        }

        pairList.sort(Comparator.comparingInt(SimpleEntry<Integer, Integer>::getKey).reversed());

        for(int i=0; i<n; i++){
            position[i] = pairList.get(i).getKey();
            speed[i] = pairList.get(i).getValue();
        }

        double maxTimeSoFar = Double.MIN_VALUE;
        int fleets = 0;

        for(int i=0; i<n; i++){
            double time = (double)(target - position[i]) / speed[i];
            if(time > maxTimeSoFar){
                fleets++;
                maxTimeSoFar = time;
            }
        }

        return fleets;
    }
}


// Method 1.5: More optimized version of my approach
/*
# OTHER WAYS TO STORE AND SORT TWO ARRAYS BASED ON ONE OF THE ARRAYS APART FROM SIMPLE-ENTRY:
1) 
// Node to store the eta and position
class node{
    int pos;
    double time;
    public node(int pos, double time){
        this.pos = pos;
        this.time = time;
    }
}

ArrayList<node> ary = new ArrayList<>();
for(int i=0; i<position.length; i++){
    ary.add(new node(position[i], ((double)(target-position[i]) / speed[i])));
}
// Sort the array of position and eta according to the position
ary.sort((a, b) -> Integer.compare(b.pos, a.pos));


2) 
int n = position.length;
int[][] cars = new int[n][2]; // cars[i] = {position, speed}

for (int i = 0; i < n; i++) {
    cars[i][0] = position[i];
    cars[i][1] = speed[i];
}

// Sort by position descending (closest to target first)
Arrays.sort(cars, (a, b) -> Integer.compare(b[0], a[0]));

*/
// import java.util.AbstractMap.SimpleEntry;

// class Solution {
//     public int carFleet(int target, int[] position, int[] speed) {
//         int n = position.length;

//         List<SimpleEntry<Integer, Integer>> pairList = new ArrayList<>();
//         for (int i = 0; i < n; i++) {
//             pairList.add(new SimpleEntry<>(position[i], speed[i]));
//         }

//         pairList.sort(Comparator.comparingInt(SimpleEntry<Integer, Integer>::getKey).reversed());

//         double maxTimeSoFar = -1.0;
//         int fleets = 0;

//         for (int i = 0; i < n; i++) {
//             int pos = pairList.get(i).getKey();
//             int spd = pairList.get(i).getValue();
//             double time = (double)(target - pos) / spd;

//             if (time > maxTimeSoFar) {
//                 fleets++;
//                 maxTimeSoFar = time;
//             }
//         }

//         return fleets;
//     }
// }







// Method 2: Monotonic Stack-based approach
/*
**Sort by position descending (closest first)** and push times; the stack becomes non-decreasing; if a car behind has time <= time ahead, it merges.

Let’s do the correct stack method.

---

# Correct stack approach

## Correct rule

Sort by position **descending** (closest to target first).
Process cars from closest → farthest:

* Compute `time`.
* If stack is empty or `time > stack.peek()`: new fleet → push time
* Else (`time <= stack.peek()`): merges → do nothing

This is effectively a stack, because times on the stack are strictly increasing.


## Thorough walkthrough (same example, correct)

Sorted by position descending:

1. (10,2) time = 1.0
2. (8,4)  time = 1.0
3. (5,1)  time = 7.0
4. (3,3)  time = 3.0
5. (0,1)  time = 12.0

Stack holds fleet times (top is most recent fleet behind the front fleets).

Start: stack = []

1. (10,2), time 1.0

* stack empty → push 1.0
  stack: [1.0]
  fleets so far: 1

2. (8,4), time 1.0

* time <= stack.peek (1.0 <= 1.0) → merges
  stack: [1.0]
  fleets: still 1
  Interpretation: car at 8 catches the fleet ahead by the target.

3. (5,1), time 7.0

* time > stack.peek (7.0 > 1.0) → new fleet
  push 7.0
  stack: [7.0, 1.0]
  fleets: 2

4. (3,3), time 3.0

* time <= stack.peek (3.0 <= 7.0) → merges into the 7.0 fleet
  stack: [7.0, 1.0]
  fleets: still 2

5. (0,1), time 12.0

* time > stack.peek (12.0 > 7.0) → new fleet
  push 12.0
  stack: [12.0, 7.0, 1.0]
  fleets: 3 ✅

Return 3.

---

## Why this “stack” is valid

After sorting closest → farthest:

* `stack.peek()` is the time of the fleet immediately ahead of the current car.
* If current car’s time is smaller/equal, it must catch up → merges → no new push.
* If it’s larger, it can’t catch up → forms new fleet → push.
*/

// class Solution {
//     public int carFleet(int target, int[] position, int[] speed) {
//         int n = position.length;
//         int[][] cars = new int[n][2]; // {pos, speed}
//         for (int i = 0; i < n; i++) {
//             cars[i][0] = position[i];
//             cars[i][1] = speed[i];
//         }

//         // Sort by position descending (closest to target first)
//         Arrays.sort(cars, (a, b) -> b[0] - a[0]);

//         Deque<Double> stack = new ArrayDeque<>(); // fleet times

//         for (int i = 0; i < n; i++) {
//             int pos = cars[i][0];
//             int spd = cars[i][1];
//             double time = (double)(target - pos) / spd;

//             // If this car takes longer than the fleet ahead, it can't catch -> new fleet
//             if (stack.isEmpty() || time > stack.peek()) {
//                 stack.push(time);
//             } 
//             // else merges into the fleet ahead; do nothing
//         }

//         return stack.size();
//     }
// }
