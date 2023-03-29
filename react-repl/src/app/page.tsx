"use client";

import styles from "./page.module.css";
import { useDeferredValue, useEffect, useRef, useState } from "react";
import { CodeEditor } from "@/app/components/editor/CodeEditor";
import { BUNDLE_SCRIPTS, compile } from "@/../../common/compile";

export default function Home() {
  const iframe$ = useRef<HTMLIFrameElement | null>(null);

  const [code, setCode] =
    useState(`import React, {useState, useEffect} from "react";
import ReactDom, {createRoot} from "react-dom/client";

function Root() {
  const [tick, setTick] = useState<number>(0);

  useEffect(() => {
    const id = setInterval(() => {
      setTick(it => it+1);
    }, 1000);
    
    return () => clearInterval(id);
  }, []);

  return <>
    Hello world #{tick}
  </>;
}

const rootDom = document.createElement("div");
document.body.append(rootDom);
const root = createRoot(rootDom);
root.render(<Root />);
`);

  const deferredCode = useDeferredValue(code);
  const [compiled, setCompiled] = useState("");

  useEffect(() => {
    const code = compile("scratch.tsx", deferredCode, setCompiled);

    if (iframe$.current && code) {
      const ifr = iframe$.current;
      const reactLoaderScript = document.createElement("script");
      const reactDomLoaderScript = document.createElement("script");
      reactLoaderScript.src = BUNDLE_SCRIPTS.react;
      reactDomLoaderScript.src = BUNDLE_SCRIPTS.reactDom;

      const script = document.createElement("script");
      script.innerHTML = code;
      reactLoaderScript.onload = () => {
        reactDomLoaderScript.onload = () => {
          ifr?.contentDocument?.body.append(script);
        };

        ifr?.contentDocument?.body.append(reactDomLoaderScript);
      };
      ifr?.contentDocument?.body.append(reactLoaderScript);

      return () => {
        if (ifr?.contentDocument?.body) {
          ifr.contentDocument.body.innerHTML = "";
        }
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
