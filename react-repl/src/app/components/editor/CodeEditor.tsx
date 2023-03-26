// based on https://www.codiga.io/blog/revisiting-codemirror-6-react-implementation/
import React from "react";
import { EditorView } from "codemirror";
import { ViewUpdate } from "@codemirror/view";
import { useCodeEditor } from "@/app/components/editor/UseCodeEditor";
import {
  OnChange,
  UseCodeEditorProps,
} from "@/app/components/editor/editor.types";

export function onUpdate(onChange: OnChange) {
  return EditorView.updateListener.of((viewUpdate: ViewUpdate) => {
    if (viewUpdate.docChanged) {
      const doc = viewUpdate.state.doc;
      const value = doc.toString();
      onChange(value, viewUpdate);
    }
  });
}

export function CodeEditor({
  value,
  onChange,
  extensions,
}: UseCodeEditorProps) {
  // wrapper for codemirror
  const ref = useCodeEditor({ value, onChange, extensions });
  return <div ref={ref as any} />;
}
