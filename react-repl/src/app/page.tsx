"use client";

import styles from "./page.module.css";
import { useState } from "react";
import { CodeEditor } from "@/app/components/CodeEditor";

export default function Home() {
  const [code, setCode] = useState("console.log('hello, world')");

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
      </div>
    </main>
  );
}
