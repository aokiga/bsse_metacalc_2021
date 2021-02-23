import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

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

fun eval(expr: Expr, env: Env): Expr =
    when(expr) {
        is Value -> expr
        is Variable -> env.getOrElse(expr) { return expr }
        is UnaryOp -> evalUnaryOp(expr, env)
        is BinaryOp -> evalBinaryOp(expr, env)
    }

fun evalBinaryOp(binaryOp: BinaryOp, env: Env): Expr {
    val x: Expr = eval(binaryOp.x, env)
    val y: Expr = eval(binaryOp.y, env)
    return when(binaryOp) {
        is Plus -> evalPlus(x, y)
        is Minus -> evalMinus(x, y)
        is Multiply -> evalMultiply(x, y)
        is Divide -> evalDivide(x, y)
        is Pow -> evalPow(x, y)
    }
}

fun evalUnaryOp(unaryOp: UnaryOp, env: Env): Expr {
    val x = eval(unaryOp.x, env)
    return when(unaryOp) {
        is Sin -> evalSin(x)
        is Cos -> evalCos(x)
        is Exp -> evalExp(x)
    }
}

fun evalPlus(x: Expr, y: Expr): Expr =
    when {
        (x is Value && y is Value) -> Value(x.value + y.value)
        (x is Value && x.value == 0.0) -> y
        (y is Value && y.value == 0.0) -> x
        else -> Plus(x, y)
    }

fun evalMinus(x: Expr, y: Expr): Expr =
    when {
        (x is Value && y is Value) -> Value(x.value - y.value)
        (y is Value && y.value == 0.0) -> x
        else -> Minus(x, y)
    }

fun evalMultiply(x: Expr, y: Expr): Expr =
    when {
        (x is Value && y is Value) -> Value(x.value * y.value)
        ((x is Value && x.value == 0.0) || (y is Value && y.value == 0.0)) -> Value(0.0)
        (x is Value && x.value == 1.0) -> y
        (y is Value && y.value == 1.0) -> x
        else -> Multiply(x, y)
    }

fun evalDivide(x: Expr, y: Expr): Expr =
    when {
        (x is Value && y is Value) -> Value(x.value / y.value)
        (x is Value && x.value == 0.0) -> Value(0.0)
        (y is Value && y.value == 0.0) -> throw ArithmeticException("Division by zero")
        (y is Value && y.value == 1.0) -> x
        else -> Divide(x, y)
    }

fun evalPow(x: Expr, y: Expr): Expr =
    when {
        (x is Value && y is Value) -> Value(x.value.pow(y.value))
        (x is Value && (x.value == 0.0 || x.value == 1.0)) -> Value(x.value)
        (y is Value && y.value == 0.0) -> Value(1.0)
        (y is Value && y.value == 1.0) -> x
        else -> Pow(x, y)
    }

fun evalSin(x: Expr): Expr =
    when {
        (x is Value) -> Value(sin(x.value))
        else -> Sin(x)
    }

fun evalCos(x: Expr): Expr =
    when {
        (x is Value) -> Value(cos(x.value))
        else -> Cos(x)
    }

fun evalExp(x: Expr): Expr =
    when {
        (x is Value) -> Value(exp(x.value))
        else -> Exp(x)
    }

fun diff(expr: Expr, variable: Variable): Expr = eval(diffExpr(expr, variable), emptyEnv)

fun diffExpr(expr: Expr, variable: Variable) =
    when(expr) {
        is Value -> Value(0.0)
        is Variable ->
            if (expr.name == variable.name) {
                Value(1.0)
            } else {
                Value(0.0)
            }
        is BinaryOp -> diffBinaryOp(expr, variable)
        is UnaryOp -> diffUnaryOp(expr, variable)
    }

fun diffBinaryOp(expr: BinaryOp, variable: Variable): Expr =
    when(expr) {
        is Plus -> diffPlus(expr, variable)
        is Minus -> diffMinus(expr, variable)
        is Multiply -> diffMultiply(expr, variable)
        is Divide -> diffDivide(expr, variable)
        is Pow -> diffPow(expr, variable)
    }

fun diffUnaryOp(expr: UnaryOp, variable: Variable): Expr =
    when(expr) {
        is Sin -> diffSin(expr, variable)
        is Cos -> diffCos(expr, variable)
        is Exp -> diffExp(expr, variable)
    }

fun diffPlus(expr: Plus, variable: Variable): Expr =
    Plus(
        diffExpr(expr.x, variable),
        diffExpr(expr.y, variable)
    )

fun diffMinus(expr: Minus, variable: Variable): Expr =
    Minus(
        diffExpr(expr.x, variable),
        diffExpr(expr.y, variable)
    )

fun diffMultiply(expr: Multiply, variable: Variable): Expr =
    Plus(
        Multiply(expr.x, diffExpr(expr.y, variable)),
        Multiply(expr.y, diffExpr(expr.x, variable))
    )

fun diffDivide(expr: Divide, variable: Variable): Expr =
    Divide(
        Minus(
            Multiply(diffExpr(expr.x, variable), expr.y),
            Multiply(diffExpr(expr.y, variable), expr.x)
        ),
        Pow(expr.y, Value(2.0))
    )

fun diffPow(expr: Pow, variable: Variable): Expr =
    Multiply(
        Multiply(
            expr.y,
            Pow(
                expr.x,
                Minus(expr.y, Value(1.0))
            )
        ),
        diffExpr(expr.x, variable)
    )

fun diffSin(expr: Sin, variable: Variable): Expr =
    Multiply(
        Cos(expr.x),
        diffExpr(expr.x, variable)
    )

fun diffCos(expr: Cos, variable: Variable): Expr =
    Minus(
        Value(0.0),
        Multiply(
            Sin(expr.x),
            diffExpr(expr.x, variable)
        )
    )

fun diffExp(expr: Exp, variable: Variable): Expr =
    Multiply(
        expr,
        diffExpr(expr.x, variable)
    )

fun main() {

}