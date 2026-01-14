import { IDatasource } from "ag-grid-community";
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
    header: "Nazwa permmisji",
    actionTarget: "PERMISSION",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 3,
  },
  {
    field: "roleIds",
    header: "Role używające permisji",
    actionTarget: "PERMISSION",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,
    flex: 1,

    dataType: "list-modal",
    modalConfig: {
      title: "Edycja roli używających permisji",
      getEndpoint: "/api/v1/roles",
      patchEndpoint: "/api/v1/roles/{idOrUuid}",
      labelField: "publicName",
      currentlySelectedIds: [],
    },
  },
];

export default function RoleListTable() {
  const [gridApi, setGridApi] = createSignal<any>(null);

  const userEndpoint = client
    .path("/api/v1/permissions")
    .method("get")
    .create();
  const rolePatch = client
    .path("/api/v1/permissions/{idOrUuid}")
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

  const datasource: IDatasource = createDatasource(userEndpoint);

  const columns = createMemo(() => createSmartColumns(roleColumns));

  return (
    <Suspense fallback={<Loading text="Wczytywanie widoku permisji" />}>
      <div class="ag-theme-quartz h-auto w-full">
        <AgGridSolid
          localeText={AG_GRID_LOCALE_PL}
          datasource={datasource}
          rowModelType="infinite"
          columnTypes={columnTypes}
          pagination={true}
          cacheBlockSize={20}
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
