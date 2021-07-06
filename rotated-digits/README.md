<h2>788. Rotated Digits</h2><h3>Easy</h3><hr><div style="user-select: auto;"><p style="user-select: auto;"><code style="user-select: auto;">x</code> is a good number if after rotating each digit individually by 180 degrees, we get a valid number that is different from <code style="user-select: auto;">x</code>. Each digit must be rotated - we cannot choose to leave it alone.</p>

<p style="user-select: auto;">A number is valid if each digit remains a digit after rotation. 0, 1, and 8 rotate to themselves; 2 and 5 rotate to each other (on this case they are rotated in a different direction, in other words 2 or 5 gets mirrored); 6 and 9 rotate to each other, and the rest of the numbers do not rotate to any other number and become invalid.</p>

<p style="user-select: auto;">Now given a positive number <code style="user-select: auto;">n</code>, how many numbers <code style="user-select: auto;">x</code> from <code style="user-select: auto;">1</code> to <code style="user-select: auto;">n</code> are good?</p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Example:</strong>
<strong style="user-select: auto;">Input:</strong> 10
<strong style="user-select: auto;">Output:</strong> 4
<strong style="user-select: auto;">Explanation:</strong> 
There are four good numbers in the range [1, 10] : 2, 5, 6, 9.
Note that 1 and 10 are not good numbers, since they remain unchanged after rotating.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Note:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">n</code> will be in range <code style="user-select: auto;">[1, 10000]</code>.</li>
</ul>
</div>