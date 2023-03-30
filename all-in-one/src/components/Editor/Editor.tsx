"use client";

import "client-only";

import MonacoEditor, {
  DiffEditor,
  useMonaco,
  loader,
} from "@monaco-editor/react";
import { useEffect } from "react";

import { vueSfc } from "./vue-sfc";
import { svelteSfc } from "./svelte-sfc";

const mapFrameworkToLanguage = {
  React: "typescript",
  Vue: "vue-sfc",
  Svelte: "svelte-sfc",
};

export default function Editor({ framework = "React" }: { framework: string }) {
  const monaco = useMonaco();

  useEffect(() => {
    if (monaco) {
      monaco.languages.register({ id: "vue-sfc" });
      monaco.languages.setLanguageConfiguration("vue-sfc", {
        brackets: [["<", ">"]],
        autoClosingPairs: [
          { open: "<", close: ">" },
          { open: '"', close: '"' },
          { open: "'", close: "'" },
          { open: "`", close: "`" },
        ],
      });
      monaco.languages.setTokensProvider("vue-sfc", vueSfc);

      monaco.languages.register({ id: "svelte-sfc" });

      monaco.languages.setLanguageConfiguration("svelte-sfc", {
        brackets: [["<", ">"]],
        autoClosingPairs: [
          { open: "<", close: ">" },
          { open: '"', close: '"' },
          { open: "'", close: "'" },
          { open: "`", close: "`" },
        ],
      });

      monaco.languages.setTokensProvider("svelte-sfc", svelteSfc);
    }
  }, [monaco !== null]);

  return (
    <MonacoEditor
      width="100%"
      height="100%"
      language={
        mapFrameworkToLanguage[
          framework as keyof typeof mapFrameworkToLanguage
        ] ?? "typescript"
      }
    />
  );
}
