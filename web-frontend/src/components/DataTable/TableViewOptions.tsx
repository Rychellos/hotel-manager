import { For } from "solid-js";

import type { Table } from "@tanstack/solid-table";

import SlidersHorizontal from "lucide-solid/icons/sliders-horizontal";
import { Button } from "~/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu";

type TableViewOptionsProps<TData> = {
  table: Table<TData>;
};

export function TableViewOptions<TData>(props: TableViewOptionsProps<TData>) {
  return (
    <DropdownMenu placement="bottom-end">
      <DropdownMenuTrigger
        as={Button}
        variant="outline"
        size="default"
        class="ml-auto h-8 flex card"
      >
        <SlidersHorizontal />
        Kolumny
      </DropdownMenuTrigger>
      <DropdownMenuContent class="w-[200px]">
        <DropdownMenuLabel>Wybierz kolumny</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <For
          each={props.table
            .getAllColumns()
            .filter(
              (column) =>
                typeof column.accessorFn !== "undefined" && column.getCanHide()
            )}
        >
          {(column) => (
            <DropdownMenuCheckboxItem
              class="capitalize"
              checked={column.getIsVisible()}
              onChange={(value) => column.toggleVisibility(!!value)}
            >
              {column.columnDef.header?.toString()}
            </DropdownMenuCheckboxItem>
          )}
        </For>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
