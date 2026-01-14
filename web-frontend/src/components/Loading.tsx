import LoaderIcon from "lucide-solid/icons/loader-pinwheel";
import { Card, CardContent } from "./ui/card";

export default function Loading(props: { text: string }) {
  return (
    <div class="flex size-full justify-center items-center">
      <Card>
        <CardContent class="flex p-4">
          <LoaderIcon class="animate-[spin_1.5s_linear_infinite]" />
          <span class="px-2">{props.text}</span>
        </CardContent>
      </Card>
    </div>
  );
}
