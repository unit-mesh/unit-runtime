import React, { useEffect, useRef } from "react";

// based on https://www.codiga.io/blog/revisiting-codemirror-6-react-implementation/

import { useState } from "react";
import { EditorView, basicSetup } from "codemirror";
import { javascript } from "@codemirror/lang-javascript";
import { Extension } from "@codemirror/state";
import { ViewUpdate } from "@codemirror/view";

export function useCodeMirror(extensions: Extension[]) {
  const ref = useRef();
  const [view, setView] = useState();

  useEffect(() => {
    if (!ref.current) return;

    const view = new EditorView({
      extensions: [
        basicSetup,
        /**
         * Check each language package to see what they support,
         * for instance javascript can use typescript and jsx.
         */
        javascript({
          jsx: true,
          typescript: true,
        }),
        ...extensions,
      ],
      parent: ref.current as any,
    });

    setView(view as any);

    /**
     * Make sure to destroy the codemirror instance
     * when our components are unmounted.
     */
    return () => {
      view.destroy();
      setView(undefined);
    };
  }, []);

  return { ref, view };
}

type OnChange = (value: string, viewUpdate: ViewUpdate) => void;

export function onUpdate(onChange: OnChange) {
  return EditorView.updateListener.of((viewUpdate: ViewUpdate) => {
    if (viewUpdate.docChanged) {
      const doc = viewUpdate.state.doc;
      const value = doc.toString();
      onChange(value, viewUpdate);
    }
  });
}

export type UseCodeEditorProps = {
  value: string;
  onChange: (value: string) => void;
  extensions: Extension[];
};

export function useCodeEditor({
  value,
  onChange,
  extensions,
}: UseCodeEditorProps) {
  const { ref, view } = useCodeMirror([onUpdate(onChange), ...extensions]);

  useEffect(() => {
    if (view) {
      const editorValue = (view as any).state.doc.toString();

      if (value !== editorValue) {
        (view as any).dispatch({
          changes: {
            from: 0,
            to: editorValue.length,
            insert: value || "",
          },
        });
      }
    }
  }, [value, view]);

  return ref;
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
