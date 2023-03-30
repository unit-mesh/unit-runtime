/// <reference lib="webworker" />

/**
 * This is a workaround for the issue with workbox and next.js
 * https://github.com/vercel/next.js/issues/33863#issuecomment-1140518693
 */

const resources = (self as any).__WB_MANIFEST; // this is just to satisfy workbox

// ... your SW code
