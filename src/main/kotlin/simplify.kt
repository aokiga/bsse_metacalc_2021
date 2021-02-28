import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

fun Expr.simplify(): Expr =
    when (this) {
        is Value -> this
        is Variable -> this
        is UnaryOp -> simplify()
        is BinaryOp -> simplify()
    }

// these smart casts look ugly, i wonder how to make it look better
fun BinaryOp.simplify(): Expr =
    when(this) {
        is Plus -> simplify()
        is Minus -> simplify()
        is Multiply -> simplify()
        is Divide -> simplify()
        is Pow -> simplify()
    }

fun UnaryOp.simplify(): Expr =
    when(this) {
        is Sin -> simplify()
        is Cos -> simplify()
        is Exp -> simplify()
    }

fun Plus.simplify(): Expr {
    val x = x.simplify()
    val y = y.simplify()
    return when {
        ((x is Value) && (y is Value)) -> Value(x.value + y.value)
        (x is Value && x.value == 0.0) -> y
        (y is Value && y.value == 0.0) -> x
        else -> Plus(x, y)
    }
}

fun Minus.simplify(): Expr {
    val x = x.simplify()
    val y = y.simplify()
    return when {
        (x is Value && y is Value) -> Value(x.value - y.value)
        (y is Value && y.value == 0.0) -> x
        else -> Minus(x, y)
    }
}

fun Multiply.simplify(): Expr {
    val x = x.simplify()
    val y = y.simplify()
    return when {
        (x is Value && y is Value) -> Value(x.value * y.value)
        ((x is Value && x.value == 0.0) || (y is Value && y.value == 0.0)) -> Value(0.0)
        (x is Value && x.value == 1.0) -> y
        (y is Value && y.value == 1.0) -> x
        else -> Multiply(x, y)
    }
}

fun Divide.simplify(): Expr {
    val x = x.simplify()
    val y = y.simplify()
    return when {
        (x is Value && y is Value) -> Value(x.value / y.value)
        (x is Value && x.value == 0.0) -> Value(0.0)
        (y is Value && y.value == 0.0) -> throw ArithmeticException("Division by zero")
        (y is Value && y.value == 1.0) -> x
        else -> Divide(x, y)
    }
}

fun Pow.simplify(): Expr {
    val x = x.simplify()
    val y = y.simplify()
    return when {
        (x is Value && y is Value) -> Value(x.value.pow(y.value))
        (x is Value && (x.value == 0.0 || x.value == 1.0)) -> Value(x.value)
        (y is Value && y.value == 0.0) -> Value(1.0)
        (y is Value && y.value == 1.0) -> x
        else -> Pow(x, y)
    }
}

fun Sin.simplify(): Expr {
    val x = x.simplify()
    return when {
        (x is Value) -> Value(sin(x.value))
        else -> Sin(x)
    }
}

fun Cos.simplify(): Expr {
    val x = x.simplify()
    return when {
        (x is Value) -> Value(cos(x.value))
        else -> Cos(x)
    }
}

fun Exp.simplify(): Expr {
    val x = x.simplify()
    return when {
        (x is Value) -> Value(exp(x.value))
        else -> Exp(x)
    }
}
