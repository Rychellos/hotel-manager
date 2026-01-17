import { flexRender, Table } from "@tanstack/solid-table";
import { TableHead, TableHeader, TableRow } from "../ui/table";
import { For, Match, Show, Switch } from "solid-js";
import { Button } from "../ui/button";

import IconSelector from "lucide-solid/icons/minus";
import ArrowDownZA from "lucide-solid/icons/arrow-down-z-a";
import ArrowDownAZ from "lucide-solid/icons/arrow-down-a-z";

export default function DataTableHeader(props: { table: Table<any> }) {
  return (
    <TableHeader>
      <For each={props.table.getHeaderGroups()}>
        {(headerGroup) => (
          <TableRow>
            <For each={headerGroup.headers}>
              {(header) => (
                <TableHead
                  colSpan={header.colSpan}
                  class={
                    header.id == "select" || header.id == "actions" ? "w-4" : ""
                  }
                >
                  <Show when={!header.isPlaceholder}>
                    <Show
                      when={header.column.getCanSort()}
                      fallback={
                        <span>
                          {flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                        </span>
                      }
                    >
                      <Button
                        variant={"ghost"}
                        size="default"
                        class="h-12"
                        onclick={() => header.column.toggleSorting()}
                      >
                        <span>
                          {flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                        </span>
                        <div class="size-full flex justify-stretch items-stretch aspect-square p-1">
                          <Switch
                            fallback={
                              <IconSelector
                                style={{
                                  width: "100%",
                                  height: "100%",
                                }}
                              />
                            }
                          >
                            <Match
                              when={header.column.getIsSorted() === "desc"}
                            >
                              <ArrowDownZA
                                style={{
                                  width: "100%",
                                  height: "100%",
                                }}
                              />
                            </Match>
                            <Match when={header.column.getIsSorted() === "asc"}>
                              <ArrowDownAZ
                                style={{
                                  width: "100%",
                                  height: "100%",
                                }}
                              />
                            </Match>
                          </Switch>
                        </div>
                      </Button>
                    </Show>
                  </Show>
                </TableHead>
              )}
            </For>
          </TableRow>
        )}
      </For>
    </TableHeader>
  );
}
