import { ValueFormatterParams, ValueParserParams } from "ag-grid-community";
import { DateInput } from "~/components/Grid/DateInput";

export const columnTypes = {
  dateString: {
    filter: "agDateColumnFilter",
    valueFormatter: (params: ValueFormatterParams) => {
      if (!params.value) return "";
      return params.value;
    },

    valueParser: (params: ValueParserParams) => {
      const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
      return dateRegex.test(params.newValue)
        ? params.newValue
        : params.oldValue;
    },
    cellEditorParams: {
      useFormatter: true,
    },
    cellEditor: (params: any) => {
      return (
        <DateInput
          value={params.value}
          onBlur={(e) => params.stopEditing(e.currentTarget.value)}
          class="h-full border-none focus-visible:ring-0"
        />
      );
    },
  },
};
