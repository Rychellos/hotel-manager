import { ActionScope, ActionType, Permission } from "../api";

export const hasPermission = (
  permissions: Set<string>,
  target: string,
  action: ActionType,
  scope: ActionScope = ActionScope.OTHER
): boolean => {
  const perm: Permission = `${target}:${action}:${scope}`;
  return permissions.has(perm);
};
