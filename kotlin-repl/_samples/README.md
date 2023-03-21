# Sample Prompt


## Ktor

我要实现一个用户注册的功能，请帮我使用 Ktor 和 MySQL JDBC，实现这个 RESTful API：

1. 帮我设计一个 API，需要考虑 JSON 格式的输入和输出数据，放在代码的注释里，格式如后面的代码。
2. 我使用的是 Serverless 数据库，请使用 `{DATABASE_URL}` 作为数据库连接字符串。
3. 你只返回给我一个 Server 类，我会自动部署到 AWS Lambda 上
4. 最后，你只返回给我代码，代码格式如下：

```kotlin
/** 
 * features: User Registration
 * POST /register
 * input: { "username": "user", "password": "password" }
 * output: { "id": 1, "username": "user" }
 */
class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
        }
    }
}
```

明白吗？明白就开始吧？

## H2

我要实现一个用户注册的功能，请帮我使用 Ktor、Exposed 实现这个 RESTful API，要求如下：

1. 帮我设计一个 API，需要考虑 JSON 格式的输入和输出数据，放在代码的注释里，格式如后面的代码。
2. 你只返回给我一个 Server 类，我会自动部署到 AWS Lambda 上
3. 最后，你只返回给我代码，代码格式如下：

```kotlin
/** 
 * features: User Registration
 * POST /register
 * input: { "username": "user", "password": "password" }
 * output: { "id": 1, "username": "user" }
 */
class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
        }
    }
}
```

明白吗？明白就开始吧？
