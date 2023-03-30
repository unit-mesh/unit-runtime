/// <reference lib="webworker" />

import routing from "workbox-routing"
import type {RouteHandlerCallbackOptions} from "workbox-core"

/**
 * This is a workaround for the issue with workbox and next.js
 * https://github.com/vercel/next.js/issues/33863#issuecomment-1140518693
 */

const resources = (self as any).__WB_MANIFEST; // this is just to satisfy workbox

// ... your SW code

routing.registerRoute(
    /^https?:\/\/HOST\/BASE_URL\/(\/.*)$/,
    async ({
      request,
      params,
      url,
    }: RouteHandlerCallbackOptions): Promise<Response> => {
      const req = request?.url || url.toString();
      const [pathname] = params as string[];
      // send the request to vite worker
      const response = await postToViteWorker(pathname)
      return response;
    }
  );


  async function postToViteWorker(pathname: string): Promise<Response> {
    self.postMessage({ pathname });

    return new Promise((resolve) => {
        self.addEventListener(
            "message",
            (event) => {
            if (event.data.pathname === pathname) {
                resolve(event.data.response as Response);
            }
            },
            { once: true }
        );
    });
  }