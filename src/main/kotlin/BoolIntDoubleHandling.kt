package lol.roxxane.roxx_data
fun intOrNull(string: String): Int? {
	for (char in string) {
		if (!char.isDigit()) {
			return null
		}
	}
	return string.toIntOrNull()
}
fun doubleOrNull(string: String): Double? {
	var foundDot = false
	for (char in string) {
		if (char == '.') {
			if (foundDot) {
				return null
			} else {
				foundDot = true
			}
		}
		else if (!char.isDigit()) {
			return null
		}
	}
	return string.toDoubleOrNull()
}
fun boolIntDoubleOrNull(string: String): Any? {
	var data: Any? = intOrNull(string)
	if (data != null) {
		return data
	}
	data = doubleOrNull(string)
	if (data != null) {
		return data
	}
	return string.toBooleanStrictOrNull()
}