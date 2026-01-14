import { useParams } from "@solidjs/router";
import { createSignal, onMount } from "solid-js";
import { client, schemas } from "~/lib/api";

export default function UserDetailById() {
  const params = useParams();
  const [data, setData] = createSignal<schemas["RoleDTO"]>();

  const userEndpoint = client
    .path("/api/v1/roles/{idOrUuid}")
    .method("get")
    .create();

  onMount(async () => {
    if (!params.id) {
      return;
    }

    const { data, ok } = await userEndpoint({ idOrUuid: params.id });

    if (ok && data) {
      setData(data);
    }
  });

  return (
    <div>
      <code>
        <pre>{JSON.stringify(data(), null, 2)}</pre>
      </code>
    </div>
  );
}
