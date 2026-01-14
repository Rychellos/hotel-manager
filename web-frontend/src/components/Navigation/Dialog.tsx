import { Button } from "~/components/ui/button";

import Menu from "lucide-solid/icons/menu";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "~/components/ui/dialog";
import { useDialogContext } from "@kobalte/core/dialog";
import { Separator } from "~/components/ui/separator";
import AppNav from "~/AppNav";
import { createEffect, createMemo } from "solid-js";
import { useLocation } from "@solidjs/router";

export default function NavigationDialog() {
  return (
    <Dialog>
      <Content />
    </Dialog>
  );
}

function Content() {
  const ctx = useDialogContext();

  const pathname = createMemo(() => useLocation().pathname);

  createEffect(() => {
    pathname();

    ctx.close();
  });

  return (
    <>
      <DialogTrigger class="sm:hidden">
        <span class="sr-only">Otwórz menu nawigacji</span>
        <Menu />
      </DialogTrigger>
      <DialogContent class="border-0 border-r absolute left-0 top-0 translate-0 h-full duration-500 data-expanded:animate-in data-closed:animate-out data-[closed]:fade-out-0 data-[expanded]:fade-in-0 data-closed:slide-out-to-left-full data-expanded:slide-in-from-left-full">
        <DialogHeader>
          <DialogTitle class="text-start">Manadżer Hotelu</DialogTitle>
          <Separator />
          <AppNav isCollapsed={() => false} />
        </DialogHeader>
      </DialogContent>
    </>
  );
}
