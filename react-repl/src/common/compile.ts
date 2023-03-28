import { transform } from "@babel/standalone";

export const BUNDLE_SCRIPTS = {
  react:
    "https://cdn.jsdelivr.net/npm/react@18.2.0/umd/react.production.min.js",
  reactDom:
    "https://cdn.jsdelivr.net/npm/react-dom@18.2.0/umd/react-dom.production.min.js",
};

export function compile(
  filename: string | null | undefined = "scratch.tsx",
  deferredCode: string,
  compiledAction?: (value: ((prevState: string) => string) | string) => void
) {
  try {
    const code = transform(deferredCode, {
      presets: ["env", "typescript", "react"],
      plugins: [
        [
          "transform-modules-umd",
          {
            globals: {
              react: "React",
              "react-dom/client": "ReactDOM",
              "react-dom": "ReactDOM",
            },
            exactGlobals: true,
          },
        ],
      ],
      filename: filename,
    }).code;
    compiledAction ? compiledAction(code ?? "") : null;

    return code;
  } catch (e) {
    console.log(e);
    return null;
  }
}
