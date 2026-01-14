import {
  GridApi,
  ICellEditorParams,
  ICellRendererParams,
} from "ag-grid-community";
import {
  createEffect,
  createMemo,
  createResource,
  createSignal,
  Suspense,
} from "solid-js";
import { ActionScope, ActionType, client } from "~/lib/api";
import Loading from "../Loading";
import AgGridSolid from "solid-ag-grid";
import { AG_GRID_LOCALE_PL } from "@ag-grid-community/locale";
import { GenericColConfig } from "~/lib/grid/types";
import { handleGridPatch } from "~/lib/grid/handleGridPatch";
import { createSmartColumns } from "~/lib/grid/factory";
import { createDatasource } from "~/lib/grid/createDatasource";
import { columnTypes } from "~/lib/grid/columnTypes";
import {
  ComboboxContent,
  ComboboxControl,
  ComboboxInput,
  ComboboxItem,
  ComboboxItemIndicator,
  ComboboxItemLabel,
  ComboboxTrigger,
  Combobox,
} from "../ui/combobox";

const standardEndpoint = client
  .path("/api/v1/standards/{idOrUuid}")
  .method("get")
  .create();

const standardEndpointPage = client
  .path("/api/v1/standards")
  .method("get")
  .create();

export const roleColumns: GenericColConfig[] = [
  {
    field: "name",
    header: "Nazwa pokoju",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 1,
  },
  {
    field: "standardId",
    header: "Standard pokoju",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 1,
    cellRenderer: (param: ICellRendererParams) => {
      const [data] = createResource(param.getValue, async (id) => {
        try {
          const { ok, data } = await standardEndpoint({ idOrUuid: id });

          return ok ? data.name : "Błąd";
        } catch {
          return "BŁĄD";
        }
      });

      return <>{data()}</>;
    },
    cellEditor: (param: ICellEditorParams) => {
      const [getFilter, setFilter] = createSignal("");

      const [data, setData] = createSignal([]);

      createEffect(async () => {
        try {
          const { ok, data } = await standardEndpointPage({
            ["name"]: getFilter(),
          });

          setData(ok ? data.content : []);
        } catch {
          setData([]);
        }
      });

      return (
        <Combobox
          options={data()}
          optionValue={"id"}
          optionLabel={"name"}
          value={param.value}
          onInput={(v) => {
            v.preventDefault();
            setFilter(v.target.value);
          }}
          onChange={(item) => {
            console.log(item);
            if (item) {
              setFilter(item["name"]);
              param.node.setDataValue(param.column.getColId(), item.id);
              param.stopEditing(item.id);
            }
          }}
          itemComponent={(props) => (
            <ComboboxItem item={props.item}>
              <ComboboxItemLabel>
                {props.item.rawValue["name"]}
              </ComboboxItemLabel>
              <ComboboxItemIndicator />
            </ComboboxItem>
          )}
        >
          <ComboboxControl>
            <ComboboxInput value={getFilter()} class="h-auto" />
            <ComboboxTrigger />
          </ComboboxControl>
          <ComboboxContent></ComboboxContent>
        </Combobox>
      );
    },
  },
  {
    field: "roomDescription",
    header: "Opis pokoju",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 1,
  },
  {
    field: "bedsAvailable",
    header: "Dostępnych łóżek",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "text",
    flex: 1,
  },
  {
    field: "basePriceOverride",
    header: "Napisanie ceny bazowa za pokój [PLN]",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "number",
    flex: 1,
  },
  {
    field: "perPersonPriceOverride",
    header: "Napisanie ceny za osobę [PLN]",
    actionTarget: "ROOM",
    actionScope: ActionScope.ONE,
    actionType: ActionType.EDIT,
    dataType: "number",
    flex: 1,
  },
];

export default function RoleListTable() {
  const [gridApi, setGridApi] = createSignal<GridApi>();

  const roomEndpoint = client.path("/api/v1/rooms").method("get").create();
  const roomPatch = client
    .path("/api/v1/rooms/{idOrUuid}")
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
      roomEndpoint,
      api && api.paginationGetPageSize !== undefined
        ? api.paginationGetPageSize()
        : 50
    );
  });

  return (
    <Suspense fallback={<Loading text="Wczytywanie widoku pokoi" />}>
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
          onCellValueChanged={(e) => handleGridPatch(e, roomPatch)}
          editType="fullRow"
          suppressClickEdit={false}
        />
      </div>
    </Suspense>
  );
}
