import { ViewUpdate } from "@codemirror/view";
import { Extension } from "@codemirror/state";

export type OnChange = (value: string, viewUpdate: ViewUpdate) => void;
export type UseCodeEditorProps = {
  value: string;
  onChange: (value: string) => void;
  extensions: Extension[];
};
