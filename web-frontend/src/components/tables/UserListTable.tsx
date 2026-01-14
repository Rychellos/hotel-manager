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
    field: "username",
    header: "Nazwa użytkowna",
    actionTarget: "USER",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
  },
  {
    field: "email",
    header: "Adres email",
    actionTarget: "USER",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
  },
  {
    field: "nextPasswordChange",
    header: "Następna zmiana hasła",
    actionTarget: "USER",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    flex: 1,
    type: "dateString",
  },
  {
    field: "roleIds",
    header: "Role",
    actionTarget: "USER",
    actionScope: ActionScope.ONE,
    actionType: ActionType.ADMIN,

    dataType: "list-modal",
    modalConfig: {
      title: "Edycja uprawnień roli",
      getEndpoint: "/api/v1/roles",
      patchEndpoint: "/api/v1/users/{idOrUuid}",
      labelField: "publicName",
      currentlySelectedIds: [],
    },
  },
];

export default function RoleListTable() {
  const [gridApi, setGridApi] = createSignal<any>(null);

  const userEndpoint = client.path("/api/v1/users").method("get").create();
  const rolePatch = client
    .path("/api/v1/users/{idOrUuid}")
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
    <Suspense fallback={<Loading text="Wczytywanie widoku tabeli" />}>
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
