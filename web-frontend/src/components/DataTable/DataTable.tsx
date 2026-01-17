import { createEffect, For, Show } from "solid-js";
import {
  ColumnDef,
  ColumnFiltersState,
  createSolidTable,
  flexRender,
  getCoreRowModel,
  getFacetedRowModel,
  getFacetedUniqueValues,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  PaginationState,
  SortingState,
  VisibilityState,
} from "@tanstack/solid-table";

import { Range } from "@solid-primitives/range";

import { Table, TableBody, TableCell, TableRow } from "~/components/ui/table";

import { Fetcher, Page, PageQuery } from "~/lib/api";
import { createSignal } from "solid-js";
import { TableToolbar } from "./TableToolbar";
import DataTablePagination from "./DataTablePagination";

import { Skeleton } from "~/components/ui/skeleton";
import SelectColumn from "./SelectColumn";
import DataTableHeader from "./DataTableHeader";
import ActionColumn, { DataTableActions } from "./ActionColumn";

function convertColumnFiltersToProperties(
  object: { id: string; value: any }[]
) {
  let returnValue: { [key: string]: any } = {};

  for (let index = 0; index < object.length; index++) {
    const element = object[index];

    returnValue[element.id] = element.value;
  }

  return returnValue;
}

export type DataTableProps<
  TData,
  Cols extends ColumnDef<TData>[],
  TFilter extends PageQuery
> = {
  fetcher: Fetcher<TData, TFilter>;
  columns: Cols;
  actions?: DataTableActions<TData>;
};

function parseSorting(sorting: SortingState) {
  if (sorting.length === 0) return undefined;

  return `${sorting[0].id},${sorting[0].desc ? "desc" : "asc"}`;
}

export default function DataTable<
  TData extends { id: number },
  TFilter extends PageQuery
>(props: DataTableProps<TData, ColumnDef<TData>[], TFilter>) {
  const [data, setData] = createSignal<TData[]>([]);
  const [pageCount, setPageCount] = createSignal(0);
  const [pagination, setPagination] = createSignal<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });

  const [loading, setLoading] = createSignal(true);

  const [rowSelection, setRowSelection] = createSignal({});
  const [columnVisibility, setColumnVisibility] = createSignal<VisibilityState>(
    {}
  );
  const [columnFilters, setColumnFilters] = createSignal<ColumnFiltersState>(
    []
  );
  const [sorting, setSorting] = createSignal<SortingState>([]);

  let fetchingJob: number;

  const fetchData = () => {
    setLoading(true);

    if (fetchingJob) {
      clearTimeout(fetchingJob);
    }

    fetchingJob = window.setTimeout(async () => {
      const queryParams = {
        page: pagination().pageIndex,
        size: pagination().pageSize,
        sort: parseSorting(sorting()),
        ...convertColumnFiltersToProperties(table.getState().columnFilters),
      } as unknown as TFilter;

      try {
        const response = await props.fetcher(queryParams);

        if (response.ok) {
          setData(response.data.content ?? []);
          setPageCount(response.data.page?.totalPages ?? 0);
        } else {
          console.error("API Error:", response.status, response.data);
        }
      } catch (err) {
        console.error("Request failed:", err);
      } finally {
        setLoading(false);
      }
    }, 300);
  };

  createEffect(() => {
    columnFilters();
    sorting();

    setPagination((prev) => ({ ...prev, pageIndex: 0 }));
  });

  createEffect(() => {
    table.getState();
    pagination();
    columnFilters();

    fetchData();
  });

  const table = createSolidTable({
    get data() {
      return data();
    },
    columns: [
      SelectColumn as ColumnDef<TData>,
      ...props.columns,
      ActionColumn(props.actions) as ColumnDef<TData>,
    ],
    manualPagination: true,
    get pageCount() {
      return pageCount();
    },
    initialState: {
      pagination: {
        pageIndex: 0,
        pageSize: 10,
      },
    },
    state: {
      get sorting() {
        return sorting();
      },
      get columnVisibility() {
        return columnVisibility();
      },
      get rowSelection() {
        return rowSelection();
      },
      get columnFilters() {
        return columnFilters();
      },
      get pagination() {
        return pagination();
      },
    },
    onPaginationChange: setPagination,
    enableRowSelection: true,
    enableMultiRowSelection: true,

    onRowSelectionChange: setRowSelection,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,

    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
  });

  return (
    <div class="space-y-4 flex flex-col min-w-fit min-h-fit">
      <TableToolbar
        table={table}
        advancedFilters={[
          {
            columnName: "username",
            columnTitle: "Szukaj po nazwie",
            type: "search",
            filtersOptions: [],
          },
        ]}
      />

      <div class="card">
        <Table>
          <DataTableHeader table={table} />

          <TableBody>
            <Show
              when={table.getRowModel().rows?.length}
              fallback={
                <Fallback
                  columns={props.columns.length + 2}
                  loading={loading()}
                  // pageSize={pagination().pageSize}
                  pageSize={10}
                  text={loading() ? "Ładowanie" : "Nic nie znaleziono"}
                />
              }
            >
              <For each={table.getRowModel().rows}>
                {(row) => (
                  <TableRow data-state={row.getIsSelected() && "selected"}>
                    <For each={row.getVisibleCells()}>
                      {(cell) => (
                        <TableCell class="h-13">
                          <div class="flex size-max items-center">
                            {flexRender(
                              cell.column.columnDef.cell,
                              cell.getContext()
                            )}
                          </div>
                        </TableCell>
                      )}
                    </For>
                  </TableRow>
                )}
              </For>

              {/* <Range
                start={0}
                to={
                  // pagination().pageSize -
                  10 - table.getFilteredRowModel().rows.length
                }
              >
                <EmptyRow
                  loading={loading()}
                  colums={props.columns.length + 2}
                />
              </Range> */}
            </Show>
          </TableBody>
        </Table>
      </div>

      <DataTablePagination table={table} />
    </div>
  );
}

function Fallback(props: {
  loading: boolean;
  columns: number;
  pageSize: number;
  text: string;
}) {
  return (
    <>
      <TableRow>
        <TableCell colSpan={props.columns} class="text-center h-13">
          {props.text}
        </TableCell>
      </TableRow>

      <Range start={1} to={props.pageSize}>
        <EmptyRow loading={props.loading} colums={props.columns} />
      </Range>
    </>
  );
}

function EmptyRow(props: { loading: boolean; colums: number }) {
  return (
    <TableRow class="border-transparent">
      <TableCell colSpan={props.colums} class="text-center h-13">
        <Show when={props.loading}>
          <div class="flex size-full p-0.5">
            <Skeleton class="size-full" radius={4} />
          </div>
        </Show>
      </TableCell>
    </TableRow>
  );
}
