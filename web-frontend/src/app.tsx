import { MetaProvider, Title } from "@solidjs/meta";
import { Router } from "@solidjs/router";
import { FileRoutes } from "@solidjs/start/router";
import { createSignal, Show, Suspense } from "solid-js";
import { UserContextProvider } from "./lib/UserContext";

import "~/components/LoginForm";

import "@fontsource/inter";
import "./app.css";
import { cookieStorage, makePersisted } from "@solid-primitives/storage";
import {
  Resizable,
  ResizableHandle,
  ResizablePanel,
} from "~/components/ui/resizable";

import { Separator } from "./components/ui/separator";

import { ColorModeProvider, cookieStorageManager } from "@kobalte/core";
import { cn } from "./lib/utils";

import { ThemeModeToggle } from "./components/ThemeModeToggle";
import AppNav from "./AppNav";
import Loading from "./components/Loading";
import NavigationDialog from "./components/Navigation/Dialog";
import { PermissionContextProvider } from "./lib/PermissionsContext";
import LoginWrapper from "./components/LoginWrapper";

export default function App() {
  const [sizes, setSizes] = makePersisted(createSignal<number[]>([]), {
    name: "resizable-sizes",
    storage: cookieStorage,
    storageOptions: {
      path: "/",
    },
  });

  const [isCollapsed, setIsCollapsed] = createSignal(false);

  return (
    <Router
      root={(props) => (
        <MetaProvider>
          <Title>Menadżer Hotelu</Title>
          <ColorModeProvider storageManager={cookieStorageManager}>
            <UserContextProvider>
              <PermissionContextProvider>
                <LoginWrapper>
                  <Resizable
                    sizes={sizes()}
                    onSizesChange={setSizes}
                    class="overflow-hidden"
                  >
                    <ResizablePanel
                      initialSize={sizes()[0] ?? 0.2}
                      minSize={0.1}
                      maxSize={0.25}
                      collapsible
                      onCollapse={(e) => {
                        setIsCollapsed(e === 0);
                      }}
                      onExpand={() => {
                        setIsCollapsed(false);
                      }}
                      class={cn(
                        isCollapsed() &&
                          "min-w-[50px] transition-all duration-300 ease-in-out",
                        "max-sm:hidden"
                      )}
                    >
                      <div class="h-13 content-center text-center">
                        <Show when={!isCollapsed()} fallback={"H"}>
                          <h1>Manadżer Hotelu</h1>
                        </Show>
                      </div>

                      <Separator />

                      <AppNav isCollapsed={isCollapsed} />
                    </ResizablePanel>

                    <ResizableHandle withHandle class="hidden sm:flex" />

                    <ResizablePanel
                      initialSize={sizes()[1] ?? 0.75}
                      minSize={0.5}
                      class="grow flex flex-col overflow-hidden"
                    >
                      <header class="h-13 p-2 flex justify-between items-center">
                        <div class="flex gap-2">
                          <NavigationDialog />
                        </div>

                        <ThemeModeToggle />
                      </header>

                      <Separator />

                      <div class="overflow-auto size-full">
                        <Suspense
                          fallback={
                            <Loading text="Trwa ładowanie aplikacji..." />
                          }
                        >
                          {props.children}
                        </Suspense>
                      </div>
                    </ResizablePanel>
                  </Resizable>
                </LoginWrapper>
              </PermissionContextProvider>
            </UserContextProvider>
          </ColorModeProvider>
        </MetaProvider>
      )}
    >
      <FileRoutes />
    </Router>
  );
}
