# 

## Application should not be `final`, so we use `open`

```
@SpringBootApplication
open class KotlinDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinDemoApplication::class.java, *args)
}

main(arrayOf<String>())
```