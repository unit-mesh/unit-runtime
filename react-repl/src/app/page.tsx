"use client";

import styles from "./page.module.css";
import { useEffect, useState } from "react";
import { CodeEditor } from "@/app/components/editor/CodeEditor";

export default function Home() {
  const [code, setCode] =
    useState(`const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<h1>Hello, world!</h1>);`);
  const [compiled, setCompiled] = useState("");

  useEffect(() => {
    // transform(code, {
    //   presets: ["@babel/preset-env", "@babel/preset-react"],
    // });
    // console.log(code);
  }, [code]);

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
