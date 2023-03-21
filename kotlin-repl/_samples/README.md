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

请帮我使用 Ktor + Kotlin + Exposed 实现一个用户注册的 RESTful API，要求如下：

1. 涉及到数据库的地方，请直接使用 Database.connect，示例：`Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")`。
2. 你只需要返回给我一个 Server 类，我会自动部署到 AWS Lambda 上
3. 

最后，你返回给我的内容，只包含代码，格式如下。请按示例代码返回：

```kotlin
%use kotless

class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        transaction {
            SchemaUtils.create(Users)
        }

        app.routing {
            get("/users") {
                val users = transaction {
                    Users.selectAll().map {
                        User(it[Users.id], it[Users.username])
                    }
                }
                call.respond(users)
            }
        }
    }
}
```

# Version 3

请帮我用 Ktor + Kotlin + Exposed 实现一个用户注册的 RESTful API 的核心部分，要求如下：

1. 涉及到数据库的地方，请直接使用 Database.connect
2. 不要导入包，不要返回任何的 `import` 语法，我会自动导入 
3. 你只需要返回给 Server 类，我会自动部署到 AWS Lambda 上

最后，只返回所有的类。代码格式如下：

```kotlin
class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        transaction {
            SchemaUtils.create(Users)
        }

        app.routing {
            get("/users") {
                val users = transaction {
                    Users.selectAll().map {
                        User(it[Users.id], it[Users.username])
                    }
                }
                call.respond(users)
            }
        }
    }
}
```
