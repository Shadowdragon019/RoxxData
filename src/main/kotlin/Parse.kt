package lol.roxxane.roxx_data

fun parse(string: String): Any? {
	return parseTokens(tokenize(string))
}
private data class Line(val depth: Int, val tokens: List<String>) {
	override fun toString(): String {
		return "$depth | ${tokens.joinToString(" ")}"
	}
}
private fun parseTokens(lines: List<Line>): Any? {
	var data: Any? = null
	for (line in lines) {
		if (line.tokens.size > 2) {
			error("")
		} else if (line.tokens.size == 2) {
			data.deepPut(line.depth, parseToken(line.tokens[0]), parseToken(line.tokens[1]))
		} else if (line.tokens.size == 1) {
			if (data == null) {
				data = parseToken(line.tokens[0])
			} else {
				data.deepAdd(line.depth, parseToken(line.tokens[0]))
			}
		}
	}
	return data
}
private fun Any?.deepGet(depth: Int): Any? {
	if (depth < 2) {
		return this
	}
	if (this is List<*>) {
		return last().deepGet(depth - 1)
	} else if (this is Map<*, *>) {
		return values.last().deepGet(depth - 1)
	}
	error("Could not return $this")
}
@Suppress("UNCHECKED_CAST")
private fun Any?.deepAdd(depth: Int, element: Any) {
	if (depth < 2) {
		if (this !is MutableList<*>) {
			error("Failed to add $element to $this as this is not a mutable list")
		}
		(this as MutableList<Any>).add(element)
	} else if (this is MutableList<*>) {
		last().deepAdd(depth - 1, element)
	} else if (this is MutableMap<*, *>) {
		values.last().deepAdd(depth - 1, element)
	} else {
		error("Failed to add $element to $this with depth $depth as this is not a mutable list")
	}
}
@Suppress("UNCHECKED_CAST")
private fun Any?.deepPut(depth: Int, key: Any, value: Any) {
	if (depth < 2) {
		if (this !is MutableMap<*, *>) {
			error("Failed to put $key & $value into $this as this is not a mutable map")
		}
		(this as MutableMap<Any, Any>)[key] = value
	} else if (this is MutableList<*>) {
		last().deepPut(depth - 1, key, value)
	} else if (this is MutableMap<*, *>) {
		values.last().deepPut(depth - 1, key, value)
	} else {
		error("Failed to put $key & $value into $this with depth $depth as this is not a mutable map")
	}
}
private fun parseToken(token: String): Any {
	var data: Any? = token.toIntOrNull()
	if (data != null) {
		return data
	}
	data = token.toDoubleOrNull()
	if (data != null) {
		return data
	}
	data = token.toBooleanStrictOrNull()
	if (data != null) {
		return data
	}
	if (token == "[]") {
		return mutableListOf<Any>()
	} else if (token == "{}") {
		return mutableMapOf<Any, Any>()
	}
	return token
}
private fun tokenize(string: String): List<Line> {
	val lines = mutableListOf<Line>()
	val iterator = string.toList().listIterator()
	while (iterator.hasNext) {
		lines.add(Line(tokenizeDepth(iterator), tokenizeLineTokens(iterator)))
	}
	return lines
}
private fun tokenizeDepth(iterator: ListIterator<Char>): Int {
	var depth = 0
	while (iterator.hasNext) {
		if (iterator.next == '\t') {
			depth++
		} else {
			iterator.previous
			break
		}
	}
	if (iterator.hasPrevious && depth != 0) {
		iterator.previous
	}
	return depth
}
private fun tokenizeLineTokens(iterator: ListIterator<Char>): List<String> {
	val tokens = mutableListOf<String>()
	while (iterator.hasNext) {
		while (iterator.hasNext) {
			val current = iterator.next
			if (current == '\n') {
				return tokens
			} else if (!current.isWhitespace()) {
				iterator.previous
				break
			}
		}
		val current = iterator.next
		if (current == '>') {
			tokenizeComment(iterator)
		} else if (current in "'\"`") {
			tokens.add(tokenizeQuoted(iterator, current))
		} else {
			iterator.previous
			tokens.add(tokenizeUnquoted(iterator))
		}
	}
	return tokens
}
private fun tokenizeUnquoted(iterator: ListIterator<Char>): String {
	val builder = StringBuilder()
	while (iterator.hasNext) {
		val current = iterator.next
		if (current == '\\') {
			builder + tokenizeBreak(iterator)
		} else if (current.isWhitespace() || current in ">'\"`") {
			iterator.previous
			break
		} else {
			builder + current
		}
	}
	return builder.string
}
private fun tokenizeQuoted(iterator: ListIterator<Char>, quote: Char): String {
	val builder = StringBuilder()
	while (iterator.hasNext) {
		val current = iterator.next
		if (current == '\\') {
			builder + tokenizeBreak(iterator)
		} else if (current == quote) {
			break
		} else {
			builder + current
		}
	}
	return builder.string
}
private fun tokenizeComment(iterator: ListIterator<Char>) {
	while (iterator.hasNext) {
		if (iterator.next in "\n<[{") {
			return
		}
	}
}
private fun tokenizeBreak(iterator: ListIterator<Char>): Char {
	if (iterator.hasNext)
		return iterator.next
	return '\\'
}
private operator fun StringBuilder.plus(any: Any?): StringBuilder {
	append(any)
	return this
}
private val <T> Iterator<T>.next: T
	get() = next()
private val Iterator<*>.hasNext: Boolean
	get() = hasNext()
private val <T> ListIterator<T>.previous: T
	get() = previous()
private val ListIterator<*>.hasPrevious: Boolean
	get() = hasPrevious()
private val StringBuilder.string: String
	get() = toString()
