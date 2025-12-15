package lol.roxxane.roxx_data.parsing
import lol.roxxane.roxx_data.boolIntDoubleOrNull
fun parse(string: String): Any? {
	return parseTokens(tokenize(string))
}
private data class Line(val depth: Int, val tokens: List<String>) {
	override fun toString(): String {
		return "$depth | ${tokens.joinToString(" ")}"
	}
}
enum class ListOrMap(val isList: Boolean) {
	LIST(true),
	MAP(false);
}
private fun parseTokens(lines: List<Line>): Any? {
	val listOrMaps = mutableListOf<ListOrMap>()
	var data: Any? = null
	for (line in lines) {
		if (line.tokens.isEmpty()) {
			continue
		}
		val depth = line.depth
		val tokens = line.tokens
		val tokenCount = tokens.size
		fun updateListOrMaps(token: String) {
			if (token == "[]") {
				if (listOrMaps.size > depth) {
					listOrMaps.set(depth, ListOrMap.LIST)
				} else {
					listOrMaps.add(depth, ListOrMap.LIST)
				}
			} else if (token == "{}") {
				if (listOrMaps.size > depth) {
					listOrMaps.set(depth, ListOrMap.MAP)
				} else {
					listOrMaps.add(depth, ListOrMap.MAP)
				}
			}
		}
		if (data == null) {
			if (tokenCount != 1) {
				error("First line $tokens can only contain one token, contained $tokenCount")
			} else {
				val token = tokens[0]
				if (token == "[]") {
					listOrMaps.add(depth, ListOrMap.LIST)
				} else if (token == "{}") {
					listOrMaps.add(depth, ListOrMap.MAP)
				}
				data = parseToken(tokens[0])
			}
		} else {
			val listOrMap = listOrMaps[depth - 1]
			if (listOrMap.isList) {
				for (token in line.tokens) {
					updateListOrMaps(token)
					data.deepAdd(line.depth, parseToken(token))
				}
			} else {
				var key: String? = null
				for (token in line.tokens) {
					if (key == null) {
						key = token
					} else {
						updateListOrMaps(token)
						data.deepPut(depth, parseToken(key), parseToken(token))
						key = null
					}
				}
				if (key != null) {
					error("Key $key does not have associated value")
				}
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
	val data: Any? = boolIntDoubleOrNull(token)
	if (data != null) {
		return data
	}
	if (token == "[]") {
		return mutableListOf<Any>()
	} else if (token == "{}") {
		return mutableMapOf<Any, Any>()
	}
	if (token.isEmpty()) { // I don't THINK it can be empty, but I'll error anyways
		error("Token was empty")
	}
	if (token.first() in "`'\"" && token.first() == token.last()) {
		return token.removeSurrounding(token.first().toString())
	}
	return token
}
fun publicTokenize(string: String): List<Pair<Int, List<String>>> {
	return tokenize(string).map { it.depth to it.tokens }.toList()
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
	return "$quote${builder.string}$quote"
}
private fun tokenizeComment(iterator: ListIterator<Char>) {
	while (iterator.hasNext) {
		val next = iterator.next
		if (next == '\\') {
			tokenizeBreak(iterator)
		} else if (next in "<") {
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