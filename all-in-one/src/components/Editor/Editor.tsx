"use client";

import "client-only";

import MonacoEditor, {
  DiffEditor,
  useMonaco,
  loader,
} from "@monaco-editor/react";

export default function Editor() {
  return <MonacoEditor />;
}
