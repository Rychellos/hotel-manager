import {
  createEffect,
  createResource,
  createSignal,
  For,
  Show,
  Suspense,
} from "solid-js";
import { Skeleton } from "~/components/ui/skeleton";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogDescription,
  DialogTrigger,
} from "~/components/ui/dialog";
import { Page } from "~/lib/api";
import { Checkbox } from "../ui/checkbox";
import { cn } from "~/lib/utils";

interface RelationModalProps {
  title: string;
  fetcher: (page: number, size: number) => Promise<any>;
  initialSelectedIds: number[];
  labelField: string;
  onSave: (ids: number[]) => void;
}

export function RelationModal(props: RelationModalProps) {
  const [page, setPage] = createSignal(0);
  const [selected, setSelected] = createSignal<number[]>(
    props.initialSelectedIds || []
  );

  const [resource] = createResource(page, async (p) => {
    const res = await props.fetcher(p, 10);
    return res as Page;
  });

  const toggle = (id: number) => {
    setSelected((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  return (
    <Dialog>
      <DialogTrigger class="bg-primary text-primary-foreground hover:bg-primary/90 rounded px-2 py-1 h-100% text-sm font-medium">
        {props.title}
      </DialogTrigger>
      <DialogContent class="p-0 flex flex-col max-h-[90vh]">
        <DialogHeader class="p-6 pb-2 border-b">
          <DialogTitle>{props.title}</DialogTitle>
          <DialogDescription>
            Zaznacz elementy z listy. Wybrano:{" "}
            <span class="font-bold text-primary">{selected().length}</span>
          </DialogDescription>
        </DialogHeader>

        <div class="flex-1 overflow-y-auto p-4 space-y-1 min-h-[300px] relative">
          <Show
            when={resource()}
            fallback={
              <div class="space-y-3">
                <Skeleton class="h-10 w-full" />
                <Skeleton class="h-10 w-full" />
                <Skeleton class="h-10 w-full" />
              </div>
            }
          >
            <div
              class={cn(
                "transition-opacity duration-200",
                resource.loading && "opacity-50 pointer-events-none"
              )}
            >
              <For
                each={resource()?.content}
                fallback={
                  <div class="text-center py-10 text-muted-foreground">
                    Brak danych do wyświetlenia
                  </div>
                }
              >
                {(item) => (
                  <div
                    onClick={() => toggle(item.id)}
                    class="flex items-center gap-3 p-3 rounded-md cursor-pointer transition-colors hover:bg-accent group"
                  >
                    <Checkbox
                      checked={selected().includes(item.id)}
                      onChange={() => {}}
                      class="pointer-events-none"
                    />

                    <span class="text-sm font-medium leading-none select-none">
                      {(item as any)[props.labelField]}
                    </span>
                  </div>
                )}
              </For>
            </div>
          </Show>
        </div>

        <div class="p-4 border-t bg-muted/20 space-y-4">
          <div class="flex justify-between items-center text-sm">
            <button
              type="button"
              disabled={page() === 0 || resource.loading}
              onClick={() => setPage((p) => p - 1)}
              class="px-3 py-1.5 border rounded hover:bg-background disabled:opacity-40 transition-colors"
            >
              Poprzednia
            </button>
            <span class="text-muted-foreground font-mono">
              {page() + 1} / {resource()?.page?.totalPages || 1}
            </span>
            <button
              type="button"
              disabled={
                page() >= (resource()?.page?.totalPages || 1) - 1 ||
                resource.loading
              }
              onClick={() => setPage((p) => p + 1)}
              class="px-3 py-1.5 border rounded hover:bg-background disabled:opacity-40 transition-colors"
            >
              Następna
            </button>
          </div>

          <DialogFooter class="flex-row justify-end gap-2">
            <button
              onClick={() => props.onSave(selected())}
              class="w-full sm:w-auto px-4 py-2 text-sm font-medium bg-primary text-primary-foreground rounded hover:bg-primary/90 transition-all active:scale-95"
            >
              Zapisz zmiany
            </button>
          </DialogFooter>
        </div>
      </DialogContent>
    </Dialog>
  );
}
