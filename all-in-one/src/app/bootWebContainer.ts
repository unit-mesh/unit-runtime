"use client";

import "client-only";

import { FileSystemAPI, WebContainer } from "@webcontainer/api";

let __webcontainerInstance: Promise<WebContainer> = new Promise(
  (resolve, reject) => {
    console.log("booting webcontainer");
    WebContainer.boot()
      .then((it) => resolve(init(it)))
      .catch((err) => reject(err));
  }
);

function init(container: WebContainer): WebContainer {
  container.fs.mkdir("/tmp");

  // init fs
  container.mount({}, { mountPoint: "/tmp" });

  return container;
}

export async function getWebContainerInstance(): Promise<WebContainer> {
  return __webcontainerInstance;
}

export async function importFs(): Promise<FileSystemAPI> {
  return (await __webcontainerInstance).fs;
}

export async function importOn() {
  return (await __webcontainerInstance).mount;
}

export async function importon() {
  return (await __webcontainerInstance).on;
}

export async function getPath() {
  return (await __webcontainerInstance).path;
}

export async function importSpawn() {
  return (await __webcontainerInstance).spawn;
}

export async function teardown() {
  return (await __webcontainerInstance).teardown();
}

export async function getWorkdir() {
  return (await __webcontainerInstance).workdir;
}
