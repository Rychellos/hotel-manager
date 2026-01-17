import { ColDef } from "ag-grid-community";
import { ColumnMeta } from "./types";
import { ActionScope, ActionType } from "../api";
import { hasPermission } from "./hasPermissions";

export interface GenericColConfig extends ColDef {
  field: string;
  header: string; // Uproszczona nazwa dla headerName
  target?: string; // Np. "USER", "ROLE" - do automatycznych uprawnień
  flex?: number;
  // Opcjonalnie: typ danych dla lepszego doboru edytora
  dataType?: "text" | "number" | "select" | "boolean" | "list-modal";
}

export const createColumns = (
  meta: GenericColConfig[],
  permissions: Set<string>
): ColDef[] => {
  return meta.map((m) => {
    // Wyciągamy nasze customowe pola, resztę (np. cellRenderer) przekazujemy do AG Grid
    const { header, target, dataType, ...agGridProps } = m;

    return {
      cellRenderer: m.dataType === "list-modal" ? m.cellRenderer : undefined,
      onCellClicked: (params) => {
        if (m.dataType === "list-modal") {
          // Ta funkcja będzie przekazana z komponentu nadrzędnego
          openRelationModal({
            colDef: m,
            data: params.data,
            rowIndex: params.node.rowIndex!,
          });
        }
      },
      headerName: header,
      // Domyślne ustawienia (można nadpisać w configu)
      cellEditor:
        dataType === "number" ? "agNumberCellEditor" : "agTextCellEditor",

      // LOGIKA UPRAWNIEŃ:
      // Editable jest funkcją, aby reagować na zmiany w locie
      editable: (params) => {
        if (!target) return false;

        return hasPermission(
          permissions,
          target,
          ActionType.EDIT,
          ActionScope.ONE
        );
      },

      // WIZUALIZACJA EDYTOWALNOŚCI:
      cellClass: (params) => {
        // Zachowujemy customowe klasy jeśli były podane
        const baseClass =
          typeof agGridProps.cellClass === "string"
            ? agGridProps.cellClass
            : "";

        if (!target) {
          return baseClass;
        }

        const canEdit = hasPermission(
          permissions,
          target,
          ActionType.EDIT,
          ActionScope.OTHER
        );

        // Dodajemy klasy Tailwind w zależności od uprawnień
        return `${baseClass} ${
          canEdit
            ? "hover:bg-accent/10 cursor-pointer"
            : "opacity-80 cursor-not-allowed"
        }`;
      },

      // Przekazujemy resztę standardowych propsów AG Grid (w tym cellRenderer!)
      ...agGridProps,
    };
  });
};
