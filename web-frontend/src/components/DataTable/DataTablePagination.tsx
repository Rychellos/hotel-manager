import type { Table } from "@tanstack/solid-table";

import IconChevronLeft from "lucide-solid/icons/chevron-left";
import IconChevronsLeft from "lucide-solid/icons/chevrons-left";
import IconChevronRight from "lucide-solid/icons/chevron-right";
import IconChevronsRight from "lucide-solid/icons/chevrons-right";

import { Button } from "~/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "~/components/ui/select";

type TablePaginationProps<TData> = {
  table: Table<TData>;
};

export default function DataTablePagination<TData>(
  props: TablePaginationProps<TData>
) {
  return (
    <div class="flex items-center justify-between sticky bottom-1 mx-1">
      <div class="flex-1 text-sm text-muted-foreground hover:opacity-30">
        <span class="bg-background p-2 border rounded shadow-sm text-base pointer-events-none select-none">
          Zaznaczono {props.table.getFilteredSelectedRowModel().rows.length} z{" "}
          {props.table.getFilteredRowModel().rows.length} wierszy.
        </span>
      </div>

      <div class="flex items-center space-x-6 lg:space-x-8 card px-3 py-1">
        <div class="flex items-center space-x-2">
          <p class="text-sm font-medium">Elementow na stronie</p>

          <Select
            value={props.table.getState().pagination.pageSize}
            onChange={(value) => value && props.table.setPageSize(value)}
            options={[10, 20, 30, 40, 50]}
            itemComponent={(props) => (
              <SelectItem item={props.item}>{props.item.rawValue}</SelectItem>
            )}
          >
            <SelectTrigger class="h-8 w-[70px]">
              <SelectValue<string>>
                {(state) => state.selectedOption()}
              </SelectValue>
            </SelectTrigger>
            <SelectContent />
          </Select>
        </div>

        <div class="flex w-[100px] items-center justify-center text-sm font-medium">
          Strona {props.table.getState().pagination.pageIndex + 1} z{" "}
          {props.table.getPageCount()}
        </div>

        <div class="flex items-center space-x-2">
          <Button
            variant="outline"
            class="hidden size-8 p-0 lg:flex"
            onClick={() => props.table.setPageIndex(0)}
            disabled={!props.table.getCanPreviousPage()}
          >
            <span class="sr-only">Przejdź do pierwszej strony</span>
            <IconChevronsLeft />
          </Button>
          <Button
            variant="outline"
            class="size-8 p-0"
            onClick={() => props.table.previousPage()}
            disabled={!props.table.getCanPreviousPage()}
          >
            <span class="sr-only">Przejdź do poprzedniej strony</span>
            <IconChevronLeft />
          </Button>
          <Button
            variant="outline"
            class="size-8 p-0"
            onClick={() => props.table.nextPage()}
            disabled={!props.table.getCanNextPage()}
          >
            <span class="sr-only">Przejdź do następnej strony</span>
            <IconChevronRight />
          </Button>
          <Button
            variant="outline"
            class="hidden size-8 p-0 lg:flex"
            onClick={() =>
              props.table.setPageIndex(props.table.getPageCount() - 1)
            }
            disabled={!props.table.getCanNextPage()}
          >
            <span class="sr-only">Przejdź do ostatniej strony</span>
            <IconChevronsRight />
          </Button>
        </div>
      </div>
    </div>
  );
}
