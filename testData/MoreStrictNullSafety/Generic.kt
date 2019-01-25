class Generic<out A> {
    fun <B>map(f: (A) -> B): Generic<B> {
        return Generic<B>()
    }
    companion object {
        fun <A>from(a: A) = Generic<A>()
    }
}