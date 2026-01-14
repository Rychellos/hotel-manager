import {
  GridApi,
  IDatasource,
  PaginationChangedEvent,
} from "ag-grid-community";
import { createEffect, createMemo, createSignal, Suspense } from "solid-js";
import { ActionScope, ActionType, client } from "~/lib/api";
import Loading from "../Loading";
import AgGridSolid from "solid-ag-grid";
import { AG_GRID_LOCALE_PL } from "@ag-grid-community/locale";
import { GenericColConfig } from "~/lib/grid/types";
import { handleGridPatch } from "~/lib/grid/handleGridPatch";
import { createSmartColumns } from "~/lib/grid/factory";
import { createDatasource } from "~/lib/grid/createDatasource";
import { columnTypes } from "~/lib/grid/columnTypes";

export const roleColumns: GenericColConfig[] = [
  {
    field: "name",
    header: "Nazwa standardu",
    actionTarget: "STANDARD",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 1,
  },
  {
    field: "standardDescription",
    header: "Opis standardu",
    actionTarget: "STANDARD",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,
    dataType: "text",
    flex: 1,
  },
  {
    field: "basePrice",
    header: "Cena bazowa za pokój [PLN]",
    actionTarget: "STANDARD",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,
    dataType: "number",
    flex: 1,
  },
  {
    field: "pricePerPerson",
    header: "Cena za osobę [PLN]",
    actionTarget: "STANDARD",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,
    dataType: "number",
    flex: 1,
  },
  {
    field: "roomIds",
    header: "Pokoje używające standardu",
    actionTarget: "STANDARD",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,
    flex: 2,

    dataType: "list-modal",
    modalConfig: {
      title: "Edycja Pokoi używających standardu",
      getEndpoint: "/api/v1/rooms",
      patchEndpoint: "/api/v1/rooms/{idOrUuid}",
      labelField: "name",
      currentlySelectedIds: [],
    },
  },
];

export default function RoleListTable() {
  const [gridApi, setGridApi] = createSignal<GridApi>();

  const userEndpoint = client.path("/api/v1/standards").method("get").create();
  const rolePatch = client
    .path("/api/v1/standards/{idOrUuid}")
    .method("patch")
    .create();

  createEffect(() => {
    const api = gridApi();

    if (api) {
      try {
        api.redrawRows();
      } catch (e) {
        console.warn("Grid not ready yet for redraw", e);
      }
    }
  });

  const columns = createMemo(() => createSmartColumns(roleColumns));

  const datasource = createMemo(() => {
    const api = gridApi();

    return createDatasource(
      userEndpoint,
      api && api.paginationGetPageSize !== undefined
        ? api.paginationGetPageSize()
        : 50
    );
  });

  return (
    <Suspense fallback={<Loading text="Wczytywanie widoku standardów" />}>
      <div class="ag-theme-quartz h-auto w-full">
        <AgGridSolid
          localeText={AG_GRID_LOCALE_PL}
          datasource={datasource()}
          rowModelType="infinite"
          columnTypes={columnTypes}
          pagination={true}
          cacheBlockSize={50}
          paginationAutoPageSize={true}
          // paginationPageSizeSelector={[20, 50, 100]}
          columnDefs={columns()}
          onGridReady={(p) => setGridApi(p.api)}
          onCellValueChanged={(e) => handleGridPatch(e, rolePatch)}
          editType="fullRow"
          suppressClickEdit={false}
          stopEditingWhenCellsLoseFocus={true}
        />
      </div>
    </Suspense>
  );
}
