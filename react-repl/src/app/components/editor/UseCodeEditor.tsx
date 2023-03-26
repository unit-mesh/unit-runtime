import { useCodeMirror } from "@/app/components/editor/UseCodeMirror";
import { useEffect } from "react";
import { onUpdate } from "@/app/components/editor/CodeEditor";
import { UseCodeEditorProps } from "@/app/components/editor/editor.types";

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
