import type { ColumnDef, Table } from "@tanstack/solid-table";

import { Button } from "~/components/ui/button";
import { TextField, TextFieldInput } from "~/components/ui/text-field";

import IconX from "lucide-solid/icons/x-circle";

import { TableFacetedFilter } from "./TableFacetedFilter";
import { TableViewOptions } from "./TableViewOptions";
import { Component, For } from "solid-js";

type DataTableToolbarProps<TData> = {
  table: Table<TData>;
  advancedFilters: {
    columnName: string;
    columnTitle: string;
    type: "search" | "select";
    filtersOptions: {
      label: string;
      value: string;
      icon?: Component;
    }[];
  }[];
};

export function TableToolbar<TData>(props: DataTableToolbarProps<TData>) {
  const isFiltered = () => props.table.getState().columnFilters.length > 0;

  return (
    <div class="flex items-center justify-between">
      <div class="flex flex-1 items-center space-x-2">
        {/* Search filtes */}
        <For each={props.advancedFilters}>
          {(columnDef) => {
            if (columnDef.type == "search")
              return (
                <TextField
                  class="card"
                  value={
                    (props.table
                      .getColumn(columnDef.columnName)
                      ?.getFilterValue() as string) ?? ""
                  }
                  onChange={(value) =>
                    props.table
                      .getColumn(columnDef.columnName)
                      ?.setFilterValue(value)
                  }
                >
                  <TextFieldInput
                    placeholder={columnDef.columnTitle}
                    class="h-8 w-[150px] lg:w-[250px] border-0"
                  />
                </TextField>
              );
          }}
        </For>

        {/* Advanced filtes */}
        <For each={props.advancedFilters}>
          {(columnDef) => {
            if (columnDef.type == "select")
              return (
                <TableFacetedFilter
                  column={props.table.getColumn(columnDef.columnName)}
                  title={columnDef.columnTitle}
                  options={columnDef.filtersOptions}
                />
              );
          }}
        </For>

        {/* clear filters */}
        {isFiltered() && (
          <Button
            variant="ghost"
            onClick={() => props.table.resetColumnFilters()}
            class="h-8 px-2 lg:px-3"
          >
            Reset
            <IconX />
          </Button>
        )}
      </div>

      {/* Choosing columns */}
      <TableViewOptions table={props.table} />
    </div>
  );
}
