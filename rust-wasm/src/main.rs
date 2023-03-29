use base64::Engine;
use base64::alphabet::Alphabet;
use wasmer::{ Store, Module, imports, Instance, Value };

use actix::{Actor, StreamHandler};
use actix_web::{web, App, Error, HttpRequest, HttpResponse, HttpServer};
use actix_web_actors::ws;
use serde::{Serialize, Deserialize};
use base64::engine::general_purpose::STANDARD;

/// Define HTTP actor
struct MyWs;

impl Actor for MyWs {
    type Context = ws::WebsocketContext<Self>;
}

#[derive(Deserialize, Clone)]
struct MessageInput {
  id: u32,
  code: String,
  entry_point: String,
  language: String,
  framework: String,
  history: bool,
}

#[derive(Serialize, Clone)]
struct MessageOutput {
  id: u32,
  msgType: String,
  #[serde(skip_serializing_if = "Option::is_none")]
  result: Option<String>,
  #[serde(skip_serializing_if = "Option::is_none")]
  error: Option<String>,
}

/// Handler for ws::Message message
impl StreamHandler<Result<ws::Message, ws::ProtocolError>> for MyWs {
    fn handle(&mut self, msg: Result<ws::Message, ws::ProtocolError>, ctx: &mut Self::Context) {
      let engine = STANDARD;

        match msg {
            Ok(ws::Message::Ping(msg)) => ctx.pong(&msg),
            Ok(ws::Message::Text(text)) => {
                let input: MessageInput = serde_json::from_str(&text).unwrap();
                let code = match engine.decode(input.code){
                  Ok(code) => code,
                  Err(e) =>  {
                    let e = MessageOutput {
                      id: input.id,
                      msgType: "rust_wasm".to_string(),
                      result: None,
                      error: Some(e.to_string()),
                    };

                    ctx.text(serde_json::to_string(&e).unwrap());
                    return;
                  },
                }; 
                let result = match run(&code, &input.entry_point){
                  Ok(result) => result,
                  Err(e) => {
                    let e = MessageOutput {
                      id: input.id,
                      msgType: "rust_wasm".to_string(),
                      result: None,
                      error: Some(e.to_string()),
                    };

                    ctx.text(serde_json::to_string(&e).unwrap());
                    return;
                  },
                };

                let result = MessageOutput {
                  id: input.id,
                  msgType: "rust_wasm".to_string(),
                  result: Some(result[0].to_string()),
                  error: None,
                };

                ctx.text(serde_json::to_string(&result).unwrap());
            },
            Ok(ws::Message::Binary(_bin_)) => ctx.text("not supported"),
            _ => (),
        }
    }
}

async fn index(req: HttpRequest, stream: web::Payload) -> Result<HttpResponse, Error> {
    let resp = ws::start(MyWs {}, &req, stream);
    println!("{:?}", resp);
    resp
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    HttpServer::new(|| App::new().route("/ws/", web::get().to(index)))
        .bind(("127.0.0.1", 8080))?
        .run()
        .await
}

fn run(module: &[u8], entry_point: &str) -> anyhow::Result<Box<[Value]>> {
    // let module_wat = r#"
    // (module
    //   (type $t0 (func (param i32) (result i32)))
    //   (func $add_one (export "add_one") (type $t0) (param $p0 i32) (result i32)
    //     get_local $p0
    //     i32.const 1
    //     i32.add))
    // "#;

    let mut store = Store::default();
    let module = Module::new(&store, &module)?;
    // The module doesn't import anything, so we create an empty import object.
    let import_object = imports! {};
    let instance = Instance::new(&mut store, &module, &import_object)?;

    let add_one = instance.exports.get_function(entry_point)?;
    let result = add_one.call(&mut store, &[])?;

    Ok(result)
}
