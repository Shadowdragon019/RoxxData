@file:Suppress("unused")
package lol.roxxane.roxx_data.stringifying
import lol.roxxane.roxx_data.boolIntDoubleOrNull
interface RoxxDataBoolTransformer {
	fun roxDataIsBool(): Boolean
	fun roxDataAsBool(): Boolean
}
interface RoxxDataIntTransformer {
	fun roxDataIsInt(): Boolean
	fun roxDataAsInt(): Int
}
interface RoxxDataDoubleTransformer {
	fun roxDataIsDouble(): Boolean
	fun roxDataAsDouble(): Double
}
interface RoxxDataStringTransformer {
	fun roxDataIsString(): Boolean
	fun roxDataAsString(): String
}
interface RoxxDataListTransformer {
	fun roxDataIsList(): Boolean
	fun roxDataAsList(): List<Any>
}
interface RoxxDataMapTransformer {
	fun roxDataIsMap(): Boolean
	fun roxDataAsMap(): Map<Any, Any>
}
interface RoxxDataCommentTransformer {
	fun roxDataIsComment(): Boolean
	fun roxDataAsComment(): String
}
private data class Transformer<R>(val predicate: (Any) -> Boolean, val transformer: (Any) -> R) {
	fun test(any: Any) = predicate(any)
	fun transform(any: Any): R = transformer(any)
	companion object {
		fun <T, R> clazz(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> R): Transformer<R> {
			return Transformer({clazz.isInstance(it) && predicate(clazz.cast(it))}, {transformer(clazz.cast(it))})
		}
	}
}
private data class CommentTransformer(val predicate: (Any) -> Boolean, val transformer: (Any) -> String) {
	fun test(any: Any) = predicate(any)
	fun transform(any: Any): Comment = Comment(transformer(any))
	companion object {
		fun <T> clazz(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> String): CommentTransformer {
			return CommentTransformer({clazz.isInstance(it) && predicate(clazz.cast(it))}, {transformer(clazz.cast(it))})
		}
	}
}
private val BOOL_TRANSFORMERS = mutableListOf<Transformer<Boolean>>()
private val INT_TRANSFORMERS = mutableListOf<Transformer<Int>>()
private val DOUBLE_TRANSFORMERS = mutableListOf<Transformer<Double>>()
private val STRING_TRANSFORMERS = mutableListOf<Transformer<String>>()
private val LIST_TRANSFORMERS = mutableListOf<Transformer<List<*>>>()
private val MAP_TRANSFORMERS = mutableListOf<Transformer<Map<*, *>>>()
private val COMMENT_TRANSFORMERS = mutableListOf<CommentTransformer>()
private val ALL_TRANSFORMERS = listOf(BOOL_TRANSFORMERS, INT_TRANSFORMERS, DOUBLE_TRANSFORMERS, STRING_TRANSFORMERS, LIST_TRANSFORMERS, MAP_TRANSFORMERS, COMMENT_TRANSFORMERS)
fun addBoolTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> Boolean) =
	BOOL_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addIntTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> Int) =
	INT_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addDoubleTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> Double) =
	DOUBLE_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addStringTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> String) =
	STRING_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addListTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> List<*>) =
	LIST_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addMapTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> Map<*, *>) =
	MAP_TRANSFORMERS.add(Transformer(predicate, transformer))
fun addCommentTransformer(predicate: (Any) -> Boolean, transformer: (Any) -> String) =
	COMMENT_TRANSFORMERS.add(CommentTransformer(predicate, transformer))

fun <T> addBoolTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> Boolean) =
	BOOL_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addIntTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> Int) =
	INT_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addDoubleTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> Double) =
	DOUBLE_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addStringTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> String) =
	STRING_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addListTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> List<*>) =
	LIST_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addMapTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> Map<*, *>) =
	MAP_TRANSFORMERS.add(Transformer.clazz(clazz, predicate, transformer))
fun <T> addCommentTransformer(clazz: Class<T>, predicate: (T) -> Boolean, transformer: (T) -> String) =
	COMMENT_TRANSFORMERS.add(CommentTransformer.clazz(clazz, predicate, transformer))

private fun <T> _true(t: T): Boolean = true
fun <T> addBoolTransformer(clazz: Class<T>, transformer: (T) -> Boolean) =
	BOOL_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addIntTransformer(clazz: Class<T>, transformer: (T) -> Int) =
	INT_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addDoubleTransformer(clazz: Class<T>, transformer: (T) -> Double) =
	DOUBLE_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addStringTransformer(clazz: Class<T>, transformer: (T) -> String) =
	STRING_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addListTransformer(clazz: Class<T>, transformer: (T) -> List<*>) =
	LIST_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addMapTransformer(clazz: Class<T>, transformer: (T) -> Map<*, *>) =
	MAP_TRANSFORMERS.add(Transformer.clazz(clazz, ::_true, transformer))
fun <T> addCommentTransformer(clazz: Class<T>, transformer: (T) -> String) =
	COMMENT_TRANSFORMERS.add(CommentTransformer.clazz(clazz, ::_true, transformer))
private operator fun StringBuilder.plus(any: Any?): StringBuilder = append(any)
private val StringBuilder.string: String
	get() = toString()
fun stringify(any: Any?): String = stringify(any, 0)
private fun stringify(any: Any?, depth: Int): String {
	fun tabs(): String = "\t".repeat(depth + 1)
	if (any == null) {
		return ""
	} else if (any is Int || any is Double || any is Boolean) {
		return any.toString()
	} else if (any is String) {
		val quotes = mutableSetOf<Char>()
		var whitespace = false
		for (char in any) {
			if (char in "'\"`" && char !in quotes) {
				quotes.add(char)
			}
			if (char.isWhitespace()) {
				whitespace = true
			}
		}
		if (!whitespace) {
			val data: Any? = boolIntDoubleOrNull(any)
			if (data != null) {
				return "\"$data\""
			}
			return any.replace("\\", "\\\\").replace(">", "\\>").replace("[", "\\[").replace("[", "\\{")
		}
		if ('"' !in quotes) {
			return "\"${any.replace("\\", "\\\\")}\""
		}
		if ('\'' !in quotes) {
			return "'${any.replace("\\", "\\\\")}'"
		}
		if ('`' !in quotes) {
			return "`${any.replace("\\", "\\\\")}`"
		}
		return "\"${any.replace("\\", "\\\\").replace("\"", "\\\"")}\""
	} else if (any is Collection<*>) {
		val builder = StringBuilder("[]")
		if (any.isNotEmpty()) {
			builder + '\n'
		}
		var i = 0
		for (element in any) {
			i++
			if (element != null) {
				builder + tabs() + stringify(element, depth + 1)
				if (i != any.size) {
					builder + '\n'
				}
			}
		}
		return builder.string
	} else if (any is Map<*, *>) {
		val builder = StringBuilder("{}")
		if (any.isNotEmpty()) {
			builder + '\n'
		}
		var i = 0
		for ((key, value) in any) {
			i++
			if (key != null && value != null) {
				if (key is Comment || value is Comment) {
					if (key is Comment) {
						builder + tabs() + stringify(key)
						if (value is Comment) {
							builder + '\n'
						}
					}
					if (value is Comment) {
						builder + tabs() + stringify(value)
					}
				} else {
					if (!(key is Boolean || key is Int || key is Double || key is String)) {
						error("Key $key in map $any was not bool, int, double, or string")
					}
					builder + tabs() + stringify(key) + ' ' + stringify(value, depth + 1)
				}
				if (i != any.size) {
					builder + '\n'
				}
			}
		}
		return builder.string
	} else if (any is Comment) {
		return ">${any.comment.replace(">", "\\>")}<"
	} else if (any is RoxxDataBoolTransformer && any.roxDataIsBool()) {
		return stringify(any.roxDataAsBool())
	} else if (any is RoxxDataIntTransformer && any.roxDataIsInt()) {
		return stringify(any.roxDataAsInt())
	} else if (any is RoxxDataDoubleTransformer && any.roxDataIsDouble()) {
		return stringify(any.roxDataAsDouble())
	} else if (any is RoxxDataStringTransformer && any.roxDataIsString()) {
		return stringify(any.roxDataAsString())
	} else if (any is RoxxDataListTransformer && any.roxDataIsList()) {
		return stringify(any.roxDataAsList())
	} else if (any is RoxxDataMapTransformer && any.roxDataIsMap()) {
		return stringify(any.roxDataAsMap())
	} else if (any is RoxxDataCommentTransformer && any.roxDataIsComment()) {
		return stringify(Comment(any.roxDataAsComment()))
	}
	for (transformers in ALL_TRANSFORMERS) {
		for (transformer in transformers) {
			if (transformer is Transformer<*> && transformer.test(any)) {
				return stringify(transformer.transform(any), depth)
			}
			if (transformer is CommentTransformer && transformer.test(any)) {
				return stringify(transformer.transform(any), depth)
			}
		}
	}
	return stringify(any.toString())
}