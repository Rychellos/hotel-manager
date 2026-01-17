import { ColumnDef } from "@tanstack/solid-table";
import { Checkbox } from "../ui/checkbox";

export default {
  id: "select",
  header: (props) => (
    <Checkbox
      checked={props.table.getIsAllPageRowsSelected()}
      indeterminate={props.table.getIsSomePageRowsSelected()}
      onChange={(value) => props.table.toggleAllPageRowsSelected(!!value)}
      aria-label="Select all"
    />
  ),
  cell: (props) => (
    <Checkbox
      checked={props.row.getIsSelected()}
      onChange={(value) => props.row.toggleSelected(!!value)}
      aria-label="Select row"
    />
  ),
  maxSize: 1,
  enableSorting: false,
  enableHiding: false,
} as ColumnDef<never, any>;
