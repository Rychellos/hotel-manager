import { ColDef, ICellRendererParams } from "ag-grid-community";
import { GenericColConfig } from "./types";
import { ActionScope, ActionType, client, PageQuery } from "../api";
import { hasActionPermission } from "../PermissionsContext";
import { RelationModal } from "~/components/Grid/RelationModal";

export const createSmartColumns = (meta: GenericColConfig[]): ColDef[] => {
  return meta.map((m) => {
    const {
      header,
      actionTarget: target,
      dataType,
      modalConfig,
      actionType,
      actionScope,
      ...agGridProps
    } = m;

    const isModalType = dataType === "list-modal";

    const canEdit = hasActionPermission(
      `${target}:${actionType ?? ActionType.ADMIN}:${
        actionScope ?? ActionScope.ONE
      }`
    );

    return {
      headerName: header,
      editable: canEdit,

      cellEditor:
        dataType === "number" ? "agNumberCellEditor" : "agTextCellEditor",

      cellRenderer: (params: ICellRendererParams) => {
        if (isModalType && modalConfig) {
          return (
            <RelationModal
              fetcher={async (page, size) => {
                const endpoint = client
                  .path(modalConfig.getEndpoint)
                  .method("get")
                  .create();

                const { data, ok } = await endpoint({
                  page: page,
                  size: size,
                } as PageQuery);

                if (ok) {
                  return data;
                }
              }}
              title={modalConfig.title}
              initialSelectedIds={params.value}
              labelField={modalConfig.labelField}
              onSave={async (idList) => {
                params.node.setDataValue(m.field, idList);
              }}
            />
          );
        } else {
          return agGridProps.cellRenderer
            ? agGridProps.cellRenderer(params)
            : params.value;
        }
      },

      ...agGridProps,
    } as ColDef;
  });
};
