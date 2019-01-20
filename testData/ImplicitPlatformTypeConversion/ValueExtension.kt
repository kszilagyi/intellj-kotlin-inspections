operator fun Value.plus(other: Value): Value = Value()

operator fun Value?.minus(other: Value): Value = Value()

operator fun Value.times(other: Value?): Value = Value()
