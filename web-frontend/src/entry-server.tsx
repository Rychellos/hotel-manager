// @refresh reload
import { createHandler, PageEvent, StartServer } from "@solidjs/start/server";

export default createHandler((context: PageEvent) => (
  <StartServer
    document={({ assets, children, scripts }) => (
      <html lang="pl" class="flex flex-col size-full overflow-hidden fixed">
        <head>
          <meta charset="utf-8" />
          <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0"
          />
          <link rel="icon" href="/favicon.ico" />
          {assets}
        </head>
        <body class="flex flex-col size-full overflow-hidden">
          <div id="app" class="flex flex-col size-full overflow-hidden">
            {children}
          </div>
          {scripts}
        </body>
      </html>
    )}
  />
));
