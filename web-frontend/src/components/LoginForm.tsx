import type { SubmitHandler } from "@modular-forms/solid";
import { createForm, FormError, valiForm } from "@modular-forms/solid";
import { AuthForm, AuthSchema } from "~/lib/loginForm";
import { TextField, TextFieldInput, TextFieldLabel } from "./ui/text-field";
import { Grid } from "./ui/grid";
import { Button } from "./ui/button";
import { Card, CardContent, CardHeader } from "./ui/card";
import { onMount, Show } from "solid-js";
import LoaderCircle from "lucide-solid/icons/loader-circle";
import { client, Permission, schemas, setAccessToken } from "~/lib/api";
import { UsePermissionsContext } from "~/lib/PermissionsContext";
import { UseUserContext } from "~/lib/UserContext";

export default function LoginForm() {
  const [getAuthorityContext, setPermissions] = UsePermissionsContext()!;
  const [getUserContext, setUserContext] = UseUserContext()!;

  const meEndpoint = client.path("/api/v1/users/me").method("get").create();

  const [authForm, { Form, Field }] = createForm<AuthForm>({
    validate: valiForm(AuthSchema),
  });

  const handleSubmit: SubmitHandler<AuthForm> = async (formData) => {
    const { ok, status, data } = await client
      .path("/api/v1/auth/login")
      .method("post")
      .create()(formData);

    if (!ok) {
      if (status == 404) {
        throw new FormError<AuthForm>({
          username: "Nie odnaleziono takiego użytkownika",
        });
      }

      if (status === 401) {
        throw new FormError<AuthForm>({
          password: "Nieprawidłowe dane logowania",
        });
      }

      console.error(data);
      throw new FormError("Coś poszło nie tak");
    }

    if ((data as schemas["AuthResponseDTO"]).permissions) {
      const permissions = (data as schemas["AuthResponseDTO"])
        .permissions as Permission[];

      setPermissions(new Set(permissions));
    }

    const currentUser = await meEndpoint({});

    if (currentUser.data) {
      setUserContext(currentUser.data);
    }

    return true;
  };

  return (
    <div class="flex justify-center items-center size-full md:container md:mx-auto">
      <Card class="w-full sm:w-1/2">
        <CardHeader>Logowanie</CardHeader>
        <CardContent>
          <Form onSubmit={handleSubmit} class="fill-background">
            <Grid class="gap-4">
              <Field name="username">
                {(field, props) => (
                  <TextField class="gap-1">
                    <TextFieldLabel class="sr-only">
                      Nazwa użytkownika
                    </TextFieldLabel>
                    <TextFieldInput
                      {...props}
                      type="email"
                      placeholder="Nazwa użytkownika"
                    />
                    <Show when={field.error}>
                      <p class="text-error-foreground">{field.error}</p>
                    </Show>
                  </TextField>
                )}
              </Field>
              <Field name="password">
                {(field, props) => (
                  <TextField class="gap-1">
                    <TextFieldLabel class="sr-only">Hasło</TextFieldLabel>
                    <TextFieldInput
                      {...props}
                      type="password"
                      placeholder="Hasło"
                    />
                    <Show when={field.error}>
                      <p class="text-error-foreground">{field.error}</p>
                    </Show>
                  </TextField>
                )}
              </Field>

              <Show when={authForm.response.message}>
                <p class="text-error-foreground">{authForm.response.message}</p>
              </Show>

              <Button type="submit" class="mt-2" disabled={authForm.submitting}>
                {authForm.submitting && (
                  <LoaderCircle class="mr-2 size-4 animate-spin" />
                )}
                Zaloguj
              </Button>
            </Grid>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
