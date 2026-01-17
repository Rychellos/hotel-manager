import { Component, createEffect, createSignal, Show } from "solid-js";
import { cn } from "~/lib/utils";
import { buttonVariants } from "~/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "~/components/ui/tooltip";
import { A, useLocation } from "@solidjs/router";

export type NavItemProps = {
  icon: Component;
  title: string;
  url: string;
  isCollapsed(): boolean;
  label?: string;
  exactUrl?: boolean;
  onNavigation?: (url: string) => void;
};

export default function NavItem(props: NavItemProps) {
  const [variant, setVariant] = createSignal<"default" | "ghost">("ghost");
  const location = useLocation();

  createEffect(() => {
    if (props.exactUrl && location.pathname === props.url) {
      setVariant("default");
    } else if (!props.exactUrl && location.pathname.startsWith(props.url)) {
      setVariant("default");
    } else {
      setVariant("ghost");
    }
  });

  return (
    <Show
      when={props.isCollapsed()}
      fallback={
        <A
          href={props.url}
          class={cn(
            buttonVariants({
              variant: variant(),
              size: "sm",
              class: "text-sm",
            }),
            variant() === "default" &&
              "dark:bg-muted dark:text-white dark:hover:bg-muted dark:hover:text-white",
            "justify-start"
          )}
        >
          <div class="mr-2">
            <props.icon />
          </div>
          {props.title}
          {props.label && (
            <span
              class={cn(
                "ml-auto",
                variant() === "default" && "text-background dark:text-white"
              )}
            >
              {props.label}
            </span>
          )}
        </A>
      }
    >
      <Tooltip openDelay={0} closeDelay={0} placement="right">
        <TooltipTrigger
          as={A}
          href={props.url}
          class={cn(
            buttonVariants({ variant: variant(), size: "icon" }),
            "size-9",
            variant() === "default" &&
              "dark:bg-muted dark:text-muted-foreground dark:hover:bg-muted dark:hover:text-white"
          )}
          onclick={
            props.onNavigation
              ? () => props.onNavigation!(props.url)
              : undefined
          }
        >
          <props.icon />
          <span class="sr-only">{props.title}</span>
        </TooltipTrigger>
        <TooltipContent class="flex items-center gap-4">
          {props.title}
          <Show when={props.label}>
            <span class="ml-auto text-muted-foreground">{props.label}</span>
          </Show>
        </TooltipContent>
      </Tooltip>
    </Show>
  );
}
