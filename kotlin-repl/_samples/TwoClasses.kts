package sample

class A() {}

class B() {}

println("sample")

val path = B::class.java.`package`.name
println("Kotlin: " + path)
