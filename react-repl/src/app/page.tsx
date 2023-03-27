"use client";

import styles from "./page.module.css";
import { useDeferredValue, useEffect, useRef, useState } from "react";
import { CodeEditor } from "@/app/components/editor/CodeEditor";

import { transform } from "@babel/standalone";

export default function Home() {
  const iframe$ = useRef<HTMLIFrameElement | null>(null);

  const [code, setCode] = useState(`import React from "react";
import {createRoot} from "react-dom";

function Root() {
  const [tick, setTick] = useState<number>(0);

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

  function compile() {
    try {
      const code = transform(deferredCode, {
        presets: ["env", "typescript", "react"],
        plugins: [
          [
            "transform-modules-umd",
            {
              globals: {
                react: "React",
                "react-dom": "ReactDom",
              },
              exactGlobals: true,
            },
          ],
        ],
        filename: "e.tsx",
      }).code;
      setCompiled(code ?? "");

      return code;
    } catch (e) {
      console.log(e);
      return null;
    }
  }

  useEffect(() => {
    const code = compile();
    console.log("code in effect: ", code);

    if (iframe$.current && code) {
      const ifr = iframe$.current;
      const reactLoaderScript = document.createElement("script");
      const reactDomLoaderScript = document.createElement("script");
      reactLoaderScript.src =
        "https://cdn.jsdelivr.net/npm/react@18.2.0/umd/react.production.min.js";
      reactDomLoaderScript.src =
        "https://cdn.jsdelivr.net/npm/react-dom@18.2.0/index.min.js";

      ifr?.contentDocument?.body.append(
        reactLoaderScript,
        reactDomLoaderScript
      );

      const script = document.createElement("script");
      script.innerHTML = code;

      ifr?.contentDocument?.body.append(script);

      return () => {
        script.remove();
      };
    }
  }, [deferredCode]);

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
