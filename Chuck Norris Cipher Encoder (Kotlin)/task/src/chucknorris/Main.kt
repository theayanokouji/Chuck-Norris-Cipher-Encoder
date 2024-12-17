package chucknorris

import kotlin.math.pow

fun convertCharToBinary(char: Char): String {
    return String.format("%7s", Integer.toBinaryString(char.code)).replace(" ", "0")
}

fun encryptString(input: String): String {
    var encryptedString = ""
    var binary = ""
    var nOnes = 0 // to count the number of zeros
    var nZeros = 0 // to count number of zeros
    for (i in input.indices) {
        binary += convertCharToBinary(input[i])
    }
    // now go through each character and encrypt it
    for (j in binary.indices) {
        if (binary[j] == '0') {
            if (nOnes > 0) {
                encryptedString = encryptedString.plus("0 ${"0".repeat(nOnes)} ")
                nOnes = 0
            }
            nZeros++
        } else {
            if (nZeros != 0) {
                encryptedString = encryptedString.plus("00 ${"0".repeat(nZeros)} ")
                nZeros = 0
            }
            nOnes++
        }
    }
    if (nOnes > 0) {
        encryptedString = encryptedString.plus("0 ${"0".repeat(nOnes)} ")
    } else if (nZeros > 0) {
        encryptedString = encryptedString.plus("00 ${"0".repeat(nZeros)} ")
    }
    return encryptedString.removeSuffix(" ")
}

fun getBinaryString(encrypted: String): String {
    val values = encrypted.split(" ")
    var binary = ""
    var counter = 0
    for (i in values.indices) {
        if (i % 2 == 0 && i <= values.lastIndex - 1) {
            when (values[i]) {
                "0" -> {
                    counter = values[i + 1].length
                    binary = binary.plus("1".repeat(counter))
                }
                "00" -> {
                    counter = values[i + 1].length
                    binary = binary.plus("0".repeat(counter))
                }
            }
        }
    }
    return binary
}

/**
 * @param encrypted - the encrypted message
 * @return the original message
 */
fun decryptString(encrypted: String): String {
    var binary = getBinaryString(encrypted)
    var original = ""
    // now find the original message by splitting up the message into bits of 7 digits
    var temp: String
    while (binary.isNotEmpty()) {
        temp = if (binary.length >= 7) {
            binary.substring(0, 7)
        } else {
            binary
        }
        original = original.plus(Char(convertBinaryToInt(temp)))
        // now update the binary string by removing the front part
        binary = binary.removePrefix(temp)
    }
    return original
}

fun convertBinaryToInt(binaryStr: String): Int {
    // first reverse the string
    val reversed = binaryStr.reversed()
    var number = 0
    var power = 0.0
    for (i in reversed.indices) {
        if (reversed[i] == '1') {
            number += 2.0.pow(power).toInt()
        }
        power++
    }
    return number
}

fun isValid(message: String): Boolean {
    val encodedMessage = message.split(" ")
    var hasOnlyZeros = false
    // check if there are only zeros in the string
    val regex = Regex("^0+$") // regular expression for 1 or more zeros
    for (i in encodedMessage.indices) {
        if (regex.matches(encodedMessage[i])) {
            hasOnlyZeros = true
        } else {
            hasOnlyZeros = false
            break
        }
    }
    var valid = false
    // check if the string is formatted the right way, with 0 or 00 at even positions
    for (i in encodedMessage.indices) {
        if (i % 2 == 0) {
            if (encodedMessage[i] == "0" || encodedMessage[i] == "00") {
                valid = true
            } else {
                valid = false
                break
            }
        }
    }
    // should be even to be valid
    val isEven = encodedMessage.size % 2 == 0
    val binary = getBinaryString(message)
    return hasOnlyZeros && valid && isEven && (binary.length % 7 == 0)
}

fun main() {
    do {
        println("\nPlease input operation (encode/decode/exit):")
        val choice = readln()
        when (choice.lowercase()) {
            "encode" -> {
                println("Input string:")
                val input = readln()
                println("\nEncoded string:\n")
                println(encryptString(input).plus("\n"))
            }
            "decode" -> {
                var validInput: Boolean
                println("Input encoded string:")
                val encodedString = readln()
                validInput = isValid(encodedString)
                // do another check to make sure the string is valid
                if (validInput) {
                    println("\nDecoded string:")
                    println(decryptString(encodedString))
                } else {
                    println("Encoded string is not valid.\n")
                }
            }
            "exit" -> {
                println("Bye!")
                break
            }
            else -> println("There is no '$choice' operation")
        }
    } while (true)
}