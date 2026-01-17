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

export const roleColumns: GenericColConfig[] = [
  {
    field: "publicName",
    header: "Nazwa roli",
    actionTarget: "ROLE",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
  },
  {
    field: "description",
    header: "Opis roli",
    actionTarget: "ROLE",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    flex: 1,
    dataType: "text",
  },
  {
    field: "permissionIds",
    header: "Uprawnienia",
    actionTarget: "ROLE",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,

    dataType: "list-modal",
    modalConfig: {
      title: "Edycja uprawnień roli",
      getEndpoint: "/api/v1/permissions",
      patchEndpoint: "/api/v1/roles/{idOrUuid}",
      labelField: "name",
      currentlySelectedIds: [],
    },
  },
];

export default function RoleListTable() {
  const [gridApi, setGridApi] = createSignal<any>(null);

  const rolesEndpoint = client.path("/api/v1/roles").method("get").create();
  const rolePatch = client
    .path("/api/v1/roles/{idOrUuid}")
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

  const datasource: IDatasource = createDatasource(rolesEndpoint);

  const columns = createMemo(() => createSmartColumns(roleColumns));

  return (
    <Suspense fallback={<Loading text="Wczytywanie widoku tabeli" />}>
      <div class="ag-theme-quartz h-auto w-full">
        <AgGridSolid
          localeText={AG_GRID_LOCALE_PL}
          datasource={datasource}
          rowModelType="infinite"
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
