import { client, Permission, schemas } from "~/lib/api";
import {
  createContext,
  createEffect,
  createSignal,
  JSXElement,
  onMount,
  ParentProps,
  Show,
  Signal,
  useContext,
} from "solid-js";
import { useColorMode } from "@kobalte/core/color-mode";
import { Toaster } from "../components/ui/sonner";
import Loading from "~/components/Loading";
import { UseUserContext } from "./UserContext";

let permissionSignal: Signal<Set<Permission>>;
/**
 * @deprecated export
 */
export let PermissionSignal: Signal<Set<Permission>>;

export const setPermissions = (permissions: Set<Permission>) => {
  permissionSignal[1](permissions);
};

export const hasActionPermission = (permission: Permission) => {
  if (!permissionSignal) {
    return false;
  }

  return permissionSignal[0]().has(permission);
};

export const PermissionsContext = createContext<Signal<Set<Permission>>>();

export function UsePermissionsContext() {
  return useContext(PermissionsContext);
}

export const PermissionContextProvider = (props: ParentProps): JSXElement => {
  const [getUserContext, _] = UseUserContext()!;
  const [isReady, setReady] = createSignal(false);

  const userPermissionEndpoint = client
    .path("/api/v1/users/{idOrUuid}/permissions")
    .method("get")
    .create();

  permissionSignal = createSignal<Set<Permission>>(new Set());
  PermissionSignal = permissionSignal;
  const [value, setValue] = permissionSignal;

  const theme = useColorMode();

  createEffect(async () => {
    const userContext = getUserContext();

    if (value().size) {
      return;
    }

    try {
      if (userContext && userContext.id) {
        const { data, ok } = await userPermissionEndpoint({
          idOrUuid: userContext.id.toString(),
        });

        const permissions = new Set<Permission>();

        if (ok) {
          data.forEach((permission) => {
            permissions.add(permission.name as Permission);
          });

          setValue(permissions);
        }
      }
    } catch (error) {}

    setReady(true);
  });

  return (
    <PermissionsContext.Provider value={[value, setValue]}>
      <Toaster
        theme={theme.colorMode()}
        richColors={true}
        toastOptions={{
          cancelButtonStyle: { "place-self": "end" },
        }}
      />
      <Show
        when={isReady()}
        fallback={<Loading text="Ładowanie danych użytkownika dla aplikacji" />}
      >
        {props.children}
      </Show>
    </PermissionsContext.Provider>
  );
};
