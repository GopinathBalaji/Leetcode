class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0;  // net gas over the entire route
        int tank = 0;   // current tank level from the candidate start
        int start = 0;  // candidate starting station

        for(int i=0;i<gas.length;i++){
            int diff = gas[i] - cost[i];
            total += diff;
            tank += diff;

            // If we run out of gas at station i, 
            // no station between 'start' and 'i' can work.
            if(tank < 0){
                start = i+1;  // try next station as new start
                tank = 0;  // reset tank
            }
        }

        // If overall net gas is negative, it's impossible
        return (total >= 0) ? start : -1;
    }
}

// Explanation
/*
Global feasibility

If the sum of all gas[i] – cost[i] over every station is negative, you can’t complete the loop from any start—there simply isn’t enough fuel overall.

Greedy restart when you run dry

Suppose you start at station A with an empty tank and walk forward. If at station B your tank ever drops below zero, you clearly can’t have started at any station between A and B, because starting later would give you even less net fuel by the time you reach B.

Therefore, you can “abandon” all stations in [A…B] as possible starts, reset your tank to zero at B+1, and try again from there.
*/