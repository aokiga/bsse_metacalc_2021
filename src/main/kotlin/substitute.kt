fun Expr.substitute(env: Env): Expr =
    when(this) {
        is Value -> this
        is Variable -> env.getOrElse(this) { this }
        is UnaryOp -> this.substitute(env)
        is BinaryOp -> this.substitute(env)
    }

fun UnaryOp.substitute(env: Env): Expr {
    val res = x.substitute(env)
    return when (this) {
        is Sin -> Sin(res)
        is Cos -> Cos(res)
        is Exp -> Exp(res)
    }
}

fun BinaryOp.substitute(env: Env): Expr {
    val resX = x.substitute(env)
    val resY = y.substitute(env)
    return when (this) {
        is Plus -> Plus(resX, resY)
        is Minus -> Minus(resX, resY)
        is Multiply -> Multiply(resX, resY)
        is Pow -> Pow(resX, resY)
        is Divide -> Divide(resX, resY)
    }
}