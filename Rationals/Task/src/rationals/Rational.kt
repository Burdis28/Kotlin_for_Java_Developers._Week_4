package rationals

import java.math.BigInteger

fun greatestCommonDivisor(a: BigInteger, b: BigInteger): BigInteger {
    return if (b == BigInteger.ZERO) a else greatestCommonDivisor(b, a % b)
}

@SuppressWarnings("")
data class Rational
    private constructor(val numerator: BigInteger,val denominator: BigInteger): Comparable<Rational> {
        companion object {
            fun create(numerator: BigInteger, denominator: BigInteger): Rational = reduceToLowestTerms(numerator, denominator)

            private fun reduceToLowestTerms(numerator: BigInteger, denominator: BigInteger): Rational {
                if (BigInteger.ZERO.equals(denominator))
                    throw IllegalArgumentException()

                val shouldBeNegative = numerator < BigInteger.ZERO
                val gcdValue = greatestCommonDivisor(numerator.abs(), denominator.abs())
                val (reducedNumerator, reducedDenominator) = Pair(numerator / gcdValue, denominator / gcdValue)

                val finalNumerator =
                    if (shouldBeNegative && reducedNumerator > BigInteger.ZERO) reducedNumerator.negate()
                    else reducedNumerator

                return Rational(finalNumerator, reducedDenominator.abs())
            }
        }

    operator fun unaryMinus(): Rational {
        return create(numerator.negate(), denominator)
    }

    operator fun plus(adder: Rational): Rational {
        val commonDenominator = this.denominator * adder.denominator
        val sumNumerator = this.numerator * adder.denominator + adder.numerator * this.denominator

        return create(sumNumerator, commonDenominator)
    }

    operator fun minus(subtracter: Rational): Rational {
        val commonDenominator = this.denominator * subtracter.denominator
        val differenceNumerator = this.numerator * subtracter.denominator - subtracter.numerator * this.denominator

        return create(differenceNumerator, commonDenominator)
    }

    operator fun div(divider: Rational): Rational {
        return create(this.numerator * divider.denominator, this.denominator * divider.numerator)
    }

    operator fun times(timer: Rational): Rational {
        return create(this.numerator * timer.numerator, this.denominator * timer.denominator)
    }


    operator fun rangeTo(end: Rational): RationalRange {
        return RationalRange(this, end)
    }

    override operator fun compareTo(other: Rational): Int {
        val first = this.numerator * other.denominator
        val second = other.numerator * this.denominator

        return first.compareTo(second)
    }

    override fun toString(): String {
        if (denominator == BigInteger.ONE)
            return "$numerator"

        return "$numerator/$denominator"
    }
}

data class RationalRange(val start: Rational, val end: Rational)

operator fun RationalRange.contains(value: Rational): Boolean {
    return value >= start && value <= end
}


fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}

fun String.toRational(): Rational {
    val (numeratorPart, denominatorPart) = this.split('/').let {
        when (it.size) {
            1 -> Pair(it[0], "1")
            2 -> Pair(it[0], it[1])
            else -> throw IllegalArgumentException("Invalid input format")
        }
    }

    val isNegated = (numeratorPart.startsWith("-") || denominatorPart.startsWith("-")) &&
            !(numeratorPart.startsWith("-") && denominatorPart.startsWith("-"))

    val finalNumerator = numeratorPart.removePrefix("-").toBigInteger().let {
        if (isNegated) it.negate() else it
    }
    val finalDenominator = denominatorPart.removePrefix("-").toBigInteger()

    return Rational.create(finalNumerator, finalDenominator)
}

infix fun Int.divBy(denominator: Int): Rational {
    return Rational.create(this.toBigInteger(), denominator.toBigInteger())

}

infix fun Long.divBy(denominator: Long): Rational {
    return Rational.create(this.toBigInteger(), denominator.toBigInteger())

}

infix fun BigInteger.divBy(denominator: BigInteger): Rational {
    return Rational.create(this, denominator)
}
