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
import { WebContainer, WebContainerProcess } from "@webcontainer/api";

const languageMap = new Map<string, string>([
  ["html", "html"],
  ["css", "css"],
  ["js", "javascript"],
  ["ts", "typescript"],
  ["vue", "vue-sfc"],
  ["svelte", "svelte-sfc"],
  ["jsx", "javascript"],
  ["tsx", "typescript"],
]);

export default function Home() {
  const [webcontainer, setWebcontainer] = useState<WebContainer | null>(null);
  const [framework, setFramework] = useState("React");

  const [files, setFiles] = useState<FileTreeItem[]>([]);
  const [currentFile, setCurrentFile] = useState<FileTreeItem | null>(null);
  const [currentLanguage, setCurrentLanguage] = useState("javascript");

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
      <div className="grid grid-rows-1 grid-cols-[200px_1fr] h-[calc(100%-3rem)] w-full">
        <FileTree
          items={files}
          refresh={() => {
            refreshTree().then(setFiles);
          }}
          downloadDeps={() => installDeps()}
          onSelected={(item) => {
            if (item.type === "file") {
              webcontainer?.fs.readFile(item.path, "utf-8").then(setInput);
              setCurrentFile(item);

              const file = item.name;
              for (const [ext, lang] of languageMap) {
                if (file.endsWith("." + ext)) {
                  setCurrentLanguage(lang);
                  break;
                }
              }
            }
          }}
        />
        <Editor
          input={input}
          onChange={setInput}
          language={currentLanguage}
          file={currentFile}
          webcontainer={webcontainer}
        />
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
  const result = await listDir("/tmp/scratch");
  console.log("listDir result", result);
  return result;
}

async function installDeps() {
  const webcontainer = await getInstance();

  const x = async (
    webcontainer: WebContainer,
    command: string,
    args: string[] = []
  ) => {
    console.log("running", command, args);
    const process = await webcontainer.spawn(command, args, {
      output: true,
      terminal: {
        cols: 80,
        rows: 32,
      },
    });
    const reader = process.output.pipeTo(
      new WritableStream({
        write(chunk) {
          console.log("[Output] ", chunk);
        },
      })
    );

    await process.kill();
  };

  console.log("starting install deps");

  if (!webcontainer) return;

  await x(webcontainer, "cd", ["./tmp/scratch"]);
  await x(webcontainer, "ls", ["/usr/local/bin"]);

  // await x(webcontainer, "ls");
  // await x(webcontainer, "npm", ["install"]);
}
