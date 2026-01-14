import { CellContext, ColumnDef } from "@tanstack/solid-table";
import { createMemo, For, Match, Show, Switch } from "solid-js";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import { A } from "@solidjs/router";
import Dots from "lucide-solid/icons/more-vertical";

import { Button } from "~/components/ui/button";
import { UseUserContext } from "~/lib/UserContext";
import { Skeleton } from "../ui/skeleton";

type basicActionEntry = {
  actionName: string;
  requiredAuthority?: string;
};

type buttonAction<TData> = {
  type: "menuClickableItem";
  onClickContext: (cell: CellContext<TData, any>) => () => void;
};

type anchorAction<TData> = {
  type: "anchor";
  href: (cell: CellContext<TData, any>) => string;
};

type separator = {
  type: "separator";
};

type clickableAction<TData> = basicActionEntry &
  (buttonAction<TData> | anchorAction<TData>);

export type DataTableActions<TData> = {
  selectActions?: (basicActionEntry & buttonAction<TData>)[];
  singleElementActions?: (clickableAction<TData> | separator)[];
};

export default <TData extends { id: any }>(
  actions: DataTableActions<TData> | undefined
) =>
  ({
    id: "actions",
    enableHiding: false,
    header: (header) => {
      if (!UseUserContext()) {
        return <Skeleton class="size-8 p-0" />;
      }

      const [appContext, set] = UseUserContext()!;
      const authorities = createMemo(() => appContext().authorities);

      return (
        <DropdownMenu placement="bottom-end">
          <DropdownMenuTrigger
            as={Button}
            variant="ghost"
            class="size-8 p-0"
            style={{ "margin-left": "calc(100% - var(--spacing) * 8)" }}
            disabled={header.table.getSelectedRowModel().rows.length < 2}
          >
            <span class="sr-only">Otwórz menu akcji dla wielu elementów</span>
            <Dots />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuLabel>Akcje</DropdownMenuLabel>
            <Show
              when={authorities().includes("AUTH_RESET_PASSWORD_OTHER_BULK")}
            >
              <DropdownMenuItem>Resetuj hasła</DropdownMenuItem>
            </Show>
            <Show when={authorities().includes("USER_DELETE_OTHER_BULK")}>
              <DropdownMenuItem>Usuń użytkowników</DropdownMenuItem>
            </Show>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
    cell: (cell) => {
      if (!UseUserContext()) {
        return <Skeleton class="size-8 p-0" />;
      }

      const [appContext, set] = UseUserContext()!;
      const authorities = createMemo(() => appContext().authorities);

      return (
        <Show when={actions?.singleElementActions?.length}>
          <DropdownMenu placement="bottom-end">
            <DropdownMenuTrigger
              as={Button<"button">}
              variant="ghost"
              class="size-8 p-0"
              style={{ "margin-left": "calc(100% - var(--spacing) * 8)" }}
              disabled={cell.table.getSelectedRowModel().rows.length > 1}
            >
              <span class="sr-only">
                Otwórz menu akcji dla pojedynczego elementu
              </span>
              <Dots />
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuLabel>Akcje</DropdownMenuLabel>
              <For each={actions?.singleElementActions}>
                {(item) => (
                  <Switch fallback={<DropdownMenuSeparator />}>
                    <Match when={item.type === "menuClickableItem"}>
                      <Show
                        when={
                          !(item as basicActionEntry).requiredAuthority ||
                          authorities().includes(
                            (item as basicActionEntry).requiredAuthority!
                          )
                        }
                      >
                        <DropdownMenuItem
                          onClick={(item as buttonAction<TData>).onClickContext(
                            cell
                          )}
                        >
                          {(item as basicActionEntry).actionName}
                        </DropdownMenuItem>
                      </Show>
                    </Match>
                    <Match when={item.type === "anchor"}>
                      <Show
                        when={
                          !(item as basicActionEntry).requiredAuthority ||
                          authorities().includes(
                            (item as basicActionEntry).requiredAuthority!
                          )
                        }
                      >
                        <DropdownMenuItem
                          as={A}
                          href={"./" + cell.row.original.id.toString()}
                        >
                          {(item as basicActionEntry).actionName}
                        </DropdownMenuItem>
                      </Show>
                    </Match>
                  </Switch>
                )}
              </For>
            </DropdownMenuContent>
          </DropdownMenu>
        </Show>
      );
    },
  } as ColumnDef<TData, any>);
