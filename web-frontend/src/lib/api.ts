import type { paths, components } from "../hotel-manager-schema";
import { Middleware, Fetcher, ApiResponse } from "openapi-typescript-fetch";
import { jwtDecode } from "jwt-decode";
import Cookies from "js-cookie";
import type { UUID } from "crypto";

export const client = Fetcher.for<paths>();
export type schemas = components["schemas"];

export const setAccessToken = (tokenString: string) => {
  const token = jwtDecode(tokenString);
  Cookies.set("accessToken", tokenString, {
    sameSite: "strict",
    expires: token.exp,
  });
};

export const getAccessToken = () => {
  return Cookies.get("accessToken");
};

const UNPROTECTED_ROUTES = [
  "/api/v1/auth/login",
  "/api/v1/auth/logout",
  "/api/v1/auth/refresh",
];

const authMiddleware: Middleware = async (url, init, next) => {
  if (!UNPROTECTED_ROUTES.some((pathname) => url.includes(pathname))) {
    if (getAccessToken()) {
      init.headers.append("Authorization", `Bearer ${getAccessToken()}`);
    }

    try {
      return await next(url, init);
    } catch (error) {
      return (error as ApiResponse).data;
    }
  }

  if (url.includes(UNPROTECTED_ROUTES[1])) {
    setAccessToken("");

    try {
      return await next(url, init);
    } catch (error) {
      return (error as ApiResponse).data;
    }
  }

  let response: ApiResponse;

  try {
    response = await next(url, init);
  } catch (error) {
    return (error as ApiResponse).data;
  }

  if (response.data) {
    setAccessToken(
      (response.data as components["schemas"]["AuthResponseDTO"]).accessToken!
    );
  }

  client.configure({
    init: {
      headers: {
        Authorization: `Bearer ${getAccessToken()}`,
      },
    },
  });

  return response;
};

client.use(authMiddleware);

export const enum ActionType {
  READ = "READ",
  CREATE = "CREATE",
  EDIT = "EDIT",
  DELETE = "DELETE",
  EXECUTE = "EXECUTE",
  ADMIN = "ADMIN",
}

export const enum ActionScope {
  ONE = "ONE",
  PAGINATED = "PAGINATED",
  ALL = "ALL",
  BULK = "BULK",
  SELF = "SELF",
  OTHER = "OTHER",
}

export type Permission = `${string}:${ActionType}:${ActionScope}`;

export type BaseDTO = {
  id: number;
  publicID: UUID;
};

export type Page<T = BaseDTO> = {
  content?: T[];
  page?: {
    size?: number;
    number?: number;
    totalElements?: number;
    totalPages?: number;
  };
};

export type PageQuery = {
  page?: number;
  size?: number;
  sort: `${string},${"asc" | "desc"}`[];
  [key: string]: any;
};

export type Fetcher<TData, TFilter> = (
  params: TFilter
) => Promise<ApiResponse<Page<TData>>>;
