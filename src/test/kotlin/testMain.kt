import org.junit.Test
import kotlin.math.exp
import kotlin.math.sin
import kotlin.test.junit.JUnitAsserter.assertEquals

class TestEval {
    @Test
    fun test1() {
        val expected = Value(sin(2.0))
        val actual = eval(
            Plus(
                Multiply(
                    Value(0.0),
                    Variable("x")
                ),
                Multiply(
                    Value(1.0),
                    Sin(Value(2.0))
                )
            ),
            emptyEnv
        )
        assertEquals(
            expected = expected,
            actual = actual,
            message = "Expected: $expected\nActual: $actual"
        )
    }

    @Test
    fun test2() {
        val expected = Cos(Variable("y"))
        val actual = eval(
            Plus(
                Multiply(
                    Value(0.0),
                    Variable("x")
                ),
                Multiply(
                    Value(1.0),
                    Cos(Variable("y"))
                )
            ),
            emptyEnv
        )
        assertEquals(
            expected = expected,
            actual = actual,
            message = "Expected: $expected\nActual: $actual"
        )
    }

    @Test
    fun test3() {
        val expected = Value(exp(2.0))
        val env = mapOf(
            Variable("y") to Value(2.0)
        )
        val actual = eval(
            Plus(
                Multiply(
                    Value(0.0),
                    Variable("x")
                ),
                Multiply(
                    Value(1.0),
                    Exp(Variable("y"))
                )
            ),
            env
        )
        assertEquals(
            expected = expected,
            actual = actual,
            message = "Expected: $expected\nActual: $actual"
        )
    }
}


class TestDiff {
    @Test
    fun test1() {
        val expected = Exp(Variable("y"))
        val actual = diff(
            Plus(
                Multiply(
                    Value(0.0),
                    Variable("x")
                ),
                Multiply(
                    Value(1.0),
                    Exp(Variable("y"))
                )
            ),
            Variable("y")
        )
        assertEquals(
            expected = expected,
            actual = actual,
            message = "Expected: $expected\nActual: $actual"
        )
    }
}