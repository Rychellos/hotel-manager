import { GridApi, IDatasource, IGetRowsParams } from "ag-grid-community";
import { ApiResponse } from "openapi-typescript-fetch";
import { Page, PageQuery } from "../api";

export const createDatasource = (
  enpoint: (fetcherArgs: PageQuery) => Promise<ApiResponse<Page>>,
  pageSize: number
): IDatasource => {
  return {
    getRows: async (params: IGetRowsParams) => {
      let sorting: `${string},${"asc" | "desc"}`[] = [];

      if (params.sortModel?.length > 0) {
        const { colId, sort } = params.sortModel[0];
        sorting.push(`${colId},${sort}`);
      }

      const pageNumber = Math.floor(params.startRow / pageSize);

      try {
        const { data, ok } = await enpoint({
          page: pageNumber,
          size: pageSize,
          sort: sorting,
        });

        if (ok && data.content) {
          params.successCallback(data.content, data.page?.totalElements);
        } else {
          params.failCallback();
        }
      } catch (error) {
        params.failCallback();
      }
    },
  };
};
