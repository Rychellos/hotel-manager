import { Show } from "solid-js";
import { Separator } from "../ui/separator";
import { title } from "process";

export default function NavHeader(props: {
  title?: string;
  isCollapsed(): boolean;
}) {
  return (
    <Show when={props.title && !props.isCollapsed()} fallback={<Separator />}>
      <Separator />
      <h3 class="py-2">{props.title}</h3>
    </Show>
  );
}
