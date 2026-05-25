import { z } from 'zod';

/** PATCH /users/me — ít nhất một trường */
export const UpdateProfileBodySchema = z
  .object({
    name: z.string().min(1).max(120).optional(),
    healthGoal: z.enum(['LOSE_WEIGHT', 'GAIN_MUSCLE', 'MAINTAIN']).nullable().optional(),
    fcmTokens: z.array(z.string()).optional(),
  })
  .refine(
    (d) =>
      d.name !== undefined ||
      d.healthGoal !== undefined ||
      d.fcmTokens !== undefined,
    { message: 'At least one field required' }
  );

export type UpdateProfileBodyDto = z.infer<typeof UpdateProfileBodySchema>;
