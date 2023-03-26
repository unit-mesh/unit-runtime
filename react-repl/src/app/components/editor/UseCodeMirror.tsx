import { Extension } from "@codemirror/state";
import { useEffect, useRef, useState } from "react";
import { basicSetup, EditorView } from "codemirror";
import { javascript } from "@codemirror/lang-javascript";

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
