import { defineConfig } from "@solidjs/start/config";
import tailwindcss from "@tailwindcss/vite";
import mkcert from "vite-plugin-mkcert";

export default defineConfig({
  ssr: false,
  vite: {
    server: {
      https: true,
    },
    plugins: [
      mkcert({
        force: true,
        savePath: "./certs",
      }),
      tailwindcss({
        optimize: true,
      }),
    ],
  },
  server: {
    preset: "static",
    devProxy: {
      "/api/": {
        target: "http://localhost:8080/api/",
        changeOrigin: true,
        secure: false,
      },
    },
    https: {
      cert: "./certs/cert.pem",
      key: "./certs/dev.pem",
    },
  },
});
