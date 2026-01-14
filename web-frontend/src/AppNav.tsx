import { Nav } from "./components/Navigation/Nav";
import { Component, createEffect, createSignal } from "solid-js";
import { NavItemProps } from "./components/Navigation/NavItem";

import House from "lucide-solid/icons/house";
import User from "lucide-solid/icons/user";
import Shield from "lucide-solid/icons/shield-check";
import Bed from "lucide-solid/icons/bed";
import Key from "lucide-solid/icons/key";
import Star from "lucide-solid/icons/star";
import { Permission } from "~/lib/api";
import { UsePermissionsContext } from "./lib/PermissionsContext";

const navGroups: {
  groupName: string;
  groupItems: {
    requiredAuthority: Permission;
    navItem: {
      title: string;
      url: string;
      icon: Component;
      label: string;
    };
  }[];
}[] = [
  {
    groupName: "Hotel",
    groupItems: [
      {
        requiredAuthority: "ROOM:READ:PAGINATED",
        navItem: {
          icon: Bed,
          label: "",
          title: "Pokoje",
          url: "/room/",
        },
      },
      {
        requiredAuthority: "STANDARD:READ:PAGINATED",
        navItem: {
          icon: Star,
          label: "",
          title: "Standardy",
          url: "/standard/",
        },
      },
    ],
  },
  {
    groupName: "Bezpieczeństwo:",
    groupItems: [
      {
        requiredAuthority: "USER:READ:PAGINATED",
        navItem: {
          title: "Użytkownicy",
          url: "/user/",
          icon: User,
          label: "",
        },
      },
      {
        requiredAuthority: "ROLE:READ:PAGINATED",
        navItem: {
          title: "Role",
          url: "/role/",
          icon: Shield,
          label: "",
        },
      },
      {
        requiredAuthority: "PERMISSION:READ:PAGINATED",
        navItem: {
          title: "Permisjie",
          url: "/permission/",
          icon: Key,
          label: "",
        },
      },
    ],
  },
];

const home: Omit<NavItemProps, "isCollapsed"> = {
  icon: House,
  title: "Strona Główna",
  url: "/",
  exactUrl: true,
  label: "",
};

export default function AppNav(props: {
  isCollapsed(): boolean;
  onNavigation?: (url: string) => void;
}) {
  const [getAuthorityContext, _] = UsePermissionsContext()!;
  const [links, setLinks] = createSignal<(NavItemProps | string)[]>([]);

  createEffect(() => {
    const authorities = getAuthorityContext();

    if (!authorities) {
      return;
    }

    setLinks(
      navGroups
        .flatMap((group) => {
          const filtered = group.groupItems.filter((el) =>
            authorities.has(el.requiredAuthority)
          );

          return filtered.length ? [group.groupName, ...filtered] : [];
        })
        .map((el) =>
          typeof el !== "string"
            ? ({
                ...el.navItem,
                isCollapsed: props.isCollapsed,
              } as NavItemProps)
            : el
        )
    );
  });

  return (
    <Nav
      isCollapsed={props.isCollapsed}
      links={[{ ...home, isCollapsed: props.isCollapsed }, ...links()]}
    />
  );
}
