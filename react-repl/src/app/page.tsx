"use client";

import styles from "./page.module.css";
import { useDeferredValue, useEffect, useRef, useState } from "react";
import { CodeEditor } from "@/app/components/editor/CodeEditor";

import initSwc, { transformSync } from "@swc/wasm-web";


export default function Home() {
  const iframe$ = useRef<HTMLIFrameElement | null>(null);

  const [code, setCode] = useState(`import React from "react";
import {createRoot} from "react-dom";

function Root() {
  const [tick, setTick] = useState<>(0);

  useEffect(() => {
    const id = setInterval(() => {
      setTick(it => it+1);
    });
    
    return () => clearInterval(id);
  }, []);

  return <>
    Hello world #{tick}
  </>;
}

const rootDom = document.createElement("div");
document.body.append(rootDom);
const root = createRoot(root)
root.render()
`);

  const deferredCode = useDeferredValue(code);
  const [compiled, setCompiled] = useState("");

  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    async function importAndRunSwcOnMount() {
      await initSwc();
      setInitialized(true);
    }
    importAndRunSwcOnMount();
  }, []);

  function compile() {
    if (!initialized) {
      return;
    }
    try {
      const result = transformSync(deferredCode, {
        jsc: {
          parser: {
            syntax: "typescript",
            tsx: true,
            decorators: false,
            dynamicImport: false,
          },
        },
        // module: {
        //   type: "umd",
        //   globals: {
        //     react: "",
        //     "react-dom": "",
        //   },
        //   ignoreDynamic: true,
        //   importInterop: "swc",
        // },
      });
      setCompiled(result.code);
      return result.code;
    } catch (e) {
      console.log(e);
      return null;
    }
  }

  useEffect(() => {
    const code = compile();

    if (iframe$.current && code) {
      const ifr = iframe$.current;
      const script = document.createElement("script");
      script.type = "module";
      script.innerHTML = code;

      ifr?.contentDocument?.body.append(script);

      return () => {
        script.remove();
      };
    }
  }, [deferredCode, initialized]);

  return (
    <main className={styles.main}>
      <div className={styles.description}>
        <CodeEditor
          value={code}
          extensions={[]}
          onChange={(newCode) => {
            setCode(newCode);
          }}
        />

        <CodeEditor
          value={compiled}
          extensions={[]}
          onChange={(compiledCode) => {
            setCompiled(compiledCode);
          }}
        />

        <iframe ref={iframe$} style={{ gridColumn: "1 / span 2" }}></iframe>
      </div>
    </main>
  );
}
