<h2>990. Satisfiability of Equality Equations</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">Given an array <font face="monospace" style="user-select: auto;">equations</font>&nbsp;of strings that represent relationships between variables, each string <code style="user-select: auto;">equations[i]</code>&nbsp;has length <code style="user-select: auto;">4</code> and takes one of two different forms: <code style="user-select: auto;">"a==b"</code> or <code style="user-select: auto;">"a!=b"</code>.&nbsp; Here, <code style="user-select: auto;">a</code> and <code style="user-select: auto;">b</code> are lowercase letters (not necessarily different) that represent one-letter variable names.</p>

<p style="user-select: auto;">Return <code style="user-select: auto;">true</code>&nbsp;if and only if it is possible to assign integers to variable names&nbsp;so as to satisfy all the given equations.</p>

<p style="user-select: auto;">&nbsp;</p>

<ol style="user-select: auto;">
</ol>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong><span id="example-input-1-1" style="user-select: auto;">["a==b","b!=a"]</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-1" style="user-select: auto;">false</span>
<strong style="user-select: auto;">Explanation: </strong>If we assign say, a = 1 and b = 1, then the first equation is satisfied, but not the second.  There is no way to assign the variables to satisfy both equations.
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong><span id="example-input-2-1" style="user-select: auto;">["b==a","a==b"]</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-2" style="user-select: auto;">true</span>
<strong style="user-select: auto;">Explanation: </strong>We could assign a = 1 and b = 1 to satisfy both equations.
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong><span id="example-input-3-1" style="user-select: auto;">["a==b","b==c","a==c"]</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-3" style="user-select: auto;">true</span>
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 4:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong><span id="example-input-4-1" style="user-select: auto;">["a==b","b!=c","c==a"]</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-4" style="user-select: auto;">false</span>
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 5:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong><span id="example-input-5-1" style="user-select: auto;">["c==c","b==d","x!=z"]</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-5" style="user-select: auto;">true</span>
</pre>

<p style="user-select: auto;">&nbsp;</p>

<p style="user-select: auto;"><strong style="user-select: auto;">Note:</strong></p>

<ol style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= equations.length &lt;= 500</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">equations[i].length == 4</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">equations[i][0]</code> and <code style="user-select: auto;">equations[i][3]</code> are lowercase letters</li>
	<li style="user-select: auto;"><code style="user-select: auto;">equations[i][1]</code> is either <code style="user-select: auto;">'='</code> or <code style="user-select: auto;">'!'</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">equations[i][2]</code> is&nbsp;<code style="user-select: auto;">'='</code></li>
</ol>
</div>
</div>
</div>
</div>
</div>
</div>