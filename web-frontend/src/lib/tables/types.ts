export interface ColumnMeta {
  header: string;
  field: string;
  flex?: number;
  editPermission?: string; // np. "USER:EDIT"
  type?: "text" | "number" | "select";
  options?: string[]; // dla dropdownów
}
