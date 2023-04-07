import { WebContainer, WebContainerProcess } from "@webcontainer/api";
import { useEffect, useRef, useState } from "react";
import { Terminal as Xterm } from "xterm";
import { FitAddon } from "xterm-addon-fit";

import "xterm/css/xterm.css";
import "./Terminal.css";

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
        convertEol: true,

        scrollOnUserInput: true,
        cursorBlink: true,
        cursorStyle: "block",
        fontFamily: "Fira Code",
        fontSize: 14,
        lineHeight: 1.5,
        theme: {
          background: "#fafafa",
          foreground: "#18181b",
        },
      });
      const fitAddon = new FitAddon();
      term.loadAddon(fitAddon);

      term.open(terminal$.current);
      document.body.addEventListener("resize", () => {
        fitAddon.fit();
      });
      setTimeout(() => {
        fitAddon.fit();
      }, 500);

      let shellProcess: WebContainerProcess | null = null;

      console.log("spawn jsh", webcontainer);
      webcontainer.spawn("jsh", {}).then(async (proc) => {
        shellProcess = proc;
        proc.output.pipeTo(
          new WritableStream({
            write(data) {
              console.log(
                "proc.output",
                data
                // new TextEncoder()
                //   .encode(data)
                //   .reduce((t, x) => t + x.toString(16).padStart(2, "0"), "")
              );

              if (data === "\x1b\x5b\x3f\x32\x30\x30\x34\x68") {
                console.log("ignore");
                return;
              }
              term.write(data);
            },
          })
        );

        const writer = proc.input.getWriter();
        setTimeout(() => {
          term.clear();
          writer.write("clear\n");
        }, 2500);
        term.onData((data) => {
          console.log("term.onData", data);
          writer.write(data);
        });
      });
      setTerm(term);

      return () => {
        term.dispose();
        setTerm(null);
        if (terminal$.current) {
          terminal$.current.innerHTML = "";
        }
        shellProcess?.kill();
      };
    }
  }, [webcontainer]);

  return (
    <div
      className={"terminal-container " + (className ?? "")}
      ref={terminal$}></div>
  );
}
