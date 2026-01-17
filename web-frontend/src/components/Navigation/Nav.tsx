import { For } from "solid-js";
import NavItem, { NavItemProps } from "./NavItem";
import NavHeader from "./NavHeader";

export interface NavProps {
  isCollapsed(): boolean;
  links: (NavItemProps | string)[];
  onNavigation?: (url: string) => void;
}

export function Nav(props: NavProps) {
  return (
    <div
      data-collapsed={props.isCollapsed()}
      class="group flex flex-col gap-4 py-2 data-[collapsed=true]:py-2"
    >
      <nav class="grid gap-1 px-2 group-[[data-collapsed=true]]:justify-center group-[[data-collapsed=true]]:px-2">
        <For each={props.links}>
          {(item) => {
            if (typeof item === "string") {
              return <NavHeader title={item} isCollapsed={props.isCollapsed} />;
            }

            return (
              <NavItem
                {...item}
                isCollapsed={props.isCollapsed}
                onNavigation={props.onNavigation}
              />
            );
          }}
        </For>
      </nav>
    </div>
  );
}
