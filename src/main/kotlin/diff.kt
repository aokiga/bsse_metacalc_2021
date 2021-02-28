fun Expr.diff(variable: Variable) =
    when(this) {
        is Value -> Value(0.0)
        is Variable ->
            if (name == variable.name) {
                Value(1.0)
            } else {
                Value(0.0)
            }
        is BinaryOp -> diff(variable)
        is UnaryOp -> diff(variable)
    }

// ugly :(
fun BinaryOp.diff(variable: Variable): Expr =
    when(this) {
        is Plus -> diff(variable)
        is Minus -> diff(variable)
        is Multiply -> diff(variable)
        is Divide -> diff(variable)
        is Pow -> diff(variable)
    }

fun UnaryOp.diff(variable: Variable): Expr =
    when(this) {
        is Sin -> diff(variable)
        is Cos -> diff(variable)
        is Exp -> diff(variable)
    }

fun Plus.diff(variable: Variable): Expr =
    Plus(
        x.diff(variable),
        y.diff(variable),
    )

fun Minus.diff(variable: Variable): Expr =
    Minus(
        x.diff(variable),
        y.diff(variable)
    )

fun Multiply.diff(variable: Variable): Expr =
    Plus(
        Multiply(x, y.diff(variable)),
        Multiply(y, x.diff(variable))
    )

fun Divide.diff(variable: Variable): Expr =
    Divide(
        Minus(
            Multiply(x.diff(variable), y),
            Multiply(y.diff(variable), x)
        ),
        Pow(y, Value(2.0))
    )

fun Pow.diff(variable: Variable): Expr =
    Multiply(
        Multiply(
            y,
            Pow(
                x,
                Minus(y, Value(1.0))
            )
        ),
        x.diff(variable)
    )

fun Sin.diff(variable: Variable): Expr =
    Multiply(
        Cos(x),
        x.diff(variable)
    )

fun Cos.diff(variable: Variable): Expr =
    Minus(
        Value(0.0),
        Multiply(
            Sin(x),
            x.diff(variable)
        )
    )

fun Exp.diff(variable: Variable): Expr =
    Multiply(
        this,
        x.diff(variable)
    )
