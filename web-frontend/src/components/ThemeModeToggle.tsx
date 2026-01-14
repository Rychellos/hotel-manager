import { useColorMode } from "@kobalte/core";

// import { IconLaptop, IconMoon, IconSun } from "~/components/icons"
import Laptop from "lucide-solid/icons/laptop";
import Moon from "lucide-solid/icons/moon";
import Sun from "lucide-solid/icons/sun";

import { Button } from "~/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";

export function ThemeModeToggle() {
  const { setColorMode } = useColorMode();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger
        as={Button}
        variant="ghost"
        size="sm"
        class="w-9 px-0"
      >
        <Sun class="size-6 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
        <Moon class="absolute size-6 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
        <span class="sr-only">Zmień motyw</span>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem onSelect={() => setColorMode("light")}>
          <Sun class="mr-2 size-4" />
          <span>Jasny</span>
        </DropdownMenuItem>
        <DropdownMenuItem onSelect={() => setColorMode("dark")}>
          <Moon class="mr-2 size-4" />
          <span>Ciemny</span>
        </DropdownMenuItem>
        <DropdownMenuItem onSelect={() => setColorMode("system")}>
          <Laptop class="mr-2 size-4" />
          <span>Systemowy</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
