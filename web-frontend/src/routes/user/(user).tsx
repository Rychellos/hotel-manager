import { lazy, Suspense } from "solid-js";
import Loading from "~/components/Loading";

export default function UserIndex() {
  return (
    <Suspense fallback={<Loading text="Wczytywanie widoku użytkowników..." />}>
      <div class="p-2 size-full flex">
        {lazy(() => import("~/components/tables/UserListTable"))()}
      </div>
    </Suspense>
  );
}
