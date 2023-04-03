const SUPPORTED_FRAMEWORK = ["react", "vue", "svelte"] as const;

export async function GET(request: Request) {
  const url = new URL(request.url);
  const framework = url.searchParams.get("framework");
  if (!SUPPORTED_FRAMEWORK.includes(framework as any)) {
    return new Response("wrong param", { status: 400 });
  }

  const result = await getBootstrapContents(framework as any);
  return new Response(JSON.stringify(result));
}

import { relative, resolve, normalize } from "node:path";
import { readdir, readFile } from "node:fs/promises";

async function getBootstrapContents(ty: (typeof SUPPORTED_FRAMEWORK)[number]) {
  if (!SUPPORTED_FRAMEWORK.includes(ty)) {
    throw new Error("unsupported framework");
  }

  const contentRoot = resolve(__PROJ_ROOT__, "bootstrap_projects", ty);

  // read the directory recursively and get all the files's content
  async function getContentsRecursively(dir: string) {
    let result: any = {};

    const files = await readdir(dir, { encoding: "utf8", withFileTypes: true });
    for (const file of files) {
      if (file.isDirectory()) {
        result[file.name] = {
          ...(await getContentsRecursively(resolve(dir, file.name))),
          _$__Ty__: "dir",
        };
      } else {
        result[file.name] = {
          _$__Ty__: "file",
          path: normalize(
            relative(contentRoot, resolve(dir, file.name))
          ).replaceAll("\\", "/"),
          content: await readFile(resolve(dir, file.name), {
            encoding: "utf8",
          }),
        };
      }
    }

    return result;
  }

  return getContentsRecursively(contentRoot);
}
