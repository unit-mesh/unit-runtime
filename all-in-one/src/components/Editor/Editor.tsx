import MonacoEditor from "@monaco-editor/react";
import type { editor } from "monaco-editor";
import type { Monaco } from "@monaco-editor/react";

import { useEffect, useRef } from "react";

import Terminal from "./Terminal";

import { vueSfc } from "./vue-sfc";
import { svelteSfc } from "./svelte-sfc";
import getInstance from "@/app/bootWebContainer";
import { WebContainer } from "@webcontainer/api";

export type EditorProps = {
  input: string;
  language: string;
  file: string;
  webcontainer: WebContainer | null;
  onChange: (value: string) => void;
};
export default function Editor({
  input = "",
  language = "javascript",
  file = "",
  webcontainer,
  onChange = () => {},
}: EditorProps) {
  const monaco$ = useRef<Monaco | null>(null);
  const editor$ = useRef<editor.IStandaloneCodeEditor | null>(null);

  useEffect(() => {
    if (monaco$.current && editor$.current) {
      const model = monaco$.current.editor.createModel(
        input,
        language,
        monaco$.current.Uri.parse(file)
      );

      editor$.current.setModel(model);

      if (file.endsWith(".jsx") || file.endsWith(".tsx")) {
        monaco$.current.languages.typescript.typescriptDefaults.setCompilerOptions(
          {
            jsx: monaco$.current.languages.typescript.JsxEmit.React,
          }
        );
      }
    }
  }, [language, file]);

  useEffect(() => {
    if (monaco$.current) {
      monaco$.current.languages.register({ id: "vue-sfc" });
      monaco$.current.languages.setLanguageConfiguration("vue-sfc", {
        brackets: [["<", ">"]],
        autoClosingPairs: [
          { open: "<", close: ">" },
          { open: '"', close: '"' },
          { open: "'", close: "'" },
          { open: "`", close: "`" },
        ],
      });
      monaco$.current.languages.setTokensProvider("vue-sfc", vueSfc);

      monaco$.current.languages.register({ id: "svelte-sfc" });

      monaco$.current.languages.setLanguageConfiguration("svelte-sfc", {
        brackets: [["<", ">"]],
        autoClosingPairs: [
          { open: "<", close: ">" },
          { open: '"', close: '"' },
          { open: "'", close: "'" },
          { open: "`", close: "`" },
        ],
      });

      monaco$.current.languages.setTokensProvider("svelte-sfc", svelteSfc);
    }
  }, [monaco$.current !== null]);

  const onInputChange = (value: string | undefined) => {
    onChange(value ?? "");
  };

  return (
    <div className="relative grid grid-cols-[3fr_2fr] h-full overflow-hidden">
      <MonacoEditor
        onMount={(editor, monaco) => {
          editor$.current = editor;
          monaco$.current = monaco;
        }}
        options={{
          fontSize: 16,
          minimap: { enabled: true },
        }}
        language={language}
        value={input}
        onChange={onInputChange}
      />
      <div className="relative h-full overflow-hidden grid grid-rows-[2fr_1fr]">
        <MonacoEditor
          options={{
            fontSize: 16,
            minimap: { enabled: false },
          }}
          language="javascript"
        />

        <Terminal className="pb-[20px]" webcontainer={webcontainer} />
      </div>
    </div>
  );
}
