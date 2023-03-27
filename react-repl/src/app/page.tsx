"use client";

import styles from "./page.module.css";
import { useDeferredValue, useEffect, useState } from "react";
import { CodeEditor } from "@/app/components/editor/CodeEditor";

import initSwc, { transformSync } from "@swc/wasm-web";

export default function Home() {
  const [code, setCode] = useState(`function Root() {
  return <>Hello world!</>;
}`);

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
    const result = transformSync(deferredCode, {
      jsc: {
        parser: {
          syntax: "typescript",
          tsx: true,
          decorators: false,
          dynamicImport: false,
        },
      },
    });
    setCompiled(result.code);
  }

  useEffect(() => {
    compile();
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
      </div>
    </main>
  );
}
