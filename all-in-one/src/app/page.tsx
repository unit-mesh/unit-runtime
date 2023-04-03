"use client";
import "client-only";

import TopPanel from "@/components/TopPanel/TopPanel";
import Editor from "@/components/Editor/Editor";
import { useEffect, useState } from "react";

import getInstance, { initFs, destroyInstance } from "./bootWebContainer";
import FileTree from "@/components/Editor/FileTree";
import { WebContainer } from "@webcontainer/api";

export default function Home() {
  const [framework, setFramework] = useState("React");
  const [webcontainer, setWebcontainer] = useState<WebContainer | null>(null);

  useEffect(() => {
    (async () => {
      console.log("#################################");
      const webcontainer = await getInstance();
      console.log("??????????????????????", webcontainer);
      setWebcontainer(webcontainer);
    })();

    return () => {
      destroyInstance();
    };
  }, []);

  const [input, setInput] = useState("");
  useEffect(() => {
    console.log("input changed", input);
  }, [input]);

  useEffect(() => {
    (async () => {
      console.log("framework changed", framework);
      const result = await fetch(
        "/api/bootfs?framework=" + framework.toLowerCase()
      ).then((it) => it.json());
      console.log("??", webcontainer);
      if (webcontainer) {
        console.log("init fs");
        await initFs(webcontainer, result);

        const r = await webcontainer?.fs.readdir("/tmp/scratch");
        console.log("re", r);
      }
    })();
  }, [framework, webcontainer]);

  return (
    <main className="h-full">
      <TopPanel framework={framework} onFrameworkSelected={setFramework} />
      <div className="grid grid-rows-1 grid-cols-[200px_1fr] h-[calc(100%-50px)] w-full">
        <FileTree items={[]} />
        <Editor framework={framework} input={input} onChange={setInput} />
      </div>
    </main>
  );
}
