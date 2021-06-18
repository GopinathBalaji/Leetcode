<h2>292. Nim Game</h2><h3>Easy</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">You are playing the following Nim Game with your friend:</p>

<ul style="user-select: auto;">
	<li style="user-select: auto;">Initially, there is a heap of stones on the table.</li>
	<li style="user-select: auto;">You and your friend will alternate taking turns, and <strong style="user-select: auto;">you go first</strong>.</li>
	<li style="user-select: auto;">On each turn, the person whose turn it is will remove 1 to 3 stones from the heap.</li>
	<li style="user-select: auto;">The one who removes the last stone is the winner.</li>
</ul>

<p style="user-select: auto;">Given <code style="user-select: auto;">n</code>, the number of stones in the heap, return <code style="user-select: auto;">true</code><em style="user-select: auto;"> if you can win the game assuming both you and your friend play optimally, otherwise return </em><code style="user-select: auto;">false</code>.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 4
<strong style="user-select: auto;">Output:</strong> false
<strong style="user-select: auto;">Explanation:</strong> These are the possible outcomes:
1. You remove 1 stone. Your friend removes 3 stones, including the last stone. Your friend wins.
2. You remove 2 stones. Your friend removes 2 stones, including the last stone. Your friend wins.
3. You remove 3 stones. Your friend removes the last stone. Your friend wins.
In all outcomes, your friend wins.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 1
<strong style="user-select: auto;">Output:</strong> true
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 2
<strong style="user-select: auto;">Output:</strong> true
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 2<sup style="user-select: auto;">31</sup> - 1</code></li>
</ul>
</div>