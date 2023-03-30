"use client";
import "client-only";

import TopPanel from "@/components/TopPanel/TopPanel";
import Editor from "@/components/Editor/Editor";
import { useState } from "react";

const initializeWebContainer = () => {
  console.log("initializeWebContainer");
  import("./bootWebContainer");
};
if (typeof window !== "undefined") {
  initializeWebContainer();
}

export default function Home() {
  const [framework, setFramework] = useState("React");

  return (
    <main className="h-full">
      <TopPanel framework={framework} onFrameworkSelected={setFramework} />
      <Editor framework={framework} />
    </main>
  );
}
