"use client";
import "client-only";

import TopPanel from "@/components/TopPanel/TopPanel";
import Editor from "@/components/Editor/Editor";
import { useEffect, useState } from "react";

import getInstance, {
  initFs,
  destroyInstance,
  initInstance,
} from "./bootWebContainer";
import FileTree from "@/components/Editor/FileTree";
import { WebContainer } from "@webcontainer/api";

export default function Home() {
  const [webcontainer, setWebcontainer] = useState<WebContainer | null>(null);
  const [framework, setFramework] = useState("React");

  const [files, setFiles] = useState<FileTreeItem[]>([]);

  useEffect(() => {
    initInstance()
      .then((webcontainer) => {
        setWebcontainer(webcontainer);
      })
      .catch((e) => {});
  }, []);

  const [input, setInput] = useState("");
  useEffect(() => {
    console.log("input changed", input);
  }, [input]);

  useEffect(() => {
    (async () => {
      if (!webcontainer) return;

      console.log("framework changed", framework);
      const result = await fetch(
        "/api/bootfs?framework=" + framework.toLowerCase()
      ).then((it) => it.json());

      console.log("??", webcontainer);
      if (webcontainer) {
        console.log("init fs");
        await initFs(webcontainer, result);
        setFiles(await refreshTree());
      }
    })();
  }, [framework, webcontainer]);

  return (
    <main className="h-full">
      <TopPanel framework={framework} onFrameworkSelected={setFramework} />
      <div className="grid grid-rows-1 grid-cols-[200px_1fr] h-[calc(100%-50px)] w-full">
        <FileTree
          items={files}
          refresh={() => {
            refreshTree().then(setFiles);
          }}
          onSelected={(item) => {
            console.log("selected", item);
            if (item.type === "file") {
              webcontainer?.fs.readFile(item.path, "utf-8").then(setInput);
            }
          }}
        />
        <Editor framework={framework} input={input} onChange={setInput} />
      </div>
    </main>
  );
}

import type { FileTreeItem } from "@/components/Editor/FileTree";
async function refreshTree(): Promise<FileTreeItem[]> {
  const webcontainer = await getInstance();
  if (!webcontainer) return [];

  const listDir: (path: string) => Promise<FileTreeItem[]> = async (
    path: string
  ) => {
    const r = await webcontainer?.fs.readdir(path, {
      withFileTypes: true,
    });

    console.log("refresh", r);

    if (!r) return [];

    return Promise.all(
      r.map(async (it) => {
        const fullPath = path + "/" + it.name;
        if (it.isDirectory()) {
          return {
            path: fullPath,
            name: it.name,
            type: "folder",
            children: await listDir(fullPath),
          };
        } else {
          return {
            path: fullPath,
            name: it.name,
            type: "file",
          };
        }
      })
    );
  };
  return listDir("/tmp/scratch");
}
