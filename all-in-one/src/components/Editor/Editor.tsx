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

export default function Editor({
  framework = "React",
  input = "",
  onChange,
}: {
  framework: string;
  input: string;
  onChange: (value: string) => void;
}) {
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

  const onInputChange = (value: string | undefined) => {
    onChange(value ?? "");
  };

  return (
    <div className="relative grid grid-cols-[3fr_2fr] h-full">
      <MonacoEditor
        height="100%"
        options={{
          fontSize: 16,
          minimap: { enabled: true },
        }}
        value={input}
        onChange={onInputChange}
        language={
          mapFrameworkToLanguage[
            framework as keyof typeof mapFrameworkToLanguage
          ] ?? "typescript"
        }
      />
      <MonacoEditor
        height="100%"
        options={{
          fontSize: 16,
          minimap: { enabled: false },
        }}
        language="javascript"
      />
    </div>
  );
}
