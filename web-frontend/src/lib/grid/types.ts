import { ColDef } from "ag-grid-community";
import { paths } from "~/hotel-manager-schema";
import { ActionScope, ActionType } from "../api";

export interface GenericColConfig extends ColDef {
  field: string;
  header: string;
  actionTarget: string;
  actionType?: ActionType;
  actionScope?: ActionScope;
  flex?: number;

  dataType?:
    | "text"
    | "number"
    | "select"
    | "boolean"
    | "list-modal"
    | "agDateColumnFilter";

  modalConfig?: {
    title: string;
    getEndpoint: keyof paths;
    patchEndpoint: keyof paths;
    labelField: string;
    currentlySelectedIds: string[];
  };
}
