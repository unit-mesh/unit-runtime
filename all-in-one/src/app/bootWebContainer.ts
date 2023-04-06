"use client";

import "client-only";

import { WebContainer } from "@webcontainer/api";
import { resolve } from "path";

let __webcontainerInstance: WebContainer | null;

async function init(container: WebContainer): Promise<WebContainer> {
  await container.fs.mkdir("/tmp");

  // init fs
  await container.mount({}, { mountPoint: "/tmp" });
  return container;
}

export async function initFs(
  container: WebContainer,
  bootfs: any,
  clean = true
) {
  const root = "/tmp/scratch";

  if (clean) {
    try {
      await container.fs.rm(root, { recursive: true });
    } catch {
    } finally {
      await container.fs.mkdir(root, { recursive: true });
    }
  }

  if (typeof bootfs !== "object") {
    throw new Error("invalid bootfs");
  }

  if (Object.keys(bootfs).length === 0) {
    throw new Error("empty bootfs");
  }

  for (const [key, value] of Object.entries(bootfs)) {
    if (key === "_$__Ty__") {
      continue;
    }

    if ((value as any)._$__Ty__ === "file") {
      await container.fs.writeFile(
        resolve(root, (value as any).path),
        (value as any).content
      );
    } else if ((value as any)._$__Ty__ === "dir") {
      await container.fs.mkdir(resolve(root, (value as any).path), {
        recursive: true,
      });
      await initFs(container, value, false);
    } else if (key === "path") {
      /** ignore */
    } else {
      throw new Error("invalid bootfs");
    }
  }
}

export const initInstance = async () => {
  if (!__webcontainerInstance) {
    __webcontainerInstance = await new Promise((resolve, reject) => {
      console.log("booting webcontainer");
      WebContainer.boot()
        .then((it) => {
          console.log("webcontainer booted");
          return resolve(init(it));
        })
        .catch((err) => reject(err));
    });
  }

  return __webcontainerInstance!;
};

export const destroyInstance = () => {
  __webcontainerInstance?.teardown();
  __webcontainerInstance = null;
};

const getInstance = async () => {
  return __webcontainerInstance;
};
export type GetInstance = typeof getInstance;
export default getInstance;
