import { ParentProps, Show } from "solid-js";
import { UseUserContext } from "~/lib/UserContext";
import LoginForm from "./LoginForm";

//
export default function LoginWrapper(props: ParentProps) {
  const [getAppContext, _] = UseUserContext()!;

  return (
    <Show when={getAppContext().id !== -1} fallback={<LoginForm />}>
      {props.children}
    </Show>
  );
}
