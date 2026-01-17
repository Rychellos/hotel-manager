import { ParentProps } from "solid-js";
import { Card, CardContent } from "~/components/ui/card";
import Construction from "lucide-solid/icons/construction";

export default function Index(props: ParentProps) {
  return (
    <div class="flex size-full justify-center items-center">
      <Card class="animate-[pulse_1.5s_linear_infinite]">
        <CardContent class="flex p-4">
          <Construction />
          <span class="px-2">Strona w budowie</span>
        </CardContent>
      </Card>
    </div>
  );
}
