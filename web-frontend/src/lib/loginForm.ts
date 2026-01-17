import * as v from "valibot";

export const AuthSchema = v.object({
  username: v.pipe(
    v.string(),
    v.nonEmpty("Proszę podać nazwę użytkownika"),
    v.minLength(4, "Nazwa użytkownika nie może mieć mniej niż 4 znaki")
  ),
  password: v.string(),
});

export type AuthForm = v.InferInput<typeof AuthSchema>;
