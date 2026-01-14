import { Title } from "@solidjs/meta";
import { A } from "@solidjs/router";
import { HttpStatusCode } from "@solidjs/start";
import { Button } from "~/components/ui/button";
import { Card, CardContent, CardHeader } from "~/components/ui/card";

export default function NotFound() {
  return (
    <main class="size-full flex">
      <Title>Nie znaleziono</Title>
      <HttpStatusCode code={404} />

      <Card class="m-auto">
        <CardHeader>Nie odnaleziono strony :(</CardHeader>

        <CardContent class="p-2 flex justify-end">
          <Button as={A} href="/">
            Okej
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
