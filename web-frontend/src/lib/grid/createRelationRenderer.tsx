import { createResource, Suspense } from "solid-js";
import { Skeleton } from "~/components/ui/skeleton";

interface RelationRendererOptions {
  getEndpoint: (id: string) => any;
  labelSelector: (item: any) => string;
}

export const createRelationRenderer = (options: RelationRendererOptions) => {
  return (params: any) => {
    if (!params.data || !params.data.id) {
      return <Skeleton class="size-full scale-75 opacity-50" />;
    }

    const [data] = createResource(
      () => params.data.id,
      async (id) => {
        try {
          const fetcher = options.getEndpoint(id);
          const { data, ok } = await fetcher({ idOrUuid: id });

          if (!ok) return [];
          if (Array.isArray(data)) return data;
          if (data && typeof data === "object" && Array.isArray(data.content))
            return data.content;
          return data ? [data] : [];
        } catch (e) {
          console.error("Renderer fetch error", e);
          return [];
        }
      }
    );

    return (
      <Suspense fallback={<Skeleton class="h-6 w-24 rounded" />}>
        <div class="truncate px-1 text-sm" title="Kliknij, aby edytować">
          {(() => {
            const list = data();
            if (!list || list.length === 0)
              return <span class="text-muted-foreground italic">—</span>;

            return list.map(options.labelSelector).join(", ");
          })()}
        </div>
      </Suspense>
    );
  };
};
