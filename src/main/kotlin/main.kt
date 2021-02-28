sealed class Expr

data class Value(val value: Double): Expr()
data class Variable(val name: String): Expr()

sealed class BinaryOp(open val x: Expr, open val y: Expr): Expr()
data class Plus(override val x: Expr, override val y: Expr): BinaryOp(x, y)
data class Minus(override val x: Expr, override val y: Expr): BinaryOp(x, y)
data class Multiply(override val x: Expr, override val y: Expr): BinaryOp(x, y)
data class Divide(override val x: Expr, override val y: Expr): BinaryOp(x, y)
data class Pow(override val x: Expr, override val y: Expr): BinaryOp(x, y)

sealed class UnaryOp(open val x: Expr): Expr()
data class Sin(override val x: Expr): UnaryOp(x)
data class Cos(override val x: Expr): UnaryOp(x)
data class Exp(override val x: Expr): UnaryOp(x)

typealias Env = Map<Variable, Value>

val emptyEnv: Env = emptyMap()

fun eval(expr: Expr, env: Env): Expr = expr.substitute(env).simplify()

fun diff(expr: Expr, variable: Variable): Expr = expr.diff(variable).simplify()

fun main() {
    val actual = Plus(
            Multiply(
                Value(0.0),
                Variable("x")
            ),
            Multiply(
                Value(1.0),
                Sin(Value(2.0))
            )
        )
    val aaa = actual.simplify()
    println(actual)
    println(aaa)
}