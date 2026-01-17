import { createResource, Suspense } from "solid-js";
import { Skeleton } from "~/components/ui/skeleton";

interface RelationRendererOptions {
  // Funkcja zwracająca endpoint z Twojego clienta
  getEndpoint: (id: string) => any;
  // Jak wyciągnąć tekst z pojedynczego elementu relacji
  labelSelector: (item: any) => string;
}

export const createRelationRenderer = (options: RelationRendererOptions) => {
  return (params: any) => {
    // Zabezpieczenie przed pustymi wierszami w infinite scrollu
    if (!params.data || !params.data.id) {
      return <Skeleton class="size-full scale-75 opacity-50" />;
    }

    const [data] = createResource(
      () => params.data.id,
      async (id) => {
        try {
          const fetcher = options.getEndpoint(id);
          const { data, ok } = await fetcher({ idOrUuid: id });

          if (!ok) return null;
          return Array.isArray(data) ? data : data.content || data;
        } catch (e) {
          console.error("Relation fetch error", e);
          return null;
        }
      }
    );

    return (
      <Suspense fallback={<Skeleton class="size-full scale-75" />}>
        <div class="truncate px-2" title="Pełna lista po najechaniu">
          {(() => {
            const rawData = data();
            if (!rawData) return "—";

            const list = Array.isArray(rawData) ? rawData : [rawData];
            if (list.length === 0) return "Brak";

            return list.map(options.labelSelector).join(", ");
          })()}
        </div>
      </Suspense>
    );
  };
};
