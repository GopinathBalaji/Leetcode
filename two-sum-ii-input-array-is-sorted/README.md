<h2>167. Two Sum II - Input array is sorted</h2><h3>Easy</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">Given an array of integers <code style="user-select: auto;">numbers</code> that is already <strong style="user-select: auto;"><em style="user-select: auto;">sorted in non-decreasing order</em></strong>, find two numbers such that they add up to a specific <code style="user-select: auto;">target</code> number.</p>

<p style="user-select: auto;">Return<em style="user-select: auto;"> the indices of the two numbers (<strong style="user-select: auto;">1-indexed</strong>) as an integer array </em><code style="user-select: auto;">answer</code><em style="user-select: auto;"> of size </em><code style="user-select: auto;">2</code><em style="user-select: auto;">, where </em><code style="user-select: auto;">1 &lt;= answer[0] &lt; answer[1] &lt;= numbers.length</code>.</p>

<p style="user-select: auto;">The tests are generated such that there is <strong style="user-select: auto;">exactly one solution</strong>. You <strong style="user-select: auto;">may not</strong> use the same element twice.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numbers = [2,7,11,15], target = 9
<strong style="user-select: auto;">Output:</strong> [1,2]
<strong style="user-select: auto;">Explanation:</strong> The sum of 2 and 7 is 9. Therefore index1 = 1, index2 = 2.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numbers = [2,3,4], target = 6
<strong style="user-select: auto;">Output:</strong> [1,3]
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numbers = [-1,0], target = -1
<strong style="user-select: auto;">Output:</strong> [1,2]
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">2 &lt;= numbers.length &lt;= 3 * 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">-1000 &lt;= numbers[i] &lt;= 1000</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">numbers</code> is sorted in <strong style="user-select: auto;">non-decreasing order</strong>.</li>
	<li style="user-select: auto;"><code style="user-select: auto;">-1000 &lt;= target &lt;= 1000</code></li>
	<li style="user-select: auto;">The tests are generated such that there is <strong style="user-select: auto;">exactly one solution</strong>.</li>
</ul>
</div>