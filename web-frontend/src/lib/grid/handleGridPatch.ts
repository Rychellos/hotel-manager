import { CellValueChangedEvent } from "ag-grid-community";
import { toast } from "solid-sonner";

export const handleGridPatch = async <
  T extends { publicId: string; id: number }
>(
  event: CellValueChangedEvent<T>,
  patchFetcher: any
) => {
  const field = event.column.getColId();
  const newValue = event.newValue;
  const oldValue = event.oldValue;
  const idOrUuid =
    event.data?.publicId?.toString() || event.data?.id?.toString();

  if (newValue === oldValue) return;

  const body = [{ op: "replace", path: `/${field}`, value: newValue }];

  try {
    const { ok } = await patchFetcher({ idOrUuid, body });
    if (ok) toast.success(`Zaktualizowano ${event.colDef.headerName}`);
    else throw new Error("API Error");
  } catch (error) {
    console.error(error);
    toast.error("Błąd zapisu");
  }
};
