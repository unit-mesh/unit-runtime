import { WebContainer, WebContainerProcess } from "@webcontainer/api";
import { useEffect, useRef, useState } from "react";
import { Terminal as Xterm } from "xterm";

export type TerminalProps = {
  className?: string;
  webcontainer: WebContainer | null;
};

export default function Terminal({ className, webcontainer }: TerminalProps) {
  const terminal$ = useRef<HTMLDivElement | null>(null);
  const [term, setTerm] = useState<Xterm | null>(null);

  useEffect(() => {
    if (terminal$.current && webcontainer) {
      const term = new Xterm({
        cols: 80,
        rows: 24,
        convertEol: true,

        cursorBlink: true,
        cursorStyle: "block",
        fontFamily: "Fira Code",
        fontSize: 14,
        lineHeight: 1.5,
        theme: {
          background: "#1e1e1e",
          foreground: "#d4d4d4",
        },
      });
      term.open(terminal$.current);

      let shellProcess: WebContainerProcess | null = null;

      console.log("spawn jsh", webcontainer);
      webcontainer.spawn("jsh").then(async (proc) => {
        shellProcess = proc;
        proc.output.pipeTo(
          new WritableStream({
            write(data) {
              term.write(data);
            },
          })
        );

        const writer = proc.input.getWriter();
        term.onData((data) => {
          console.log("term.onData", data);
          writer.write(data);
        });
      });
      setTerm(term);
      return () => {
        term.dispose();
        setTerm(null);
        shellProcess?.kill();
      };
    }
  }, [webcontainer]);

  return (
    <div className={className}>
      <div className="w-full h-full relative" ref={terminal$}></div>
    </div>
  );
}
