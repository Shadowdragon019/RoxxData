@file:Suppress("unused")
package lol.roxxane.roxx_data
import com.google.gson.*
import com.google.gson.internal.LazilyParsedNumber
import com.google.gson.stream.JsonReader
import lol.roxxane.roxx_data.parsing.parse
import lol.roxxane.roxx_data.stringifying.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()!!
fun main() {
	test()
}
fun test() {
	val tests = File("src/main/kotlin/test")
	if (tests.isDirectory) {
		for (file in tests.listFiles() ?: arrayOf()) {
			if (file == null) {
				continue
			}
			if (file.name.startsWith("disabled")) {
				continue
			}
			println("--- ${file.name} ---")
			val text = file.reader().readText()
			stringify(parse(text)).println()
		}
	}
}
fun commentExamples() {
	val data = listOf(
		Comment("Test"),
		1,
		2,
		Comment("69"),
		mapOf(
			Comment("Wheeee!!!") to 1,
			10 to Comment("WHOA"),
			Comment("A") to Comment("B")
		)
	)
	stringify(data).println()
	stringify(parse(stringify(data))).println()
	addCommentTransformer(Pair::class.java) { it.first.toString() + " " + it.second }
	stringify(1 to 3).println()
}
fun jsonTests() {
	addBoolTransformer(JsonPrimitive::class.java, JsonPrimitive::isBoolean, JsonPrimitive::getAsBoolean)
	addIntTransformer(JsonPrimitive::class.java, {
		var result = false
		if (it.isNumber) {
			val number = it.asNumber
			if (number is LazilyParsedNumber) {
				val int = number.toString().toIntOrNull()
				if (int != null) {
					result = true
				}
			}
		}
		result
	}, JsonPrimitive::getAsInt)
	addDoubleTransformer(JsonPrimitive::class.java, JsonPrimitive::isNumber, JsonPrimitive::getAsDouble)
	addStringTransformer(JsonPrimitive::class.java, JsonPrimitive::isString, JsonPrimitive::getAsString)
	addListTransformer(JsonArray::class.java, JsonArray::isJsonArray, JsonArray::asList)
	addMapTransformer(JsonObject::class.java, JsonObject::isJsonObject, JsonObject::asMap)
	val jsonTests = File("src/main/kotlin/json_tests")
	val outputDirectory = File("src/main/kotlin/json_tests_output")
	if (jsonTests.isDirectory && outputDirectory.isDirectory) {
		for (file in jsonTests.listFiles() ?: arrayOf()) {
			if (file == null) {
				continue
			}
			if (file.name.startsWith("disabled")) {
				continue
			}
			println("--- ${file.name} ---")
			val json: JsonElement = PRETTY_GSON.fromJson(JsonReader(FileReader(file)), JsonElement::class.java)
			val output = File(outputDirectory, file.nameWithoutExtension)
			if (!output.exists()) {
				output.createNewFile()
			}
			val string = stringify(json)
			val writer = FileWriter(output)
			writer.write(string)
			writer.close()
		}
	}
}
fun Any?.println() {
	println(this)
}
fun Any?.print() {
	print(this)
}