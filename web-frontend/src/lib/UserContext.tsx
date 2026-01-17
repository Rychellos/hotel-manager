import { client, Permission, schemas } from "~/lib/api";
import {
  createContext,
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
import { setPermissions } from "./PermissionsContext";

export const UserContext = createContext<Signal<schemas["UserDTO"]>>();

export function UseUserContext() {
  return useContext(UserContext);
}

export const UserContextProvider = (props: ParentProps): JSXElement => {
  const [isReady, setReady] = createSignal(false);
  const [value, setValue] = createSignal<schemas["UserDTO"]>({
    email: "",
    id: -1,
    publicId: "-1",
    roleIds: [],
    username: "null",
  });
  const theme = useColorMode();

  const meEndpoint = client.path("/api/v1/users/me").method("get").create();
  const refreshEndpoint = client
    .path("/api/v1/auth/refresh")
    .method("post")
    .create();

  onMount(async () => {
    try {
      const { data, ok } = await meEndpoint({});

      if (ok) {
        setValue(data);
      } else {
        throw data;
      }
    } catch (error) {
      const { ok, data } = await refreshEndpoint({});

      if (!ok) {
        setReady(true);

        return;
      }

      if ((data as schemas["AuthResponseDTO"]).permissions) {
        const permissions = (data as schemas["AuthResponseDTO"])
          .permissions as Permission[];
        setPermissions(new Set(permissions));
      }

      try {
        const currentUser = await meEndpoint({});

        if (currentUser.data) {
          setValue(currentUser.data);
        }
      } catch {}
    }

    setReady(true);
  });

  return (
    <UserContext.Provider value={[value, setValue]}>
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
    </UserContext.Provider>
  );
};
