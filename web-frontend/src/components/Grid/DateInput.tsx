import { splitProps, Component } from "solid-js";
import { cn } from "~/lib/utils";
import { JSX } from "solid-js";

interface DateInputProps extends JSX.InputHTMLAttributes<HTMLInputElement> {
  class?: string;
}

export function DateInput(props: DateInputProps) {
  const [local, others] = splitProps(props, ["class"]);

  const handleInput = (e: InputEvent & { currentTarget: HTMLInputElement }) => {
    let value = e.currentTarget.value.replace(/\D/g, ""); // Usuń wszystko co nie jest cyfrą

    // Automatyczne dodawanie myślników (YYYY-MM-DD)
    if (value.length > 4 && value.length <= 6) {
      value = `${value.slice(0, 4)}-${value.slice(4)}`;
    } else if (value.length > 6) {
      value = `${value.slice(0, 4)}-${value.slice(4, 6)}-${value.slice(6, 8)}`;
    }

    e.currentTarget.value = value;
  };

  return (
    <input
      {...others}
      type="text"
      maxlength="10"
      placeholder="YYYY-MM-DD"
      onInput={handleInput}
      value={"test??"}
      class={cn("w-full border-0", local.class)}
    />
  );
}
